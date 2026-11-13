package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class BetterF5Module extends GoldModule {
    private static BetterF5Module instance;
    private boolean noCameraCollision = true;
    public BetterF5Module() { super("BetterF5", "Render"); instance = this; }
    public static boolean noCameraCollision() { return instance != null && instance.enabled() && instance.noCameraCollision; }
    public void setNoCameraCollision(boolean value) { noCameraCollision = value; }
    @Override protected void onTick(MinecraftClient client) {}
}