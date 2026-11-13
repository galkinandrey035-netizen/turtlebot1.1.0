package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.TargetESPModule;

public final class GoldTargetEspRenderer {
    private GoldTargetEspRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldTargetEspRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!TargetESPModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.crosshairTarget instanceof EntityHitResult hit)) return;
        Vec3d camera = context.camera().getPos();
        var box = hit.getEntity().getBoundingBox().offset(-camera.x, -camera.y, -camera.z);
        var lines = context.consumers().getBuffer(RenderLayer.getLines());
        context.matrixStack().push();
        try { RenderLines.drawBox(lines, context.matrixStack().peek(), box, 1.0f, 0.8f, 0.15f, 1.0f); }
        finally { context.matrixStack().pop(); }
    }
}