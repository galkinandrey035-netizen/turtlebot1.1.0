package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Client-side night-vision brightness override. */
public final class FullBrightModule extends GoldModule {
    private static FullBrightModule instance;
    public FullBrightModule() { super("FullBright", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}