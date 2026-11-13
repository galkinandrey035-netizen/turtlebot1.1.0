package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.state.BotController;

import java.util.List;

/** Рисует оставшийся маршрут бота линией. */
public final class PathRenderer {
    public static boolean enabled = true;

    private PathRenderer() {}

    public static void register(BotController bot) {
        WorldRenderEvents.LAST.register(ctx -> render(ctx, bot));
    }

    private static void render(WorldRenderContext ctx, BotController bot) {
        if (!enabled || !bot.isActive() || bot.isPaused()
                || ctx.matrixStack() == null || ctx.consumers() == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        List<BlockPos> path = bot.route();
        if (path.isEmpty()) return;

        Vec3d camera = ctx.camera().getPos();
        Vec3d player = mc.player.getPos();
        var matrices = ctx.matrixStack();
        matrices.push();
        try {
            var entry = matrices.peek();
            VertexConsumer lines = ctx.consumers().getBuffer(RenderLayer.getLines());
            double px = player.x - camera.x;
            double py = player.y + 0.1 - camera.y;
            double pz = player.z - camera.z;
            for (BlockPos node : path) {
                double x = node.getX() + 0.5 - camera.x;
                double y = node.getY() + 0.1 - camera.y;
                double z = node.getZ() + 0.5 - camera.z;
                line(lines, entry, px, py, pz, x, y, z);
                px = x; py = y; pz = z;
            }
        } finally {
            matrices.pop();
        }
    }

    private static void line(VertexConsumer out, net.minecraft.client.util.math.MatrixStack.Entry entry,
                             double x1, double y1, double z1, double x2, double y2, double z2) {
        float dx = (float) (x2 - x1);
        float dy = (float) (y2 - y1);
        float dz = (float) (z2 - z1);
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-5f) return;
        float nx = -dy / length;
        float ny = dx / length;
        float nz = 0.0f;
        out.vertex(entry, (float) x1, (float) y1, (float) z1).color(0.2f, 1f, 0.4f, 0.9f)
                .normal(entry, nx, ny, nz);
        out.vertex(entry, (float) x2, (float) y2, (float) z2).color(0.2f, 1f, 0.4f, 0.9f)
                .normal(entry, nx, ny, nz);
    }
}
