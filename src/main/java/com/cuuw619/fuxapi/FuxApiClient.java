package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxConfig;
import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.lwjgl.glfw.GLFW;

@Mod(value = FuxApi.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT)
public final class FuxApiClient {
    public static final String KEY_CATEGORY = "key.categories.fuxapi";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key.fuxapi.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KEY_CATEGORY);

    public FuxApiClient(ModContainer container) {
        FuxConfig.load();
        FuxSettingsRegistry.load();
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) -> new FuxSettingsScreen(parent));
        if (ModList.get().isLoaded("sodium")) FuxApi.LOGGER.info("Sodium detected; Fux settings are compatibility-safe and independent.");
    }
    @SubscribeEvent public static void registerKeys(RegisterKeyMappingsEvent event) { event.register(OPEN_MENU); }
    @SubscribeEvent public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        while (OPEN_MENU.consumeClick()) if (minecraft.screen == null) minecraft.setScreen(new FuxMenuScreen(null));
    }
}
