package ru.turtlebot.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.NoTraceModule;

@Mixin(GameRenderer.class)
public abstract class NoTraceMixin {
    @Inject(method = "findCrosshairTarget", at = @At("HEAD"), cancellable = true)
    private void turtlebot$ignorePlayerOcclusion(Entity camera, double blockRange,
            double entityRange, float tickDelta, CallbackInfoReturnable<HitResult> cir) {
        if (!NoTraceModule.playersEnabled() || entityRange <= 0.0) return;
        double range = Math.max(blockRange, entityRange);
        Vec3d start = camera.getCameraPosVec(tickDelta);
        Vec3d direction = camera.getRotationVec(tickDelta);
        Vec3d end = start.add(direction.multiply(range));
        Box searchBox = camera.getBoundingBox().stretch(direction.multiply(range)).expand(1.0);
        EntityHitResult hit = ProjectileUtil.raycast(camera, start, end, searchBox,
                entity -> entity instanceof PlayerEntity && entity != camera &&
                        entity.isAlive() && entity.canHit(), entityRange * entityRange);
        if (hit != null) cir.setReturnValue(hit);
    }
}