package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Optional local timestamps for incoming chat messages. */
public final class BetterChatModule extends GoldModule {
    private static BetterChatModule instance;
    private boolean timestamps;
    private boolean seconds = true;
    public BetterChatModule() { super("BetterChat", "Render"); instance = this; }
    public static boolean timestamps() { return instance != null && instance.enabled() && instance.timestamps; }
    public static boolean seconds() { return instance != null && instance.seconds; }
    public void setTimestamps(boolean value) { timestamps = value; }
    public void setSeconds(boolean value) { seconds = value; }
    @Override protected void onTick(MinecraftClient client) {}
}