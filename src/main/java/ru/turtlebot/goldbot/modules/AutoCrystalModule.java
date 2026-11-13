package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Blocks;
import ru.turtlebot.goldbot.FriendRegistry;
import ru.turtlebot.goldbot.GoldModule;

/**
 * Conservative AutoCrystal implementation.
 * Uses a distance-based self-protection envelope rather than pretending to
 * reproduce GoldBot's original hidden damage simulator.
 */
public final class AutoCrystalModule extends GoldModule {
    private boolean attackEnabled = true;
    private boolean placeEnabled = true;
    private double enemyRange = 5.0;
    private double selfSafeRange = 6.5;
    private long nextActionAt;

    public AutoCrystalModule() {
        super("AutoCrystal", "Combat");
    }

    public void setAttackEnabled(boolean value) { attackEnabled = value; }
    public void setPlaceEnabled(boolean value) { placeEnabled = value; }
    public void setEnemyRange(double value) { enemyRange = Math.max(2.0, Math.min(8.0, value)); }
    public void setSelfSafeRange(double value) { selfSafeRange = Math.max(5.0, Math.min(10.0, value)); }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null ||
                client.currentScreen != null || System.currentTimeMillis() < nextActionAt) return;

        ClientPlayerEntity player = client.player;
        PlayerEntity target = client.world.getPlayers().stream()
                .filter(other -> other != player && other.isAlive())
                .filter(other -> !FriendRegistry.isFriend(other.getGameProfile().getName()))
                .filter(other -> player.squaredDistanceTo(other) <= enemyRange * enemyRange)
                .min((a, b) -> Double.compare(player.squaredDistanceTo(a), player.squaredDistanceTo(b)))
                .orElse(null);
        if (target == null) return;

        if (attackEnabled) {
            for (EndCrystalEntity crystal : client.world.getEntitiesByClass(
                    EndCrystalEntity.class, player.getBoundingBox().expand(8.0), EndCrystalEntity::isAlive)) {
                if (crystal.squaredDistanceTo(target) <= 36.0 &&
                        player.squaredDistanceTo(crystal) >= selfSafeRange * selfSafeRange) {
                    client.interactionManager.attackEntity(player, crystal);
                    player.swingHand(Hand.MAIN_HAND);
                    nextActionAt = System.currentTimeMillis() + 150L;
                    return;
                }
            }
        }

        if (!placeEnabled || player.getMainHandStack().isEmpty() &&
                player.getOffHandStack().isEmpty()) return;
        int crystalSlot = findCrystalSlot(player);
        if (crystalSlot < 0) return;

        BlockPos targetBlock = target.getBlockPos();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos base = targetBlock.add(dx, -1, dz);
                if (!client.world.getBlockState(base).isOf(Blocks.OBSIDIAN) &&
                        !client.world.getBlockState(base).isOf(Blocks.BEDROCK)) continue;
                if (!client.world.isAir(base.up()) || !client.world.isAir(base.up(2))) continue;
                Vec3d crystalPos = Vec3d.ofCenter(base.up());
                if (crystalPos.squaredDistanceTo(target.getPos()) > 20.25 ||
                        crystalPos.squaredDistanceTo(player.getPos()) < selfSafeRange * selfSafeRange) continue;
                Box crystalBox = new Box(base.up()).expand(0.1);
                if (!client.world.getEntitiesByClass(EndCrystalEntity.class, crystalBox,
                        EndCrystalEntity::isAlive).isEmpty()) continue;

                int previousSlot = player.getInventory().selectedSlot;
                player.getInventory().selectedSlot = crystalSlot;
                BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(base).add(0.0, 0.5, 0.0),
                        Direction.UP, base, false);
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                player.getInventory().selectedSlot = previousSlot;
                nextActionAt = System.currentTimeMillis() + 250L;
                return;
            }
        }
    }

    private static int findCrystalSlot(PlayerEntity player) {
        for (int slot = 0; slot < 9; slot++) {
            if (player.getInventory().getStack(slot).isOf(Items.END_CRYSTAL)) return slot;
        }
        return -1;
    }
}
