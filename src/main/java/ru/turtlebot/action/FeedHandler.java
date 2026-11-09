package ru.turtlebot.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.util.BotLog;
import ru.turtlebot.util.Geo;

public final class FeedHandler {
    private TurtleEntity turtle;
    private int phase, ticks, attempts, countBefore;
    private boolean wasInLove;
    public boolean outOfGrass;

    public void begin(TurtleEntity target) {
        turtle = target;
        phase = 0;
        ticks = 0;
        attempts = 0;
        outOfGrass = false;
    }

    public StepResult tick(MinecraftClient mc) {
        BotConfig cfg = ConfigManager.get();
        ClientPlayerEntity player = mc.player;
        if (turtle == null || !turtle.isAlive() || turtle.isRemoved()) return StepResult.FAILED;
        if (turtle.isBaby()) {
            BotLog.warn("Цель оказалась детёнышем — кормление отменено");
            return StepResult.FAILED;
        }
        if (turtle.hasEgg()) {
            BotLog.info("Черепаха с кладкой — кормление пропущено");
            return StepResult.FAILED;
        }
        if (Geo.distToBox(player.getEyePos(), turtle.getBoundingBox()) > cfg.feedReach + 0.6) {
            return StepResult.NEED_APPROACH;
        }

        ticks++;
        switch (phase) {
            case 0 -> {
                if (!InventoryUtil.ensureSeagrassInHand(mc)) {
                    outOfGrass = true;
                    return StepResult.FAILED;
                }
                if (player.getMainHandStack().isOf(Items.SEAGRASS) && ticks >= 3) {
                    phase = 1;
                    ticks = 0;
                } else if (ticks > 20) {
                    return StepResult.FAILED;
                }
            }
            case 1 -> {
                Geo.lookAt(player, turtle.getPos().add(0, turtle.getHeight() / 2.0, 0));
                if (ticks < 2) break;
                if (!player.getMainHandStack().isOf(Items.SEAGRASS)) {
                    phase = 0;
                    ticks = 0;
                    break;
                }
                countBefore = player.getMainHandStack().getCount();
                wasInLove = turtle.isInLove();
                var result = mc.interactionManager.interactEntity(player, turtle, Hand.MAIN_HAND);
                player.swingHand(Hand.MAIN_HAND);
                if (!result.isAccepted()) {
                    attempts++;
                    if (attempts >= cfg.feedAttemptsLimit) return StepResult.FAILED;
                    ticks = 0;
                    break;
                }
                phase = 2;
                ticks = 0;
            }
            case 2 -> {
                ItemStack s = player.getMainHandStack();
                int now = s.isOf(Items.SEAGRASS) ? s.getCount() : 0;
                if (now < countBefore || (!wasInLove && turtle.isInLove())) return StepResult.DONE;
                // In creative mode the stack is not consumed; interaction was accepted in phase 1.
                if (player.getAbilities().creativeMode && ticks >= 5) return StepResult.DONE;
                if (ticks >= cfg.feedConfirmTicks) {
                    attempts++;
                    if (attempts >= cfg.feedAttemptsLimit) return StepResult.FAILED;
                    BotLog.debug("Кормление не подтверждено, повтор " + (attempts + 1) + "/" + cfg.feedAttemptsLimit);
                    phase = 1;
                    ticks = 0;
                }
            }
            default -> { }
        }
        return StepResult.RUNNING;
    }
}
