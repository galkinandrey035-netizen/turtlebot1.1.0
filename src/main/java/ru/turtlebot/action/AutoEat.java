package ru.turtlebot.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.util.Hand;

/** Ест безопасную еду из хотбара при голоде 16/20 или ниже. Не использует useKey. */
public final class AutoEat {
    private static final int THRESHOLD = 16;
    private static int restoreSlot = -1;
    private static int idleTicks;
    private static int cooldown;

    private AutoEat() {}

    public static boolean isSafeFood(ItemStack stack) {
        if (stack.isEmpty() || !stack.contains(DataComponentTypes.FOOD)) return false;

        // Не используем еду с известными побочными эффектами или телепортацией.
        if (stack.isOf(Items.CHORUS_FRUIT) || stack.isOf(Items.SUSPICIOUS_STEW)
                || stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            return false;
        }

        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable != null) {
            for (var effect : consumable.onConsumeEffects()) {
                if (effect instanceof ApplyEffectsConsumeEffect applyEffects) {
                    for (StatusEffectInstance statusEffect : applyEffects.effects()) {
                        if (statusEffect.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) {
            release(mc);
            return false;
        }
        if (cooldown > 0) cooldown--;
        int hunger = player.getHungerManager().getFoodLevel();

        if (restoreSlot >= 0) {
            if (player.isUsingItem()) {
                idleTicks = 0;
                return true;
            }
            if (hunger > THRESHOLD || !isSafeFood(player.getMainHandStack())) {
                release(mc);
                return false;
            }
            if (++idleTicks > 20) {
                release(mc);
                cooldown = 200;
                return false;
            }
            mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
            return true;
        }

        if (cooldown > 0 || hunger > THRESHOLD || mc.currentScreen != null) return false;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (isSafeFood(player.getInventory().getStack(i))) {
                slot = i;
                break;
            }
        }
        if (slot < 0) return false;

        restoreSlot = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = slot;
        idleTicks = 0;
        mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
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
