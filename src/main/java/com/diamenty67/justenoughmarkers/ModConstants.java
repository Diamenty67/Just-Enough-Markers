package com.diamenty67.justenoughmarkers;

import net.minecraft.resources.ResourceLocation;

public class ModConstants {
    public static final String MOD_ID = "jem";
    public static final String MOD_NAME = "Just Enough Markers";

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
