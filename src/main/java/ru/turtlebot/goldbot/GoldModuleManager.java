package ru.turtlebot.goldbot;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.modules.AntiAfkModule;
import ru.turtlebot.goldbot.modules.AutoSwapModule;
import ru.turtlebot.goldbot.modules.TriggerBotModule;
import ru.turtlebot.goldbot.modules.NoFriendDamageModule;
import ru.turtlebot.goldbot.modules.NoInteractModule;
import ru.turtlebot.goldbot.modules.GuiMoveModule;
import ru.turtlebot.goldbot.modules.ClickPearlModule;
import ru.turtlebot.goldbot.modules.AutoJoinModule;
import ru.turtlebot.goldbot.modules.AutoUseModule;
import ru.turtlebot.goldbot.modules.AutoTotemModule;
import ru.turtlebot.goldbot.modules.AutoTpAcceptModule;
import ru.turtlebot.goldbot.modules.AutoRespawnModule;
import ru.turtlebot.goldbot.modules.AutoEatModule;
import ru.turtlebot.goldbot.modules.AutoLeaveModule;
import ru.turtlebot.goldbot.modules.AutoToolModule;
import ru.turtlebot.goldbot.modules.SprintModule;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class GoldModuleManager {
    private static final Map<String, GoldModule> MODULES = new LinkedHashMap<>();

    static {
        register(new SprintModule());
        register(new AntiAfkModule());
        register(new AutoRespawnModule());
        register(new AutoEatModule());
        register(new AutoLeaveModule());
        register(new AutoToolModule());
        register(new AutoTpAcceptModule());
        register(new AutoTotemModule());
        register(new GuiMoveModule());
        register(new ClickPearlModule());
        register(new AutoJoinModule());
        register(new AutoUseModule());
        register(new AutoSwapModule());
        register(new TriggerBotModule());
        register(new NoFriendDamageModule());
        register(new NoInteractModule());
    }

    private GoldModuleManager() {}

    private static void register(GoldModule module) {
        MODULES.put(module.name().toLowerCase(Locale.ROOT), module);
    }

    public static void tick(MinecraftClient client) {
        for (GoldModule module : MODULES.values()) module.tick(client);
    }

    public static GoldModule get(String name) {
        return name == null ? null : MODULES.get(name.toLowerCase(Locale.ROOT));
    }

    public static Collection<GoldModule> all() { return MODULES.values(); }

    public static void disableAll(MinecraftClient client) {
        for (GoldModule module : MODULES.values()) module.setEnabled(false, client);
    }
}
