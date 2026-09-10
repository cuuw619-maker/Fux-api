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

    public FuxHudEditorScreen(Screen parent){super(Component.literal("Fux HUD Editor"));this.parent=parent;}

    @Override protected void init(){
        addRenderableWidget(Button.builder(Component.literal("Reset layout"),b->FuxHudLayout.reset()).bounds(width/2-195,height-34,100,20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"),b->onClose()).bounds(width/2+95,height-34,100,20).build());
    }

    @Override public void render(GuiGraphics g,int mx,int my,float pt){
        g.fill(0,0,width,height,0xD9000000);
        int a=accent();
        g.drawCenteredString(font,Component.literal("FUX HUD EDITOR"),width/2,18,0xFFFFFFFF);
        g.drawCenteredString(font,Component.literal("Drag to move • wheel over an element to resize • smooth motion saves automatically"),width/2,34,0xFF777D88);
        drawGrid(g); FuxHudLayout.Data d=FuxHudLayout.data();
        drawCompass(g,d,a); drawInfo(g,d,a); drawTarget(g,d,a); drawWatermark(g,d,a); drawModuleList(g,d,a); drawCoords(g,d,a); drawKeys(g,d,a);
        super.render(g,mx,my,pt);
    }

    private void drawGrid(GuiGraphics g){for(int x=0;x<width;x+=40)g.fill(x,0,x+1,height,0x182A2F38);for(int y=0;y<height;y+=40)g.fill(0,y,width,y+1,0x182A2F38);}
    private void drawCompass(GuiGraphics g,FuxHudLayout.Data d,int a){int cx=Math.round(d.compassX*width),cy=Math.round(d.compassY*height),w=Math.round(Math.min(720,width-80)*d.compassScale);g.fill(cx-w/2,cy,cx+w/2,cy+42,0x30101016);g.fill(cx-w/2,cy+41,cx+w/2,cy+43,a);g.drawCenteredString(font,Component.literal("COMPASS ×"+String.format("%.2f",d.compassScale)),cx,cy+15,0xFFFFFFFF);label(g,"Compass",cx,cy-13,a);}
    private void drawInfo(GuiGraphics g,FuxHudLayout.Data d,int a){int x=Math.round(d.infoX*width),y=Math.round(d.infoY*height);g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(d.infoScale,d.infoScale,1);g.drawString(font,"144 FPS",-65,0,0xFFFFFFFF,false);g.drawString(font,"12:34:56",-65,16,a,false);g.pose().popPose();label(g,"FPS / Clock ×"+String.format("%.2f",d.infoScale),x-32,y-13,a);}
    private void drawTarget(GuiGraphics g,FuxHudLayout.Data d,int a){int x=Math.round(d.targetEspX*width),y=Math.round(d.targetEspY*height),s=Math.round(54*d.targetEspScale);g.fill(x-s/2,y-s/2,x+s/2,y+s/2,0x24101016);g.fill(x-s/2,y-s/2,x-s/2+2,y+s/2,a);g.drawCenteredString(font,Component.literal("TARGET ESP ×"+String.format("%.2f",d.targetEspScale)),x,y-4,0xFFFFFFFF);label(g,"Target ESP",x,y-s/2-13,a);}
    private void drawWatermark(GuiGraphics g,FuxHudLayout.Data d,int a){int x=Math.round(d.watermarkX*width),y=Math.round(d.watermarkY*height);g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(d.watermarkScale,d.watermarkScale,1);g.drawString(font,"FUX • 1.21.1",0,0,0xFFFFFFFF,false);g.pose().popPose();label(g,"Watermark",x+35,y-13,a);}
    private void drawModuleList(GuiGraphics g,FuxHudLayout.Data d,int a){int x=Math.round(d.moduleListX*width),y=Math.round(d.moduleListY*height);g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(d.moduleListScale,d.moduleListScale,1);g.drawString(font,"FUX MODULES",0,0,a,false);g.drawString(font,"Sprint",0,15,0xFFE7E9EE,false);g.drawString(font,"Target ESP",0,28,0xFFE7E9EE,false);g.pose().popPose();label(g,"Module list",x+35,y-13,a);}
    private void drawCoords(GuiGraphics g,FuxHudLayout.Data d,int a){int x=Math.round(d.coordsX*width),y=Math.round(d.coordsY*height);g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(d.coordsScale,d.coordsScale,1);g.drawString(font,"XYZ 123.4  64.0  -28.7",0,0,0xFFDDE0E6,false);g.pose().popPose();label(g,"Coordinates",x+45,y-13,a);}
    private void drawKeys(GuiGraphics g,FuxHudLayout.Data d,int a){int x=Math.round(d.keystrokesX*width),y=Math.round(d.keystrokesY*height);g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(d.keystrokesScale,d.keystrokesScale,1);g.fill(20,0,38,18,0xAA242832);g.fill(0,20,18,38,0xAA242832);g.fill(20,20,38,38,0xAA242832);g.fill(40,20,58,38,0xAA242832);g.drawString(font,"W",25,5,0xFFFFFFFF,false);g.drawString(font,"A",5,25,0xFFFFFFFF,false);g.drawString(font,"S",25,25,0xFFFFFFFF,false);g.drawString(font,"D",45,25,0xFFFFFFFF,false);g.pose().popPose();label(g,"Keystrokes",x+29,y-13,a);}
    private void label(GuiGraphics g,String text,int x,int y,int a){int w=font.width(text)+10;g.fill(x-w/2,y-2,x+w/2,y+12,0xC0101218);g.drawCenteredString(font,Component.literal(text),x,y,a);}

    @Override public boolean mouseClicked(double mx,double my,int button){if(button!=0)return super.mouseClicked(mx,my,button);if(hit("compass",mx,my)){begin("compass",mx,my);return true;}if(hit("info",mx,my)){begin("info",mx,my);return true;}if(hit("targetEsp",mx,my)){begin("targetEsp",mx,my);return true;}if(hit("watermark",mx,my)){begin("watermark",mx,my);return true;}if(hit("moduleList",mx,my)){begin("moduleList",mx,my);return true;}if(hit("coords",mx,my)){begin("coords",mx,my);return true;}if(hit("keystrokes",mx,my)){begin("keystrokes",mx,my);return true;}return super.mouseClicked(mx,my,button);}

    private void begin(String id,double mx,double my){dragging=id;FuxHudLayout.Data d=FuxHudLayout.data();float x=px(id,d),y=py(id,d);dragOffsetX=(float)mx-x;dragOffsetY=(float)my-y;}
    private float px(String id,FuxHudLayout.Data d){return switch(id){case "compass"->d.compassX;case "info"->d.infoX;case "targetEsp"->d.targetEspX;case "watermark"->d.watermarkX;case "moduleList"->d.moduleListX;case "coords"->d.coordsX;default->d.keystrokesX;}*width;}
    private float py(String id,FuxHudLayout.Data d){return switch(id){case "compass"->d.compassY;case "info"->d.infoY;case "targetEsp"->d.targetEspY;case "watermark"->d.watermarkY;case "moduleList"->d.moduleListY;case "coords"->d.coordsY;default->d.keystrokesY;}*height;}

    @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy){if(button!=0||dragging==null)return super.mouseDragged(mx,my,button,dx,dy);FuxHudLayout.Data d=FuxHudLayout.data();float nx=((float)mx-dragOffsetX)/Math.max(1,width),ny=((float)my-dragOffsetY)/Math.max(1,height);nx=Math.max(.02F,Math.min(.98F,nx));ny=Math.max(.03F,Math.min(.94F,ny));switch(dragging){case "compass"->{d.compassX=nx;d.compassY=ny;}case "info"->{d.infoX=nx;d.infoY=ny;}case "targetEsp"->{d.targetEspX=nx;d.targetEspY=ny;}case "watermark"->{d.watermarkX=nx;d.watermarkY=ny;}case "moduleList"->{d.moduleListX=nx;d.moduleListY=ny;}case "coords"->{d.coordsX=nx;d.coordsY=ny;}default->{d.keystrokesX=nx;d.keystrokesY=ny;}}FuxHudLayout.clamp();return true;}

    @Override public boolean mouseScrolled(double mx,double my,double sx,double sy){FuxHudLayout.Data d=FuxHudLayout.data();String id=findHit(mx,my);if(id==null)return super.mouseScrolled(mx,my,sx,sy);float delta=.05F*(float)sy;switch(id){case "compass"->d.compassScale=clampScale(d.compassScale+delta);case "info"->d.infoScale=clampScale(d.infoScale+delta);case "targetEsp"->d.targetEspScale=clampScale(d.targetEspScale+delta);case "watermark"->d.watermarkScale=clampScale(d.watermarkScale+delta);case "moduleList"->d.moduleListScale=clampScale(d.moduleListScale+delta);case "coords"->d.coordsScale=clampScale(d.coordsScale+delta);default->d.keystrokesScale=clampScale(d.keystrokesScale+delta);}FuxHudLayout.save();return true;}
    private float clampScale(float v){return Math.max(.5F,Math.min(2F,v));}
    private String findHit(double mx,double my){String[] ids={"compass","info","targetEsp","watermark","moduleList","coords","keystrokes"};for(String id:ids)if(hit(id,mx,my))return id;return null;}
    private boolean hit(String id,double mx,double my){FuxHudLayout.Data d=FuxHudLayout.data();float x=px(id,d),y=py(id,d),s=switch(id){case "compass"->Math.min(720,width-80)*d.compassScale/2F;case "info"->80*d.infoScale;case "targetEsp"->48*d.targetEspScale;case "watermark"->65*d.watermarkScale;case "moduleList"->80*d.moduleListScale;case "coords"->120*d.coordsScale;default->70*d.keystrokesScale;};return mx>=x-s&&mx<=x+s&&my>=y-14&&my<=y+Math.max(42,s);}
    @Override public boolean mouseReleased(double mx,double my,int button){if(button==0&&dragging!=null){FuxHudLayout.save();dragging=null;return true;}return super.mouseReleased(mx,my,button);}
    @Override public void onClose(){FuxHudLayout.save();Minecraft.getInstance().setScreen(parent);}
    private int accent(){return switch(FuxSettingsRegistry.ACCENT.get()){case "BLUE"->0xFF4F8CFF;case "GREEN"->0xFF45C98A;default->0xFF8B5CF6;};}
}
