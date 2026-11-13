package ru.turtlebot.mixin;

import dev.goldbot.SkyFx;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public abstract class GoldBotSkyMixin {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void turtlebot$skyColor(Vec3d cameraPos, float tickDelta,
                                    CallbackInfoReturnable<Integer> cir) {
        if (SkyFx.active((World) (Object) this)) {
            cir.setReturnValue(0xFF000000 | (SkyFx.zenith(System.currentTimeMillis()) & 0xFFFFFF));
        }
    }

    @Inject(method = "getStarBrightness", at = @At("HEAD"), cancellable = true)
    private void turtlebot$starBrightness(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (SkyFx.active((World) (Object) this)) {
            cir.setReturnValue(SkyFx.stars());
        }
    }
}
