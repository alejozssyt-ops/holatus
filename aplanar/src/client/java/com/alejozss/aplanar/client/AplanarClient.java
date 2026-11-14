package com.alejozss.aplanar.client;

import com.alejozss.aplanar.AplanarArea;
import com.alejozss.aplanar.AplanarConfig;
import com.alejozss.aplanar.AplanarMod;
import com.alejozss.aplanar.net.BreakAreaPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.glfw.GLFW;

public class AplanarClient implements ClientModInitializer {
	private static KeyMapping activateKey;
	private static int size;
	private static boolean attackWasDown;
	private static Selection current;
	private static BlockPos lastCenter;
	private static Direction lastFace;
	private static int lastSize = -1;

	@Override
	public void onInitializeClient() {
		AplanarConfig config = AplanarConfig.get();
		size = config.defaultSize;

		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(AplanarMod.MOD_ID, "main"));
		activateKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.aplanar.activate",
				InputConstants.Type.MOUSE,
				GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
				category));

		ClientTickEvents.END_CLIENT_TICK.register(AplanarClient::tick);
		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(AplanarMod.MOD_ID, "hud"),
				new AplanarHud());
		LevelRenderEvents.BEFORE_GIZMOS.register(context -> SelectionRenderer.render());
	}

	public static boolean isActive() {
		Minecraft client = Minecraft.getInstance();
		return activateKey != null
				&& activateKey.isDown()
				&& client.gui.screen() == null
				&& client.player != null
				&& client.level != null;
	}

	public static void scrollSize(double direction) {
		int step = size < 20 ? 2 : (size < 100 ? 6 : 10);
		int delta = direction > 0 ? step : -step;
		int max = AplanarConfig.get().maxSize;
		size = Math.max(1, Math.min(max, size + delta));
		if (size % 2 == 0) {
			size--;
		}
	}

	public static Selection getSelection() {
		return current;
	}

	public static int getSize() {
		return size;
	}

	private static void tick(Minecraft client) {
		if (!isActive()) {
			current = null;
			attackWasDown = client.options.keyAttack.isDown();
			return;
		}

		updateSelection(client);

		boolean attackDown = client.options.keyAttack.isDown();
		if (attackDown && !attackWasDown && current != null && current.blockCount() > 0) {
			ClientPlayNetworking.send(new BreakAreaPayload(current.center(), current.face(), current.size()));
		}
		attackWasDown = attackDown;
	}

	private static void updateSelection(Minecraft client) {
		if (!(client.hitResult instanceof BlockHitResult hit)) {
			current = null;
			lastCenter = null;
			lastSize = -1;
			return;
		}

		BlockPos center = hit.getBlockPos();
		Direction face = hit.getDirection();

		if (current == null || !center.equals(lastCenter) || face != lastFace || size != lastSize) {
			int blockCount = AplanarArea.countNonAir(client.level, center, face, size);
			current = new Selection(center.immutable(), face, size, blockCount);
			lastCenter = center.immutable();
			lastFace = face;
			lastSize = size;
		}
	}
}
