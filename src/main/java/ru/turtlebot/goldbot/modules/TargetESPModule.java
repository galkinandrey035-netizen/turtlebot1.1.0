package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class TargetESPModule extends GoldModule {
    private static TargetESPModule instance;
    public TargetESPModule() { super("TargetESP", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}