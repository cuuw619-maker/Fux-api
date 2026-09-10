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

    /** Scroll changes the persistent zoom target. Positive wheel input means zoom in. */
    public static void onMouseZoom(double wheel) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || wheel == 0.0) return;
        beginZoom(mc);
        zoomTarget = clamp(zoomTarget - (float) wheel * 0.075F, 0.12F, 1.0F);
    }

    private static void updateZoom(Minecraft mc) {
        if (mc.player == null || mc.options == null) return;
        boolean key = ZOOM.isDown() && mc.screen == null;
        if (key) {
            beginZoom(mc);
            // Holding Z enters the configured zoom level once; wheel input can then fine-tune it.
            if (Math.abs(zoomTarget - 1.0F) < 0.0001F) {
                zoomTarget = clamp((float) (FuxSettingsRegistry.ZOOM_FOV.get() / Math.max(1.0, zoomBaseFov)), 0.12F, 1.0F);
            }
        }
        if (!key && zoomBaseFov >= 0 && Math.abs(zoomTarget - 1.0F) < 0.0001F) {
            finishZoom(mc);
            return;
        }
        float speed = FuxSettingsRegistry.ANIMATIONS.get() ? 12.0F : 100.0F;
        zoomCurrent = FuxUiAnimation.smooth(zoomCurrent, zoomTarget, speed, 1.0F / 60.0F);
        if (zoomBaseFov >= 0) {
            int normal = zoomBaseFov;
            mc.options.fov().set(Math.max(10, Math.min(normal, Math.round(normal * zoomCurrent))));
        }
    }

    private static void beginZoom(Minecraft mc) {
        if (zoomBaseFov < 0) zoomBaseFov = mc.options.fov().get();
    }

    private static void finishZoom(Minecraft mc) {
        if (zoomBaseFov >= 0) mc.options.fov().set(zoomBaseFov);
        zoomCurrent = zoomTarget = 1.0F;
        zoomBaseFov = -1;
    }

    private static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }
}
