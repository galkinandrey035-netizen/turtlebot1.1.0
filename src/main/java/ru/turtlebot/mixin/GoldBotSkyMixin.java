package ru.turtlebot.mixin;

import ru.turtlebot.runtime.GoldBotRuntime;

import dev.goldbot.SkyFx;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public abstract class GoldBotSkyMixin {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void turtlebot$skyColor(Vec3d cameraPos, float tickDelta,
                                    CallbackInfoReturnable<Integer> cir) {
        if (GoldBotRuntime.skyActive(this)) {
            cir.setReturnValue(0xFF000000 | (SkyFx.zenith(System.currentTimeMillis()) & 0xFFFFFF));
        }
    }

    @Inject(method = "getStarBrightness", at = @At("HEAD"), cancellable = true)
    private void turtlebot$starBrightness(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (GoldBotRuntime.skyActive(this)) {
            cir.setReturnValue(SkyFx.stars());
        }
    }
}
