package com.cuuw619.fuxapi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    private static final long START=System.currentTimeMillis();

    @Inject(method="render",at=@At("TAIL"))
    private void fux$renderLoader(GuiGraphics g,int mouseX,int mouseY,float partialTick,CallbackInfo ci){
        long elapsed=System.currentTimeMillis()-START; float t=elapsed/1000f; int w=g.guiWidth(),h=g.guiHeight(),cx=w/2,cy=h/2,accent=0xFF8B5CF6;
        // Cover Mojang's red loader completely with the Fux loader surface.
        g.fill(0,0,w,h,0xFF030405);
        renderParticles(g,w,h,t,accent);
        float pulse=.5f+.5f*(float)Math.sin(t*2.2f); float spin=t*.85f;
        int r=Math.min(82,Math.max(48,Math.min(w,h)/8));
        // Three-layer animated loader rings.
        for(int ring=0;ring<3;ring++){float phase=spin*(ring%2==0?1f:-1f)+ring*2.1f;for(int i=0;i<18;i++){double a=phase+i*Math.PI*2/18.0;int rr=r-ring*15;int px=cx+(int)(Math.cos(a)*rr),py=cy+(int)(Math.sin(a)*rr);int alpha=20+i*4;g.fill(px-1,py-1,px+2,py+2,(Math.min(120,alpha)<<24)|(accent&0xFFFFFF));}}
        float logoScale=1f+.035f*pulse;g.pose().pushPose();g.pose().translate(cx,cy-34,0);g.pose().scale(logoScale,logoScale,1);g.drawCenteredString(Minecraft.getInstance().font,"FUX",0,0,0xFFFFFFFF);g.pose().popPose();
        g.drawCenteredString(Minecraft.getInstance().font,"CLIENT",cx,cy-12,accent);
        int barW=Math.min(300,Math.max(140,w/3)),barX=cx-barW/2,barY=cy+34;float load=.5f+.5f*(float)Math.sin(t*1.7f);int fill=(int)(barW*(.18f+.64f*load));
        g.fill(barX,barY,barX+barW,barY+2,0xFF20242B);g.fill(barX,barY,barX+fill,barY+2,accent);g.drawCenteredString(Minecraft.getInstance().font,"Loading client",cx,barY+12,0xFF6E737C);
    }
    private static void renderParticles(GuiGraphics g,int w,int h,float t,int accent){for(int i=0;i<64;i++){float seed=i*37.719f, bx=fract((float)Math.sin(seed*12.9898f)*43758.5453f),by=fract((float)Math.sin(seed*78.233f)*12741.371f);float x=bx*w+(float)Math.sin(t*(.35f+i*.011f)+seed)*30f;float y=h-((by*(h+80f)+t*(10f+(i%7)*3f))%(h+80f));float tw=.35f+.65f*(float)Math.sin(t*(1.1f+i*.06f)+seed);int alpha=Math.max(15,Math.min(100,(int)(tw*90)));int size=i%13==0?3:(i%4==0?2:1);int ix=((int)x%w+w)%w,iy=((int)y%h+h)%h;g.fill(ix,iy,ix+size,iy+size,(alpha<<24)|(accent&0xFFFFFF));}}
    private static float fract(float v){return v-(float)Math.floor(v);}
}
