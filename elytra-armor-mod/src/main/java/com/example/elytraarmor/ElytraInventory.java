package com.example.elytraarmor;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** One-slot Container view over the player's elytra attachment (used by the extra slot). */
public final class ElytraInventory implements Container {
	private final Player player;

	public ElytraInventory(Player player) {
		this.player = player;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return ElytraArmorMod.getElytra(player).isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return ElytraArmorMod.getElytra(player);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack current = ElytraArmorMod.getElytra(player);
		if (current.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack out = current.split(amount);
		ElytraArmorMod.setElytra(player, current);
		return out;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack current = ElytraArmorMod.getElytra(player);
		ElytraArmorMod.setElytra(player, ItemStack.EMPTY);
		return current;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		ElytraArmorMod.setElytra(player, stack);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player p) {
		return true;
	}

	@Override
	public void clearContent() {
		ElytraArmorMod.setElytra(player, ItemStack.EMPTY);
	}
}
