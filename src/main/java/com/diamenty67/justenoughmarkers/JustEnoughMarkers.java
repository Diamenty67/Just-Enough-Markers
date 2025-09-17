package com.diamenty67.justenoughmarkers;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModLoadingContext;

@Mod(JEMConstants.MOD_ID)
public class JustEnoughMarkers {
    public JustEnoughMarkers() {
        // Saving the config
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, JEMConfig.COMMON_SPEC);
    }
}
