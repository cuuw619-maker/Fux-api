package com.cuuw619.fuxapi.mixin;

import com.cuuw619.fuxapi.FuxApiClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class ZoomScrollMixin {
    @Inject(method = "onScroll", at = @At("HEAD"))
    private void fux$scrollZoom(long window, double xOffset, double yOffset, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (window != mc.getWindow().getWindow() || mc.screen != null || mc.player == null) return;
        FuxApiClient.onMouseZoom(yOffset);
    }
}
