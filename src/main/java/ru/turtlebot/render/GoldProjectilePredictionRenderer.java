package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.turtlebot.goldbot.modules.ProjectilePredictionModule;

public final class GoldProjectilePredictionRenderer {
    private GoldProjectilePredictionRenderer() {}
    public static void register() { WorldRenderEvents.LAST.register(GoldProjectilePredictionRenderer::render); }
    private static void render(WorldRenderContext context) {
        if (!ProjectilePredictionModule.active() || context.matrixStack() == null || context.consumers() == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ItemStack held = client.player.getMainHandStack();
        if (!(held.isOf(Items.ENDER_PEARL) || held.isOf(Items.SNOWBALL) || held.isOf(Items.EGG) ||
                held.isOf(Items.EXPERIENCE_BOTTLE) || held.isOf(Items.SPLASH_POTION) ||
                held.isOf(Items.LINGERING_POTION) || held.isOf(Items.TRIDENT))) return;
        double speed = held.isOf(Items.TRIDENT) ? 2.5 : 1.5;
        double gravity = held.isOf(Items.TRIDENT) ? 0.05 : 0.03;
        Vec3d position = client.player.getEyePos();
        Vec3d velocity = client.player.getRotationVec(1.0f).normalize().multiply(speed);
        Vec3d camera = context.camera().getPos();
        VertexConsumer out = context.consumers().getBuffer(RenderLayer.getLines());
        var matrices = context.matrixStack();
        matrices.push();
        try {
            var entry = matrices.peek();
            for (int i = 0; i < 100; i++) {
                Vec3d next = position.add(velocity);
                BlockHitResult hit = client.world.raycast(new RaycastContext(position, next,
                        RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, client.player));
                if (hit.getType() != HitResult.Type.MISS) next = hit.getPos();
                Vec3d a = position.subtract(camera), b = next.subtract(camera);
                out.vertex(entry, (float)a.x, (float)a.y, (float)a.z).color(0.25f, 1.0f, 0.4f, 0.95f).normal(entry, 0, 1, 0);
                out.vertex(entry, (float)b.x, (float)b.y, (float)b.z).color(0.25f, 1.0f, 0.4f, 0.95f).normal(entry, 0, 1, 0);
                if (hit.getType() != HitResult.Type.MISS) break;
                position = next;
                velocity = velocity.multiply(0.99).add(0.0, -gravity, 0.0);
            }
        } finally { matrices.pop(); }
    }
}