package com.example.elytraarmor;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Armor-style slot that only accepts vanilla elytra. */
public class ElytraSlot extends Slot {
	private static final Identifier GHOST_ICON = Identifier.withDefaultNamespace("container/slot/chestplate");

	public ElytraSlot(Container container, int x, int y) {
		super(container, 0, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.is(Items.ELYTRA);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public Identifier getNoItemIcon() {
		return GHOST_ICON;
	}
}
