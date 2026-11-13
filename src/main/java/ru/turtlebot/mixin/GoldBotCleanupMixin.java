package ru.turtlebot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Disables the explicitly unwanted GoldBot visuals after its own config loads.
 * The original GoldBot classes and mining logic are not modified.
 */
@Pseudo
@Mixin(targets = "dev.goldbot.GoldBotMod")
public abstract class GoldBotCleanupMixin {
    @Inject(method = "onInitializeClient", at = @At("TAIL"), remap = false)
    private void turtlebot$disableRemovedFeatures(CallbackInfo ci) {
        try {
            Class<?> modClass = Class.forName("dev.goldbot.GoldBotMod");
            Object config = modClass.getField("cfg").get(null);
            if (config == null) return;

            String[] disabled = {
                    "chinaHat",
                    "customModels",
                    "hitBubbles",
                    "wings",
                    "trailLine",
                    "cosmetics"
            };
            for (String fieldName : disabled) {
                Field field = config.getClass().getField(fieldName);
                field.setBoolean(config, false);
            }

            Method save = config.getClass().getMethod("save");
            save.invoke(config);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            System.err.println("[TurtleBot] Не удалось отключить удалённые функции GoldBot: "
                    + exception.getMessage());
        }
    }
}
