package ru.turtlebot.mixin;

import dev.goldbot.Config;
import dev.goldbot.GoldBotMod;
import dev.goldbot.SkyFx;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.Properties.class)
public abstract class GoldBotAmbienceMixin {
    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void turtlebot$ambienceTime(CallbackInfoReturnable<Long> cir) {
        Config config = GoldBotMod.cfg;
        if (config != null && config.ambience) {
            cir.setReturnValue((long) config.ambienceTime);
            return;
        }

        long skyTime = SkyFx.time();
        if (skyTime >= 0L) cir.setReturnValue(skyTime);
    }
}
