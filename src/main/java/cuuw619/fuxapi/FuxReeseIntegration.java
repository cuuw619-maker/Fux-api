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
    private static final String RSO_ID = "reeses-sodium-options";

    private FuxReeseIntegration() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!ModList.get().isLoaded(RSO_ID)) return;
        Screen screen = event.getScreen();
        if (!isRsoScreen(screen)) return;

        // Reese's uses a 16:9 content area and a three-button toolbar below it.
        // Put Fux beside that toolbar instead of at the bottom edge where it could be clipped.
        int contentWidth = screen.width;
        if ((float) screen.width / Math.max(1, screen.height) > 16.0F / 9.0F) {
            contentWidth = (int) (screen.height * 16.0F / 9.0F);
        }
        int contentX = (screen.width - contentWidth) / 2;
        int tabX = contentX + contentWidth / 20 / 2;
        int tabWidth = contentWidth - contentWidth / 20;
        int toolbarY = screen.height / 8 * 7 + 5;
        int x = tabX + tabWidth - 4 * 65 - 3 * 4;

        event.addListener(Button.builder(Component.literal("Fux HUD"),
                        b -> Minecraft.getInstance().setScreen(new FuxHudEditorScreen(screen)))
                .bounds(x, toolbarY, 65, 20)
                .build());
    }

    private static boolean isRsoScreen(Screen screen) {
        Class<?> type = screen.getClass();
        for (int depth = 0; type != null && depth < 6; depth++, type = type.getSuperclass()) {
            if (RSO_SCREEN.equals(type.getName())) return true;
        }
        return false;
    }
}
