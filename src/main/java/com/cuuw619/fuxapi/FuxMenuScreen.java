package com.cuuw619.fuxapi;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class FuxMenuScreen extends Screen {
    private static final int PANEL_WIDTH = 760;
    private static final int PANEL_HEIGHT = 430;
    private static final int SIDEBAR_WIDTH = 150;

    private static final String[] CATEGORIES = {
            "Combat", "Movement", "Player", "Render", "World", "Misc", "Settings"
    };

    private int panelX;
    private int panelY;
    private int selectedCategory = 0;
    private boolean dragging;
    private double dragOffsetX;
    private double dragOffsetY;

    public FuxMenuScreen(Screen parent) {
        super(Component.literal("Fux API"));
    }

    @Override
    protected void init() {
        panelX = Math.max(12, (this.width - PANEL_WIDTH) / 2);
        panelY = Math.max(12, (this.height - PANEL_HEIGHT) / 2);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        // Subtle backdrop.
        graphics.fill(0, 0, this.width, this.height, 0x66000000);

        // Panel shadow and body.
        graphics.fill(panelX + 5, panelY + 7, panelX + PANEL_WIDTH + 5, panelY + PANEL_HEIGHT + 7, 0x55000000);
        graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xF0101116);
        graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, 0xFF8B5CF6);

        renderSidebar(graphics, mouseX, mouseY);
        renderContent(graphics, mouseX, mouseY);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderSidebar(GuiGraphics graphics, int mouseX, int mouseY) {
        int left = panelX;
        int top = panelY;

        graphics.fill(left, top + 2, left + SIDEBAR_WIDTH, top + PANEL_HEIGHT, 0xFF13151B);

        graphics.drawString(this.font, Component.literal("FUX"), left + 18, top + 18, 0xFFFFFFFF, false);
        graphics.drawString(this.font, Component.literal("API"), left + 18, top + 34, 0xFF8B5CF6, false);

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = top + 70 + i * 43;
            boolean selected = i == selectedCategory;
            boolean hovered = mouseX >= left + 10 && mouseX < left + SIDEBAR_WIDTH - 10
                    && mouseY >= y && mouseY < y + 34;

            if (selected) {
                graphics.fill(left + 10, y, left + SIDEBAR_WIDTH - 10, y + 34, 0xFF26202F);
                graphics.fill(left + 10, y, left + 13, y + 34, 0xFF8B5CF6);
            } else if (hovered) {
                graphics.fill(left + 10, y, left + SIDEBAR_WIDTH - 10, y + 34, 0xFF1D2028);
            }

            int textColor = selected ? 0xFFFFFFFF : 0xFFB8BBC5;
            graphics.drawString(this.font, Component.literal(CATEGORIES[i]), left + 24, y + 10, textColor, false);
        }
    }

    private void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        int left = panelX + SIDEBAR_WIDTH;
        int top = panelY;
        int right = panelX + PANEL_WIDTH;

        graphics.drawString(this.font, Component.literal(CATEGORIES[selectedCategory]), left + 24, top + 20, 0xFFFFFFFF, false);
        graphics.drawString(this.font, Component.literal("Fux API base menu"), left + 24, top + 38, 0xFF777B86, false);

        // Close button.
        int closeX = right - 34;
        int closeY = top + 14;
        boolean closeHovered = mouseX >= closeX && mouseX < closeX + 20 && mouseY >= closeY && mouseY < closeY + 20;
        graphics.fill(closeX, closeY, closeX + 20, closeY + 20, closeHovered ? 0xFF30232A : 0xFF20232A);
        graphics.drawCenteredString(this.font, Component.literal("×"), closeX + 10, closeY + 5, 0xFFDDDEE4);

        // Empty module surface: no gameplay functions are registered yet.
        int cardX = left + 24;
        int cardY = top + 72;
        int cardW = PANEL_WIDTH - SIDEBAR_WIDTH - 48;
        int cardH = PANEL_HEIGHT - 96;
        graphics.fill(cardX, cardY, cardX + cardW, cardY + cardH, 0xFF171A20);
        graphics.fill(cardX, cardY, cardX + 2, cardY + cardH, 0xFF272B35);

        graphics.drawCenteredString(this.font, Component.literal("No modules registered"),
                cardX + cardW / 2, cardY + cardH / 2 - 8, 0xFFD7D9E0);
        graphics.drawCenteredString(this.font, Component.literal("The menu framework is ready for the next stage."),
                cardX + cardW / 2, cardY + cardH / 2 + 10, 0xFF777B86);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int closeX = panelX + PANEL_WIDTH - 34;
            int closeY = panelY + 14;
            if (mouseX >= closeX && mouseX < closeX + 20 && mouseY >= closeY && mouseY < closeY + 20) {
                this.onClose();
                return true;
            }

            if (mouseX >= panelX && mouseX < panelX + PANEL_WIDTH && mouseY >= panelY && mouseY < panelY + 54) {
                dragging = true;
                dragOffsetX = mouseX - panelX;
                dragOffsetY = mouseY - panelY;
                return true;
            }

            for (int i = 0; i < CATEGORIES.length; i++) {
                int y = panelY + 70 + i * 43;
                if (mouseX >= panelX + 10 && mouseX < panelX + SIDEBAR_WIDTH - 10
                        && mouseY >= y && mouseY < y + 34) {
                    selectedCategory = i;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            panelX = clamp((int) (mouseX - dragOffsetX), 8, Math.max(8, width - PANEL_WIDTH - 8));
            panelY = clamp((int) (mouseY - dragOffsetY), 8, Math.max(8, height - PANEL_HEIGHT - 8));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
