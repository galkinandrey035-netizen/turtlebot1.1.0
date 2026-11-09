package ru.turtlebot.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

/** Автоматически ест еду из хотбара при голоде 16/20 или ниже. */
public final class AutoEat {
    private static int restoreSlot = -1;

    private AutoEat() {}

    public static boolean tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) {
            release(mc);
            return false;
        }

        int hunger = p.getHungerManager().getFoodLevel();
        if (restoreSlot >= 0) {
            ItemStack held = p.getMainHandStack();
            if (hunger > 16 || !held.isFood()) {
                release(mc);
                return false;
            }
            mc.options.useKey.setPressed(true);
            return true;
        }

        if (hunger > 16 || mc.currentScreen != null) return false;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (p.getInventory().getStack(i).isFood()) {
                slot = i;
                break;
            }
        }
        if (slot < 0) return false;

        restoreSlot = p.getInventory().selectedSlot;
        p.getInventory().selectedSlot = slot;
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
