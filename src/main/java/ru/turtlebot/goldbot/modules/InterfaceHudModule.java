package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class InterfaceHudModule extends GoldModule {
    private static InterfaceHudModule instance;
    public InterfaceHudModule() { super("InterfaceHud", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}