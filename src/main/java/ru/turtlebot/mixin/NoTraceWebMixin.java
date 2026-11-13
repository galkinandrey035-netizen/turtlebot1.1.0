package ru.turtlebot.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.NoTraceModule;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class NoTraceWebMixin {
    @Inject(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At("HEAD"), cancellable = true)
    private void turtlebot$ignoreCobwebOutline(BlockView world, BlockPos pos, ShapeContext context,
            CallbackInfoReturnable<net.minecraft.util.shape.VoxelShape> cir) {
        if (NoTraceModule.webEnabled() &&
                ((BlockState) (Object) this).isOf(Blocks.COBWEB)) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }
}