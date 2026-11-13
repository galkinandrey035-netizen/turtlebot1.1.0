package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import ru.turtlebot.goldbot.FriendRegistry;
import ru.turtlebot.goldbot.GoldModule;

public final class TriggerBotModule extends GoldModule {
    private float range = 3.5F;

    public TriggerBotModule() { super("TriggerBot", "Combat"); }

    public void setRange(float value) {
        range = Math.max(1.0F, Math.min(6.0F, value));
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null ||
                client.currentScreen != null || !(client.crosshairTarget instanceof EntityHitResult hit)) return;

        if (!(hit.getEntity() instanceof LivingEntity target) || target == client.player ||
                !target.isAlive() || client.player.squaredDistanceTo(target) > range * range ||
                FriendRegistry.isFriend(target.getName().getString()) ||
                client.player.getAttackCooldownProgress(0.5F) < 1.0F) return;

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
    }
}
