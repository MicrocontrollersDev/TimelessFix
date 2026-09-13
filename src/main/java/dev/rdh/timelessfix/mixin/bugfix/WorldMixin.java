package dev.rdh.timelessfix.mixin.bugfix;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public abstract boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty);
    @Unique private int tf$range = 17;
    @Unique private static final Set<Block> tf$ignoredBlock = Sets.newHashSet(Blocks.glass, Blocks.glass_pane, Blocks.stained_glass, Blocks.stained_glass_pane, Blocks.iron_bars);

    @Inject(method = "checkLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 0))
    private void tf$updateRange(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.tf$range = this.isAreaLoaded(pos, 18, false) ? 17 : 15;
    }

    @ModifyExpressionValue(method  = "checkLightFor", at = @At(value = "CONSTANT", args = "intValue=17", ordinal = 0))
    private int tf$setStaticRange(int original) {
        return 16;
    }

    @ModifyExpressionValue(method  = "checkLightFor", at = {@At(value = "CONSTANT", args = "intValue=17", ordinal = 1), @At(value = "CONSTANT", args = "intValue=17", ordinal = 2)})
    private int setVariableRange(int original) {
        return this.tf$range;
    }

    @WrapOperation(method = "rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getCollisionBoundingBox(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/AxisAlignedBB;"))
    private AxisAlignedBB tf$ignoreSpecialBlocks(Block instance, World worldIn, BlockPos pos, IBlockState state, Operation<AxisAlignedBB> original) {
        return tf$ignoredBlock.contains(state.getBlock()) ? null : original.call(instance, worldIn, pos, state);
    }
}
