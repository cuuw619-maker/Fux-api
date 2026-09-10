package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
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

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT)
public final class FuxHudOverlay {
    private static final DateTimeFormatter CLOCK=DateTimeFormatter.ofPattern("HH:mm:ss");
    private static float smoothYaw;private static boolean yawReady;private static float targetAlpha;private static float targetScale=1;
    private FuxHudOverlay(){}
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        Minecraft mc=Minecraft.getInstance();LocalPlayer p=mc.player;if(p==null||mc.options.hideGui||mc.screen!=null)return;GuiGraphics g=event.getGuiGraphics();int w=g.guiWidth(),h=g.guiHeight(),a=accent();
        float yaw=p.getYRot();if(!yawReady){smoothYaw=yaw;yawReady=true;}else smoothYaw=FuxUiAnimation.smoothDamp(smoothYaw,smoothYaw+shortest(yaw-smoothYaw),.075F);
        FuxHudLayout.Data d=FuxHudLayout.data();drawCompass(g,Math.round(d.compassX*w),Math.round(d.compassY*h),w,smoothYaw,a);drawInfo(g,Math.round(d.infoX*w),Math.round(d.infoY*h),mc,a);
        if(FuxModuleManager.byName("Target ESP")!=null&&FuxModuleManager.byName("Target ESP").isEnabled())drawTarget(g,w,h,p,a);
    }
    private static void drawCompass(GuiGraphics g,int center,int top,int width,float yaw,int accent){
        int cw=Math.min(760,width-50),left=center-cw/2,right=center+cw/2;float pxPerDegree=cw/180F;
        for(int worldDeg=0;worldDeg<360;worldDeg+=5){float delta=shortest(worldDeg-yaw);if(Math.abs(delta)>90)continue;int xx=center+Math.round(delta*pxPerDegree);if(xx<left||xx>right)continue;boolean major=worldDeg%15==0;int tick=major?12:6;int col=major?accent:0xFF565B66;g.fill(xx,top+38-tick,xx+1,top+38,col);if(worldDeg%45==0)g.drawCenteredString(Minecraft.getInstance().font,directionFor(worldDeg),xx,top+2,worldDeg%90==0?0xFFFFFFFF:0xFF9DA2AC);}
        g.fill(center-1,top+17,center+1,top+39,accent);g.drawCenteredString(Minecraft.getInstance().font,"▼",center,top+37,0xFFFFFFFF);
    }
    private static void drawInfo(GuiGraphics g,int right,int top,Minecraft mc,int accent){g.drawString(mc.font,mc.getFps()+" FPS",right-70,top+2,0xFFE8E9ED,false);g.drawString(mc.font,LocalTime.now().format(CLOCK),right-70,top+18,accent,false);}
    private static void drawTarget(GuiGraphics g,int w,int h,LocalPlayer p,int accent){
        LivingEntity t=findTarget(p);targetAlpha=FuxUiAnimation.smoothDamp(targetAlpha,t==null?0:1,.14F);targetScale=FuxUiAnimation.smoothDamp(targetScale,t==null?.96F:1,.10F);if(t==null&&targetAlpha<.02)return;
        FuxHudLayout.Data d=FuxHudLayout.data();int cx=Math.round(d.targetEspX*w),cy=Math.round(d.targetEspY*h),box=Math.round(42*targetScale),alpha=Math.max(0,Math.min(255,Math.round(220*targetAlpha))),col=(alpha<<24)|(accent&0xFFFFFF);
        g.fill(cx-box,cy-box,cx-box+2,cy+box,col);g.fill(cx+box-2,cy-box,cx+box,cy+box,col);g.fill(cx-box,cy-box,cx+box,cy-box+2,col);g.fill(cx-box,cy+box-2,cx+box,cy+box,col);
        String name=t==null?"":t.getName().getString();if(name.length()>18)name=name.substring(0,18);g.drawCenteredString(Minecraft.getInstance().font,name,cx,cy+box+6,(Math.round(235*targetAlpha)<<24)|0xFFFFFF);
        float hp=Math.max(0,Math.min(1,t.getHealth()/Math.max(1,t.getMaxHealth())));int barW=box*2;g.fill(cx-box,cy+box+18,cx+box,cy+box+21,0xFF24262B);g.fill(cx-box,cy+box+18,cx-box+(int)(barW*hp),cy+box+21,col);
    }
    private static LivingEntity findTarget(LocalPlayer p){LivingEntity best=null;double bd=64;for(LivingEntity e:p.level().getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().inflate(8))){if(e==p||!e.isAlive())continue;double d=p.distanceToSqr(e);if(d<bd){bd=d;best=e;}}return best;}
    private static int accent(){return switch(FuxSettingsRegistry.ACCENT.get()){case "BLUE"->0xFF4F8CFF;case "GREEN"->0xFF45C98A;default->0xFF8B5CF6;};}
    private static float shortest(float d){d%=360;if(d>180)d-=360;if(d<-180)d+=360;return d;}
    private static String directionFor(int d){return switch(Math.floorMod(Math.round(d/45F),8)){case 0->"S";case 1->"SW";case 2->"W";case 3->"NW";case 4->"N";case 5->"NE";case 6->"E";default->"SE";};}
}
