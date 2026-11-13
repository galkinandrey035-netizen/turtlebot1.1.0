package ru.turtlebot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Applies the Dark Glass palette to GoldBot's existing ClickGUI controls.
 * Only render-time color literals are changed; widget callbacks and GoldBot state
 * remain untouched.
 */
@Pseudo
@Mixin(targets = {
        "dev.goldbot.ClickGuiScreen$ToggleRow",
        "dev.goldbot.ClickGuiScreen$GButton",
        "dev.goldbot.ClickGuiScreen$TabBtn",
        "dev.goldbot.ClickGuiScreen$GSlider",
        "dev.goldbot.ClickGuiScreen$DropRow",
        "dev.goldbot.ClickGuiScreen$NavBtn"
})
public abstract class GoldBotDarkGlassWidgetsMixin {
    // Gold accent -> restrained violet.
    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -1525469), require = 0, remap = false)
    private int turtlebot$accent(int original) { return 0xFF9B86FF; }

    // Gold hover/active tint -> blue-violet highlight.
    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -5724513), require = 0, remap = false)
    private int turtlebot$accentSoft(int original) { return 0xFF7566C8; }

    // Main surface -> translucent midnight glass.
    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -13815495), require = 0, remap = false)
    private int turtlebot$surface(int original) { return 0xE8171B2B; }

    // Secondary surface.
    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14473683), require = 0, remap = false)
    private int turtlebot$surfaceRaised(int original) { return 0xE9232940; }

    // Borders and track backgrounds.
    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -15067904), require = 0, remap = false)
    private int turtlebot$track(int original) { return 0xCC111522; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14013131), require = 0, remap = false)
    private int turtlebot$trackRaised(int original) { return 0xDD20263A; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14210252), require = 0, remap = false)
    private int turtlebot$field(int original) { return 0xE51B2032; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14736855), require = 0, remap = false)
    private int turtlebot$fieldEdge(int original) { return 0xFF414967; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -13949936), require = 0, remap = false)
    private int turtlebot$activeNav(int original) { return 0xFF343052; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -13881287), require = 0, remap = false)
    private int turtlebot$navBorder(int original) { return 0xFF514A78; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -1513761), require = 0, remap = false)
    private int turtlebot$primaryText(int original) { return 0xFFF0EEFF; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -7632251), require = 0, remap = false)
    private int turtlebot$mutedText(int original) { return 0xFF9CA3BC; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -536758), require = 0, remap = false)
    private int turtlebot$whiteAccent(int original) { return 0xFFB9ADFF; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -2514150), require = 0, remap = false)
    private int turtlebot$softAccent(int original) { return 0xFF8275D8; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -15395044), require = 0, remap = false)
    private int turtlebot$toggleOff(int original) { return 0xFF30364B; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -12960184), require = 0, remap = false)
    private int turtlebot$toggleOn(int original) { return 0xFF7566C8; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -1998014173), require = 0, remap = false)
    private int turtlebot$dropShadow(int original) { return 0x66080B16; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = 872415231), require = 0, remap = false)
    private int turtlebot$dropGlass(int original) { return 0x551C2134; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = 1441315107), require = 0, remap = false)
    private int turtlebot$dropHighlight(int original) { return 0x552F3150; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = 587202559), require = 0, remap = false)
    private int turtlebot$dropBorder(int original) { return 0x332C3552; }
}
