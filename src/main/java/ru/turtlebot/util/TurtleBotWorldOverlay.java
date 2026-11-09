package ru.turtlebot.util;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.TurtleBotClient;
import ru.turtlebot.data.TurtleRegistry;
import ru.turtlebot.state.BotController;

import java.util.List;
import java.util.Locale;

/**
 * Рисует текущий маршрут бота и таймер повторного кормления над ранее
 * накормленными черепахами. Только клиентская визуализация.
 */
public final class TurtleBotWorldOverlay {
    private TurtleBotWorldOverlay() {}

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(TurtleBotWorldOverlay::render);
    }

    private static void render(WorldRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || context.matrixStack() == null
                || context.consumers() == null) return;

        Vec3d camera = context.camera().getPos();
        BotController controller = TurtleBotClient.controller();
        if (controller != null && controller.isActive()) {
            drawPath(context, controller.currentPath(), camera);
        }
        drawCooldownLabels(context, camera);
    }

    private static void drawPath(WorldRenderContext context, List<BlockPos> path, Vec3d camera) {
        if (path == null || path.isEmpty()) return;
        VertexConsumer lines = context.consumers().getBuffer(RenderLayer.getLines());
        var matrices = context.matrixStack();
        matrices.push();
        try {
            for (BlockPos node : path) {
                double x = node.getX() - camera.x;
                double y = node.getY() - camera.y;
                double z = node.getZ() - camera.z;
                Box marker = new Box(x + 0.18, y + 0.025, z + 0.18,
                        x + 0.82, y + 0.11, z + 0.82);
                WorldRenderer.drawBox(matrices, lines, marker, 0.15f, 0.85f, 1.0f, 0.95f);
            }
        } finally {
            matrices.pop();
        }
    }

    private static void drawCooldownLabels(WorldRenderContext context, Vec3d camera) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TurtleRegistry registry = TurtleBotClient.registry();
        if (registry == null) return;

        List<TurtleEntity> turtles = mc.world.getEntitiesByClass(TurtleEntity.class,
                mc.player.getBoundingBox().expand(48.0),
                turtle -> turtle.isAlive() && registry.wasFed(turtle.getUuid()));
        var matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();

        for (TurtleEntity turtle : turtles) {
            long remaining = Math.max(0L, registry.eligibleAt(turtle.getUuid()) - System.currentTimeMillis());
            String label = remaining == 0L ? "Можно кормить" : "До кормления: " + formatTime(remaining);
            int color = remaining == 0L ? 0xFF55FF55 : 0xFFFFD966;

            Vec3d pos = turtle.getPos().add(0.0, turtle.getHeight() + 0.35, 0.0).subtract(camera);
            matrices.push();
            try {
                matrices.translate(pos.x, pos.y, pos.z);
                matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
                matrices.scale(-0.025f, -0.025f, 0.025f);
                float x = -mc.textRenderer.getWidth(label) / 2.0f;
                mc.textRenderer.draw(label, x, 0.0f, color, true,
                        matrices.peek().getPositionMatrix(), consumers,
                        net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH,
                        0x70000000, 0xF000F0);
            } finally {
                matrices.pop();
            }
        }
    }

    private static String formatTime(long millis) {
        long seconds = (millis + 999L) / 1000L;
        return String.format(Locale.ROOT, "%02d:%02d", seconds / 60L, seconds % 60L);
    }
}
