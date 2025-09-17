package com.diamenty67.justenoughmarkers;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModLoadingContext;

@Mod(ModConstants.MOD_ID)
public class JustEnoughMarkers {
    public JustEnoughMarkers() {
        // Enregistrement de la config
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }
}
