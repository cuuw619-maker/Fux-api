package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.cuuw619.fuxapi.module.FuxModule;
import com.cuuw619.fuxapi.module.FuxModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.HashMap;
import java.util.Map;

public final class FuxPulseMenuScreen extends Screen {
    private static final int W=900,H=540,SIDE=180;
    private static final String[] CATS={"Combat","Movement","Player","Render","World","Misc","Settings"};
    private int x,y,category; private float scale=1; private boolean dragging,closing; private double ox,oy;
    private long openedAt,categoryChangedAt,closingAt; private final Map<String,Long> changed=new HashMap<>();
    public FuxPulseMenuScreen(Screen parent){super(Component.literal("Fux API"));}
    @Override protected void init(){scale=Math.min(1,Math.min((width-20f)/W,(height-20f)/H));x=Math.max(10,(int)((width/scale-W)/2));y=Math.max(10,(int)((height/scale-H)/2));openedAt=categoryChangedAt=System.currentTimeMillis();}
    @Override public void tick(){if(closing&&System.currentTimeMillis()-closingAt>=190)minecraft.setScreen(null);}

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float pt){
        float rawOpen=FuxUiAnimation.progress(openedAt,260), open=FuxUiAnimation.easeOutQuint(rawOpen);
        float rawClose=closing?FuxUiAnimation.progress(closingAt,190):0, close=closing?1.0F-FuxUiAnimation.easeInOutCubic(rawClose):1.0F;
        float v=open*close;
        int alpha=Math.max(0,Math.min(235,Math.round(190*v)));
        g.fill(0,0,width,height,(alpha<<24));
        g.pose().pushPose();
        g.pose().scale(scale,scale,1);
        int mx=(int)(mouseX/scale),my=(int)(mouseY/scale);
        float panelScale=0.965F+0.035F*v;
        float centerX=x+W/2F,centerY=y+H/2F;
        g.pose().translate(centerX,centerY,0);g.pose().scale(panelScale,panelScale,1);g.pose().translate(-centerX,-centerY,0);
        drawVisualBackground(g,x,y,v); drawPanel(g,x,y,mx,my,v); g.pose().popPose();
        super.render(g,mouseX,mouseY,pt);
    }

    private void drawVisualBackground(GuiGraphics g,int px,int py,float v){
        int a=accent();float t=System.currentTimeMillis()/1000.0F;
        if(v<0.01F)return;
        g.fill(px-30,py-24,px+W+30,py+H+24,((int)(26*v)<<24)|(a&0xFFFFFF));
        if(!FuxSettingsRegistry.COMPACT_MODE.get()){
            for(int i=0;i<64;i++){
                float seed=i*37.719F;float bx=fract((float)Math.sin(seed*12.9898F)*43758.5453F),by=fract((float)Math.sin(seed*78.233F)*12741.371F);
                float speed=0.35F+(i%9)*0.045F;float wave=(float)Math.sin(t*speed+seed)*18F;
                int dx=px+12+(int)(bx*(W-24)+wave),dy=py+18+(int)(((by*(H+80)+t*(5F+(i%7)*1.7F))%(H+80)));
                int aa=Math.max(10,Math.min(95,(int)((0.25F+0.75F*(0.5F+0.5F*(float)Math.sin(t*(0.8F+i*0.025F)+seed)))*70F*v)));
                int s=i%19==0?3:(i%5==0?2:1);g.fill(dx,dy,dx+s,dy+s,(aa<<24)|(a&0xFFFFFF));
            }
            for(int i=0;i<12;i++){
                double ang=t*(0.12+i*0.009)+i*Math.PI*2/12;float r=110+i*7;
                int dx=px+W/2+(int)(Math.cos(ang)*r),dy=py+H/2+(int)(Math.sin(ang)*r*0.58);
                int aa=(int)(34*v);g.fill(dx,dy,dx+2,dy+2,(aa<<24)|(a&0xFFFFFF));
            }
        }
    }

    private void drawPanel(GuiGraphics g,int px,int py,int mx,int my,float v){
        int a=accent();g.fill(px+8,py+10,px+W+8,py+H+10,0x55000000);g.fill(px,py,px+W,py+H,0xF0080B10);g.fill(px,py,px+W,py+3,a);g.fill(px,py+3,px+SIDE,py+H,0xFF0D1117);
        text(g,"FUX",px+22,py+20,0xFFFFFFFF);text(g,"CLIENT",px+22,py+40,a);text(g,"MODULES",px+22,py+72,0xFF626874);
        for(int i=0;i<CATS.length;i++){int cy=py+84+i*48;boolean s=category==i,h=inside(mx,my,px+10,cy,SIDE-20,38);if(s){g.fill(px+10,cy,px+SIDE-10,cy+38,0xFF28202F);g.fill(px+10,cy,px+13,cy+38,a);}else if(h)g.fill(px+10,cy,px+SIDE-10,cy+38,0xFF1B2028);text(g,CATS[i],px+26,cy+12,s?0xFFFFFFFF:0xFF9DA1AB);}
        text(g,"1.21.1 / NeoForge",px+22,py+H-22,0xFF444954);
        int left=px+SIDE+30,top=py+22,right=px+W-28;float cp=FuxUiAnimation.easeOutQuint(FuxUiAnimation.progress(categoryChangedAt,180));int shift=(int)((1-cp)*14);text(g,CATS[category],left+shift,top,0xFFFFFFFF);text(g,category==6?"Interface settings":"Client modules",left+shift,top+20,0xFF666B76);
        int close=right-22;g.fill(close,py+15,close+22,py+37,inside(mx,my,close,py+15,22,22)?0xFF30232A:0xFF1E2229);g.drawCenteredString(font,Component.literal("×"),close+11,py+20,0xFFE1E2E7);
        if(category==6)drawSettings(g,left+shift,py+76,right-left,mx,my);else drawModules(g,left+shift,py+76,right-left,mx,my);
        if(v<1)g.fill(px,py,px+W,py+H,((int)((1-v)*90)<<24));
    }

    private void drawModules(GuiGraphics g,int left,int top,int w,int mx,int my){
        var ms=FuxModuleManager.byCategory(CATS[category]);if(ms.isEmpty()){g.fill(left,top,left+w,top+H-102,0xFF13171D);text(g,"No modules registered",left+20,top+22,0xFFD9DBE1);text(g,"Module slots are ready for the next stage.",left+20,top+45,0xFF666B76);return;}
        for(int i=0;i<ms.size();i++){FuxModule m=ms.get(i);int baseY=top+i*62;boolean h=inside(mx,my,left,baseY,w,52);float hover= h?1F:0F;int cy=baseY-(int)(2*hover);boolean en=m.isEnabled();long ch=changed.getOrDefault(m.getName(),0L);float p=ch==0?1:FuxUiAnimation.easeOutCubic(FuxUiAnimation.progress(ch,170));int bg=h?0xFF20252E:0xFF171B21;g.fill(left,cy,left+w,cy+52,bg);if(en)g.fill(left,cy,left+4,cy+52,accent());text(g,m.getName(),left+16,cy+11,0xFFE7E8EC);text(g,description(m),left+16,cy+31,0xFF69707B);int sx=left+w-52;g.fill(sx,cy+17,sx+38,cy+35,en?accent():0xFF303640);int end=en?sx+22:sx+3;int knob=(int)(sx+3+(end-(sx+3))*p);g.fill(knob,cy+19,knob+12,cy+33,0xFFEDEEF2);}
    }

    private String description(FuxModule m){return switch(m.getName()){
        case "Sprint"->"Automatically sprints while moving forward.";case "AutoWalk"->"Automatically moves forward while active.";case "AutoJump"->"Enables automatic jumping.";case "FOV"->"Sets the client field of view to 100.";case "Fullbright"->"Raises client gamma while enabled.";
        case "Trail"->"Dynamic particle trail behind the player.";case "Bamboo Hat"->"Adds a lightweight animated halo.";case "China Clouds"->"Animated cloud particles around the player.";case "Hit Particles"->"Particles when an entity takes damage.";case "NoFall"->"Suppresses fall-distance accumulation.";case "Velocity"->"Reduces incoming knockback.";default->"Client module";};}

    private void drawSettings(GuiGraphics g,int left,int top,int w,int mx,int my){
        text(g,"Interface",left,top,0xFF8F94A0);toggle(g,"Animations",FuxSettingsRegistry.ANIMATIONS.get(),left,top+26,w,mx,my);toggle(g,"Compact mode",FuxSettingsRegistry.COMPACT_MODE.get(),left,top+72,w,mx,my);
        g.fill(left,top+118,left+w,top+174,0xFF181B22);text(g,"Zoom camera",left+15,top+131,0xFFE2E3E8);text(g,String.format("FOV %.0f  •  Z",FuxSettingsRegistry.ZOOM_FOV.get()),left+15,top+151,accent());
        g.fill(left,top+190,left+w,top+246,0xFF181B22);text(g,"UI scale",left+15,top+203,0xFFE2E3E8);text(g,String.format("%.2f",FuxSettingsRegistry.UI_SCALE.get()),left+w-50,top+203,accent());
        g.fill(left,top+262,left+w,top+312,inside(mx,my,left,top+262,w,50)?0xFF20252E:0xFF181B22);text(g,"HUD editor",left+15,top+275,0xFFE2E3E8);text(g,"Drag compass, info and Target ESP",left+15,top+294,accent());
    }
    private void toggle(GuiGraphics g,String n,boolean val,int x,int y,int w,int mx,int my){g.fill(x,y,x+w,y+38,inside(mx,my,x,y,w,38)?0xFF20242D:0xFF181B22);text(g,n,x+14,y+11,0xFFE2E3E8);int sx=x+w-48;g.fill(sx,y+10,sx+34,y+26,val?accent():0xFF30343D);g.fill(val?sx+20:sx+2,y+12,val?sx+31:sx+13,y+24,0xFFEDEEF2);}

    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button!=0)return super.mouseClicked(mx,my,button);mx/=scale;my/=scale;
        if(inside(mx,my,x+W-50,y+15,24,24)){closeAnimated();return true;}if(inside(mx,my,x,y,W,55)){dragging=true;ox=mx-x;oy=my-y;return true;}
        for(int i=0;i<CATS.length;i++){int cy=y+84+i*48;if(inside(mx,my,x+10,cy,SIDE-20,38)){category=i;categoryChangedAt=System.currentTimeMillis();return true;}}
        if(category==6){int left=x+SIDE+30,top=y+76,w=W-SIDE-58;if(inside(mx,my,left,top+26,w,38)){FuxSettingsRegistry.ANIMATIONS.set(!FuxSettingsRegistry.ANIMATIONS.get());FuxSettingsRegistry.save();return true;}if(inside(mx,my,left,top+72,w,38)){FuxSettingsRegistry.COMPACT_MODE.set(!FuxSettingsRegistry.COMPACT_MODE.get());FuxSettingsRegistry.save();return true;}if(inside(mx,my,left,top+118,w,56)){double n=FuxSettingsRegistry.ZOOM_FOV.get()+5;if(n>60)n=10;FuxSettingsRegistry.ZOOM_FOV.set(n);FuxSettingsRegistry.save();return true;}if(inside(mx,my,left,top+190,w,56)){double n=FuxSettingsRegistry.UI_SCALE.get()+.1;if(n>1.5)n=.75;FuxSettingsRegistry.UI_SCALE.set(Math.round(n*100)/100.0);FuxSettingsRegistry.save();return true;}if(inside(mx,my,left,top+262,w,50)){minecraft.setScreen(new FuxHudEditorScreen(this));return true;}}
        else {var ms=FuxModuleManager.byCategory(CATS[category]);int left=x+SIDE+30,top=y+76,w=W-SIDE-58;for(int i=0;i<ms.size();i++)if(inside(mx,my,left,top+i*62,w,52)){ms.get(i).toggle();changed.put(ms.get(i).getName(),System.currentTimeMillis());return true;}}
        return super.mouseClicked(mx*scale,my*scale,button);
    }
    private void closeAnimated(){if(!closing){closing=true;closingAt=System.currentTimeMillis();}}
    @Override public void onClose(){closeAnimated();}
    @Override public boolean mouseDragged(double mx,double my,int b,double dx,double dy){if(dragging&&b==0&&!closing){mx/=scale;my/=scale;x=clamp((int)(mx-ox),10,Math.max(10,(int)(width/scale)-W-10));y=clamp((int)(my-oy),10,Math.max(10,(int)(height/scale)-H-10));return true;}return super.mouseDragged(mx,my,b,dx,dy);}
    @Override public boolean mouseReleased(double mx,double my,int b){if(b==0)dragging=false;return super.mouseReleased(mx,my,b);}
    @Override public boolean keyPressed(int key,int scan,int mod){if(key==256){closeAnimated();return true;}return super.keyPressed(key,scan,mod);}
    private void text(GuiGraphics g,String s,int x,int y,int color){g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(1.08f,1.08f,1);g.drawString(font,s,0,0,color,false);g.pose().popPose();}
    private int accent(){return switch(FuxSettingsRegistry.ACCENT.get()){case "BLUE"->0xFF4F8CFF;case "GREEN"->0xFF45C98A;default->0xFF8B5CF6;};}
    private static float fract(float v){return v-(float)Math.floor(v);}private static boolean inside(double mx,double my,int x,int y,int w,int h){return mx>=x&&mx<x+w&&my>=y&&my<y+h;}private static int clamp(int v,int min,int max){return Math.max(min,Math.min(max,v));}
}
