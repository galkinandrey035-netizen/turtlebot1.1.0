package ru.turtlebot.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.turtlebot.state.BotController;

import java.util.List;

/** Рисует оставшийся маршрут бота линией сквозь блоки. */
public final class PathRenderer {
    public static boolean enabled = true;

    private PathRenderer() {}

    public static void register(BotController bot) {
        WorldRenderEvents.LAST.register(ctx -> render(ctx, bot));
    }

    private static void render(WorldRenderContext ctx, BotController bot) {
        if (!enabled || !bot.isActive() || bot.isPaused()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || ctx.matrixStack() == null) return;
        List<BlockPos> path = bot.route();
        if (path.isEmpty()) return;

        Vec3d cam = ctx.camera().getPos();
        Matrix4f m = ctx.matrixStack().peek().getPositionMatrix();
        Vec3d me = mc.player.getLerpedPos(ctx.tickDelta());

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bb = tess.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(3f);

        bb.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bb.vertex(m, (float) (me.x - cam.x), (float) (me.y + 0.1 - cam.y), (float) (me.z - cam.z))
                .color(0.2f, 1f, 0.4f, 0.9f).next();
        for (BlockPos n : path) {
            bb.vertex(m, (float) (n.getX() + 0.5 - cam.x), (float) (n.getY() + 0.1 - cam.y),
                    (float) (n.getZ() + 0.5 - cam.z)).color(0.2f, 1f, 0.4f, 0.9f).next();
        }
        tess.draw();

        RenderSystem.lineWidth(1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
