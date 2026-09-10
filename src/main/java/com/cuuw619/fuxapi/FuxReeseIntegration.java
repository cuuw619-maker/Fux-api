package com.cuuw619.fuxapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class FuxReeseIntegration {
    private FuxReeseIntegration() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        String name = screen.getClass().getName();
        if (!name.equals("me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen")) return;

        int x = Math.max(4, screen.width - 150);
        int y = Math.max(4, screen.height - 28);
        Button button = Button.builder(ComponentText("Fux HUD"), b -> Minecraft.getInstance().setScreen(new FuxHudEditorScreen(screen)))
                .bounds(x, y, 68, 20)
                .build();
        event.addListener(button);
    }

    private static net.minecraft.network.chat.Component ComponentText(String text) {
        return net.minecraft.network.chat.Component.literal(text);
    }
}
