package dev.rdh.sarcio.mixin.bugfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin {
    @WrapOperation(method = "deleteChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    private int sarcio$skipNullChatLine(ChatLine line, Operation<Integer> original) {
        return line == null ? -1 : original.call(line);
    }
}
