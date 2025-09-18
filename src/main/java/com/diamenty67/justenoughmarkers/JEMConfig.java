package com.diamenty67.justenoughmarkers;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class JEMConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final JEMConfig COMMON;

    public final ModConfigSpec.ConfigValue<String> recipeIdFilter;
    public final ModConfigSpec.ConfigValue<List<? extends String>> outputFilter;
    public final ModConfigSpec.ConfigValue<String> tooltipLine1;
    public final ModConfigSpec.ConfigValue<String> tooltipLine2;
    public final ModConfigSpec.BooleanValue onlySpecificRecipeID;
    public final ModConfigSpec.IntValue defaultOffsetX;
    public final ModConfigSpec.IntValue defaultOffsetY;
    public final ModConfigSpec.ConfigValue<List<? extends String>> categories;


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        COMMON = new JEMConfig(builder);
        COMMON_SPEC = builder.build();
    }

    private JEMConfig(ModConfigSpec.Builder builder) {
        // --- General settings ---
        builder.comment("General settings for Just Enough Markers")
                .push("general");
            recipeIdFilter = builder
                    .comment("Text used to filter recipes with an Recipe ID.",
                            "Recipes with an Recipe ID containing this text will display the marker",
                            " Default: kubejs")
                    .define("recipeIdFilter", "kubejs");
            outputFilter = builder
                    .comment("List of item IDs for recipes WITHOUT an ID that should display the marker",
                            " Format: [\"modId:item\"]",
                            " Example: [\"mysticalagriculture:inferium_seeds\", \"ironsspellbooks:fireball_scroll\"]")
                    .defineListAllowEmpty(
                            "outputFilter",
                            () -> List.of(),
                            obj -> obj instanceof String
                    );
            tooltipLine1 = builder
                    .comment("First line of the tooltip when hovering over the marker",
                            " Default: Modified recipe")
                    .define("tooltipLine1", "Modified recipe");
            tooltipLine2 = builder
                    .comment("Second line of the tooltip when hovering over the marker",
                            " Default: According to the modpack creator")
                    .define("tooltipLine2", "According to the modpack creator");
        builder.pop();

        // --- Debug settings ---
        builder.comment("Debug settings for testing or development")
                .push("debug");

            onlySpecificRecipeID = builder
                    .comment("If true, the marker will display on all recipes (ignores filters)",
                            "Useful for testing or debugging",
                            " Default: false")
                    .define("onlySpecificRecipeID", false);
            defaultOffsetX = builder
                    .comment("Default X offset for the marker if no category override exists")
                    .defineInRange("defaultOffsetX", 19, -1000, 1000);
            defaultOffsetY = builder
                    .comment("Default Y offset for the marker if no category override exists")
                    .defineInRange("defaultOffsetY", -25, -1000, 1000);
        builder.pop();

        // --- Custom marker categories ---
        builder.comment("Custom offsets for specific recipe categories")
                .push("CustomOffsetsMarkerCategories");
            categories = builder
                    .comment("Adjust marker position for specific recipe categories",
                            " Format: \"categoryId;offsetX;offsetY\"",
                            " Example: \"minecraft;crafting;-14;-14\", \"ae2:charger;3;3\"")
                    .defineListAllowEmpty(
                            "categories",
                            () -> List.of("minecraft:compostable;0;0","minecraft:fuel;0;0","apothic_enchanting:enchanting;5;5","apothic_spawners:spawner_modifiers;0;0","easy_villagers:breeding;0;-19","easy_villagers:converting;0;-19","easy_villagers:incubating;0;-19","jei:information;0;-110","jei_mekanism_multiblocks:multiblock.mekanism.boiler;-135;-110","jei_mekanism_multiblocks:multiblock.mekanism.dynamic_tank;-135;-110","jei_mekanism_multiblocks:multiblock.mekanism.evaporation_plant;-135;-110","jei_mekanism_multiblocks:multiblock.mekanism.matrix;-135;-110","jei_mekanism_multiblocks:multiblock.mekanism.sps;-135;-110","jei_mekanism_multiblocks:multiblock.mekanismgenerators.fission_reactor;-135;-110","jei_mekanism_multiblocks:multiblock.mekanismgenerators.fusion_reactor;-135;-110","jei_mekanism_multiblocks:multiblock.mekanismgenerators.turbine;-135;-110","mysticalagriculture:enchanter;20;0","mysticalagriculture:reprocessor;20;0","mysticalagriculture:soul_extractor;20;0","mysticalagriculture:soulium_spawner;20;0","tombstone:combine;20;0","reliquary:infernal_tear;20;0","mob_grinding_utils:solidify;20;0","jeed:effect_info;0;-110","justenoughbreeding:breeding;0;0","powah:coolant;0;0","powah:heat_source;0;0","powah:magmatic;0;0","powah:reactor_fuel;0;0","powah:solid_coolant;0;0","tiab:resource_generator;0;0","chipped:workbench;0;-95","cataclysm:weapon_infusion;20;0","betterarcheology:identifying;-5;-5","jeresources:dungeon;0;-110","jeresources:enchantment;0;-115","jeresources:mob;0;-115","jeresources:plant;0;-115","jeresources:villager;0;0","jeresources:worldgen;0;-75","aether:ambrosium_enchanting;20;0","aether:block_placement_ban;20;0","aether:item_placement_ban;20;0","knightlib:great_chalice_interaction;0;0"),
                            obj -> obj instanceof String
                    );
        builder.pop();
    }
}
