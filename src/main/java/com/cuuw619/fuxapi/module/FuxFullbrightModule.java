package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;

public final class FuxFullbrightModule extends FuxModule {
    private Double previousGamma;

    public FuxFullbrightModule() { super("Fullbright", "Render"); }

    @Override protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        previousGamma = mc.options.gamma().get();
        mc.options.gamma().set(1.0D);
    }

    @Override protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (previousGamma != null) mc.options.gamma().set(previousGamma);
        previousGamma = null;
    }
}
