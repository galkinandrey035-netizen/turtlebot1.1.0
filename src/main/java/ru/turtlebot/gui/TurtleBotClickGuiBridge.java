package ru.turtlebot.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.turtlebot.TurtleBotClient;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;


/**
 * Adds TurtleBot controls to the original GoldBot ClickGUI without changing
 * any class or behavior inside dev.goldbot.*.
 */
public final class TurtleBotClickGuiBridge {
    private static final int BUTTON_WIDTH = 58;
    private static final int BUTTON_HEIGHT = 16;
    private static final int GAP = 3;
    private static final String TARGET_SCREEN = "aethereal.ClickGuiScreen";

    private TurtleBotClickGuiBridge() {}

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!TARGET_SCREEN.equals(screen.getClass().getName())) return;

            ScreenEvents.afterRender(screen).register((current, context, mouseX, mouseY, tickDelta) ->
                    render(client, current, context, mouseX, mouseY));

            ScreenMouseEvents.allowMouseClick(screen).register((current, mouseX, mouseY, button) -> {
                if (button != 0) return true;
                int[] origin = origin(current);
                int x = origin[0];
                int y = origin[1];
                int totalWidth = BUTTON_WIDTH * 4 + GAP * 3;
                if (mouseY < y || mouseY > y + BUTTON_HEIGHT || mouseX < x || mouseX > x + totalWidth) {
                    return true;
                }

                int index = (int) ((mouseX - x) / (BUTTON_WIDTH + GAP));
                if (index < 0 || index > 3) return true;
                if (mouseX > x + index * (BUTTON_WIDTH + GAP) + BUTTON_WIDTH) return true;

                handleClick(client, current, index);
                return false;
            });
        });
    }

    private static void render(MinecraftClient client, Screen screen, DrawContext context,
                               int mouseX, int mouseY) {
        int[] origin = origin(screen);
        String[] labels = {"TB Start", "TB Pause", "TB Stop", "TB Config"};
        BotController bot = TurtleBotClient.controller();

        for (int i = 0; i < labels.length; i++) {
            int x = origin[0] + i * (BUTTON_WIDTH + GAP);
            int y = origin[1];
            boolean hovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH
                    && mouseY >= y && mouseY <= y + BUTTON_HEIGHT;
            int color = switch (i) {
                case 0 -> 0xFF245C3A;
                case 1 -> 0xFF665321;
                case 2 -> 0xFF652C2C;
                default -> 0xFF343B4B;
            };
            if (hovered) color = (color & 0xFF000000) | (((color >> 16 & 0xFF) + 22) << 16)
                    | (((color >> 8 & 0xFF) + 22) << 8) | Math.min(255, (color & 0xFF) + 22);
            context.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, color);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(labels[i]),
                    x + BUTTON_WIDTH / 2, y + 4, 0xFFFFFFFF);
        }

        String state = bot == null ? "not ready"
                : bot.isPaused() ? "paused" : bot.isActive() ? "running" : "stopped";
        String chest = ConfigManager.get().chestSet ? "chest set" : "chest missing";
        context.drawText(client.textRenderer, Text.literal("TurtleBot: " + state + " | " + chest),
                origin[0], origin[1] + BUTTON_HEIGHT + 3, 0xFFB8DCC5, false);
    }

    private static int[] origin(Screen screen) {
        // GoldBot's original ClickGUI is centered and uses private, obfuscated layout fields.
        // Keep TurtleBot controls in the free top-left margin without reflecting into GoldBot internals.
        return new int[]{8, 8};
    }

    private static void handleClick(MinecraftClient client, Screen parent, int index) {
        BotController bot = TurtleBotClient.controller();
        switch (index) {
            case 0 -> {
                if (bot == null) return;
                String error = bot.start();
                if (error != null) BotLog.reply("§c" + error);
            }
            case 1 -> {
                if (bot == null) return;
                if (bot.isPaused()) bot.resume();
                else bot.pause();
            }
            case 2 -> {
                if (bot != null) bot.stop("остановлено через GoldBot ClickGUI");
            }
            case 3 -> client.setScreen(ClothScreen.build(parent));
            default -> { }
        }
    }
}
