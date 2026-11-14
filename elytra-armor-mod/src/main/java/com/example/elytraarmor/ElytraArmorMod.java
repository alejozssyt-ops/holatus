package com.example.elytraarmor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The extra elytra is stored as a Fabric data attachment on the player:
 * persistent (saved with the player), copied on death/respawn/dimension change,
 * and synced to the owning client. No new item is registered.
 */
public class ElytraArmorMod implements ModInitializer {
	public static final String MOD_ID = "elytraarmor";

	public static final AttachmentType<ItemStack> ELYTRA = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(MOD_ID, "elytra_slot"),
			builder -> builder
					.persistent(ItemStack.OPTIONAL_CODEC)
					.copyOnDeath()
					.syncWith(ItemStack.OPTIONAL_STREAM_CODEC, AttachmentSyncPredicate.targetOnly()));

	@Override
	public void onInitialize() {
		// Attachment is registered statically; nothing else to do.
	}

	public static ItemStack getElytra(Player player) {
		return player.getAttachedOrElse(ELYTRA, ItemStack.EMPTY);
	}

	public static void setElytra(Player player, ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			player.removeAttached(ELYTRA);
		} else {
			player.setAttached(ELYTRA, stack);
		}
	}
}
