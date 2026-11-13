package ru.turtlebot.goldbot.modules;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import ru.turtlebot.goldbot.GoldModule;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;

public final class AutoTpAcceptModule extends GoldModule {
    private static final String[] MARKERS = {"has requested teleport", "просит телепортироваться", "хочет телепортироваться к вам", "хочет телепортироваться к Вам"};
    private final Set<String> trustedNames = ConcurrentHashMap.newKeySet();
    private boolean friendsOnly;
    private volatile boolean pending;

    public AutoTpAcceptModule() {
        super("AutoTpAccept", "Misc");
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> receive(message));
        ClientReceiveMessageEvents.CHAT.register((message, signed, sender, params, timestamp) -> receive(message));
    }
    public void setFriendsOnly(boolean value) { friendsOnly = value; }
    public void addTrustedName(String name) { if (name != null && !name.isBlank()) trustedNames.add(name.toLowerCase(Locale.ROOT)); }
    public void removeTrustedName(String name) { if (name != null) trustedNames.remove(name.toLowerCase(Locale.ROOT)); }

    private void receive(Text text) {
        if (!enabled() || text == null) return;
        String raw = text.getString();
        String lower = raw.toLowerCase(Locale.ROOT);
        boolean match = false;
        for (String marker : MARKERS) if (lower.contains(marker.toLowerCase(Locale.ROOT))) { match = true; break; }
        if (!match) return;
        if (friendsOnly) {
            String sender = raw.split("\\s+", 2)[0].replaceAll("[^A-Za-z0-9_]", "").toLowerCase(Locale.ROOT);
            if (!trustedNames.contains(sender)) return;
        }
        pending = true;
    }

    @Override protected void onTick(MinecraftClient client) {
        if (pending && client.player != null && client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendChatCommand("tpaccept");
            pending = false;
        }
    }
    @Override protected void onDisable(MinecraftClient client) { pending = false; }
}