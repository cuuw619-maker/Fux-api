package com.cuuw619.fuxapi.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FuxModuleManager {
    private static final List<FuxModule> MODULES=new ArrayList<>();
    private FuxModuleManager(){}
    public static void init(){if(!MODULES.isEmpty())return;MODULES.add(new FuxSprintModule());MODULES.add(new FuxAutoWalkModule());MODULES.add(new FuxAutoJumpModule());MODULES.add(new FuxFovModule());MODULES.add(new FuxFullbrightModule());MODULES.add(new FuxTrailModule());MODULES.add(new FuxBambooHatModule());MODULES.add(new FuxChinaCloudsModule());MODULES.add(new FuxHitParticlesModule());}
    public static List<FuxModule> all(){return Collections.unmodifiableList(MODULES);}
    public static List<FuxModule> byCategory(String category){return MODULES.stream().filter(m->m.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());}
    public static FuxModule byName(String name){return MODULES.stream().filter(m->m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);}
    public static void tick(){for(FuxModule m:MODULES)if(m.isEnabled())m.onClientTick();}
}
