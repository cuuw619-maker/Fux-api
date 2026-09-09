package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class FuxSprintModule extends FuxModule {
    public FuxSprintModule() { super("Sprint", "Movement"); }

    @Override public void onClientTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) return;
        if (player.zza > 0.0F && !player.isSprinting() && !player.isUsingItem()) {
            player.setSprinting(true);
        }
    }
}
