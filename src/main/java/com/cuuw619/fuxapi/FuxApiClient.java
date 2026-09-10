package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxConfig;
import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.cuuw619.fuxapi.module.FuxModuleManager;
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
    public static final KeyMapping ZOOM = new KeyMapping("key.fuxapi.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, KEY_CATEGORY);

    private static float zoomCurrent = 1.0F;
    private static int zoomBaseFov = -1;

    public FuxApiClient(ModContainer container) {
        FuxConfig.load();
        FuxSettingsRegistry.load();
        FuxHudLayout.load();
        FuxModuleManager.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) -> new FuxSettingsScreen(parent));
        if (ModList.get().isLoaded("sodium")) FuxApi.LOGGER.info("Sodium detected; Fux settings are compatibility-safe and independent.");
    }

    @SubscribeEvent public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
        event.register(ZOOM);
    }

    @SubscribeEvent public static void onClientTick(ClientTickEvent.Post event) {
        FuxModuleManager.tick();
        Minecraft minecraft = Minecraft.getInstance();
        while (OPEN_MENU.consumeClick()) {
            if (minecraft.screen == null) minecraft.setScreen(new FuxPulseMenuScreen(null));
        }
        updateZoom(minecraft);
    }

    private static void updateZoom(Minecraft mc) {
        if (mc.player == null || mc.options == null) return;
        boolean active = ZOOM.isDown() && mc.screen == null;
        int normal = zoomBaseFov >= 0 ? zoomBaseFov : mc.options.fov().get();
        if (active && zoomBaseFov < 0) zoomBaseFov = mc.options.fov().get();
        if (!active && zoomBaseFov < 0) return;
        normal = zoomBaseFov >= 0 ? zoomBaseFov : normal;
        float target = active ? (float) (FuxSettingsRegistry.ZOOM_FOV.get() / Math.max(1.0, normal)) : 1.0F;
        if (!FuxSettingsRegistry.ANIMATIONS.get()) zoomCurrent = target;
        else zoomCurrent += (target - zoomCurrent) * 0.22F;
        int nextFov = Math.max(10, Math.min(normal, Math.round(normal * zoomCurrent)));
        mc.options.fov().set(nextFov);
        if (!active && Math.abs(zoomCurrent - 1.0F) < 0.01F) {
            mc.options.fov().set(normal);
            zoomCurrent = 1.0F;
            zoomBaseFov = -1;
        }
    }
}
