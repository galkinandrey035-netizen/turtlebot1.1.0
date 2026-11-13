package ru.turtlebot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import ru.turtlebot.command.BotCommands;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.data.TurtleRegistry;
import ru.turtlebot.render.PathRenderer;
import ru.turtlebot.state.BotController;
import ru.turtlebot.util.BotLog;
import ru.turtlebot.util.FedTurtleHighlighter;

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

        ru.turtlebot.gui.TurtleBotClickGuiBridge.register();
        ru.turtlebot.render.TargetHudOverlay.register();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                BotCommands.register(dispatcher, controller, registry));
        ClientTickEvents.END_CLIENT_TICK.register(controller::tick);
        ClientTickEvents.END_CLIENT_TICK.register(mc -> FedTurtleHighlighter.tick(mc, registry));
        PathRenderer.register(controller);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (ConfigManager.get().continueOnWorldChange && controller.isActive()) {
                controller.prepareForWorldChange();
            } else {
                controller.stop("отключение от сервера");
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
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
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            controller.stop("закрытие клиента");
            registry.save();
        });
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
                world.isClient && controller.isActive() && world.getBlockState(pos).isOf(Blocks.TURTLE_EGG)
                        ? ActionResult.FAIL : ActionResult.PASS);

        BotLog.info("TurtleBot загружен. Команда: /turtlebot (или /tb)");
    }
}
