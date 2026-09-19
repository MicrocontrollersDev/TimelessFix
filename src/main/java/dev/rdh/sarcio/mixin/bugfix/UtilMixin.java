package dev.rdh.sarcio.mixin.bugfix;

import java.util.concurrent.FutureTask;

import net.minecraft.util.Util;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Util.class)
public class UtilMixin {
    @Inject(method = "runTask", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;fatal(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false), cancellable = true)
    private static <V> void sarcio$stopLogSpam(FutureTask<V> task, Logger logger, CallbackInfoReturnable<V> cir) {
        cir.setReturnValue(null);
    }
}
