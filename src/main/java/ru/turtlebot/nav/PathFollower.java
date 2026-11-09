package ru.turtlebot.nav;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.config.ConfigManager;

import java.util.List;

public final class PathFollower {
    public enum Status { RUNNING, FINISHED, STUCK, BLOCKED }

    private static final float TURN_SPEED = 18f;
    private static final float WALK_ANGLE = 25f;
    private static final double LOOKAHEAD = 0.8;

    private List<BlockPos> path = List.of();
    private int idx, stillTicks, nodeTicks;
    private Vec3d lastPos, segStart;

    public boolean hasPath() { return idx < path.size(); }

    public void set(List<BlockPos> p) {
        path = p; idx = 0; stillTicks = 0; nodeTicks = 0; lastPos = null; segStart = null;
    }

    public void clear() { path = List.of(); idx = 0; segStart = null; }

    public List<BlockPos> remainingPath() {
        if (idx >= path.size()) return List.of();
        return List.copyOf(path.subList(idx, path.size()));
    }

    private static boolean isEgg(MinecraftClient mc, BlockPos pos) {
        return mc.world != null && mc.world.getBlockState(pos).isOf(Blocks.TURTLE_EGG);
    }

    public Status tick(MinecraftClient mc) {
        if (idx >= path.size()) { Keys.release(mc); return Status.FINISHED; }
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.world == null) { Keys.release(mc); return Status.BLOCKED; }
        if (segStart == null) segStart = new Vec3d(p.getX(), p.getY(), p.getZ());
        BlockPos next = path.get(idx);

        Walkability w = new Walkability(mc.world, ConfigManager.get());
        w.autoStrict(p.getBlockPos());
        if (!w.standable(next)) { Keys.release(mc); return Status.BLOCKED; }

        double yawRad = Math.toRadians(p.getYaw());
        BlockPos ahead = BlockPos.ofFloored(
                p.getX() - Math.sin(yawRad) * 0.6, p.getY(), p.getZ() + Math.cos(yawRad) * 0.6);
        if (isEgg(mc, ahead) || isEgg(mc, ahead.down())) {
            Keys.release(mc);
            return Status.BLOCKED;
        }

        double nx = next.getX() + 0.5, nz = next.getZ() + 0.5;
        double sx = nx - segStart.x, sz = nz - segStart.z;
        double segLen = Math.sqrt(sx * sx + sz * sz);
        double along = segLen > 1e-3
                ? ((p.getX() - segStart.x) * sx + (p.getZ() - segStart.z) * sz) / segLen : 0;
        double dist = Math.hypot(nx - p.getX(), nz - p.getZ());
        boolean last = idx == path.size() - 1;
        boolean yOk = Math.abs(p.getY() - next.getY()) < 0.9;
        boolean passed = !last && segLen > 1e-3 && along > segLen + 0.15;

        if ((dist < (last ? 0.35 : 0.4) || passed) && yOk && p.isOnGround()) {
            segStart = new Vec3d(nx, p.getY(), nz);
            idx++;
            nodeTicks = 0;
            if (idx >= path.size()) { Keys.release(mc); return Status.FINISHED; }
            next = path.get(idx);
            nx = next.getX() + 0.5; nz = next.getZ() + 0.5;
            sx = nx - segStart.x; sz = nz - segStart.z;
            segLen = Math.sqrt(sx * sx + sz * sz);
            along = segLen > 1e-3
                    ? ((p.getX() - segStart.x) * sx + (p.getZ() - segStart.z) * sz) / segLen : 0;
            last = idx == path.size() - 1;
        }

        double ax = nx, az = nz;
        if (!last && segLen > 1e-3) {
            double look = Math.min(segLen, Math.max(along, 0) + LOOKAHEAD);
            ax = segStart.x + sx / segLen * look;
            az = segStart.z + sz / segLen * look;
        }

        float desired = (float) (MathHelper.atan2(az - p.getZ(), ax - p.getX()) * 180.0 / Math.PI) - 90f;
        float diff = MathHelper.wrapDegrees(desired - p.getYaw());
        p.setYaw(p.getYaw() + MathHelper.clamp(diff, -TURN_SPEED, TURN_SPEED));
        p.setPitch(p.getPitch() + (15f - p.getPitch()) * 0.12f);
        Keys.walk(mc, Math.abs(diff) < WALK_ANGLE);

        Vec3d pos = p.getPos();
        if (lastPos != null && pos.squaredDistanceTo(lastPos) < 0.0004) stillTicks++; else stillTicks = 0;
        lastPos = pos;
        nodeTicks++;
        if (stillTicks >= 25 || nodeTicks > 60) { Keys.release(mc); return Status.STUCK; }
        return Status.RUNNING;
    }
}
