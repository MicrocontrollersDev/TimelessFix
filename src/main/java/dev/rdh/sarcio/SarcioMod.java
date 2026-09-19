package dev.rdh.sarcio;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SarcioMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final SarcioConfig CONFIG = SarcioConfig.load();

	@Override
	public void onInitialize() {
		LOGGER.info("Sarcio initializing");
		if (CONFIG.releaseCrashReserve) {
			Minecraft.memoryReserve = new byte[0];
		}

		boolean hasCeleritas;
		try {
			Class.forName("org.taumc.celeritas.api.OptionGUIConstructionEvent");
			hasCeleritas = true;
		} catch (ClassNotFoundException e) {
			hasCeleritas = false;
		}

		if (hasCeleritas) {
			try {
				Class.forName("dev.rdh.sarcio.celeritas.CeleritasConfigIntegration")
					.getMethod("register").invoke(null);
			} catch (ReflectiveOperationException exception) {
				LOGGER.warn("Could not register Sarcio options with Celeritas", exception);
			}
		}
	}
}
