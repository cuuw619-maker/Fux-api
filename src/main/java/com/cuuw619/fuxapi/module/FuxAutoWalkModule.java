package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class FuxAutoWalkModule extends FuxModule {
    public FuxAutoWalkModule() { super("AutoWalk", "Movement"); }

    @Override public void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !player.isAlive() || mc.screen != null) return;
        player.zza = 1.0F;
    }
}
