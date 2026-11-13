package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import ru.turtlebot.goldbot.GoldModule;

/** Makes invisible entities visible to the local player. */
public final class AntiInvisibleModule extends GoldModule {
    private static AntiInvisibleModule instance;
    private boolean showSelf;
    public AntiInvisibleModule() { super("AntiInvisible", "Render"); instance = this; }
    public static boolean shouldReveal(Entity entity) {
        if (instance == null || !instance.enabled()) return false;
        return entity != MinecraftClient.getInstance().player || instance.showSelf;
    }
    public void setShowSelf(boolean value) { showSelf = value; }
    @Override protected void onTick(MinecraftClient client) {}
}