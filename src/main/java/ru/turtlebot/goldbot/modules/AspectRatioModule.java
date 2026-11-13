package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Scales the horizontal projection aspect ratio without changing the window size. */
public final class AspectRatioModule extends GoldModule {
    private static AspectRatioModule instance;
    private double ratio = 1.0;
    public AspectRatioModule() { super("AspectRatio", "Render"); instance = this; }
    public static double ratio() { return instance == null || !instance.enabled() ? 1.0 : instance.ratio; }
    public void setRatio(double value) { ratio = Math.max(0.1, Math.min(2.0, value)); }
    @Override protected void onTick(MinecraftClient client) {}
}