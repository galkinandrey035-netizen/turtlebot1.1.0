package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class ProjectilePredictionModule extends GoldModule {
    private static ProjectilePredictionModule instance;
    public ProjectilePredictionModule() { super("ProjectilePrediction", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}