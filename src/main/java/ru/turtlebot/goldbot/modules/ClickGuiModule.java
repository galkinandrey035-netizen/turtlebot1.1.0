package ru.turtlebot.goldbot.modules;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.ClickGuiScreen;
import ru.turtlebot.goldbot.GoldModule;
public final class ClickGuiModule extends GoldModule {
 private static final KeyBinding OPEN=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.turtlebot.clickgui",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_F10,"category.turtlebot"));
 public ClickGuiModule(){super("ClickGUI","Misc");}
 @Override protected void onTick(MinecraftClient c){while(OPEN.wasPressed())if(c.currentScreen==null)c.setScreen(new ClickGuiScreen());}
}