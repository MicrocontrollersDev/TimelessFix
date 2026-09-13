package dev.rdh.timelessfix.mixin.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.rdh.timelessfix.TimelessFix;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
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

    @WrapOperation(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    private MovingObjectPosition tf$ignoreCollisionlessBlocks(WorldClient instance, Vec3 vec31, Vec3 vec32, Operation<MovingObjectPosition> original) {
        return instance.rayTraceBlocks(vec31, vec32, false, true, true);
    }
}
