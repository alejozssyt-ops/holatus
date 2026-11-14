package com.alejozss.aplanar;

import com.alejozss.aplanar.net.BreakAreaPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AplanarMod implements ModInitializer {
	public static final String MOD_ID = "aplanar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(BreakAreaPayload.ID, BreakAreaPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(BreakAreaPayload.ID, (payload, context) -> {
			ServerPlayer player = context.player();
			if (player.isSpectator()) {
				return;
			}

			int size = Math.max(1, Math.min(AplanarConfig.MAX_SIZE, payload.size()));
			if (size % 2 == 0) {
				size--;
			}
			if (size < 1) {
				size = 1;
			}
			AplanarMining.start(player, payload.center(), payload.face(), size);
		});

		ServerTickEvents.END_SERVER_TICK.register(AplanarMining::tick);

		LOGGER.info("Aplanar cargado");
	}
}
