package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.module.FuxModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid=FuxApi.MOD_ID,value=Dist.CLIENT)
public final class FuxVisualEffects {
    private static int ticks;
    private FuxVisualEffects(){}

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event){
        Minecraft mc=Minecraft.getInstance(); LocalPlayer p=mc.player;
        if(p==null||mc.level==null||mc.screen!=null)return; ticks++;
        if(enabled("Trail")&&ticks%2==0){
            double back=.38;double x=p.getX()-p.getDeltaMovement().x*4.0-back*Math.sin(Math.toRadians(p.getYRot()));double y=p.getY()+.12;double z=p.getZ()-p.getDeltaMovement().z*4.0+back*Math.cos(Math.toRadians(p.getYRot()));
            mc.level.addParticle(ParticleTypes.END_ROD,x,y,z,0,.012,0);
        }
        if(enabled("Bamboo Hat")&&ticks%3==0){double t=ticks*.12;for(int i=0;i<10;i++){double a=t+i*Math.PI/5.0,r=.62;mc.level.addParticle(ParticleTypes.HAPPY_VILLAGER,p.getX()+Math.cos(a)*r,p.getY()+2.25+Math.sin(t*1.5)*.04,p.getZ()+Math.sin(a)*r,0,.01,0);}}
        if(enabled("China Clouds")&&ticks%5==0){double t=ticks*.07;for(int i=0;i<4;i++){double a=t+i*Math.PI/2.0;mc.level.addParticle(ParticleTypes.CLOUD,p.getX()+Math.cos(a)*.75,p.getY()+.1+Math.sin(a)*.08,p.getZ()+Math.sin(a)*.75,0,.01,0);}}
        if(enabled("Hit Particles")&&p.hurtTime>0&&ticks%2==0){for(int i=0;i<5;i++)mc.level.addParticle(ParticleTypes.CRIT,p.getX()+(mc.level.random.nextDouble()-.5),p.getY()+1+mc.level.random.nextDouble(),p.getZ()+(mc.level.random.nextDouble()-.5),0,.04,0);}
        if(enabled("Trajectory")&&ticks%2==0){
            var look=p.getViewVector(1.0F);double ox=p.getX(),oy=p.getEyeY(),oz=p.getZ();
            for(int i=1;i<=18;i+=2){double gravity=.008*i*i;double x=ox+look.x*i*.35,y=oy+look.y*i*.35-gravity,z=oz+look.z*i*.35;mc.level.addParticle(ParticleTypes.END_ROD,x,y,z,0,0,0);}
        }
        if(enabled("Damage Numbers")&&p.hurtTime>0&&ticks%3==0){
            // Lightweight Pulse-style impact feedback; damage text can be layered by the HUD without world-space labels.
            for(int i=0;i<2;i++)mc.level.addParticle(ParticleTypes.DAMAGE_INDICATOR,p.getX()+(mc.level.random.nextDouble()-.5),p.getY()+1.0,p.getZ()+(mc.level.random.nextDouble()-.5),0,.03,0);
        }
    }
    private static boolean enabled(String name){var m=FuxModuleManager.byName(name);return m!=null&&m.isEnabled();}
}
