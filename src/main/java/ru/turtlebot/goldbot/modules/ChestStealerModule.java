package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import ru.turtlebot.goldbot.GoldModule;

/** Moves one available container item into the player's inventory per delay. */
public final class ChestStealerModule extends GoldModule {
    private long delayMillis = 100L;
    private long nextMoveAt;

    public ChestStealerModule() {
        super("ChestStealer", "Player");
    }

    public void setDelayMillis(long value) {
        delayMillis = Math.max(0L, Math.min(1000L, value));
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null ||
                !(client.currentScreen instanceof GenericContainerScreen)) return;
        long now = System.currentTimeMillis();
        if (now < nextMoveAt) return;

        for (Slot slot : client.player.currentScreenHandler.slots) {
            if (!slot.hasStack() || slot.inventory == client.player.getInventory()) continue;
            client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId,
                    slot.id, 0, SlotActionType.QUICK_MOVE, client.player);
            nextMoveAt = now + delayMillis;
            return;
        }
    }
}