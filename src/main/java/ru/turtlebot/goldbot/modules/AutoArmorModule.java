package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ArmorItem;
import net.minecraft.screen.slot.SlotActionType;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoArmorModule extends GoldModule {
    private int cooldown;

    public AutoArmorModule() { super("AutoArmor", "Combat"); }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null || client.currentScreen != null) return;
        if (cooldown > 0) { cooldown--; return; }
        var player = client.player;
        var inventory = player.getInventory();
        for (int invIndex = 9; invIndex < 36; invIndex++) {
            var stack = inventory.getStack(invIndex);
            if (!(stack.getItem() instanceof ArmorItem armor)) continue;
            var equipmentSlot = armor.getSlotType();
            var equipped = player.getEquippedStack(equipmentSlot);
            if (!equipped.isEmpty()) continue;
            int handlerSlot = invIndex;
            client.interactionManager.clickSlot(player.playerScreenHandler.syncId,
                    handlerSlot, 0, SlotActionType.QUICK_MOVE, player);
            cooldown = 8;
            return;
        }
    }
}
