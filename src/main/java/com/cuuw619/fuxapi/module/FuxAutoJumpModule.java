package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;

public final class FuxAutoJumpModule extends FuxModule {
    public FuxAutoJumpModule() { super("AutoJump", "Movement"); }

    @Override protected void onEnable() {
        Minecraft.getInstance().options.autoJump().set(true);
    }

    @Override protected void onDisable() {
        Minecraft.getInstance().options.autoJump().set(false);
    }
}
