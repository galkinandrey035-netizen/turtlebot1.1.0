package ru.turtlebot.command;

import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.util.BotLog;

import java.util.Locale;

public final class StaffCommands {
    private StaffCommands() {}

    /**
     * @return true when the message was consumed as a local .staff command.
     */
    public static boolean handle(String message) {
        if (message == null) return false;
        String trimmed = message.trim();
        if (!trimmed.equals(".staff") && !trimmed.startsWith(".staff ")) return false;

        String[] parts = trimmed.split("\\s+", 4);
        if (parts.length == 1 || "help".equalsIgnoreCase(parts[1])) {
            help();
            return true;
        }

        BotConfig cfg = ConfigManager.get();
        String action = parts[1].toLowerCase(Locale.ROOT);
        if ("add".equals(action) && parts.length >= 4 && "prefix".equalsIgnoreCase(parts[2])) {
            String prefix = parts[3].trim();
            if (prefix.isEmpty()) {
                BotLog.reply("§cУкажи префикс: .staff add prefix <текст>");
                return true;
            }
            if (prefix.length() > 48) prefix = prefix.substring(0, 48);
            for (String existing : cfg.staffPrefixes) {
                if (existing.equalsIgnoreCase(prefix)) {
                    BotLog.reply("§eТакой префикс уже добавлен: " + prefix);
                    return true;
                }
            }
            cfg.staffPrefixes.add(prefix);
            ConfigManager.save();
            BotLog.reply("§aДобавлен префикс сотрудника: " + prefix);
            return true;
        }

        if ("remove".equals(action) && parts.length >= 4 && "prefix".equalsIgnoreCase(parts[2])) {
            String prefix = parts[3].trim();
            boolean removed = cfg.staffPrefixes.removeIf(p -> p.equalsIgnoreCase(prefix));
            ConfigManager.save();
            BotLog.reply(removed ? "§aУдалён префикс: " + prefix : "§cПрефикс не найден: " + prefix);
            return true;
        }

        if ("list".equals(action)) {
            if (cfg.staffPrefixes.isEmpty()) {
                BotLog.reply("§eПользовательские префиксы не заданы.");
            } else {
                BotLog.reply("§6Префиксы сотрудников: §f" + String.join(", ", cfg.staffPrefixes));
            }
            return true;
        }

        if ("clear".equals(action)) {
            cfg.staffPrefixes.clear();
            ConfigManager.save();
            BotLog.reply("§aСписок пользовательских префиксов очищен.");
            return true;
        }

        help();
        return true;
    }

    private static void help() {
        BotLog.reply("§6.staff add prefix <текст> §7— добавить префикс");
        BotLog.reply("§6.staff remove prefix <текст> §7— удалить префикс");
        BotLog.reply("§6.staff list §7— показать список");
        BotLog.reply("§6.staff clear §7— очистить список");
    }
}
