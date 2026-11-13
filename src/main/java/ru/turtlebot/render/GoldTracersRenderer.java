package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.TracersModule;

public final class GoldTracersRenderer {
    private GoldTracersRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldTracersRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!TracersModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        Vec3d camera = context.camera().getPos();
        VertexConsumer out = context.consumers().getBuffer(RenderLayer.getLines());
        var matrices = context.matrixStack();
        matrices.push();
        try {
            var entry = matrices.peek();
            for (PlayerEntity target : client.world.getPlayers()) {
                if (target == client.player || target.isSpectator() || !target.isAlive()) continue;
                Vec3d point = target.getPos().add(0.0, target.getHeight() * 0.55, 0.0).subtract(camera);
                Vec3d start = Vec3d.ZERO;
                float dx = (float) (point.x - start.x);
                float dy = (float) (point.y - start.y);
                float dz = (float) (point.z - start.z);
                float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (length < 1.0E-5f) continue;
                out.vertex(entry, 0, 0, 0).color(0.2f, 0.8f, 1.0f, 0.9f)
                        .normal(entry, -dy / length, dx / length, 0.0f);
                out.vertex(entry, (float) point.x, (float) point.y, (float) point.z)
                        .color(0.2f, 0.8f, 1.0f, 0.9f)
                        .normal(entry, -dy / length, dx / length, 0.0f);
            }
        } finally { matrices.pop(); }
    }
}