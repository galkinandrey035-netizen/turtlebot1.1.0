package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Bypasses block occlusion when targeting player entities. */
public final class NoTraceModule extends GoldModule {
    private static NoTraceModule instance;
    private boolean players = true;

    public NoTraceModule() { super("NoTrace", "Player"); instance = this; }
    public static boolean playersEnabled() {
        return instance != null && instance.enabled() && instance.players;
    }
    public void setPlayers(boolean value) { players = value; }
    @Override protected void onTick(MinecraftClient client) {}
}