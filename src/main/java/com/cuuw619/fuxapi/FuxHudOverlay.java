package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT)
public final class FuxHudOverlay {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static float smoothYaw;
    private static boolean yawReady;
    private FuxHudOverlay() {}

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance(); LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) return;
        GuiGraphics g=event.getGuiGraphics(); int width=g.guiWidth(), accent=accent();
        float target=player.getYRot(); if(!yawReady){smoothYaw=target;yawReady=true;} else smoothYaw += shortest(target-smoothYaw)*0.16F;
        drawCompass(g,width/2,16,width,smoothYaw,accent); drawInfo(g,width-14,16,mc,accent);
    }

    private static void drawCompass(GuiGraphics g,int center,int top,int width,float yaw,int accent){
        int cw=Math.min(720,width-80), left=center-cw/2,right=center+cw/2,height=42; float dpp=180f/cw, heading=normalize(yaw);
        // Deliberately no panel, background, border or outline: only the compass itself is drawn.
        for(int deg=-180;deg<=180;deg+=10){float delta=shortest((heading+deg)-heading);int x=center+Math.round(delta/dpp);if(x<left||x>right)continue;boolean major=deg%30==0;int tick=major?11:6;g.fill(x,top+height-tick,x+1,top+height,major?accent:0xFF666B76);if(major){String label=directionFor(heading+deg);g.drawCenteredString(Minecraft.getInstance().font,label,x,top+4,isCardinal(label)?0xFFFFFFFF:0xFF9DA2AC);}}
        g.fill(center-1,top+18,center+1,top+height,accent);g.drawCenteredString(Minecraft.getInstance().font,"▼",center,top+height-4,0xFFFFFFFF);
    }

    private static void drawInfo(GuiGraphics g,int right,int top,Minecraft mc,int accent){
        // Raw text only: no box, backdrop, border or outline.
        g.drawString(mc.font,mc.getFps()+" FPS",right-66,top+4,0xFFE8E9ED,false);
        g.drawString(mc.font,LocalTime.now().format(CLOCK),right-66,top+20,accent,false);
    }
    private static int accent(){return switch(FuxSettingsRegistry.ACCENT.get()){case "BLUE"->0xFF4F8CFF;case "GREEN"->0xFF45C98A;default->0xFF8B5CF6;};}
    private static float normalize(float d){d%=360f;return d<0?d+360:d;}
    private static float shortest(float d){d%=360f;if(d>180)d-=360;if(d<-180)d+=360;return d;}
    private static String directionFor(float h){return switch(Math.floorMod(Math.round(normalize(h)/45f),8)){case 0->"S";case 1->"SW";case 2->"W";case 3->"NW";case 4->"N";case 5->"NE";case 6->"E";default->"SE";};}
    private static boolean isCardinal(String s){return s.length()==1;}
}
