package dev.rdh.sarcio.mixin.bugfix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontRenderer.class)
public class FontRendererMixin {
    @Inject(method = {"setUnicodeFlag", "onResourceManagerReload"}, at = @At("TAIL"))
    private void sarcio$rewrapChat(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.ingameGUI != null && mc.fontRendererObj == (Object) this) {
            mc.ingameGUI.getChatGUI().refreshChat();
        }
    }
}
