package dev.rdh.sarcio.mixin.bugfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
    @Shadow
    protected boolean dragSplitting;

    @Shadow
    private int dragSplittingLimit;

    @Shadow
    private int dragSplittingRemnant;

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    void sarcio$closeMenu(Minecraft instance, GuiScreen guiScreenIn, Operation<Void> original) {
        instance.thePlayer.closeScreen();
    }

    @Inject(method = "updateDragSplitting", at = @At("TAIL"))
    private void sarcio$fixCloneDragCount(CallbackInfo ci) {
        ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
        if (itemStack != null && this.dragSplitting && this.dragSplittingLimit == 2) {
            this.dragSplittingRemnant = itemStack.getMaxStackSize();
        }
    }
}
