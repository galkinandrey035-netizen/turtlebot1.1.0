package ru.turtlebot.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.util.BotLog;
import ru.turtlebot.util.Geo;

public final class FeedHandler {
    private TurtleEntity turtle;
    private int phase, ticks, attempts, countBefore, swapWaitTicks;
    private boolean wasInLove, swapRequested;
    public boolean outOfGrass;

    public void begin(TurtleEntity target) {
        turtle = target;
        phase = 0;
        ticks = 0;
        attempts = 0;
        countBefore = 0;
        swapWaitTicks = 0;
        wasInLove = false;
        swapRequested = false;
        outOfGrass = false;
    }

    public StepResult tick(MinecraftClient mc) {
        BotConfig cfg = ConfigManager.get();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) return StepResult.FAILED;
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
                if (!player.getMainHandStack().isOf(Items.SEAGRASS)) {
                    if (swapRequested) {
                        swapWaitTicks++;
                        if (swapWaitTicks >= 40) {
                            // Give the server inventory transaction time to settle before retrying.
                            swapRequested = false;
                            swapWaitTicks = 0;
                        }
                    } else {
                        if (!InventoryUtil.ensureSeagrassInHand(mc)) {
                            outOfGrass = true;
                            return StepResult.FAILED;
                        }
                        if (!player.getMainHandStack().isOf(Items.SEAGRASS)) {
                            swapRequested = true;
                            swapWaitTicks = 0;
                        }
                    }
                } else {
                    swapRequested = false;
                    swapWaitTicks = 0;
                    if (ticks >= 3) {
                        phase = 1;
                        ticks = 0;
                    }
                }
                if (ticks > 80) {
                    BotLog.warn("Не удалось выбрать морскую траву в основную руку");
                    return StepResult.FAILED;
                }
            }
            case 1 -> {
                Geo.lookAt(player, turtle.getPos().add(0, turtle.getHeight() * 0.55, 0));
                if (ticks < 2) break;
                if (!player.getMainHandStack().isOf(Items.SEAGRASS)) {
                    phase = 0;
                    ticks = 0;
                    swapRequested = false;
                    swapWaitTicks = 0;
                    break;
                }

                Vec3d eye = player.getEyePos();
                Vec3d end = eye.add(player.getRotationVec(1.0F)
                        .multiply(player.getEntityInteractionRange()));
                var hitPosition = turtle.getBoundingBox().expand(0.1).raycast(eye, end);
                if (hitPosition.isEmpty()) {
                    // Do not send an entity interaction unless the player's view actually intersects the turtle.
                    ticks = 1;
                    break;
                }

                countBefore = player.getMainHandStack().getCount();
                wasInLove = turtle.isInLove();
                EntityHitResult hit = new EntityHitResult(turtle, hitPosition.get());

                ActionResult result = mc.interactionManager.interactEntityAtLocation(
                        player, turtle, hit, Hand.MAIN_HAND);
                if (result == ActionResult.PASS) {
                    // Match vanilla's right-click fallback: generic entity interaction after interact-at returns PASS.
                    result = mc.interactionManager.interactEntity(player, turtle, Hand.MAIN_HAND);
                }
                if (result.isAccepted()) player.swingHand(Hand.MAIN_HAND);

                if (!result.isAccepted()) {
                    attempts++;
                    BotLog.debug("Сервер/клиент не принял взаимодействие с черепахой ("
                            + result + "), попытка " + attempts + "/" + cfg.feedAttemptsLimit);
                    if (attempts >= cfg.feedAttemptsLimit) return StepResult.FAILED;
                    ticks = 0;
                    break;
                }
                phase = 2;
                ticks = 0;
            }
            case 2 -> {
                ItemStack hand = player.getMainHandStack();
                int now = hand.isOf(Items.SEAGRASS) ? hand.getCount() : 0;

                // In survival/adventure, a reduced stack is the authoritative inventory update
                // sent after the server processes the feeding interaction.
                if (now < countBefore || (!wasInLove && turtle.isInLove())) return StepResult.DONE;

                // Creative mode does not consume the item; wait for the turtle's love state to replicate.
                if (player.getAbilities().creativeMode && ticks >= cfg.feedConfirmTicks) {
                    if (turtle.isInLove()) return StepResult.DONE;
                }

                if (ticks >= cfg.feedConfirmTicks) {
                    attempts++;
                    if (attempts >= cfg.feedAttemptsLimit) return StepResult.FAILED;
                    BotLog.debug("Кормление не подтверждено сервером, повтор "
                            + (attempts + 1) + "/" + cfg.feedAttemptsLimit);
                    phase = 1;
                    ticks = 0;
                }
            }
            default -> { }
        }
        return StepResult.RUNNING;
    }
}
