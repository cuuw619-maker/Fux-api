package com.cuuw619.fuxapi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public final class FuxVelocityModule extends FuxModule {
    public FuxVelocityModule() { super("Velocity", "Combat"); }
    @Override public void onClientTick() {
        var player = Minecraft.getInstance().player;
        if (player == null || player.hurtTime <= 0) return;
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x * 0.35D, motion.y, motion.z * 0.35D);
    }
}
