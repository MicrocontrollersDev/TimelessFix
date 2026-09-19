package dev.rdh.sarcio;

public interface CompactableNibbleArray {
	byte[] sarcio$writableData();

	void sarcio$compact();
}
