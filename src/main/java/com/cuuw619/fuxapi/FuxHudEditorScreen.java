package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class FuxHudEditorScreen extends Screen {
    private final Screen parent;
    private String dragging;
    private float dragOffsetX, dragOffsetY;

    public FuxHudEditorScreen(Screen parent) {
        super(Component.literal("Fux HUD Editor"));
        this.parent = parent;
    }

    @Override protected void init() {
        addRenderableWidget(Button.builder(Component.literal("Reset layout"), b -> FuxHudLayout.reset())
                .bounds(width / 2 - 145, height - 34, 90, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(width / 2 + 55, height - 34, 90, 20).build());
    }

    @Override public void render(GuiGraphics g, int mx, int my, float pt) {
        g.fill(0, 0, width, height, 0xD9000000);
        int accent = accent();
        g.drawCenteredString(font, Component.literal("FUX HUD EDITOR"), width / 2, 18, 0xFFFFFFFF);
        g.drawCenteredString(font, Component.literal("Drag the elements. Positions are saved automatically."), width / 2, 34, 0xFF777D88);
        drawGrid(g);
        drawCompass(g, accent);
        drawInfo(g, accent);
        drawTarget(g, accent);
        super.render(g, mx, my, pt);
    }

    private void drawGrid(GuiGraphics g) {
        for (int x = 0; x < width; x += 40) g.fill(x, 0, x + 1, height, 0x182A2F38);
        for (int y = 0; y < height; y += 40) g.fill(0, y, width, y + 1, 0x182A2F38);
    }

    private void drawCompass(GuiGraphics g, int accent) {
        int cx = Math.round(FuxHudLayout.data().compassX * width);
        int cy = Math.round(FuxHudLayout.data().compassY * height);
        int w = Math.min(720, width - 80);
        g.fill(cx - w / 2, cy, cx + w / 2, cy + 42, 0x30101016);
        g.fill(cx - w / 2, cy + 41, cx + w / 2, cy + 43, accent);
        g.drawCenteredString(font, "COMPASS", cx, cy + 15, 0xFFFFFFFF);
        label(g, "Compass", cx, cy - 13, accent);
    }

    private void drawInfo(GuiGraphics g, int accent) {
        int x = Math.round(FuxHudLayout.data().infoX * width);
        int y = Math.round(FuxHudLayout.data().infoY * height);
        g.drawString(font, "144 FPS", x - 65, y, 0xFFFFFFFF, false);
        g.drawString(font, "12:34:56", x - 65, y + 16, accent, false);
        label(g, "FPS / Clock", x - 32, y - 13, accent);
    }

    private void drawTarget(GuiGraphics g, int accent) {
        int x = Math.round(FuxHudLayout.data().targetEspX * width);
        int y = Math.round(FuxHudLayout.data().targetEspY * height);
        int s = 54;
        g.fill(x - s / 2, y - s / 2, x + s / 2, y + s / 2, 0x24101016);
        g.fill(x - s / 2, y - s / 2, x - s / 2 + 2, y + s / 2, accent);
        g.drawCenteredString(font, "TARGET ESP", x, y - 4, 0xFFFFFFFF);
        label(g, "Target ESP", x, y - s / 2 - 13, accent);
    }

    private void label(GuiGraphics g, String text, int x, int y, int accent) {
        int w = font.width(text) + 10;
        g.fill(x - w / 2, y - 2, x + w / 2, y + 12, 0xC0101218);
        g.drawCenteredString(font, Component.literal(text), x, y, accent);
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return super.mouseClicked(mx, my, button);
        if (hitCompass(mx, my)) { begin("compass", mx, my); return true; }
        if (hitInfo(mx, my)) { begin("info", mx, my); return true; }
        if (hitTarget(mx, my)) { begin("targetEsp", mx, my); return true; }
        return super.mouseClicked(mx, my, button);
    }

    private void begin(String id, double mx, double my) {
        dragging = id;
        FuxHudLayout.Data d = FuxHudLayout.data();
        float x = switch (id) { case "compass" -> d.compassX; case "info" -> d.infoX; default -> d.targetEspX; } * width;
        float y = switch (id) { case "compass" -> d.compassY; case "info" -> d.infoY; default -> d.targetEspY; } * height;
        dragOffsetX = (float)mx - x;
        dragOffsetY = (float)my - y;
    }

    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button != 0 || dragging == null) return super.mouseDragged(mx, my, button, dx, dy);
        FuxHudLayout.Data d = FuxHudLayout.data();
        float nx = ((float)mx - dragOffsetX) / Math.max(1, width);
        float ny = ((float)my - dragOffsetY) / Math.max(1, height);
        nx = Math.max(0.02F, Math.min(0.98F, nx));
        ny = Math.max(0.03F, Math.min(0.92F, ny));
        switch (dragging) { case "compass" -> { d.compassX = nx; d.compassY = ny; } case "info" -> { d.infoX = nx; d.infoY = ny; } default -> { d.targetEspX = nx; d.targetEspY = ny; } }
        FuxHudLayout.clamp();
        return true;
    }

    @Override public boolean mouseReleased(double mx, double my, int button) {
        if (button == 0 && dragging != null) { FuxHudLayout.save(); dragging = null; return true; }
        return super.mouseReleased(mx, my, button);
    }

    @Override public void onClose() { FuxHudLayout.save(); Minecraft.getInstance().setScreen(parent); }

    private boolean hitCompass(double mx, double my) { int x=Math.round(FuxHudLayout.data().compassX*width), y=Math.round(FuxHudLayout.data().compassY*height), w=Math.min(720,width-80); return mx>=x-w/2&&mx<=x+w/2&&my>=y-10&&my<=y+52; }
    private boolean hitInfo(double mx, double my) { int x=Math.round(FuxHudLayout.data().infoX*width), y=Math.round(FuxHudLayout.data().infoY*height); return mx>=x-75&&mx<=x+15&&my>=y-10&&my<=y+40; }
    private boolean hitTarget(double mx, double my) { int x=Math.round(FuxHudLayout.data().targetEspX*width), y=Math.round(FuxHudLayout.data().targetEspY*height); return mx>=x-38&&mx<=x+38&&my>=y-38&&my<=y+38; }
    private int accent(){return switch(FuxSettingsRegistry.ACCENT.get()){case "BLUE"->0xFF4F8CFF;case "GREEN"->0xFF45C98A;default->0xFF8B5CF6;};}
}
