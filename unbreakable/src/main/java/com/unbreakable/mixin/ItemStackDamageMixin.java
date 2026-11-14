package com.unbreakable.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels every durability loss. "hurtAndBreak" without a descriptor targets all of its
 * overloads (LivingEntity+slot/hand, ServerLevel+player+onBreak...), and the handler
 * takes no target arguments so it is valid for each one. Works for items from any mod
 * because it hooks ItemStack itself, not individual items.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackDamageMixin {
	@Shadow
	public abstract boolean isDamageableItem();

	@Inject(method = "hurtAndBreak", at = @At("HEAD"), cancellable = true)
	private void unbreakable$cancelDamage(CallbackInfo ci) {
		if (this.isDamageableItem()) {
			ci.cancel();
		}
	}
}
