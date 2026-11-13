package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.SwordItem;
import ru.turtlebot.goldbot.GoldModule;

/** Ignores player entities or cobweb outlines during targeting. */
public final class NoTraceModule extends GoldModule {
    private static NoTraceModule instance;
    private boolean players;
    private boolean web = true;
    private boolean disableWithSword = true;

    public NoTraceModule() { super("NoTrace", "Player"); instance = this; }
    private boolean allowed() {
        MinecraftClient client = MinecraftClient.getInstance();
        return !disableWithSword || client.player == null ||
                !(client.player.getMainHandStack().getItem() instanceof SwordItem);
    }
    public static boolean playersEnabled() {
        return instance != null && instance.enabled() && instance.players && instance.allowed();
    }
    public static boolean webEnabled() {
        return instance != null && instance.enabled() && instance.web && instance.allowed();
    }
    public void setPlayers(boolean value) { players = value; }
    public void setWeb(boolean value) { web = value; }
    public void setDisableWithSword(boolean value) { disableWithSword = value; }
    @Override protected void onTick(MinecraftClient client) {}
}