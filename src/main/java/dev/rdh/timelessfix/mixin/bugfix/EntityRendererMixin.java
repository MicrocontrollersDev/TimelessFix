package dev.rdh.timelessfix.mixin.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.rdh.timelessfix.TimelessFix;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Unique private boolean tf$renderingDebugCrosshair = false;

    @WrapMethod(method = "renderWorldDirections")
    private void tf$wrapDebugCrosshair(float partialTicks, Operation<Void> original) {
        if (!TimelessFix.CONFIG.modernParallax) {
            original.call(partialTicks);
            return;
        }

        this.tf$renderingDebugCrosshair = true;
        original.call(partialTicks);
        this.tf$renderingDebugCrosshair = false;
    }

    @ModifyExpressionValue(method = "orientCamera", at = @At(value = "CONSTANT", args = "floatValue=-0.1F"))
    private float tf$fixParallax(float original) {
        return !TimelessFix.CONFIG.modernParallax || this.tf$renderingDebugCrosshair ? original : 0.05F;
    }

    @WrapOperation(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 3))
    private int tf$offsetTranslucents(RenderGlobal instance, EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn, Operation<Integer> original) {
        GlStateManager.doPolygonOffset(-1.0F, -1.0F);
        GlStateManager.enablePolygonOffset();
        int ret = original.call(instance, blockLayerIn, partialTicks, pass, entityIn);
        GlStateManager.disablePolygonOffset();
        return ret;
    }
}
