package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import ru.turtlebot.goldbot.GoldModule;

import java.util.HashSet;
import java.util.Set;

public final class AutoDuelModule extends GoldModule {
    private final Set<String> challenged = new HashSet<>();
    private long nextSendAt;
    private long resetAt;
    private long intervalMillis = 500L;

    public AutoDuelModule() { super("AutoDuel", "Combat"); }

    public void setIntervalMillis(long value) {
        intervalMillis = Math.max(300L, Math.min(5000L, value));
    }

    @Override protected void onEnable(MinecraftClient client) {
        challenged.clear();
        nextSendAt = 0L;
        resetAt = System.currentTimeMillis() + 60_000L;
    }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.getNetworkHandler() == null) return;
        long now = System.currentTimeMillis();
        if (now >= resetAt) {
            challenged.clear();
            resetAt = now + 60_000L;
        }
        if (now < nextSendAt) return;
        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            if (entry == null || entry.getProfile() == null) continue;
            String name = entry.getProfile().getName();
            if (name == null || name.equalsIgnoreCase(client.player.getGameProfile().getName())
                    || !name.matches("[A-Za-z0-9_]{3,16}") || !challenged.add(name)) continue;
            client.player.networkHandler.sendChatCommand("duel " + name);
            nextSendAt = now + intervalMillis;
            return;
        }
        nextSendAt = now + 1000L;
    }

    @Override protected void onDisable(MinecraftClient client) {
        challenged.clear();
    }
}
