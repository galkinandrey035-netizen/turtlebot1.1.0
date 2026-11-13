package ru.turtlebot.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.BetterF5Module;

@Mixin(Camera.class)
public abstract class BetterF5Mixin {
    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void turtlebot$disableCameraCollision(float desiredDistance, CallbackInfoReturnable<Float> cir) {
        if (BetterF5Module.noCameraCollision()) cir.setReturnValue(desiredDistance);
    }
}