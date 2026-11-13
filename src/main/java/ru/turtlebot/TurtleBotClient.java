package ru.turtlebot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.util.ActionResult;
import ru.turtlebot.command.BotCommands;
import ru.turtlebot.command.GoldBotCommands;
import ru.turtlebot.goldbot.GoldModuleManager;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.data.TurtleRegistry;
import ru.turtlebot.render.PathRenderer;
import ru.turtlebot.util.FedTurtleHighlighter;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;
import ru.turtlebot.util.TurtleBotWorldOverlay;

public final class TurtleBotClient implements ClientModInitializer {
    private static BotController controller;
    private static TurtleRegistry registry;
    public static BotController controller() { return controller; }
    public static TurtleRegistry registry() { return registry; }
    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        registry = new TurtleRegistry();
        registry.load();
        controller = new BotController(registry);
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, access) -> {
                    BotCommands.register(dispatcher, controller, registry);
                    GoldBotCommands.register(dispatcher);
                });
        ClientTickEvents.END_CLIENT_TICK.register(controller::tick);
        ClientTickEvents.END_CLIENT_TICK.register(GoldModuleManager::tick);
        ClientTickEvents.END_CLIENT_TICK.register(mc -> FedTurtleHighlighter.tick(mc, registry));
        PathRenderer.register(controller);
        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> {
            if (ConfigManager.get().continueOnWorldChange && controller.isActive()) {
                controller.prepareForWorldChange();
            } else {
                controller.stop("отключение от сервера");
            }
        });
        ClientPlayConnectionEvents.JOIN.register((h, sender, mc) -> {
            BotConfig config = ConfigManager.get();
            if (config.enabled) {
                String error = controller.start();
                if (error != null) {
                    config.enabled = false;
                    ConfigManager.save();
                    BotLog.warn("Не удалось продолжить TurtleBot после смены мира: " + error);
                } else {
                    BotLog.info("TurtleBot продолжил работу в новом мире");
                }
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> {
            controller.stop("закрытие клиента");
            GoldModuleManager.disableAll(c);
            registry.save();
        });
        AttackBlockCallback.EVENT.register((player, world, hand, pos, dir) ->
                world.isClient && controller.isActive() ? ActionResult.FAIL : ActionResult.PASS);
        BotLog.info("TurtleBot загружен. Команда: /turtlebot (или /tb)");
    }
}
