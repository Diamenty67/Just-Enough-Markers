package com.diamenty67.justenoughmarkers;

import com.diamenty67.justenoughmarkers.client.ConfigAutoReloader;
import com.diamenty67.justenoughmarkers.client.JEMKeyMappings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.ModLoadingContext;

@Mod(JEMConstants.MOD_ID)
public class JustEnoughMarkers {
    public JustEnoughMarkers() {
        // Quarantine an unparseable config file before Forge trips over it and crashes the game
        ConfigPreflight.checkAndQuarantineIfBroken();

        // Saving the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, JEMConfig.COMMON_SPEC);

        // The markers (and move mode) are only drawn/used on the client
        if (FMLEnvironment.dist.isClient()) {
            ConfigAutoReloader.start();
            FMLJavaModLoadingContext.get().getModEventBus().addListener(JEMKeyMappings::register);
            MinecraftForge.EVENT_BUS.addListener(JEMKeyMappings::onKeyInput);
        }
    }
}
