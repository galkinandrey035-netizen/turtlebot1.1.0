package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.GoldModule;

/**
 * Configurable horizontal movement boost. The boost is applied only while
 * the player provides directional input and does not alter vertical velocity.
 */
public final class SpeedModule extends GoldModule {
    private double boostPerTick = 0.039;

    public SpeedModule() {
        super("Speed", "Movement");
    }

    public void setBoostPerTick(double value) {
        boostPerTick = Math.max(0.001, Math.min(0.2, value));
    }

    public double getBoostPerTick() {
        return boostPerTick;
    }

    @Override
    protected void onTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null || client.currentScreen != null ||
                player.isGliding() || player.isTouchingWater() || player.isInLava() ||
                player.isSneaking()) return;

        float forward = player.input.movementForward;
        float sideways = player.input.movementSideways;
        if (forward == 0.0F && sideways == 0.0F) return;

        double yaw = Math.toRadians(player.getYaw());
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double dx = -sin * forward + cos * sideways;
        double dz = cos * forward + sin * sideways;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-6) return;

        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x + dx / length * boostPerTick,
                velocity.y, velocity.z + dz / length * boostPerTick);
    }
}
