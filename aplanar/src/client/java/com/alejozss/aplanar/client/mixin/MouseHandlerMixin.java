package com.alejozss.aplanar.client.mixin;

import com.alejozss.aplanar.client.AplanarClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Inject(method = "onScroll(JDD)V", at = @At("HEAD"), cancellable = true)
	private void aplanar$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
		if (yOffset != 0.0 && AplanarClient.isActive()) {
			AplanarClient.scrollSize(yOffset);
			ci.cancel();
		}
	}
}
