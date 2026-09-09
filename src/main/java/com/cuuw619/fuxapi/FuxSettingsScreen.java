package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSetting;
import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class FuxSettingsScreen extends Screen {
    private final Screen parent;
    public FuxSettingsScreen(Screen parent) { super(Component.literal("Fux API Settings")); this.parent = parent; }

    @Override protected void init() {}

    @Override public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g, mx, my, pt);
        int w=620,h=350,x=(width-w)/2,y=(height-h)/2;
        g.fill(x+5,y+7,x+w+5,y+h+7,0x55000000); g.fill(x,y,x+w,y+h,0xF0101116); g.fill(x,y,x+w,y+2,0xFF8B5CF6);
        g.drawString(font,"Fux API",x+24,y+20,0xFFFFFFFF,false);
        g.drawString(font,"Settings",x+24,y+38,0xFF8B5CF6,false);
        g.drawString(font,"Reese's Sodium Options-inspired controls",x+24,y+56,0xFF777B86,false);
        drawSetting(g,"Animations",FuxSettingsRegistry.ANIMATIONS,x+24,y+82,572,mx,my);
        drawSetting(g,"Blur",FuxSettingsRegistry.BLUR,x+24,y+128,572,mx,my);
        drawSetting(g,"Compact mode",FuxSettingsRegistry.COMPACT_MODE,x+24,y+174,572,mx,my);
        drawSetting(g,"UI scale",FuxSettingsRegistry.UI_SCALE,x+24,y+220,572,mx,my);
        drawSetting(g,"Accent",FuxSettingsRegistry.ACCENT,x+24,y+266,572,mx,my);
        g.drawCenteredString(font,"Click a setting to cycle/change it",width/2,y+h-28,0xFF777B86);
        super.render(g,mx,my,pt);
    }

    private void drawSetting(GuiGraphics g,String label,FuxSetting<?> s,int x,int y,int w,int mx,int my){
        boolean hover=mx>=x&&mx<x+w&&my>=y&&my<y+36;
        g.fill(x,y,x+w,y+36,hover?0xFF252A33:0xFF191C22);
        g.drawString(font,label,x+14,y+11,0xFFE2E3E8,false);
        String value=String.valueOf(s.get());
        if(s.get() instanceof Double) value=String.format("%.2f",(Double)s.get());
        g.drawRightAlignedString(font,value,x+w-14,y+11,0xFF9F7AEA,false);
    }

    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button!=0)return super.mouseClicked(mx,my,button);
        int w=620,h=350,x=(width-w)/2,y=(height-h)/2;
        if(mx>=x&&mx<x+36&&my>=y&&my<y+36){onClose();return true;}
        if(cycle(mx,my,x+24,y+82,572,36,FuxSettingsRegistry.ANIMATIONS))return true;
        if(cycle(mx,my,x+24,y+128,572,36,FuxSettingsRegistry.BLUR))return true;
        if(cycle(mx,my,x+24,y+174,572,36,FuxSettingsRegistry.COMPACT_MODE))return true;
        if(cycle(mx,my,x+24,y+220,572,36,FuxSettingsRegistry.UI_SCALE))return true;
        if(cycle(mx,my,x+24,y+266,572,36,FuxSettingsRegistry.ACCENT))return true;
        return super.mouseClicked(mx,my,button);
    }

    private boolean cycle(double mx,double my,int x,int y,int w,int h,FuxSetting<?> s){
        if(mx<x||mx>=x+w||my<y||my>=y+h)return false;
        if(s==FuxSettingsRegistry.ANIMATIONS)s.set(!FuxSettingsRegistry.ANIMATIONS.get());
        else if(s==FuxSettingsRegistry.BLUR)s.set(!FuxSettingsRegistry.BLUR.get());
        else if(s==FuxSettingsRegistry.COMPACT_MODE)s.set(!FuxSettingsRegistry.COMPACT_MODE.get());
        else if(s==FuxSettingsRegistry.UI_SCALE){double n=FuxSettingsRegistry.UI_SCALE.get()+.1;FuxSettingsRegistry.UI_SCALE.set(n>1.5?.75:Math.round(n*10)/10.0);}
        else {String a=FuxSettingsRegistry.ACCENT.get();FuxSettingsRegistry.ACCENT.set(a.equals("PURPLE")?"BLUE":a.equals("BLUE")?"GREEN":"PURPLE");}
        FuxSettingsRegistry.save(); return true;
    }
    @Override public void onClose(){if(parent!=null)minecraft.setScreen(parent);else super.onClose();}
}
