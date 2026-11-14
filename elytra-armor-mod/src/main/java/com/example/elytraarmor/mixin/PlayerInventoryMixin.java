package com.example.elytraarmor.mixin;

import com.example.elytraarmor.ElytraArmorMod;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** dropAll() is only called on death when keepInventory is off: drop the extra elytra with everything else. */
@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin {
	@Shadow @Final public Player player;

	@Inject(method = "dropAll", at = @At("HEAD"))
	private void elytraarmor$dropElytra(CallbackInfo ci) {
		ItemStack stack = ElytraArmorMod.getElytra(this.player);
		if (!stack.isEmpty()) {
			ElytraArmorMod.setElytra(this.player, ItemStack.EMPTY);
			this.player.drop(stack, true, false);
		}
	}
}
