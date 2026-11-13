package ru.turtlebot.mixin;

import dev.goldbot.ItemPhysics;
import dev.goldbot.ItemPhysState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class GoldBotItemPhysicsMixin {
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void turtlebot$fillPhysicsState(ItemEntity entity, ItemEntityRenderState state,
                                            float tickDelta, CallbackInfo ci) {
        if (!(state instanceof ItemPhysState physicsState)) return;
        boolean flat = !(entity.getStack().getItem() instanceof BlockItem);
        physicsState.goldbot$set(entity.isOnGround(), flat, entity.isTouchingWater(),
                entity.getId(), entity.age + tickDelta);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void turtlebot$beginPhysics(ItemEntityRenderState state, MatrixStack matrices,
                                        VertexConsumerProvider vertexConsumers, int light,
                                        CallbackInfo ci) {
        ItemPhysics.cur = state instanceof ItemPhysState physicsState ? physicsState : null;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void turtlebot$endPhysics(ItemEntityRenderState state, MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers, int light,
                                      CallbackInfo ci) {
        ItemPhysics.cur = null;
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
    private float turtlebot$physicsBob(float value) {
        return ItemPhysics.bob(value);
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void turtlebot$physicsTranslate(MatrixStack matrices, float x, float y, float z) {
        ItemPhysics.translate(matrices, x, y, z);
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
    private void turtlebot$physicsTranslateD(MatrixStack matrices, double x, double y, double z) {
        ItemPhysics.translate(matrices, (float) x, (float) y, (float) z);
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    private void turtlebot$physicsRotate(MatrixStack matrices, Quaternionf rotation) {
        ItemPhysics.rotate(matrices, rotation);
    }
}
