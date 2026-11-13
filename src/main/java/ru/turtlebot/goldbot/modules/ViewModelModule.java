package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import ru.turtlebot.goldbot.GoldModule;

/** Independent first-person item offsets for both hands. */
public final class ViewModelModule extends GoldModule {
    private static ViewModelModule instance;
    private final float[] main = new float[3];
    private final float[] off = new float[3];
    public ViewModelModule() { super("ViewModel", "Render"); instance = this; }
    public static float[] offset(Hand hand) {
        if (instance == null || !instance.enabled()) return new float[3];
        return hand == Hand.MAIN_HAND ? instance.main : instance.off;
    }
    public void setMain(float x, float y, float z) { main[0]=x; main[1]=y; main[2]=z; }
    public void setOff(float x, float y, float z) { off[0]=x; off[1]=y; off[2]=z; }
    @Override protected void onTick(MinecraftClient client) {}
}