package dev.rdh.sarcio.mixin.tweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    private IStream stream;

    @Inject(method = "initStream", at = @At("HEAD"), cancellable = true)
    private void sarcio$skipTwitchInit(CallbackInfo ci) {
        this.stream = new NullStream(null);
        ci.cancel();
    }

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
