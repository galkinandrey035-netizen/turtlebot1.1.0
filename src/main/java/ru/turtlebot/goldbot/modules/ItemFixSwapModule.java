package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.ClientConnection;
import ru.turtlebot.goldbot.GoldModule;

/** Keeps the server's selected hotbar slot synchronized only when an action is sent. */
public final class ItemFixSwapModule extends GoldModule {
    private static ItemFixSwapModule instance;
    private int serverSlot = -1;
    private static boolean syncing;

    public ItemFixSwapModule() { super("ItemFixSwap", "Player"); instance = this; }

    @Override protected void onEnable(MinecraftClient client) {
        serverSlot = client.player == null ? -1 : client.player.getInventory().getSelectedSlot();
    }

    @Override protected void onDisable(MinecraftClient client) {
        if (client.player != null && client.getNetworkHandler() != null) {
            int slot = client.player.getInventory().getSelectedSlot();
            client.getNetworkHandler().getConnection().send(new UpdateSelectedSlotC2SPacket(slot));
            serverSlot = slot;
        }
    }

    public static boolean cancelSlotPacket(Packet<?> packet) {
        return instance != null && instance.enabled() && !syncing &&
                packet instanceof UpdateSelectedSlotC2SPacket selected &&
                selected.getSelectedSlot() != instance.serverSlot;
    }

    public static void beforeAction(ClientConnection connection, Packet<?> packet) {
        if (instance == null || !instance.enabled() || syncing || !isAction(packet)) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        int selected = client.player.getInventory().getSelectedSlot();
        if (selected == instance.serverSlot) return;
        syncing = true;
        try {
            connection.send(new UpdateSelectedSlotC2SPacket(selected));
            instance.serverSlot = selected;
        } finally {
            syncing = false;
        }
    }

    private static boolean isAction(Packet<?> packet) {
        return packet instanceof PlayerInteractItemC2SPacket ||
                packet instanceof PlayerInteractBlockC2SPacket ||
                packet instanceof PlayerInteractEntityC2SPacket ||
                packet instanceof PlayerActionC2SPacket;
    }

    @Override protected void onTick(MinecraftClient client) {}
}