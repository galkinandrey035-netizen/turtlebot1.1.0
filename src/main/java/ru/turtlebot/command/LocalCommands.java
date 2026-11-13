package ru.turtlebot.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.registry.Registries;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.util.BotLog;

import java.util.Locale;
import java.util.Map;

public final class LocalCommands {
    private LocalCommands() {}

    public static boolean handle(String message) {
        if (message == null || !message.startsWith(".")) return false;
        String trimmed = message.trim();
        if (trimmed.isEmpty()) return false;
        String[] parts = trimmed.substring(1).split("\\s+", 4);
        String root = parts[0].toLowerCase(Locale.ROOT);
        String[] args = new String[Math.max(0, parts.length - 1)];
        System.arraycopy(parts, 1, args, 0, args.length);
        switch (root) {
            case "help", "h", "?" -> help();
            case "staff", "sl" -> staff(args);
            case "friend", "friends", "f" -> friend(args);
            case "blockesp", "besp" -> blockEsp(args);
            case "macros", "macro" -> macros(args);
            default -> { return false; }
        }
        return true;
    }

    private static void staff(String[] a) {
        BotConfig c = ConfigManager.get();
        if (a.length == 0 || a[0].equalsIgnoreCase("help")) {
            BotLog.reply(".staff add <ник> | add prefix <префикс> | del <ник> | remove prefix <префикс> | list | clear [prefix]");
            return;
        }
        String action = a[0].toLowerCase(Locale.ROOT);
        if (action.equals("add") && a.length >= 3 && a[1].equalsIgnoreCase("prefix")) {
            String value = join(a, 2);
            if (value.isBlank()) { BotLog.reply("§cУкажи префикс."); return; }
            if (containsIgnoreCase(c.staffPrefixes, value)) { BotLog.reply("§eПрефикс уже добавлен."); return; }
            c.staffPrefixes.add(value);
            ConfigManager.save();
            BotLog.reply("§aДобавлен префикс стаффа: " + value);
        } else if (action.equals("add") && a.length >= 2) {
            String name = join(a, 1);
            if (containsIgnoreCase(c.staffNames, name)) { BotLog.reply("§eИгрок уже в стафф-листе."); return; }
            c.staffNames.add(name); ConfigManager.save(); BotLog.reply("§aДобавлен сотрудник: " + name);
        } else if ((action.equals("del") || action.equals("remove")) && a.length >= 3 && a[1].equalsIgnoreCase("prefix")) {
            String value = join(a, 2);
            boolean ok = c.staffPrefixes.removeIf(x -> x.equalsIgnoreCase(value));
            ConfigManager.save(); BotLog.reply(ok ? "§aПрефикс удалён: " + value : "§cПрефикс не найден.");
        } else if ((action.equals("del") || action.equals("remove")) && a.length >= 2) {
            String name = join(a, 1);
            boolean ok = c.staffNames.removeIf(x -> x.equalsIgnoreCase(name));
            ConfigManager.save(); BotLog.reply(ok ? "§aСотрудник удалён: " + name : "§cИгрок не найден.");
        } else if (action.equals("list")) {
            BotLog.reply("§6Стафф-имена: §f" + (c.staffNames.isEmpty() ? "пусто" : String.join(", ", c.staffNames)));
            BotLog.reply("§6Стафф-префиксы: §f" + (c.staffPrefixes.isEmpty() ? "пусто" : String.join(", ", c.staffPrefixes)));
        } else if (action.equals("clear")) {
            if (a.length > 1 && a[1].equalsIgnoreCase("prefix")) { c.staffPrefixes.clear(); BotLog.reply("§aПрефиксы очищены."); }
            else { c.staffNames.clear(); BotLog.reply("§aСтафф-лист имён очищен."); }
            ConfigManager.save();
        } else BotLog.reply("Использование: .staff add <ник> | add prefix <текст> | del <ник> | remove prefix <текст> | list | clear [prefix]");
    }

    private static void friend(String[] a) {
        BotConfig c = ConfigManager.get();
        if (a.length == 0 || a[0].equalsIgnoreCase("help")) { BotLog.reply(".friend add <ник> | remove <ник> | list | clear"); return; }
        String action = a[0].toLowerCase(Locale.ROOT);
        if (action.equals("add") && a.length >= 2) {
            String name = join(a, 1);
            if (containsIgnoreCase(c.friendNames, name)) { BotLog.reply("§eДруг уже в списке: " + name); return; }
            c.friendNames.add(name); ConfigManager.save(); BotLog.reply("§aДобавлен друг: " + name);
        } else if ((action.equals("remove") || action.equals("del")) && a.length >= 2) {
            String name = join(a, 1);
            boolean ok = c.friendNames.removeIf(x -> x.equalsIgnoreCase(name));
            ConfigManager.save(); BotLog.reply(ok ? "§aДруг удалён: " + name : "§cДруг не найден: " + name);
        } else if (action.equals("list")) {
            BotLog.reply(c.friendNames.isEmpty() ? "§eСписок друзей пуст." : "§6Друзья (" + c.friendNames.size() + "): §f" + String.join(", ", c.friendNames));
        } else if (action.equals("clear")) {
            int n = c.friendNames.size(); c.friendNames.clear(); ConfigManager.save(); BotLog.reply("§aУдалено друзей: " + n);
        } else BotLog.reply("Использование: .friend add <ник> | remove <ник> | list | clear");
    }

