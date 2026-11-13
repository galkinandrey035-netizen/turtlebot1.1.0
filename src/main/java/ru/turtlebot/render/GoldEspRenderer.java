package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.ESPModule;

public final class GoldEspRenderer {
    private GoldEspRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldEspRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!ESPModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        Vec3d camera = context.camera().getPos();
        var matrices = context.matrixStack();
        var lines = context.consumers().getBuffer(RenderLayer.getLines());
        matrices.push();
        try {
            for (PlayerEntity target : client.world.getPlayers()) {
                if (target == client.player || target.isSpectator() || !target.isAlive()) continue;
                Box box = target.getBoundingBox().offset(-camera.x, -camera.y, -camera.z);
                WorldRenderer.drawBox(matrices, lines, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 0.95f, 0.25f, 0.25f, 0.9f);
            }
        } finally { matrices.pop(); }
    }
}