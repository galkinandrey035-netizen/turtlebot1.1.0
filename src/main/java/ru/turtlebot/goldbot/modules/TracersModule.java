package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Draws lines from the camera to other players. */
public final class TracersModule extends GoldModule {
    private static TracersModule instance;
    public TracersModule() { super("Tracers", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}