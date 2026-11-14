package com.unbreakable;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnbreakableMod implements ModInitializer {
	public static final String MOD_ID = "unbreakable";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Unbreakable] All items are now unbreakable.");
	}
}
