package ru.turtlebot.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.text.Text;
import ru.turtlebot.TurtleBotClient;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;

/**
 * Adds a compact TurtleBot control panel to GoldBot's existing ClickGUI.
 * GoldBot classes and its original mining/home logic remain untouched.
 */
public final class TurtleBotClickGuiBridge {
    private static final int PANEL_X = 8;
    private static final int PANEL_Y = 8;
    private static final int PANEL_WIDTH = 211;
    private static final int PANEL_HEIGHT = 45;
    private static final int BUTTON_WIDTH = 65;
    private static final int BUTTON_HEIGHT = 17;
    private static final int BUTTON_GAP = 4;
    private static final int BUTTON_Y = PANEL_Y + 21;
    private static final String[] TARGET_SCREENS = {
            "aethereal.ClickGuiScreen",
            "dev.goldbot.ClickGuiScreen"
    };

    private TurtleBotClickGuiBridge() {}

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isTargetScreen(screen)) return;

            ScreenEvents.afterRender(screen).register((current, context, mouseX, mouseY, tickDelta) ->
                    render(client, context, mouseX, mouseY));

            ScreenMouseEvents.allowMouseClick(screen).register((current, mouseX, mouseY, button) -> {
                if (button != 0 || mouseY < BUTTON_Y || mouseY > BUTTON_Y + BUTTON_HEIGHT) return true;
                int relativeX = (int) mouseX - (PANEL_X + 5);
                if (relativeX < 0) return true;

                int stride = BUTTON_WIDTH + BUTTON_GAP;
                int index = relativeX / stride;
                if (index < 0 || index > 2 || relativeX % stride >= BUTTON_WIDTH) return true;

                handleClick(client, index);
                return false;
            });
        });
    }

    private static void render(MinecraftClient client, DrawContext context, int mouseX, int mouseY) {
        // A distinct compact panel; the original GoldBot ClickGUI layout is not redesigned.
        context.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEIGHT, 0xE91B1D24);
        context.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + 1, 0xFFB58A3A);
        context.fill(PANEL_X, PANEL_Y + PANEL_HEIGHT - 1,
                PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEIGHT, 0xFF55515A);
        context.drawText(client.textRenderer, Text.literal("TurtleBot"), PANEL_X + 7, PANEL_Y + 6,
                0xFFFFD27A, false);

        String[] labels = {"Старт", "Пауза", "Сундук"};
        int[] colors = {0xFF245C3A, 0xFF665321, 0xFF343B4B};
        for (int i = 0; i < labels.length; i++) {
            int x = PANEL_X + 5 + i * (BUTTON_WIDTH + BUTTON_GAP);
            boolean hovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH
                    && mouseY >= BUTTON_Y && mouseY <= BUTTON_Y + BUTTON_HEIGHT;
            int color = colors[i];
            if (hovered) {
                color = (color & 0xFF000000)
                        | (Math.min(255, ((color >> 16) & 0xFF) + 22) << 16)
                        | (Math.min(255, ((color >> 8) & 0xFF) + 22) << 8)
                        | Math.min(255, (color & 0xFF) + 22);
            }
            context.fill(x, BUTTON_Y, x + BUTTON_WIDTH, BUTTON_Y + BUTTON_HEIGHT, color);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(labels[i]),
                    x + BUTTON_WIDTH / 2, BUTTON_Y + 5, 0xFFFFFFFF);
        }
    }

    private static boolean isTargetScreen(Screen screen) {
        String className = screen.getClass().getName();
        for (String target : TARGET_SCREENS) {
            if (target.equals(className)) return true;
        }
        return false;
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
