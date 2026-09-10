package com.cuuw619.fuxapi.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables Minecraft's post-process blur used by vanilla GUI screens.
 * The normal translucent/dimmed screen background remains intact.
 */
@Mixin(Screen.class)
public abstract class NoBlurScreenMixin {
    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
    private void fux$disableBlur(GuiGraphics graphics, CallbackInfo ci) {
        ci.cancel();
    }
}
