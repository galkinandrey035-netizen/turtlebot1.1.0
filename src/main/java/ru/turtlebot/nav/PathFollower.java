package ru.turtlebot.nav;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.config.ConfigManager;

import java.util.List;

public final class PathFollower {
    public enum Status { RUNNING, FINISHED, STUCK, BLOCKED }

    private List<BlockPos> path = List.of();
    private int idx, stillTicks, nodeTicks;
    private Vec3d lastPos;

    public boolean hasPath() { return idx < path.size(); }

    public void set(List<BlockPos> p) {
        path = p; idx = 0; stillTicks = 0; nodeTicks = 0; lastPos = null;
    }

    public void clear() { path = List.of(); idx = 0; }\n\n    public List<BlockPos> remainingPath() {\n        return idx >= path.size() ? List.of() : List.copyOf(path.subList(idx, path.size()));\n    }

    public Status tick(MinecraftClient mc) {
        if (idx >= path.size()) { Keys.release(mc); return Status.FINISHED; }
        ClientPlayerEntity p = mc.player;
        BlockPos next = path.get(idx);

        Walkability w = new Walkability(mc.world, ConfigManager.get());
        w.autoStrict(p.getBlockPos());
        if (!w.standable(next)) { Keys.release(mc); return Status.BLOCKED; }

        double dx = next.getX() + 0.5 - p.getX();
        double dz = next.getZ() + 0.5 - p.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        boolean last = idx == path.size() - 1;
        boolean yOk = Math.abs(p.getY() - next.getY()) < 0.9;
        if (dist < (last ? 0.35 : 0.4) && yOk && p.isOnGround()) {
            idx++;
            nodeTicks = 0;
            if (idx >= path.size()) { Keys.release(mc); return Status.FINISHED; }
            next = path.get(idx);
            dx = next.getX() + 0.5 - p.getX();
            dz = next.getZ() + 0.5 - p.getZ();
        }

        float desired = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90f;
        float diff = MathHelper.wrapDegrees(desired - p.getYaw());
        p.setYaw(p.getYaw() + MathHelper.clamp(diff, -7f, 7f));
        p.setPitch(p.getPitch() + (15f - p.getPitch()) * 0.12f);
        // Сначала довернуть корпус на месте, затем идти прямо к следующей точке.\n        // Это не даёт инерции движения превращать поворот в бег по кругу.\n        Keys.walk(mc, Math.abs(diff) < 12f);

        Vec3d pos = p.getPos();
        if (lastPos != null && pos.squaredDistanceTo(lastPos) < 0.0004) stillTicks++; else stillTicks = 0;
        lastPos = pos;
        nodeTicks++;
        if (stillTicks >= 25 || nodeTicks > 60) { Keys.release(mc); return Status.STUCK; }
        return Status.RUNNING;
    }
}
