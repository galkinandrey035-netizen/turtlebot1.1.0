package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

/**
 * GoldBot-compatible AntiAFK actions: periodic jump, yaw rotation, or chat message/command.
 * All intervals are measured in seconds and clamped to the original module's 1..60 range.
 */
public final class AntiAfkModule extends GoldModule {
    public enum Mode { JUMP, ROTATE, MESSAGE }

    private Mode mode = Mode.JUMP;
    private int intervalSeconds = 1;
    private float rotationAngle = 10.0F;
    private String message = "";
    private int elapsedTicks;

    public AntiAfkModule() {
        super("AntiAFK", "Player");
    }

    public void setMode(Mode mode) {
        if (mode != null && this.mode != mode) {
            this.mode = mode;
            elapsedTicks = 0;
        }
    }

    public Mode getMode() { return mode; }

    public void setIntervalSeconds(int seconds) {
        intervalSeconds = Math.max(1, Math.min(60, seconds));
        elapsedTicks = 0;
    }

    public int getIntervalSeconds() { return intervalSeconds; }

    public void setRotationAngle(float angle) {
        if (Float.isFinite(angle)) rotationAngle = Math.max(1.0F, Math.min(180.0F, angle));
    }

    public float getRotationAngle() { return rotationAngle; }

    public void setMessage(String message) {
        this.message = message == null ? "" : message;
    }

    public String getMessage() { return message; }

    @Override
    protected void onEnable(MinecraftClient client) {
        elapsedTicks = 0;
    }

    @Override
    protected void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.currentScreen != null) return;

        if (++elapsedTicks < intervalSeconds * 20) return;
        if (mode == Mode.JUMP && !player.isOnGround()) return;

        elapsedTicks = 0;
        switch (mode) {
            case JUMP -> player.jump();
            case ROTATE -> player.setYaw(player.getYaw() + rotationAngle);
            case MESSAGE -> sendMessage(player);
        }
    }

    private void sendMessage(ClientPlayerEntity player) {
        if (message.isBlank() || player.networkHandler == null) return;
        if (message.startsWith("/")) {
            player.networkHandler.sendChatCommand(message.substring(1));
        } else {
            player.networkHandler.sendChatMessage(message);
        }
    }
}
