package ru.turtlebot.action;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/** Ест безопасную еду из хотбара при голоде 16/20 или ниже. Не использует useKey. */
public final class AutoEat {
    private static final int THRESHOLD = 16;
    private static int restoreSlot = -1;
    private static int idleTicks;
    private static int cooldown;

    private AutoEat() {}

    public static boolean isSafeFood(ItemStack s) {
        if (s.isEmpty()) return false;
        FoodComponent f = s.getItem().getFoodComponent();
        if (f == null) return false;
        if (s.isOf(Items.CHORUS_FRUIT) || s.isOf(Items.SUSPICIOUS_STEW)
                || s.isOf(Items.GOLDEN_APPLE) || s.isOf(Items.ENCHANTED_GOLDEN_APPLE)) return false;
        for (Pair<StatusEffectInstance, Float> e : f.getStatusEffects()) {
            if (e.getFirst().getEffectType().getCategory() == StatusEffectCategory.HARMFUL) return false;
        }
        return true;
    }

    public static boolean tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.interactionManager == null) { release(mc); return false; }
        if (cooldown > 0) cooldown--;
        int hunger = p.getHungerManager().getFoodLevel();

        if (restoreSlot >= 0) {
            if (p.isUsingItem()) { idleTicks = 0; return true; }
            if (hunger > THRESHOLD || !isSafeFood(p.getMainHandStack())) { release(mc); return false; }
            if (++idleTicks > 20) { release(mc); cooldown = 200; return false; }
            mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
            return true;
        }

        if (cooldown > 0 || hunger > THRESHOLD || mc.currentScreen != null) return false;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (isSafeFood(p.getInventory().getStack(i))) { slot = i; break; }
        }
        if (slot < 0) return false;

        restoreSlot = p.getInventory().selectedSlot;
        p.getInventory().selectedSlot = slot;
        idleTicks = 0;
        mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
        return true;
    }

    public static void release(MinecraftClient mc) {
        if (mc.player != null) {
            if (mc.player.isUsingItem() && mc.interactionManager != null) {
                mc.interactionManager.stopUsingItem(mc.player);
            }
            if (restoreSlot >= 0) mc.player.getInventory().selectedSlot = restoreSlot;
        }
        restoreSlot = -1;
        idleTicks = 0;
    }
}
