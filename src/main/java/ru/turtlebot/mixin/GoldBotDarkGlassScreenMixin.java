package ru.turtlebot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Dark Glass skin for the complete GoldBot ClickGUI frame and card renderer.
 * This changes only rendering constants; layout, input handling and feature
 * callbacks remain owned by the original GoldBot implementation.
 */
@Pseudo
@Mixin(targets = "dev.goldbot.ClickGuiScreen")
public abstract class GoldBotDarkGlassScreenMixin {
    @ModifyConstant(method = "panel", constant = @Constant(intValue = -1525469), require = 0, remap = false)
    private static int turtlebot$panelGlow(int original) { return 0x887D6BFF; }

    @ModifyConstant(method = "panel", constant = @Constant(intValue = -182772444), require = 0, remap = false)
    private static int turtlebot$panelGradientTop(int original) { return 0xF0181C2D; }

    @ModifyConstant(method = "panel", constant = @Constant(intValue = -183562220), require = 0, remap = false)
    private static int turtlebot$panelGradientBottom(int original) { return 0xF0111525; }

    @ModifyConstant(method = "panel", constant = @Constant(intValue = -865195), require = 0, remap = false)
    private static int turtlebot$panelRingOuter(int original) { return 0xAA8172D8; }

    @ModifyConstant(method = "panel", constant = @Constant(intValue = -6522350), require = 0, remap = false)
    private static int turtlebot$panelRingInner(int original) { return 0x553C496F; }

    @ModifyConstant(method = "box", constant = @Constant(intValue = -13881287), require = 0, remap = false)
    private static int turtlebot$cardBorder(int original) { return 0xFF414967; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -536758), require = 0, remap = false)
    private int turtlebot$logoAccent(int original) { return 0xFFB9ADFF; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -2514150), require = 0, remap = false)
    private int turtlebot$logoAccentSoft(int original) { return 0xFF8275D8; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -15067904), require = 0, remap = false)
    private int turtlebot$headerDark(int original) { return 0xFF111522; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -1525469), require = 0, remap = false)
    private int turtlebot$headerAccent(int original) { return 0xFF9B86FF; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -13881287), require = 0, remap = false)
    private int turtlebot$headerBorder(int original) { return 0xFF414967; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -15592423), require = 0, remap = false)
    private int turtlebot$surface(int original) { return 0xE8171B2B; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -14934234), require = 0, remap = false)
    private int turtlebot$surfaceRaised(int original) { return 0xE9232940; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -1513761), require = 0, remap = false)
    private int turtlebot$text(int original) { return 0xFFF0EEFF; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -3562969), require = 0, remap = false)
    private int turtlebot$mutedText(int original) { return 0xFF9CA3BC; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -13949936), require = 0, remap = false)
    private int turtlebot$activeTab(int original) { return 0xFF343052; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -5724513), require = 0, remap = false)
    private int turtlebot$hover(int original) { return 0xFF7566C8; }

    @ModifyConstant(method = "method_25420", constant = @Constant(intValue = -15592422), require = 0, remap = false)
    private int turtlebot$deepSurface(int original) { return 0xFF111522; }
}
