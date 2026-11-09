package ru.turtlebot.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/** Автоматически ест любую еду из инвентаря при голоде 16/20 или ниже. */
public final class AutoEat {
    private static int restoreSlot = -1;

    private AutoEat() {}

    public static boolean tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            release(mc);
            return false;
        }

        int hunger = player.getHungerManager().getFoodLevel();
        if (restoreSlot >= 0) {
            ItemStack held = player.getMainHandStack();
            if (hunger > 16 || !held.isFood() || mc.currentScreen != null) {
                release(mc);
                return false;
            }
            mc.options.useKey.setPressed(true);
            return true;
        }

        if (hunger > 16 || mc.currentScreen != null || mc.interactionManager == null) return false;

        int foodSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isFood()) {
                foodSlot = i;
                break;
            }
        }

        if (foodSlot < 0) {
            for (int i = 9; i < 36; i++) {
                if (player.getInventory().getStack(i).isFood()) {
                    foodSlot = i;
                    break;
                }
            }
            if (foodSlot >= 9) {
                int selected = player.getInventory().selectedSlot;
                mc.interactionManager.clickSlot(player.playerScreenHandler.syncId,
                        foodSlot, selected, SlotActionType.SWAP, player);
                foodSlot = selected;
            }
        }

        if (foodSlot < 0) return false;

        restoreSlot = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = foodSlot;
        mc.options.useKey.setPressed(true);
        return true;
    }

    public static void release(MinecraftClient mc) {
        mc.options.useKey.setPressed(false);
        if (mc.player != null && restoreSlot >= 0) {
            mc.player.getInventory().selectedSlot = restoreSlot;
        }
        restoreSlot = -1;
    }
}
