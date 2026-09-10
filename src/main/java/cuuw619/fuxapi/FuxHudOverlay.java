package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.cuuw619.fuxapi.module.FuxModule;
import com.cuuw619.fuxapi.module.FuxModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid=FuxApi.MOD_ID, value=Dist.CLIENT)
public final class FuxHudOverlay {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static float yaw;
    private static boolean yawReady;
    private static float targetAlpha, targetScale = .94F;
    private static final float[] current = new float[14];
    private static boolean layoutReady;
    private FuxHudOverlay() {}

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null || mc.options.hideGui || mc.screen != null) return;
        GuiGraphics g=e.getGuiGraphics(); int w=g.guiWidth(), h=g.guiHeight(), a=accent();
        FuxHudLayout.Data d=FuxHudLayout.data();
        if (!layoutReady) { syncLayout(d,w,h); layoutReady=true; }
        animateLayout(d,w,h);

        float py=p.getYRot();
        if(!yawReady){yaw=py;yawReady=true;}
        yaw=FuxUiAnimation.smooth(yaw,yaw+shortest(py-yaw),18.0F,1.0F/60.0F);

        drawWatermark(g,Math.round(current[6]),Math.round(current[7]),a,d.watermarkScale);
        drawCompass(g,Math.round(current[0]),Math.round(current[1]),w,yaw,a,d.compassScale);
        drawInfo(g,Math.round(current[2]),Math.round(current[3]),mc,a,d.infoScale);
        drawModuleList(g,Math.round(current[8]),Math.round(current[9]),a,d.moduleListScale);
        drawCoords(g,Math.round(current[10]),Math.round(current[11]),p,a,d.coordsScale);
        drawKeystrokes(g,Math.round(current[12]),Math.round(current[13]),p,a,d.keystrokesScale);
        if(FuxModuleManager.byName("Target ESP")!=null&&FuxModuleManager.byName("Target ESP").isEnabled()) drawTarget(g,w,h,p,a,d.targetEspScale);
    }

    private static void syncLayout(FuxHudLayout.Data d,int w,int h){
        current[0]=d.compassX*w; current[1]=d.compassY*h; current[2]=d.infoX*w; current[3]=d.infoY*h;
        current[6]=d.watermarkX*w; current[7]=d.watermarkY*h; current[8]=d.moduleListX*w; current[9]=d.moduleListY*h;
        current[10]=d.coordsX*w; current[11]=d.coordsY*h; current[12]=d.keystrokesX*w; current[13]=d.keystrokesY*h;
    }

    private static void animateLayout(FuxHudLayout.Data d,int w,int h){
        float[] target={d.compassX*w,d.compassY*h,d.infoX*w,d.infoY*h,0,0,d.watermarkX*w,d.watermarkY*h,d.moduleListX*w,d.moduleListY*h,d.coordsX*w,d.coordsY*h,d.keystrokesX*w,d.keystrokesY*h};
        for(int i=0;i<current.length;i++) if(target[i]!=0) current[i]=FuxUiAnimation.smooth(current[i],target[i],20.0F,1.0F/60.0F);
    }

    private static void drawWatermark(GuiGraphics g,int x,int y,int a,float scale){
        g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(scale,scale,1);
        g.drawString(Minecraft.getInstance().font,"FUX",0,0,0xFFFFFFFF,false);g.drawString(Minecraft.getInstance().font," • 1.21.1",fontWidth(),0,a,false);g.pose().popPose();
    }
    private static int fontWidth(){return Minecraft.getInstance().font.width("FUX");}

    private static void drawCompass(GuiGraphics g,int c,int top,int width,float y,int a,float scale){
        g.pose().pushPose();g.pose().translate(c,top,0);g.pose().scale(scale,scale,1);
        int cw=Math.min(760,width-50);float ppd=cw/180F;
        for(int deg=0;deg<360;deg+=5){float delta=shortest(deg-y);if(Math.abs(delta)>90)continue;int x=Math.round(delta*ppd);boolean major=deg%15==0;g.fill(x,38-(major?12:6),x+1,38,major?a:0xFF565B66);if(deg%45==0)g.drawCenteredString(Minecraft.getInstance().font,directionFor(deg),x,2,deg%90==0?0xFFFFFFFF:0xFF9DA2AC);}
        g.fill(-1,17,1,39,a);g.drawCenteredString(Minecraft.getInstance().font,"▼",0,37,0xFFFFFFFF);g.pose().popPose();
    }

    private static void drawInfo(GuiGraphics g,int x,int top,Minecraft mc,int a,float scale){g.pose().pushPose();g.pose().translate(x,top,0);g.pose().scale(scale,scale,1);g.drawString(mc.font,mc.getFps()+" FPS",-70,2,0xFFE8E9ED,false);g.drawString(mc.font,LocalTime.now().format(CLOCK),-70,18,a,false);g.pose().popPose();}

    private static void drawModuleList(GuiGraphics g,int x,int y,int a,float scale){
        g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(scale,scale,1);g.drawString(Minecraft.getInstance().font,"FUX",0,0,a,false);int row=16;
        int shown=0;for(FuxModule m:FuxModuleManager.all()) if(m.isEnabled()){g.drawString(Minecraft.getInstance().font,m.getName(),0,row+shown*13,0xFFE5E7EB,false);shown++;if(shown>=8)break;}g.pose().popPose();
    }

    private static void drawCoords(GuiGraphics g,int x,int y,LocalPlayer p,int a,float scale){g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(scale,scale,1);String s=String.format("XYZ %.1f  %.1f  %.1f",p.getX(),p.getY(),p.getZ());g.drawString(Minecraft.getInstance().font,s,0,0,0xFFDDE0E6,false);g.pose().popPose();}

    private static void drawKeystrokes(GuiGraphics g,int x,int y,LocalPlayer p,int a,float scale){g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(scale,scale,1);String[] k={"W","A","S","D"};for(int i=0;i<4;i++){boolean down=switch(i){case 0->p.input.up;case 1->p.input.left;case 2->p.input.down;default->p.input.right;};int xx=i==0?20:i==1?0:i==2?20:40,yy=i==0?0:i==2?20:20;g.fill(xx,yy,xx+18,yy+18,down?0xCC38313F:0xAA14171D);g.drawCenteredString(Minecraft.getInstance().font,k[i],xx+9,yy+5,down?a:0xFF8A909B);}g.pose().popPose();}

    private static void drawTarget(GuiGraphics g,int w,int h,LocalPlayer p,int a,float scale){
        LivingEntity t=findTarget(p);targetAlpha=FuxUiAnimation.smooth(targetAlpha,t==null?0:1,14.0F,1.0F/60.0F);targetScale=FuxUiAnimation.smooth(targetScale,t==null?.94F:1F,12.0F,1.0F/60.0F);if(t==null&&targetAlpha<.02)return;
        FuxHudLayout.Data d=FuxHudLayout.data();int cx=Math.round(d.targetEspX*w),cy=Math.round(d.targetEspY*h);g.pose().pushPose();g.pose().translate(cx,cy,0);g.pose().scale(scale*targetScale,scale*targetScale,1);
        int box=42,alpha=Math.max(0,Math.min(255,Math.round(220*targetAlpha))),col=(alpha<<24)|(a&0xFFFFFF);g.fill(-box,-box,-box+2,box,col);g.fill(box-2,-box,box,box,col);g.fill(-box,-box,box,-box+2,col);g.fill(-box,box-2,box,box,col);
        String n=t.getName().getString();if(n.length()>18)n=n.substring(0,18);g.drawCenteredString(Minecraft.getInstance().font,n,0,48,(Math.round(235*targetAlpha)<<24)|0xFFFFFF);float hp=Math.max(0,Math.min(1,t.getHealth()/Math.max(1,t.getMaxHealth())));g.fill(-box,60,box,63,0xFF24262B);g.fill(-box,60,-box+(int)(box*2*hp),63,col);g.pose().popPose();
    }

    private static LivingEntity findTarget(LocalPlayer p){LivingEntity best=null;double bd=64;for(LivingEntity e:p.level().getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().inflate(8))){if(e==p||!e.isAlive())continue;double d=p.distanceToSqr(e);if(d<bd){bd=d;best=e;}}return best;}
    private static int accent(){return switch(FuxSettingsRegistry.ACCENT.get()){case "BLUE"->0xFF4F8CFF;case "GREEN"->0xFF45C98A;default->0xFF8B5CF6;};}
    private static float shortest(float d){d%=360;if(d>180)d-=360;if(d<-180)d+=360;return d;}
    private static String directionFor(int d){return switch(Math.floorMod(Math.round(d/45F),8)){case 0->"S";case 1->"SW";case 2->"W";case 3->"NW";case 4->"N";case 5->"NE";case 6->"E";default->"SE";};}
}
