package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.GoldModule;

public final class FreeCamModule extends GoldModule {
    private static FreeCamModule instance;
    private Vec3d position;
    private float yaw, pitch, speed = 2.0f;
    private boolean freeze = true;

    public FreeCamModule() { super("FreeCam", "Misc"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled() && instance.position != null; }
    public static boolean freezePlayer() { return active() && instance.freeze; }
    public static Vec3d cameraPosition() { return instance == null ? null : instance.position; }
    public static float cameraYaw() { return instance == null ? 0 : instance.yaw; }
    public static float cameraPitch() { return instance == null ? 0 : instance.pitch; }
    public static boolean shouldCancel(net.minecraft.network.packet.Packet<?> packet) {
        return freezePlayer() && packet instanceof net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
    }
    public void setSpeed(float value) { speed = MathHelper.clamp(value, 0.1f, 10.0f); }
    public void setFreeze(boolean value) { freeze = value; }

    @Override protected void onEnable(MinecraftClient client) {
        if (client.player == null) { setEnabled(false, client); return; }
        position = client.player.getEyePos();
        yaw = client.player.getYaw();
        pitch = client.player.getPitch();
    }

    @Override protected void onDisable(MinecraftClient client) {
        position = null;
        if (client.player != null) client.player.setVelocity(Vec3d.ZERO);
    }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || position == null) return;
        long window = client.getWindow().getHandle();
        if (key(window, GLFW.GLFW_KEY_LEFT)) yaw -= 2.5f;
        if (key(window, GLFW.GLFW_KEY_RIGHT)) yaw += 2.5f;
        if (key(window, GLFW.GLFW_KEY_UP)) pitch = Math.max(-89.0f, pitch - 2.0f);
        if (key(window, GLFW.GLFW_KEY_DOWN)) pitch = Math.min(89.0f, pitch + 2.0f);

        double forward = (key(window, GLFW.GLFW_KEY_W) ? 1 : 0) - (key(window, GLFW.GLFW_KEY_S) ? 1 : 0);
        double side = (key(window, GLFW.GLFW_KEY_D) ? 1 : 0) - (key(window, GLFW.GLFW_KEY_A) ? 1 : 0);
        double vertical = (key(window, GLFW.GLFW_KEY_SPACE) ? 1 : 0) - (key(window, GLFW.GLFW_KEY_LEFT_SHIFT) ? 1 : 0);
        double radians = Math.toRadians(yaw);
        double dx = -Math.sin(radians) * forward + Math.cos(radians) * side;
        double dz = Math.cos(radians) * forward + Math.sin(radians) * side;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length > 0) { dx /= length; dz /= length; }
        position = position.add(dx * speed, vertical * speed, dz * speed);
        if (freeze) client.player.setVelocity(Vec3d.ZERO);
    }

    private static boolean key(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
}