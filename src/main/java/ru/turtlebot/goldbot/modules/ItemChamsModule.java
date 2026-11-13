package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class ItemChamsModule extends GoldModule {
    private static ItemChamsModule instance;
    private float red = 0.25f, green = 0.8f, blue = 1.0f, alpha = 1.0f;
    public ItemChamsModule() { super("ItemChams", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    public static float[] color() { return instance == null ? new float[]{1,1,1,1} : new float[]{instance.red,instance.green,instance.blue,instance.alpha}; }
    public void setColor(float r,float g,float b,float a) { red=clamp(r); green=clamp(g); blue=clamp(b); alpha=clamp(a); }
    private static float clamp(float v) { return Math.max(0,Math.min(1,v)); }
    @Override protected void onTick(MinecraftClient client) {}
}