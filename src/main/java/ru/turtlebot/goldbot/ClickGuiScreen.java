package ru.turtlebot.goldbot;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.turtlebot.TurtleBotClient;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.gui.ClothScreen;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;

import java.util.ArrayList;
import java.util.List;

public final class ClickGuiScreen extends Screen {
    private final List<GoldModule> modules = new ArrayList<>(GoldModuleManager.all());
    private static final int RH = 14, CW = 150;
    private static final int BUTTON_Y = 30, BUTTON_H = 15, MODULES_Y = 72;

    public ClickGuiScreen() {
        super(Text.literal("GoldBot ClickGUI"));
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        c.fill(0, 0, width, height, 0xB0101018);
        c.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFFFF);

        int buttonW = Math.max(58, (width - 36) / 4);
        String[] labels = {"TB: Start", "TB: Pause", "TB: Stop", "TB: Settings"};
        for (int i = 0; i < labels.length; i++) {
            int x = 12 + i * (buttonW + 4);
            c.fill(x, BUTTON_Y, x + buttonW, BUTTON_Y + BUTTON_H, 0xFF34343E);
            c.drawCenteredTextWithShadow(textRenderer, labels[i], x + buttonW / 2, BUTTON_Y + 4, 0xFFFFFFFF);
        }

        BotController bot = TurtleBotClient.controller();
        String botState = bot == null ? "не инициализирован" :
                bot.isPaused() ? "пауза" : bot.isActive() ? "работает" : "остановлен";
        c.drawText(textRenderer, "TurtleBot: " + botState
                + " | сундук: " + (ConfigManager.get().chestSet ? "задан" : "не задан"),
                12, 51, 0xFF77DDAA, false);

        for (int i = 0; i < modules.size(); i++) {
            GoldModule m = modules.get(i);
            int col = i / 26, row = i % 26;
            int x = 12 + col * CW, y = MODULES_Y + row * RH;
            c.fill(x - 3, y - 2, x + CW - 8, y + 11, 0x55303038);
            c.drawText(textRenderer, m.name(), x, y, m.enabled() ? 0xFF55DD88 : 0xFFCCCCCC, false);
            c.drawText(textRenderer, m.category(), x + 92, y, 0xFF888888, false);
        }
        super.render(c, mx, my, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && my >= BUTTON_Y && my <= BUTTON_Y + BUTTON_H) {
            int buttonW = Math.max(58, (width - 36) / 4);
            int index = (int) ((mx - 12) / (buttonW + 4));
            if (index >= 0 && index < 4 && mx <= 12 + index * (buttonW + 4) + buttonW) {
                BotController bot = TurtleBotClient.controller();
                switch (index) {
                    case 0 -> {
                        if (bot != null) {
                            String error = bot.start();
                            if (error != null) BotLog.reply("§c" + error);
                        }
                    }
                    case 1 -> {
                        if (bot != null) {
                            if (bot.isPaused()) bot.resume();
                            else bot.pause();
                        }
                    }
                    case 2 -> {
                        if (bot != null) bot.stop("остановлено через ClickGUI");
                    }
                    case 3 -> {
                        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
                            client.setScreen(ClothScreen.build(this));
                        } else {
                            BotLog.reply("§cДля настроек TurtleBot установите Cloth Config.");
                        }
                    }
                }
                return true;
            }
        }

        if (button == 0 && my >= MODULES_Y) {
            for (int i = 0; i < modules.size(); i++) {
                int col = i / 26, row = i % 26;
                int x = 12 + col * CW, y = MODULES_Y + row * RH;
                if (mx >= x - 3 && mx <= x + CW - 8 && my >= y - 2 && my <= y + 11) {
                    modules.get(i).toggle(client);
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
