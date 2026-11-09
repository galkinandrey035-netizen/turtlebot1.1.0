package ru.turtlebot.util;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
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
        MatrixStack matrices = context.matrixStack();
        matrices.push();
        try {
            MatrixStack.Entry entry = matrices.peek();
            BlockPos previous = null;
            for (BlockPos node : path) {
                double x = node.getX() + 0.5 - camera.x;
                double y = node.getY() + 0.08 - camera.y;
                double z = node.getZ() + 0.5 - camera.z;
                if (previous != null) {
                    double px = previous.getX() + 0.5 - camera.x;
                    double py = previous.getY() + 0.08 - camera.y;
                    double pz = previous.getZ() + 0.5 - camera.z;
                    drawLine(lines, entry, px, py, pz, x, y, z);
                }
                double r = 0.20;
                drawLine(lines, entry, x - r, y, z - r, x + r, y, z - r);
                drawLine(lines, entry, x + r, y, z - r, x + r, y, z + r);
                drawLine(lines, entry, x + r, y, z + r, x - r, y, z + r);
                drawLine(lines, entry, x - r, y, z + r, x - r, y, z - r);
                previous = node;
            }
        } finally {
            matrices.pop();
        }
    }

    private static void drawLine(VertexConsumer consumer, MatrixStack.Entry entry,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2) {
        consumer.vertex(entry.getPositionMatrix(), (float) x1, (float) y1, (float) z1)
                .color(0.15f, 0.85f, 1.0f, 0.95f)
                .normal(entry.getNormalMatrix(), 0.0f, 1.0f, 0.0f).next();
        consumer.vertex(entry.getPositionMatrix(), (float) x2, (float) y2, (float) z2)
                .color(0.15f, 0.85f, 1.0f, 0.95f)
                .normal(entry.getNormalMatrix(), 0.0f, 1.0f, 0.0f).next();
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
