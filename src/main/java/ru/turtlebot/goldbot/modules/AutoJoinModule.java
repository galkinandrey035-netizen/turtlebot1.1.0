package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.SlotActionType;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoJoinModule extends GoldModule {
    private int worldNumber = 1;
    private int delayTicks;

    public AutoJoinModule() { super("AutoJoin", "Misc"); }

    public void setWorldNumber(int value) { worldNumber = Math.max(1, Math.min(74, value)); }

    @Override protected void onEnable(MinecraftClient client) { delayTicks = 0; }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null ||
                !(client.currentScreen instanceof HandledScreen<?>)) return;
        if (delayTicks-- > 0) return;
        var handler = client.player.currentScreenHandler;
        String target = "ГРИФ #" + worldNumber;
        for (var slot : handler.slots) {
            if (!slot.hasStack()) continue;
            String name = slot.getStack().getName().getString().toUpperCase(java.util.Locale.ROOT);
            if (name.contains(target) || (worldNumber == 1 && name.contains("ГРИФЕРСКОЕ ВЫЖИВАНИЕ"))) {
                client.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.PICKUP, client.player);
                delayTicks = 20;
                return;
            }
        }
        delayTicks = 10;
    }
}
