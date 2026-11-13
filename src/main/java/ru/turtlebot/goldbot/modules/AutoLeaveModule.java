package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

/**
 * Disconnects from the current server when another player enters the configured radius.
 * Disabled by default; enabling it is an explicit user action.
 */
public final class AutoLeaveModule extends GoldModule {
    private static final double DEFAULT_RADIUS = 10.0;
    private static final double MIN_RADIUS = 5.0;
    private static final double MAX_RADIUS = 40.0;

    private double radius = DEFAULT_RADIUS;

    public AutoLeaveModule() {
        super("AutoLeave", "Misc");
    }

    public void setRadius(double radius) {
        if (!Double.isFinite(radius)) return;
        this.radius = Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, radius));
    }

    public double getRadius() {
        return radius;
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.getNetworkHandler() == null) return;

        for (PlayerEntity other : client.world.getPlayers()) {
            if (other == client.player || other.isRemoved()) continue;
            double distanceSquared = client.player.squaredDistanceTo(other);
            if (distanceSquared <= radius * radius) {
                String playerName = other.getGameProfile().getName();
                client.getNetworkHandler().getConnection().disconnect(
                        Text.literal("[AutoLeave] Nearby player detected: " + playerName));
                setEnabled(false, client);
                return;
            }
        }
    }
}
