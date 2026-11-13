package ru.turtlebot.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.FullBrightModule;

@Mixin(GameRenderer.class)
public abstract class FullBrightMixin {
    @Inject(method = "getNightVisionStrength", at = @At("HEAD"), cancellable = true)
    private static void turtlebot$fullBright(LivingEntity entity, float tickDelta,
            CallbackInfoReturnable<Float> cir) {
        if (FullBrightModule.active()) cir.setReturnValue(1.0F);
    }
}