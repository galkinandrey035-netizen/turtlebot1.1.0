package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.ArrowsModule;

public final class GoldArrowsRenderer {
    private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};
    private GoldArrowsRenderer() {}
    public static void register() { HudRenderCallback.EVENT.register(GoldArrowsRenderer::render); }
    private static void render(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        if (!ArrowsModule.active()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        int cx = context.getScaledWindowWidth() / 2;
        int cy = context.getScaledWindowHeight() / 2;
        for (PlayerEntity target : client.world.getPlayers()) {
            if (target == client.player || target.isSpectator() || !target.isAlive()) continue;
            Vec3d delta = target.getPos().subtract(client.player.getPos());
            double angle = Math.atan2(delta.z, delta.x);
            float degrees = MathHelper.wrapDegrees((float) (Math.toDegrees(angle) - client.player.getYaw() + 90.0));
            int sector = Math.floorMod(Math.round(degrees / 45.0f), 8);
            int radius = 78;
            double screenAngle = Math.toRadians(degrees - 90.0f);
            int x = cx + (int) (Math.cos(screenAngle) * radius);
            int y = cy + (int) (Math.sin(screenAngle) * radius);
            String arrow = ARROWS[sector];
            context.drawText(client.textRenderer, arrow, x - client.textRenderer.getWidth(arrow) / 2,
                    y - 5, 0xFFFF5555, true);
        }
    }
}