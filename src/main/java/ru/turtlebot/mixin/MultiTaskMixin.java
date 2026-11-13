package ru.turtlebot.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.turtlebot.goldbot.modules.MultiTaskModule;

@Mixin(MinecraftClient.class)
public abstract class MultiTaskMixin {
    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean turtlebot$allowMiningWhileUsingItem(ClientPlayerEntity player) {
        return !MultiTaskModule.enabledNow() && player.isUsingItem();
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    private boolean turtlebot$allowUseWhileMining(ClientPlayerInteractionManager manager) {
        return !MultiTaskModule.enabledNow() && manager.isBreakingBlock();
    }
}