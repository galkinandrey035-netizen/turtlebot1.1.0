package ru.turtlebot.goldbot;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.modules.*;
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
        register(new AutoCrystalModule());
        register(new AirStuckModule());
        register(new SpeedModule());
        register(new ChestStealerModule());
        register(new FakePlayerModule());
        register(new NoDelayModule());
        register(new NoPushModule());
        register(new ItemScrollerModule());
        register(new MultiTaskModule());
        register(new ChorusControlModule());
        register(new NoTraceModule());
    }
    private GoldModuleManager() {}
    private static void register(GoldModule module) { MODULES.put(module.name().toLowerCase(Locale.ROOT), module); }
    public static void tick(MinecraftClient client) { for (GoldModule module : MODULES.values()) module.tick(client); }
    public static GoldModule get(String name) { return name == null ? null : MODULES.get(name.toLowerCase(Locale.ROOT)); }
    public static Collection<GoldModule> all() { return MODULES.values(); }
    public static void disableAll(MinecraftClient client) { for (GoldModule module : MODULES.values()) module.setEnabled(false, client); }
}