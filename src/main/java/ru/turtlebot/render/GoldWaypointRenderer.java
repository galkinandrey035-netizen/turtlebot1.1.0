package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.modules.WaypointModule;

public final class GoldWaypointRenderer {
    private GoldWaypointRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldWaypointRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!WaypointModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        Vec3d camera = context.camera().getPos();
        var lines = context.consumers().getBuffer(RenderLayer.getLines());
        var matrices = context.matrixStack();
        matrices.push();
        try {
            for (WaypointModule.Waypoint waypoint : WaypointModule.all().values()) {
                if (!waypoint.dimension().equals(client.world.getRegistryKey().getValue())) continue;
                Vec3d p = waypoint.position();
                Box box = new Box(p.x - 0.3, p.y, p.z - 0.3, p.x + 0.3, p.y + 1.8, p.z + 0.3)
                        .offset(-camera.x, -camera.y, -camera.z);
                WorldRenderer.drawBox(matrices, lines, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 0.3f, 1.0f, 0.5f, 0.95f);
            }
        } finally { matrices.pop(); }
    }
}