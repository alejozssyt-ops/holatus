package com.example.elytraarmor.mixin;

import com.example.elytraarmor.ElytraInventory;
import com.example.elytraarmor.ElytraSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Appends the elytra slot (last index, same on client and server) next to the chestplate slot. */
@Mixin(InventoryMenu.class)
public abstract class PlayerScreenHandlerMixin extends AbstractContainerMenu {
	// Chestplate slot is at (8, 26); the elytra slot goes right next to it. Adjust here if desired.
	@Unique private static final int ELYTRA_SLOT_X = 26;
	@Unique private static final int ELYTRA_SLOT_Y = 26;

	@Unique private Slot elytraarmor$slot;

	protected PlayerScreenHandlerMixin(MenuType<?> type, int containerId) {
		super(type, containerId);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void elytraarmor$addSlot(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
		this.elytraarmor$slot = this.addSlot(new ElytraSlot(new ElytraInventory(owner), ELYTRA_SLOT_X, ELYTRA_SLOT_Y));
	}

	/** Shift-clicking an elytra from the inventory sends it to the elytra slot if it is free. */
	@Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
	private void elytraarmor$quickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
		if (this.elytraarmor$slot == null || index < 0 || index >= this.slots.size()) {
			return;
		}
		Slot from = this.slots.get(index);
		if (from == this.elytraarmor$slot || !from.hasItem()) {
			return;
		}
		ItemStack stack = from.getItem();
		if (stack.is(Items.ELYTRA) && !this.elytraarmor$slot.hasItem()) {
			ItemStack moved = stack.copy();
			this.elytraarmor$slot.set(moved);
			from.set(ItemStack.EMPTY);
			cir.setReturnValue(moved.copy());
		}
	}
}
