package com.example.elytraarmor.mixin;

import com.example.elytraarmor.ElytraArmorMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The extra elytra is exposed to vanilla as the player's (otherwise unused) BODY equipment slot.
 * Vanilla's own gliding code (canGlide, gliding durability damage, break handling) then
 * works unchanged on it, in parallel with whatever is in the CHEST slot.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	/** Players: BODY slot reads the elytra attachment. */
	@Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
	private void elytraarmor$bodySlot(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
		if (slot == EquipmentSlot.BODY && (Object) this instanceof Player player) {
			cir.setReturnValue(ElytraArmorMod.getElytra(player));
		}
	}

	/** An usable elytra in the BODY slot counts as a glider (same usability rule as vanilla). */
	@Inject(method = "canGlideUsing", at = @At("RETURN"), cancellable = true)
	private static void elytraarmor$glideWithBody(ItemStack stack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() && slot == EquipmentSlot.BODY && stack.is(Items.ELYTRA)
				&& stack.getDamageValue() < stack.getMaxDamage() - 1) {
			cir.setReturnValue(true);
		}
	}

	/** Durability is mutated in place by vanilla; re-set the attachment periodically so it syncs to the client. */
	@Inject(method = "updateFallFlying", at = @At("TAIL"))
	private void elytraarmor$syncDurability(CallbackInfo ci) {
		if ((Object) this instanceof ServerPlayer player && player.getFallFlyingTicks() % 10 == 0) {
			ItemStack stack = ElytraArmorMod.getElytra(player);
			ElytraArmorMod.setElytra(player, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
		}
	}
}
