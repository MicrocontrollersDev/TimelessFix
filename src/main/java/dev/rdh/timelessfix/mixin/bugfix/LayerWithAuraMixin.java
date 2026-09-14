package dev.rdh.timelessfix.mixin.bugfix;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import net.minecraft.entity.boss.EntityWither;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerWitherAura.class)
public class LayerWithAuraMixin {
    @Inject(method = "doRenderLayer(Lnet/minecraft/entity/boss/EntityWither;FFFFFFF)V", at = @At("RETURN"))
    private void tf$fixDepth(EntityWither entitylivingbaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci) {
        GlStateManager.depthMask(true);
    }
}
