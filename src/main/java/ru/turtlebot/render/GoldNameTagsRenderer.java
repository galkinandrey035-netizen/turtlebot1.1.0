package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.NameTagsModule;

public final class GoldNameTagsRenderer {
    private GoldNameTagsRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldNameTagsRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!NameTagsModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        Vec3d camera = context.camera().getPos();
        var matrices = context.matrixStack();
        var consumers = context.consumers();
        for (PlayerEntity target : client.world.getPlayers()) {
            if (target == client.player || target.isSpectator() || !target.isAlive()) continue;
            Vec3d position = target.getPos().add(0.0, target.getHeight() + 0.35, 0.0).subtract(camera);
            String name = target.getGameProfile().getName();
            matrices.push();
            try {
                matrices.translate(position.x, position.y, position.z);
                matrices.multiply(client.getEntityRenderDispatcher().getRotation());
                matrices.scale(-0.025f, -0.025f, 0.025f);
                float x = -client.textRenderer.getWidth(name) / 2.0f;
                client.textRenderer.draw(Text.literal(name), x, 0.0f, 0xFFFFFFFF, true,
                        matrices.peek().getPositionMatrix(), consumers,
                        net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH,
                        0x55000000, 0xF000F0);
            } finally { matrices.pop(); }
        }
    }
}