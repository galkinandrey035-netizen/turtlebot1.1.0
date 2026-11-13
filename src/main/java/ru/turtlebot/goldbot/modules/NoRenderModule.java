package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

/** Selectively hides common screen overlays and camera effects. */
public final class NoRenderModule extends GoldModule {
    private static NoRenderModule instance;
    private boolean fire, hurtCam, portal, nausea, potions, vignette;
    public NoRenderModule() { super("NoRender", "Render"); instance = this; }
    public static boolean fire() { return active() && instance.fire; }
    public static boolean hurtCam() { return active() && instance.hurtCam; }
    public static boolean portal() { return active() && instance.portal; }
    public static boolean nausea() { return active() && instance.nausea; }
    public static boolean potions() { return active() && instance.potions; }
    public static boolean vignette() { return active() && instance.vignette; }
    private static boolean active() { return instance != null && instance.enabled(); }
    public void setFire(boolean v) { fire = v; }
    public void setHurtCam(boolean v) { hurtCam = v; }
    public void setPortal(boolean v) { portal = v; }
    public void setNausea(boolean v) { nausea = v; }
    public void setPotions(boolean v) { potions = v; }
    public void setVignette(boolean v) { vignette = v; }
    @Override protected void onTick(MinecraftClient client) {}
}