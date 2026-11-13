package ru.turtlebot.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import ru.turtlebot.TurtleBotClient;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public final class TurtleBotClickGuiBridge {
    private static final int PANEL_WIDTH = 232;
    private static final int PANEL_HEIGHT = 54;
    private static final int BUTTON_WIDTH = 71;
    private static final int BUTTON_HEIGHT = 19;
    private static final int BUTTON_GAP = 5;
    private static final int BUTTON_OFFSET_X = 7;
    private static final int BUTTON_OFFSET_Y = 29;
    private static final int SEARCH_WIDTH = 180;
    private static final int SEARCH_HEIGHT = 18;
    private static final int SEARCH_RESULT_ROW = 18;
    private static final String[] TARGET_SCREENS = {
            "aethereal.ClickGuiScreen",
            "dev.goldbot.ClickGuiScreen"
    };
    private static final Map<Screen, SearchState> SEARCH_STATES = new WeakHashMap<>();
    private static final List<SearchEntry> SEARCH_ENTRIES = List.of(
            new SearchEntry("Auto Eat", 0, 0),
            new SearchEntry("Auto Leave", 0, 1),
            new SearchEntry("Rejoin", 0, 2),
            new SearchEntry("Drop", 0, 3),
            new SearchEntry("GoldBot", 0, 4),
            new SearchEntry("Block ESP", 0, 5),
            new SearchEntry("Discord", 0, 6),

            new SearchEntry("Fullbright", 1, 0),
            new SearchEntry("Camera", 1, 0),
            new SearchEntry("Nimb", 1, 0),
            new SearchEntry("Jump Circles", 1, 0),
            new SearchEntry("ESP", 1, 0),
            new SearchEntry("ViewModel", 1, 0),
            new SearchEntry("Sword Animation", 1, 0),
            new SearchEntry("TargetHUD", 1, 0),
            new SearchEntry("Ambience", 1, 1),
            new SearchEntry("Sky", 1, 1),
            new SearchEntry("Fog", 1, 1),
            new SearchEntry("Item Physics", 1, 1),
            new SearchEntry("Block Highlight", 1, 1),
            new SearchEntry("Hit Particles", 1, 2),
            new SearchEntry("Trail Particles", 1, 2),
            new SearchEntry("Track Particles", 1, 2),
            new SearchEntry("Damage Particles", 1, 2),
            new SearchEntry("Ambient Particles", 1, 2),
            new SearchEntry("Block Particles", 1, 2),
            new SearchEntry("Armor", 1, 3),
            new SearchEntry("Arrows", 1, 3),
            new SearchEntry("Aspect Ratio", 1, 3),
            new SearchEntry("Sprint", 1, 3),
            new SearchEntry("Island", 1, 3),

            new SearchEntry("Auto Clicker", 2, 0),
            new SearchEntry("Totem", 2, 1),
            new SearchEntry("Attack Aura", 2, 2),
            new SearchEntry("Auto Omen", 2, 3),
            new SearchEntry("Auto Trade", 2, 4),
            new SearchEntry("Friends", 2, 5),

            new SearchEntry("Gui Move", 3, 0),
            new SearchEntry("Speed", 3, 1),
            new SearchEntry("FreeCam", 3, 2),
            new SearchEntry("Elytra Swap", 3, 3),
            new SearchEntry("Click Pearl", 3, 4)
    );

    private TurtleBotClickGuiBridge() {}

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isTargetScreen(screen)) return;

            SearchState state = SEARCH_STATES.computeIfAbsent(screen, ignored -> new SearchState());
            if (!state.hooksRegistered) {
                ScreenEvents.afterRender(screen).register((current, context, mouseX, mouseY, tickDelta) ->
                        render(client, current, context, mouseX, mouseY));
                ScreenMouseEvents.allowMouseClick(screen).register((current, mouseX, mouseY, button) ->
                        handleMouseClick(client, current, mouseX, mouseY, button));
                state.hooksRegistered = true;
            }
            state.field = null;
        });
    }

    private static void addSearchField(MinecraftClient client, Screen screen, SearchState state) {
        int[] frame = frameOrigin(screen);
        TextFieldWidget field = new TextFieldWidget(client.textRenderer,
                frame[0] + 10, frame[1] + 8, SEARCH_WIDTH, SEARCH_HEIGHT,
                Text.literal("Поиск функций"));
        field.setMaxLength(48);
        field.setPlaceholder(Text.literal("Поиск функций"));
        field.setText(state.query);
        field.setChangedListener(value -> state.query = value);
        state.field = field;

        try {
            Method addChild = findScreenAddChild();
            addChild.invoke(screen, field);
        } catch (ReflectiveOperationException exception) {
            BotLog.reply("§cНе удалось добавить поиск функций в ClickGUI: " + exception.getMessage());
        }
    }

    private static Method findScreenAddChild() throws NoSuchMethodException {
        for (Method method : Screen.class.getDeclaredMethods()) {
            String name = method.getName();
            if ((name.equals("addDrawableChild") || name.equals("method_37063"))
                    && method.getParameterCount() == 1) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException("Screen.addDrawableChild");
    }

    private static void render(MinecraftClient client, Screen screen, DrawContext context,
                               int mouseX, int mouseY) {
        int[] panel = origin(screen);
        int px = panel[0];
        int py = panel[1];

        context.fill(px - 2, py - 2, px + PANEL_WIDTH + 2, py + PANEL_HEIGHT + 2, 0x66070A16);
        context.fill(px, py, px + PANEL_WIDTH, py + PANEL_HEIGHT, 0xD9141728);
        context.fill(px, py, px + PANEL_WIDTH, py + 1, 0xFF8B72FF);
        context.fill(px, py + 1, px + PANEL_WIDTH, py + 2, 0x663F8CFF);
        context.fill(px, py + PANEL_HEIGHT - 1, px + PANEL_WIDTH, py + PANEL_HEIGHT, 0xAA59618A);
        context.drawText(client.textRenderer, Text.literal("TurtleBot"), px + 7, py + 6,
                0xFFC7BCFF, false);

        String[] labels = {"Старт", "Пауза", "Сундук"};
        int[] colors = {0xCC285C63, 0xCC51477D, 0xCC293B66};
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
            context.fill(x - 1, y - 1, x + BUTTON_WIDTH + 1, y + BUTTON_HEIGHT + 1, 0x553F8CFF);
            context.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, color);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(labels[i]),
                    x + BUTTON_WIDTH / 2, y + 5, 0xFFFFFFFF);
        }

        SearchState state = SEARCH_STATES.get(screen);
        if (state != null) {
            if (state.field == null) addSearchField(client, screen, state);
            if (!state.query.isBlank()) {
                renderSearchResults(client, context, screen, state, mouseX, mouseY);
            }
        }
    }

    private static void renderSearchResults(MinecraftClient client, DrawContext context, Screen screen,
                                            SearchState state, int mouseX, int mouseY) {
        List<SearchEntry> results = findMatches(state.query);
        int[] frame = frameOrigin(screen);
        int x = frame[0] + 10;
        int y = frame[1] + 29;
        int width = SEARCH_WIDTH;
        int rows = Math.max(1, Math.min(6, results.size()));
        int height = rows * SEARCH_RESULT_ROW + 4;

        context.fill(x, y, x + width, y + height, 0xF0141720);
        context.fill(x, y, x + width, y + 1, 0xFF8B72FF);
        if (results.isEmpty()) {
            context.drawText(client.textRenderer, Text.literal("Ничего не найдено"),
                    x + 7, y + 7, 0xFFB9BEC8, false);
            return;
        }

        for (int i = 0; i < rows; i++) {
            int rowY = y + 2 + i * SEARCH_RESULT_ROW;
            boolean hovered = mouseX >= x && mouseX <= x + width
                    && mouseY >= rowY && mouseY < rowY + SEARCH_RESULT_ROW;
            if (hovered) context.fill(x + 2, rowY, x + width - 2,
                    rowY + SEARCH_RESULT_ROW, 0xFF34365F);
            context.drawText(client.textRenderer, Text.literal(results.get(i).name),
                    x + 7, rowY + 5, 0xFFF0F0F0, false);
        }
    }

    private static boolean handleMouseClick(MinecraftClient client, Screen screen,
                                            double mouseX, double mouseY, int button) {
        if (button != 0) return true;

        SearchState state = SEARCH_STATES.get(screen);
        if (state != null && !state.query.isBlank()) {
            List<SearchEntry> results = findMatches(state.query);
            int[] frame = frameOrigin(screen);
            int x = frame[0] + 10;
            int y = frame[1] + 29;
            int row = ((int) mouseY - y - 2) / SEARCH_RESULT_ROW;
            int maxRows = Math.min(6, results.size());
            if (mouseX >= x && mouseX <= x + SEARCH_WIDTH
                    && mouseY >= y + 2 && row >= 0 && row < maxRows) {
                navigateTo(client, screen, results.get(row), state);
                return false;
            }
        }

        int[] panel = origin(screen);
        int relativeX = (int) mouseX - (panel[0] + BUTTON_OFFSET_X);
        int relativeY = (int) mouseY - (panel[1] + BUTTON_OFFSET_Y);
        if (relativeY < 0 || relativeY > BUTTON_HEIGHT || relativeX < 0) return true;

        int stride = BUTTON_WIDTH + BUTTON_GAP;
        int index = relativeX / stride;
        if (index < 0 || index > 2 || relativeX % stride >= BUTTON_WIDTH) return true;

        handleClick(client, index);
        return false;
    }

    private static List<SearchEntry> findMatches(String query) {
        String needle = query.toLowerCase(Locale.ROOT).trim();
        List<SearchEntry> matches = new ArrayList<>();
        for (SearchEntry entry : SEARCH_ENTRIES) {
            if (entry.name.toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(entry);
                if (matches.size() == 6) break;
            }
        }
        return matches;
    }

    private static void navigateTo(MinecraftClient client, Screen screen,
                                   SearchEntry entry, SearchState state) {
        try {
            Field cat = screen.getClass().getDeclaredField("cat");
            Field tab = screen.getClass().getDeclaredField("tab");
            cat.setAccessible(true);
            tab.setAccessible(true);
            cat.setInt(null, entry.category);
            tab.setInt(null, entry.tab);

            state.query = "";
            state.field = null;
            Method init = findScreenInit(screen);
            init.invoke(screen);
        } catch (ReflectiveOperationException exception) {
            BotLog.reply("§cНе удалось открыть найденную функцию: " + exception.getMessage());
        }
    }

    private static Method findScreenInit(Screen screen) throws NoSuchMethodException {
        for (Method method : screen.getClass().getDeclaredMethods()) {
            String name = method.getName();
            if ((name.equals("init") || name.equals("method_25426")) && method.getParameterCount() == 0) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException("ClickGuiScreen.init");
    }

    private static int[] frameOrigin(Screen screen) {
        try {
            int x = readInt(screen, "panelX");
            int y = readInt(screen, "panelY");
            return new int[]{x, y};
        } catch (ReflectiveOperationException ignored) {
            return new int[]{Math.max(8, (screen.width - 420) / 2), 8};
        }
    }

    private static int[] origin(Screen screen) {
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

    private static final class SearchState {
        private String query = "";
        private TextFieldWidget field;
        private boolean hooksRegistered;
    }

    private record SearchEntry(String name, int category, int tab) {}
}
