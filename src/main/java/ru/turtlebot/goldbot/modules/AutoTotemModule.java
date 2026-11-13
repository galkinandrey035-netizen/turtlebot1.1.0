package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoTotemModule extends GoldModule {
    private float healthThreshold = 4.0F;
    private int cooldown;

    public AutoTotemModule() { super("AutoTotem", "Combat"); }
    public void setHealthThreshold(float value) { healthThreshold = Math.max(1.0F, Math.min(20.0F, value)); }
    public float healthThreshold() { return healthThreshold; }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null || client.currentScreen != null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) || client.player.getHealth() > healthThreshold) return;
        int index = -1;
        for (int i = 0; i < 36; i++) if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) { index = i; break; }
        if (index < 0) return;
        int slotId = index < 9 ? index + 36 : index;
        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, slotId, 40, SlotActionType.SWAP, client.player);
        cooldown = 10;
    }
}