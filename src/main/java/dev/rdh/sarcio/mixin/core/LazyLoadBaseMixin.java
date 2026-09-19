package dev.rdh.sarcio.mixin.core;

import net.minecraft.util.LazyLoadBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LazyLoadBase.class)
abstract class LazyLoadBaseMixin<T> {
	@Shadow private T value;
	@Shadow private boolean isLoaded;
	@Shadow protected abstract T load();

	/**
	 * @author rdh
	 * @reason vanilla publishes isLoaded before value, so a second thread can read a null value
	 */
	@SuppressWarnings("OverwriteModifiers")
	@Overwrite
	public synchronized T getValue() {
		if (!this.isLoaded) {
			this.value = this.load();
			this.isLoaded = true;
		}

		return this.value;
	}
}
