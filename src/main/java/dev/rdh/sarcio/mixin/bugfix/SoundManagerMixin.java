package dev.rdh.sarcio.mixin.bugfix;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Redirect(
        method = "playSound",
        slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to play unknown soundEvent: {}")),
        at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Lorg/apache/logging/log4j/Marker;Ljava/lang/String;[Ljava/lang/Object;)V", remap = false)
    )
    private void sarcio$silenceUnknownSound(Logger logger, Marker marker, String message, Object[] args) {
    }

    @Unique
    private final Set<String> sarcio$pausedChannels = new HashSet<>();

    @WrapWithCondition(method = "pauseAllSounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;pause(Ljava/lang/String;)V"))
    private boolean sarcio$pauseOnlyPlaying(@Coerce SoundSystem instance, String sourcename) {
        return instance.playing(sourcename) && this.sarcio$pausedChannels.add(sourcename);
    }

    @WrapWithCondition(method = "resumeAllSounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;play(Ljava/lang/String;)V"))
    private boolean sarcio$resumeOnlyPaused(@Coerce SoundSystem instance, String sourcename) {
        return this.sarcio$pausedChannels.contains(sourcename);
    }

    @Inject(method = "resumeAllSounds", at = @At("TAIL"))
    private void sarcio$clearPausedChannels(CallbackInfo ci) {
        this.sarcio$pausedChannels.clear();
    }
}
