package ru.turtlebot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Dark Glass palette for every built-in GoldBot ClickGUI control. */
@Pseudo
@Mixin(targets = {
        "dev.goldbot.ClickGuiScreen$ToggleRow",
        "dev.goldbot.ClickGuiScreen$GButton",
        "dev.goldbot.ClickGuiScreen$TabBtn",
        "dev.goldbot.ClickGuiScreen$GSlider",
        "dev.goldbot.ClickGuiScreen$DropRow",
        "dev.goldbot.ClickGuiScreen$NavBtn",
        "dev.goldbot.ClickGuiScreen$GField",
        "dev.goldbot.ClickGuiScreen$ColorRow",
        "dev.goldbot.ClickGuiScreen$IconBtn",
        "dev.goldbot.ClickGuiScreen$SaveBtn"
})
public abstract class GoldBotDarkGlassWidgetsMixin {
    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -1525469), require = 0, remap = false)
    private int turtlebot$goldAccent(int original) { return 0xFF9B86FF; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -5724513), require = 0, remap = false)
    private int turtlebot$hoverAccent(int original) { return 0xFF7566C8; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -13815495), require = 0, remap = false)
    private int turtlebot$surface(int original) { return 0xE8171B2B; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14473683), require = 0, remap = false)
    private int turtlebot$surfaceRaised(int original) { return 0xE9232940; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -15067904), require = 0, remap = false)
    private int turtlebot$track(int original) { return 0xCC111522; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14013131), require = 0, remap = false)
    private int turtlebot$trackRaised(int original) { return 0xDD252B42; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14210252), require = 0, remap = false)
    private int turtlebot$field(int original) { return 0xE51B2032; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -14736855), require = 0, remap = false)
    private int turtlebot$fieldEdge(int original) { return 0xFF414967; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -13881287), require = 0, remap = false)
    private int turtlebot$border(int original) { return 0xFF414967; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -13949936), require = 0, remap = false)
    private int turtlebot$active(int original) { return 0xFF343052; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -1513761), require = 0, remap = false)
    private int turtlebot$text(int original) { return 0xFFF0EEFF; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -7632251), require = 0, remap = false)
    private int turtlebot$mutedText(int original) { return 0xFF9CA3BC; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -536758), require = 0, remap = false)
    private int turtlebot$accentLight(int original) { return 0xFFB9ADFF; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -2514150), require = 0, remap = false)
    private int turtlebot$accentSoft(int original) { return 0xFF8275D8; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -15395044), require = 0, remap = false)
    private int turtlebot$toggleOff(int original) { return 0xFF30364B; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -12960184), require = 0, remap = false)
    private int turtlebot$toggleOn(int original) { return 0xFF7566C8; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -1998014173), require = 0, remap = false)
    private int turtlebot$popupShadow(int original) { return 0x66080B16; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = 872415231), require = 0, remap = false)
    private int turtlebot$popupSurface(int original) { return 0x551C2134; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = 1441315107), require = 0, remap = false)
    private int turtlebot$popupHover(int original) { return 0x552F3150; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = 587202559), require = 0, remap = false)
    private int turtlebot$popupBorder(int original) { return 0x553C496F; }

    @ModifyConstant(method = "method_48579", constant = @Constant(intValue = -2053862), require = 0, remap = false)
    private int turtlebot$saveAccent(int original) { return 0xFF8275D8; }
}
