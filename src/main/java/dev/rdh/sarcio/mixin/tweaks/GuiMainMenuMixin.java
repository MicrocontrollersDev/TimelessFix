package dev.rdh.sarcio.mixin.tweaks;

import dev.rdh.sarcio.SarcioMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {
    @Shadow
    private GuiButton realmsButton;

    @Shadow
    private boolean f_45412987;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void sarcio$disableRealms(CallbackInfo ci) {
        this.f_45412987 = SarcioMod.CONFIG.disableRealms;
    }

    @Inject(method = "addSingleplayerMultiplayerButtons", at = @At(value = "RETURN"))
    private void sarcio$disableRealmsAgain(int i, int j, CallbackInfo ci) {
        this.realmsButton.visible = !SarcioMod.CONFIG.disableRealms;
    }
}
