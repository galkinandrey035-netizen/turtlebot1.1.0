package ru.turtlebot.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModuleManager;
import ru.turtlebot.goldbot.GoldModule;
import ru.turtlebot.goldbot.modules.InterfaceModule;
import ru.turtlebot.goldbot.modules.InterfaceHudModule;
import ru.turtlebot.goldbot.modules.NewHUDModule;

public final class GoldInterfaceHudRenderer {
    private GoldInterfaceHudRenderer() {}
    public static void register() { HudRenderCallback.EVENT.register((context, tickCounter) -> {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        int y = 8;
        if (InterfaceModule.active()) {
            context.drawText(client.textRenderer, "GoldBot", 8, y, 0xFF55FFFF, true);
            y += 12;
            for (GoldModule module : GoldModuleManager.all()) {
                if (!module.enabled()) continue;
                context.drawText(client.textRenderer, module.name(), 8, y, 0xFFFFFFFF, true);
                y += 10;
            }
        }
        if (InterfaceHudModule.active()) {
            String coords = String.format("XYZ %.1f / %.1f / %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ());
            context.drawText(client.textRenderer, coords, 8, context.getScaledWindowHeight() - 26, 0xFFFFFFFF, true);
            context.drawText(client.textRenderer, "FPS " + client.getCurrentFps(),
                    8, context.getScaledWindowHeight() - 14, 0xFF55FFFF, true);
        }
        if (NewHUDModule.active()) {
            String status = String.format("HP %.1f | Food %d | Armor %d",
                    client.player.getHealth(), client.player.getHungerManager().getFoodLevel(), client.player.getArmor());
            int x = context.getScaledWindowWidth() - client.textRenderer.getWidth(status) - 8;
            context.drawText(client.textRenderer, status, x, 8, 0xFFFFFFFF, true);
        }
    }); }
}