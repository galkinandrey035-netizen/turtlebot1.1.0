package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class VisualSpinModule extends GoldModule {
    private static VisualSpinModule instance;
    private float yawSpeed = 60.0f;
    private float pitchAmplitude = 30.0f;
    private float pitchSpeed = 12.0f;
    private float yaw;
    private float pitchPhase;
    private boolean onlyThirdPerson = true;
    public VisualSpinModule() { super("VisualSpin", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    public static float yawOffset() { return instance == null ? 0 : instance.yaw; }
    public static float pitchOffset() {
        return instance == null ? 0 : (float)Math.sin(Math.toRadians(instance.pitchPhase)) * instance.pitchAmplitude;
    }
    public static boolean onlyThirdPerson() { return instance != null && instance.onlyThirdPerson; }
    public void setYawSpeed(float value) { yawSpeed = Math.max(0, Math.min(360, value)); }
    public void setPitchAmplitude(float value) { pitchAmplitude = Math.max(0, Math.min(90, value)); }
    public void setPitchSpeed(float value) { pitchSpeed = Math.max(0, Math.min(60, value)); }
    public void setOnlyThirdPerson(boolean value) { onlyThirdPerson = value; }
    @Override protected void onTick(MinecraftClient client) {
        yaw = (yaw + yawSpeed) % 360.0f;
        pitchPhase = (pitchPhase + pitchSpeed) % 360.0f;
    }
}