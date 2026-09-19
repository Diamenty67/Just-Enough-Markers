package com.diamenty67.justenoughmarkers;

import com.diamenty67.justenoughmarkers.client.ConfigAutoReloader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.ModLoadingContext;

@Mod(JEMConstants.MOD_ID)
public class JustEnoughMarkers {
    public JustEnoughMarkers() {
        // Saving the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, JEMConfig.COMMON_SPEC);

        // The markers are only drawn on the client, so only reload the config there
        if (FMLEnvironment.dist.isClient()) {
            ConfigAutoReloader.start();
        }
    }
}
