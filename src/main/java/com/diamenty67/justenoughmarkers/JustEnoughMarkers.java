package com.diamenty67.justenoughmarkers;

import com.diamenty67.justenoughmarkers.client.JEMKeyMappings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(JEMConstants.MOD_ID)
public class JustEnoughMarkers {
    public JustEnoughMarkers(IEventBus modEventBus, ModContainer modContainer) {
        // Saving the config
        modContainer.registerConfig(ModConfig.Type.COMMON, JEMConfig.COMMON_SPEC);

        // The markers (and move mode) are only drawn/used on the client
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(JEMKeyMappings::register);
            NeoForge.EVENT_BUS.addListener(JEMKeyMappings::onKeyInput);
        }
    }
}
