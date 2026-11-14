package com.alejozss.aplanar.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class AplanarHud implements HudElement {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BACKGROUND = 0x90000000;

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		Selection selection = AplanarClient.getSelection();
		if (selection == null || !AplanarClient.isActive()) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		Component line1 = Component.translatable("aplanar.hud.size", selection.size(), selection.size());
		Component line2 = Component.translatable("aplanar.hud.blocks", selection.blockCount());

		int centerX = graphics.guiWidth() / 2;
		int top = graphics.guiHeight() / 2 + 24;
		int width = Math.max(client.font.width(line1), client.font.width(line2));
		int left = centerX - width / 2 - 4;

		graphics.fill(left, top - 3, left + width + 8, top + 21, BACKGROUND);
		graphics.centeredText(client.font, line1, centerX, top, WHITE);
		graphics.centeredText(client.font, line2, centerX, top + 11, WHITE);
	}
}
