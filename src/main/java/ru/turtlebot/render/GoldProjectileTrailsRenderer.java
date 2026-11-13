package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.ProjectileTrailsModule;

import java.util.ArrayDeque;

public final class GoldProjectileTrailsRenderer {
    private GoldProjectileTrailsRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldProjectileTrailsRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!ProjectileTrailsModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        Vec3d camera = context.camera().getPos();
        VertexConsumer out = context.consumers().getBuffer(RenderLayer.getLines());
        var matrices = context.matrixStack();
        matrices.push();
        try {
            var entry = matrices.peek();
            for (ArrayDeque<Vec3d> points : ProjectileTrailsModule.trails().values()) {
                Vec3d previous = null;
                for (Vec3d point : points) {
                    if (previous != null) {
                        Vec3d a = previous.subtract(camera), b = point.subtract(camera);
                        out.vertex(entry, (float)a.x, (float)a.y, (float)a.z).color(1f, 0.55f, 0.1f, 0.9f).normal(entry, 0, 1, 0);
                        out.vertex(entry, (float)b.x, (float)b.y, (float)b.z).color(1f, 0.55f, 0.1f, 0.9f).normal(entry, 0, 1, 0);
                    }
                    previous = point;
                }
            }
        } finally { matrices.pop(); }
    }
}