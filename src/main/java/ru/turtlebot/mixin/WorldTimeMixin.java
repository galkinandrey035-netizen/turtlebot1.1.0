package ru.turtlebot.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.WorldTimeModule;
import ru.turtlebot.goldbot.GoldModuleManager;

@Mixin(World.class)
public abstract class WorldTimeMixin {
    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void turtlebot$visualTime(CallbackInfoReturnable<Long> cir) {
        if ((Object) this instanceof ClientWorld && GoldModuleManager.get("WorldTime") != null
                && GoldModuleManager.get("WorldTime").enabled()) {
            cir.setReturnValue(WorldTimeModule.visualTimeOfDay());
        }
    }
}