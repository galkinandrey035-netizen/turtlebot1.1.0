package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Hand;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoUseModule extends GoldModule {
    private boolean useInvisibility = false;
    private float eatHealthThreshold = 10.0F;
    private int cooldown;

    public AutoUseModule() { super("AutoUse", "Player"); }
    public void setUseInvisibility(boolean value) { useInvisibility = value; }
    public void setEatHealthThreshold(float value) { eatHealthThreshold = Math.max(1.0F, Math.min(20.0F, value)); }

    private static boolean hasInvisibilityEffect(PotionContentsComponent contents) {
        for (var effect : contents.getEffects()) {
            if (effect.getEffectType().equals(StatusEffects.INVISIBILITY)) return true;
        }
        return false;
    }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null || client.currentScreen != null) return;
        if (cooldown > 0) { cooldown--; return; }
        var player = client.player;
        if (useInvisibility && !player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            for (int i = 0; i < 9; i++) {
                var stack = player.getInventory().getStack(i);
                if (stack.isOf(Items.POTION)) {
                    var contents = stack.get(DataComponentTypes.POTION_CONTENTS);
                    if (contents != null && hasInvisibilityEffect(contents)) {
                        int previous = player.getInventory().selectedSlot;
                        player.getInventory().selectedSlot = i;
                        client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                        player.getInventory().selectedSlot = previous;
                        cooldown = 20;
                        return;
                    }
                }
            }
        }
        if (player.getHealth() <= eatHealthThreshold && player.getHungerManager().isNotFull()) {
            int bestSlot = -1;
            int bestFood = -1;
            for (int i = 0; i < 9; i++) {
                var stack = player.getInventory().getStack(i);
                var food = stack.get(DataComponentTypes.FOOD);
                if (food != null && food.nutrition() > bestFood) {
                    bestFood = food.nutrition();
                    bestSlot = i;
                }
            }
            if (bestSlot >= 0) {
                int previous = player.getInventory().selectedSlot;
                player.getInventory().selectedSlot = bestSlot;
                client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                player.getInventory().selectedSlot = previous;
                cooldown = 20;
            }
        }
    }
}
