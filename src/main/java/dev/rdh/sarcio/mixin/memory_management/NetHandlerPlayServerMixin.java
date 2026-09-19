package dev.rdh.sarcio.mixin.memory_management;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandlerPlayServer.class)
abstract class NetHandlerPlayServerMixin {
	@WrapMethod(method = "processVanilla250Packet")
	private void releaseCustomPayload(C17PacketCustomPayload packet, Operation<Void> original) {
		original.call(packet);
		PacketBuffer data = packet.getBufferData();
		if (data != null && data.refCnt() > 0) {
			data.release();
		}
	}
}
