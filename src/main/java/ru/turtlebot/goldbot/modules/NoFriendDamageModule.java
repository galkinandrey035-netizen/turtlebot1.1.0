package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import ru.turtlebot.goldbot.FriendRegistry;
import ru.turtlebot.goldbot.GoldModule;

public final class NoFriendDamageModule extends GoldModule {
    public NoFriendDamageModule() { super("NoFriendDamage", "Combat"); }

    public static boolean shouldBlock(Entity target) {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.world != null && client.world.isClient &&
                GoldModuleEnabled() && FriendRegistry.isFriend(target.getName().getString());
    }

    private static boolean GoldModuleEnabled() {
        return ru.turtlebot.goldbot.GoldModuleManager.get("NoFriendDamage") != null &&
                ru.turtlebot.goldbot.GoldModuleManager.get("NoFriendDamage").enabled();
    }

    @Override
    protected void onTick(MinecraftClient client) {}
}
