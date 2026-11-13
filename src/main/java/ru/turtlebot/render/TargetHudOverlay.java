package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import ru.turtlebot.config.ConfigManager;

import java.util.Locale;

public final class TargetHudOverlay {
    private static LivingEntity target;
    private static long expiresAt;

    private TargetHudOverlay() {}

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && entity instanceof LivingEntity living) {
                target = living;
                expiresAt = System.currentTimeMillis() + 3500L;
            }
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
    }

    private static void render(DrawContext context) {
        if (!ConfigManager.get().targetHudEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return;
        if (client.crosshairTarget instanceof EntityHitResult hit
                && hit.getEntity() instanceof LivingEntity living) {
            target = living;
            expiresAt = System.currentTimeMillis() + 1500L;
        }

        LivingEntity current = target;
        if (current == null) return;
        if (!current.isAlive() || System.currentTimeMillis() > expiresAt) {
            target = null;
            return;
        }

        if (client.player == null || client.world == null || current.getWorld() != client.world) {
            target = null;
            return;
        }

        float maxHealth = Math.max(1.0F, current.getMaxHealth());
        float health = Math.max(0.0F, Math.min(maxHealth, current.getHealth()));
        int x = 10;
        int y = 78;
        int width = 174;
        int height = 48;
        int barWidth = Math.round((width - 16) * (health / maxHealth));

        context.fill(x, y, x + width, y + height, 0xD9141720);
        context.fill(x, y, x + width, y + 1, 0xFFE0B45E);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF494D58);
        context.drawText(client.textRenderer, current.getDisplayName(), x + 8, y + 6,
                0xFFFFFFFF, false);
        context.fill(x + 8, y + 27, x + width - 8, y + 33, 0xFF343945);
        if (barWidth > 0) {
            context.fill(x + 8, y + 27, x + 8 + barWidth, y + 33, 0xFFE05A5A);
        }
        String healthText = String.format(Locale.ROOT, "%.1f / %.1f HP", health, maxHealth);
        context.drawText(client.textRenderer, Text.literal(healthText), x + 8, y + 36,
                0xFFE8E8E8, false);
    }
}
