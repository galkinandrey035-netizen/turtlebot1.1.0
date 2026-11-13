package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Allows item use while a block is being mined, matching the source module behavior. */
public final class MultiTaskModule extends GoldModule {
    private static MultiTaskModule instance;
    public MultiTaskModule() { super("MultiTask", "Player"); instance = this; }
    public static boolean enabledNow() { return instance != null && instance.enabled(); }
    @Override protected void onTick(MinecraftClient client) {}
}