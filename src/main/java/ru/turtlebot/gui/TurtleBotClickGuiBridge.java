package ru.turtlebot.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import ru.turtlebot.TurtleBotClient;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;

import java.lang.reflect.Field;

/**
 * Adds a compact TurtleBot control panel inside GoldBot's existing ClickGUI.
 * GoldBot classes and its original mining/home logic remain untouched.
 */
public final class TurtleBotClickGuiBridge {
    private static final int PANEL_WIDTH = 211;
    private static final int PANEL_HEIGHT = 45;
    private static final int BUTTON_WIDTH = 65;
    private static final int BUTTON_HEIGHT = 17;
    private static final int BUTTON_GAP = 4;
    private static final int BUTTON_OFFSET_X = 5;
    private static final int BUTTON_OFFSET_Y = 21;
    private static final String[] TARGET_SCREENS = {
            "aethereal.ClickGuiScreen",
            "dev.goldbot.ClickGuiScreen"
    };

    private TurtleBotClickGuiBridge() {}

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isTargetScreen(screen)) return;

            ScreenEvents.afterRender(screen).register((current, context, mouseX, mouseY, tickDelta) ->
                    render(client, current, context, mouseX, mouseY));

            ScreenMouseEvents.allowMouseClick(screen).register((current, mouseX, mouseY, button) -> {
                if (button != 0) return true;
                int[] panel = origin(current);
                int relativeX = (int) mouseX - (panel[0] + BUTTON_OFFSET_X);
                int relativeY = (int) mouseY - (panel[1] + BUTTON_OFFSET_Y);
                if (relativeY < 0 || relativeY > BUTTON_HEIGHT || relativeX < 0) return true;

                int stride = BUTTON_WIDTH + BUTTON_GAP;
                int index = relativeX / stride;
                if (index < 0 || index > 2 || relativeX % stride >= BUTTON_WIDTH) return true;

                handleClick(client, index);
                return false;
            });
        });
    }

    private static void render(MinecraftClient client, Screen screen, DrawContext context,
                               int mouseX, int mouseY) {
        int[] panel = origin(screen);
        int px = panel[0];
        int py = panel[1];

        // A distinct compact card placed relative to GoldBot's actual GUI frame.
        context.fill(px, py, px + PANEL_WIDTH, py + PANEL_HEIGHT, 0xE91B1D24);
        context.fill(px, py, px + PANEL_WIDTH, py + 1, 0xFFB58A3A);
        context.fill(px, py + PANEL_HEIGHT - 1, px + PANEL_WIDTH, py + PANEL_HEIGHT, 0xFF55515A);
        context.drawText(client.textRenderer, Text.literal("TurtleBot"), px + 7, py + 6,
                0xFFFFD27A, false);

        String[] labels = {"Старт", "Пауза", "Сундук"};
        int[] colors = {0xFF245C3A, 0xFF665321, 0xFF343B4B};
        for (int i = 0; i < labels.length; i++) {
            int x = px + BUTTON_OFFSET_X + i * (BUTTON_WIDTH + BUTTON_GAP);
            int y = py + BUTTON_OFFSET_Y;
            boolean hovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH
                    && mouseY >= y && mouseY <= y + BUTTON_HEIGHT;
            int color = colors[i];
            if (hovered) {
                color = (color & 0xFF000000)
                        | (Math.min(255, ((color >> 16) & 0xFF) + 22) << 16)
                        | (Math.min(255, ((color >> 8) & 0xFF) + 22) << 8)
                        | Math.min(255, (color & 0xFF) + 22);
            }
            context.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, color);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(labels[i]),
                    x + BUTTON_WIDTH / 2, y + 5, 0xFFFFFFFF);
        }
    }

    private static boolean isTargetScreen(Screen screen) {
        String className = screen.getClass().getName();
        for (String target : TARGET_SCREENS) {
            if (target.equals(className)) return true;
        }
        return false;
    }

    private static int[] origin(Screen screen) {
        // These fields belong to the original GoldBot ClickGuiScreen. Reflection avoids
        // compile-time coupling to its private implementation and keeps the panel in-frame.
        try {
            int x = readInt(screen, "panelX");
            int y = readInt(screen, "panelY");
            int width = readInt(screen, "panelW");
            if (width >= PANEL_WIDTH + 24) {
                return new int[]{x + width - PANEL_WIDTH - 12, y + 8};
            }
            return new int[]{x + 8, y + 8};
        } catch (ReflectiveOperationException ignored) {
            return new int[]{Math.max(8, (screen.width - PANEL_WIDTH) / 2), 8};
        }
    }

    private static int readInt(Screen screen, String name) throws ReflectiveOperationException {
        Field field = screen.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(screen);
    }

    private static void handleClick(MinecraftClient client, int index) {
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
            case 2 -> setChestFromCrosshair(client);
            default -> { }
        }
    }

    private static void setChestFromCrosshair(MinecraftClient client) {
        if (client.world == null || !(client.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            BotLog.reply("§cНаведите прицел на сундук или бочку.");
            return;
        }

        var pos = hit.getBlockPos();
        var block = client.world.getBlockState(pos).getBlock();
        if (!(block instanceof ChestBlock || block instanceof BarrelBlock)) {
            BotLog.reply("§cПод прицелом нет сундука или бочки.");
            return;
        }

        BotConfig config = ConfigManager.get();
        config.chestSet = true;
        config.chestX = pos.getX();
        config.chestY = pos.getY();
        config.chestZ = pos.getZ();
        config.chestDimension = client.world.getRegistryKey().getValue().toString();
        ConfigManager.save();
        BotLog.reply("§aСундук TurtleBot задан: " + pos.toShortString());
    }
}
