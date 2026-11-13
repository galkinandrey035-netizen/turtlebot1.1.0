package ru.turtlebot.mixin;

import dev.goldbot.ViewModel;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class GoldBotHeldItemMixin {
    @Unique
    private boolean turtlebot$viewModelPushed;

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"))
    private void turtlebot$applyViewModel(float tickDelta, MatrixStack matrices,
                                          VertexConsumerProvider.Immediate vertexConsumers,
                                          ClientPlayerEntity player, int light, CallbackInfo ci) {
        turtlebot$viewModelPushed = false;
        if (!ViewModel.active()) return;
        matrices.push();
        GoldBotRuntime.viewModelApply(matrices);
        turtlebot$viewModelPushed = true;
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("RETURN"))
    private void turtlebot$restoreViewModel(float tickDelta, MatrixStack matrices,
                                            VertexConsumerProvider.Immediate vertexConsumers,
                                            ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (!turtlebot$viewModelPushed) return;
        matrices.pop();
        turtlebot$viewModelPushed = false;
    }
}
