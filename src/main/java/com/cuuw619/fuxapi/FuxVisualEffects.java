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
        Minecraft mc=Minecraft.getInstance(); LocalPlayer p=mc.player; if(p==null||mc.level==null||mc.screen!=null)return; ticks++;
        if(FuxModuleManager.byName("Trail")!=null&&FuxModuleManager.byName("Trail").isEnabled()&&ticks%2==0){
            double back=0.38; double x=p.getX()-p.getDeltaMovement().x*4.0-back*Math.sin(Math.toRadians(p.getYRot())); double y=p.getY()+0.12; double z=p.getZ()-p.getDeltaMovement().z*4.0+back*Math.cos(Math.toRadians(p.getYRot()));
            mc.level.addParticle(ParticleTypes.END_ROD,x,y,z,0,0.012,0);
        }
        if(FuxModuleManager.byName("Bamboo Hat")!=null&&FuxModuleManager.byName("Bamboo Hat").isEnabled()&&ticks%3==0){
            double t=ticks*.12; for(int i=0;i<8;i++){double a=t+i*Math.PI/4.0;double r=.62;mc.level.addParticle(ParticleTypes.HAPPY_VILLAGER,p.getX()+Math.cos(a)*r,p.getY()+2.25+Math.sin(t*1.5)*.04,p.getZ()+Math.sin(a)*r,0,0.01,0);}
        }
        if(FuxModuleManager.byName("China Clouds")!=null&&FuxModuleManager.byName("China Clouds").isEnabled()&&ticks%5==0){
            double t=ticks*.07;for(int i=0;i<3;i++){double a=t+i*2.094;mc.level.addParticle(ParticleTypes.CLOUD,p.getX()+Math.cos(a)*.75,p.getY()+.1+Math.sin(a)*.08,p.getZ()+Math.sin(a)*.75,0,0.01,0);}
        }
        if(FuxModuleManager.byName("Hit Particles")!=null&&FuxModuleManager.byName("Hit Particles").isEnabled()&&p.hurtTime>0&&ticks%2==0){
            for(int i=0;i<3;i++)mc.level.addParticle(ParticleTypes.CRIT,p.getX()+(mc.level.random.nextDouble()-.5),p.getY()+1+mc.level.random.nextDouble(),p.getZ()+(mc.level.random.nextDouble()-.5),0,0.04,0);
        }
    }
}
