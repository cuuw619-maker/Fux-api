package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;

public final class FuxFovModule extends FuxModule {
    private Integer previousFov;

    public FuxFovModule() { super("FOV", "Player"); }

    @Override protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        previousFov = mc.options.fov().get();
        mc.options.fov().set(100);
    }

    @Override protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (previousFov != null) mc.options.fov().set(previousFov);
        previousFov = null;
    }
}
