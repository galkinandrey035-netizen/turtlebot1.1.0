package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import ru.turtlebot.goldbot.GoldModule;

/**
 * Eats the most nutritious food item from the hotbar when hunger is low.
 * Restores the previously selected slot after the eating action finishes.
 */
public final class AutoEatModule extends GoldModule {
    private static final int HUNGER_THRESHOLD = 16;
    private static final int RETRY_TICKS = 20;

    private int originalSlot = -1;
    private int retryCooldown;

    public AutoEatModule() {
        super("AutoEat", "Player");
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null
                || client.currentScreen != null) {
            restore(client);
            return;
        }

        if (retryCooldown > 0) retryCooldown--;

        if (originalSlot >= 0) {
            if (!client.player.isUsingItem()) restore(client);
            return;
        }

        if (retryCooldown > 0 || client.player.isUsingItem()
                || client.player.getHungerManager().getFoodLevel() > HUNGER_THRESHOLD) {
            return;
        }

        int bestSlot = -1;
        int bestNutrition = 0;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = client.player.getInventory().getStack(slot);
            FoodComponent food = stack.get(DataComponentTypes.FOOD);
            if (food != null && food.nutrition() > bestNutrition) {
                bestNutrition = food.nutrition();
                bestSlot = slot;
            }
        }

        if (bestSlot < 0) {
            retryCooldown = RETRY_TICKS;
            return;
        }

        originalSlot = client.player.getInventory().selectedSlot;
        client.player.getInventory().selectedSlot = bestSlot;
        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        if (!client.player.isUsingItem()) {
            restore(client);
            retryCooldown = RETRY_TICKS;
        }
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && originalSlot >= 0) {
            client.player.getInventory().selectedSlot = originalSlot;
        }
        originalSlot = -1;
    }

    @Override
    protected void onDisable(MinecraftClient client) {
        restore(client);
    }
}
