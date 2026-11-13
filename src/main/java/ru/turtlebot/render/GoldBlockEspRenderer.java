package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.BlockESPModule;

public final class GoldBlockEspRenderer {
    private GoldBlockEspRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldBlockEspRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!BlockESPModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        Vec3d camera = context.camera().getPos();
        var matrices = context.matrixStack();
        var lines = context.consumers().getBuffer(RenderLayer.getLines());
        matrices.push();
        try {
            for (var pos : BlockESPModule.positions()) {
                Box box = new Box(pos).expand(0.002).offset(-camera.x, -camera.y, -camera.z);
                WorldRenderer.drawBox(matrices, lines, box, 0.25f, 0.9f, 1.0f, 0.95f);
            }
        } finally { matrices.pop(); }
    }
}