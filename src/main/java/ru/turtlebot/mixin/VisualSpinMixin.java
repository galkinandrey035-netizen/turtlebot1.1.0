package ru.turtlebot.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.VisualSpinModule;

@Mixin(Camera.class)
public abstract class VisualSpinMixin {
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void turtlebot$visualSpin(BlockView area, Entity focusedEntity, boolean thirdPerson,
            boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!VisualSpinModule.active() || (VisualSpinModule.onlyThirdPerson() && !thirdPerson)) return;
        Camera camera = (Camera) (Object) this;
        setRotation(camera.getYaw() + VisualSpinModule.yawOffset(),
                camera.getPitch() + VisualSpinModule.pitchOffset());
    }
}