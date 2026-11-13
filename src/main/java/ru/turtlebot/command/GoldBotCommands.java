package ru.turtlebot.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;
import ru.turtlebot.goldbot.GoldModuleManager;
import ru.turtlebot.util.BotLog;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class GoldBotCommands {
    private GoldBotCommands() {}

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("goldbot")
                .executes(ctx -> { list(); return 1; })
                .then(literal("list").executes(ctx -> { list(); return 1; }))
                .then(literal("toggle")
                        .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<FabricClientCommandSource, String>argument(
                                "module", com.mojang.brigadier.arguments.StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (GoldModule module : GoldModuleManager.all()) builder.suggest(module.name());
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String name = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "module");
                                    GoldModule module = GoldModuleManager.get(name);
                                    if (module == null) {
                                        BotLog.reply("§cНеизвестный модуль: " + name);
                                        return 0;
                                    }
                                    module.toggle(MinecraftClient.getInstance());
                                    BotLog.reply(module.name() + ": " + (module.enabled() ? "§aвключён" : "§cвыключен"));
                                    return 1;
                                }))));
    }

    private static void list() {
        BotLog.reply("§6GoldBot — перенесённые модули:");
        for (GoldModule module : GoldModuleManager.all()) {
            BotLog.reply("§7[" + module.category() + "] §f" + module.name()
                    + " §8— " + (module.enabled() ? "§aON" : "§cOFF"));
        }
        BotLog.reply("Использование: /goldbot toggle <module>");
    }
}
