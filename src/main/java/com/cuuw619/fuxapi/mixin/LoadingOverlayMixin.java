package com.cuuw619.fuxapi.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    private static final long START = System.currentTimeMillis();

    @Inject(method = "render", at = @At("TAIL"))
    private void fux$renderBlackLoading(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        long elapsed = System.currentTimeMillis() - START;
        float pulse = 0.5F + 0.5F * (float)Math.sin(elapsed / 420.0D);
        int w = graphics.guiWidth();
        int h = graphics.guiHeight();

        graphics.fill(0, 0, w, h, 0xFF030405);
        int cx = w / 2;
        int cy = h / 2;
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, "FUX", cx, cy - 34, 0xFFFFFFFF);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, "CLIENT", cx, cy - 17, 0xFF8B5CF6);

        int barW = Math.min(260, Math.max(120, w / 3));
        int barX = cx - barW / 2;
        int barY = cy + 18;
        graphics.fill(barX, barY, barX + barW, barY + 3, 0xFF20242B);
        int fill = (int)(barW * (0.18F + pulse * 0.64F));
        graphics.fill(barX, barY, barX + fill, barY + 3, 0xFF8B5CF6);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, "Initializing client", cx, barY + 14, 0xFF6E737C);
    }
}
