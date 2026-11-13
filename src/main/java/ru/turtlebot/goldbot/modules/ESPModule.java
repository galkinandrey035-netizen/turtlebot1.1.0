package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Draws wireframe boxes around visible client-world players. */
public final class ESPModule extends GoldModule {
    private static ESPModule instance;
    public ESPModule() { super("ESP", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}