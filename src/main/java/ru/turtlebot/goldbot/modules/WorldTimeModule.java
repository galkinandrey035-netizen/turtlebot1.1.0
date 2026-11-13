package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Changes only the local visual day/night cycle. */
public final class WorldTimeModule extends GoldModule {
    private static WorldTimeModule instance;
    private float hour = 12.0F;
    public WorldTimeModule() { super("WorldTime", "Render"); instance = this; }
    public static long visualTimeOfDay() {
        if (instance == null) return 6000L;
        long ticks = (long) ((instance.hour - 6.0F) * 1000.0F);
        return Math.floorMod(ticks, 24000L);
    }
    public void setHour(float value) { hour = Math.max(0.0F, Math.min(24.0F, value)); }
    @Override protected void onTick(MinecraftClient client) {}
}