package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

/** Holds the player at the position captured when the module is enabled. */
public final class AirStuckModule extends GoldModule {
    private double anchorX;
    private double anchorY;
    private double anchorZ;
    private float anchorYaw;
    private float anchorPitch;

    public AirStuckModule() {
        super("AirStuck", "Movement");
    }

    @Override
    protected void onEnable(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;
        anchorX = player.getX();
        anchorY = player.getY();
        anchorZ = player.getZ();
        anchorYaw = player.getYaw();
        anchorPitch = player.getPitch();
    }

    @Override
    protected void onTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;
        player.setVelocity(0.0, 0.0, 0.0);
        player.setPosition(anchorX, anchorY, anchorZ);
        player.setYaw(anchorYaw);
        player.setPitch(anchorPitch);
        player.setOnGround(false);
    }

    @Override
    protected void onDisable(MinecraftClient client) {
        if (client.player != null) client.player.setVelocity(0.0, 0.0, 0.0);
    }
}
