package com.cuuw619.fuxapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class FuxReeseIntegration {
    private static final String RSO_SCREEN = "me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen";
    private FuxReeseIntegration() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!ModList.get().isLoaded("reeses-sodium-options") && !ModList.get().isLoaded("reeses-sodium-options-neoforge")) return;
        Screen screen = event.getScreen();
        if (!isRsoScreen(screen)) return;
        int x = Math.max(8, screen.width - 92);
        int y = Math.max(8, screen.height - 32);
        event.addListener(Button.builder(Component.literal("Fux HUD"), b -> Minecraft.getInstance().setScreen(new FuxHudEditorScreen(screen)))
                .bounds(x, y, 84, 22).build());
    }

    private static boolean isRsoScreen(Screen screen) {
        return screen.getClass().getName().equals(RSO_SCREEN)
                || screen.getClass().getSuperclass() != null && screen.getClass().getSuperclass().getName().equals(RSO_SCREEN);
    }
}
