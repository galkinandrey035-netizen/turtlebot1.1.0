package ru.turtlebot.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.FreeCamModule;

@Mixin(Camera.class)
public abstract class FreeCamMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void turtlebot$detachCamera(BlockView area, Entity focusedEntity, boolean thirdPerson,
            boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!FreeCamModule.active()) return;
        Vec3d pos = FreeCamModule.cameraPosition();
        if (pos == null) return;
        setPos(pos.x, pos.y, pos.z);
        setRotation(FreeCamModule.cameraYaw(), FreeCamModule.cameraPitch());
    }
}