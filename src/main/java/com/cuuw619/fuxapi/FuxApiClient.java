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
    private static float zoomTarget = 1.0F;
    private static int zoomBaseFov = -1;

    public FuxApiClient(ModContainer container) {
        FuxConfig.load();
        FuxSettingsRegistry.load();
        FuxHudLayout.load();
        FuxModuleManager.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) -> new FuxSettingsScreen(parent));
        if (ModList.get().isLoaded("sodium")) FuxApi.LOGGER.info("Sodium detected; Fux visual layer enabled.");
    }

    @SubscribeEvent public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
        event.register(ZOOM);
    }

    @SubscribeEvent public static void onClientTick(ClientTickEvent.Post event) {
        FuxModuleManager.tick();
        Minecraft mc = Minecraft.getInstance();
        while (OPEN_MENU.consumeClick()) if (mc.screen == null) mc.setScreen(new FuxPulseMenuScreen(null));
        updateZoom(mc);
    }

    public static void onMouseZoom(double wheel) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (zoomBaseFov < 0) zoomBaseFov = mc.options.fov().get();
        zoomTarget = clamp(zoomTarget - (float) wheel * 0.055F, 0.20F, 1.0F);
    }

    private static void updateZoom(Minecraft mc) {
        if (mc.player == null || mc.options == null) return;
        boolean key = ZOOM.isDown() && mc.screen == null;
        if (key && zoomBaseFov < 0) zoomBaseFov = mc.options.fov().get();
        if (key) zoomTarget = (float) (FuxSettingsRegistry.ZOOM_FOV.get() / Math.max(1.0, zoomBaseFov));
        if (!key && zoomBaseFov >= 0 && zoomTarget >= 0.999F) {
            zoomTarget = 1.0F;
        }
        float factor = FuxSettingsRegistry.ANIMATIONS.get() ? 0.105F : 1.0F;
        zoomCurrent += (zoomTarget - zoomCurrent) * factor;
        if (zoomBaseFov >= 0) {
            int normal = zoomBaseFov;
            mc.options.fov().set(Math.max(10, Math.min(normal, Math.round(normal * zoomCurrent))));
            if (!key && zoomTarget >= 0.999F && Math.abs(zoomCurrent - 1.0F) < 0.002F) {
                mc.options.fov().set(normal);
                zoomCurrent = zoomTarget = 1.0F;
                zoomBaseFov = -1;
            }
        }
    }

    private static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }
}
