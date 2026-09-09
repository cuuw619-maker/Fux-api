package com.cuuw619.fuxapi;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.neoforged.fml.common.Mod;

@Mod(FuxApi.MOD_ID)
public final class FuxApi {
    public static final String MOD_ID = "fuxapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FuxApi() {
        LOGGER.info("Fux API {} loaded", "0.1.0");
    }
}
