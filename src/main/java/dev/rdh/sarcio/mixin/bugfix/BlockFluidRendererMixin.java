package dev.rdh.sarcio.mixin.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockFluidRenderer.class)
public class BlockFluidRendererMixin {
    @ModifyExpressionValue(method = "renderFluid", at = @At(value = "CONSTANT", args = "floatValue=0.001F"))
    private float sarcio$fixFluidGap(float original, @Local(argsOnly = true) IBlockState state) {
        return state.getBlock().getMaterial() == Material.water ? -original : original;
    }
}
