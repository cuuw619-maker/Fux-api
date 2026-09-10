package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;

public final class FuxNoFallModule extends FuxModule {
    public FuxNoFallModule() { super("NoFall", "Player"); }
    @Override public void onClientTick() {
        var player = Minecraft.getInstance().player;
        if (player != null && player.fallDistance > 2.0F) player.fallDistance = 0.0F;
    }
}