    private static void blockEsp(String[] a) {
        BotConfig c = ConfigManager.get();
        if (a.length == 0 || a[0].equalsIgnoreCase("list")) {
            BotLog.reply(c.espBlockIds.isEmpty() ? "§eBlock ESP: список пуст." : "§6Block ESP: §f" + String.join(", ", c.espBlockIds));
        } else if (a[0].equalsIgnoreCase("add")) {
            String id = a.length >= 2 ? join(a, 1) : lookedBlockId();
            if (id == null || id.isBlank()) { BotLog.reply("§cНаведись на блок или укажи ID: .blockesp add <namespace:block>"); return; }
            if (containsIgnoreCase(c.espBlockIds, id)) { BotLog.reply("§eБлок уже в списке: " + id); return; }
            c.espBlockIds.add(id); ConfigManager.save(); BotLog.reply("§aBlock ESP: добавлен " + id);
        } else if ((a[0].equalsIgnoreCase("del") || a[0].equalsIgnoreCase("remove")) && a.length >= 2) {
            String id = join(a, 1);
            boolean ok = c.espBlockIds.removeIf(x -> x.equalsIgnoreCase(id));
            ConfigManager.save(); BotLog.reply(ok ? "§aBlock ESP: удалён " + id : "§cBlock ESP: блока нет в списке");
        } else if (a[0].equalsIgnoreCase("clear")) {
            c.espBlockIds.clear(); ConfigManager.save(); BotLog.reply("§aBlock ESP: список очищен.");
        } else BotLog.reply("Использование: .blockesp add [id] | del <id> | list | clear");
    }

    private static String lookedBlockId() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || !(mc.crosshairTarget instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return null;
        return Registries.BLOCK.getId(mc.world.getBlockState(hit.getBlockPos()).getBlock()).toString();
    }

    private static void macros(String[] a) {
        BotConfig c = ConfigManager.get();
        if (a.length == 0 || a[0].equalsIgnoreCase("list")) {
            if (c.macros.isEmpty()) { BotLog.reply("§eМакросов нет. .macros add <клавиша> <текст или /команда>"); return; }
            BotLog.reply("§6Макросы (" + c.macros.size() + "):");
            c.macros.forEach((key, value) -> BotLog.reply("§f" + key + " §7→ §f" + value));
        } else if ((a[0].equalsIgnoreCase("add") || a[0].equalsIgnoreCase("set")) && a.length >= 3) {
            String key = a[1].toUpperCase(Locale.ROOT);
            if (ru.turtlebot.state.MacroController.parseKey(key) < 0) { BotLog.reply("§cНеизвестная клавиша: " + key + " (G, F6, NUMPAD_1, M4)"); return; }
            String value = join(a, 2);
            c.macros.put(key, value); ConfigManager.save(); BotLog.reply("§aМакрос " + key + " → " + value);
        } else if ((a[0].equalsIgnoreCase("del") || a[0].equalsIgnoreCase("remove")) && a.length >= 2) {
            String key = a[1].toUpperCase(Locale.ROOT);
            boolean ok = c.macros.remove(key) != null; ConfigManager.save(); BotLog.reply(ok ? "§aМакрос удалён: " + key : "§cМакрос не найден: " + key);
        } else if (a[0].equalsIgnoreCase("clear")) {
            int n = c.macros.size(); c.macros.clear(); ConfigManager.save(); BotLog.reply("§aУдалено макросов: " + n);
        } else BotLog.reply("Использование: .macros add <клавиша> <текст> | del <клавиша> | list | clear");
    }

    private static void help() {
        BotLog.reply("§6Локальные команды TurtleBot:");
        BotLog.reply("§f.staff §7— список сотрудников и префиксов");
        BotLog.reply("§f.friend §7— управление друзьями");
        BotLog.reply("§f.blockesp §7— список блоков для ESP");
        BotLog.reply("§f.macros §7— макросы клавиш");
        BotLog.reply("§f.turtlebot / .tb §7— управление ботом черепах");
    }

    private static String join(String[] a, int start) {
        StringBuilder b = new StringBuilder();
        for (int i = start; i < a.length; i++) { if (i > start) b.append(' '); b.append(a[i]); }
        return b.toString().trim();
    }
    private static boolean containsIgnoreCase(Iterable<String> values, String value) {
        for (String x : values) if (x != null && x.equalsIgnoreCase(value)) return true;
        return false;
    }
}
