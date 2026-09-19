package dev.rdh.sarcio.mixin.tweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.stream.IStream;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @WrapWithCondition(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderStreamIndicator(F)V"))
    private boolean sarcio$removeStreamIndicator(EntityRenderer instance, float partialTicks) {
        return false;
    }

    @WrapWithCondition(method = "runGameLoop", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;m_34249743()V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;m_06861983()V")
    })
    private boolean sarcio$removeStreamCalls(IStream instance) {
        return false;
    }
}
