package com.diamenty67.justenoughmarkers;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class JEMConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final JEMConfig COMMON;

    public final ForgeConfigSpec.ConfigValue<Object> recipeIdFilter;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> outputFilter;
    public final ForgeConfigSpec.ConfigValue<String> tooltipLine1;
    public final ForgeConfigSpec.ConfigValue<String> tooltipLine2;
    public final ForgeConfigSpec.BooleanValue hideTooltipDetails;
    public final ForgeConfigSpec.BooleanValue onlySpecificRecipeID;
    public final ForgeConfigSpec.IntValue defaultOffsetX;
    public final ForgeConfigSpec.IntValue defaultOffsetY;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> categories;


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new JEMConfig(builder);
        COMMON_SPEC = builder.build();
    }

    private JEMConfig(ForgeConfigSpec.Builder builder) {
        // --- General settings ---
        builder.comment("General settings for Just Enough Markers")
                .push("general");
            recipeIdFilter = builder
                    .comment("List of texts used to filter recipes with a Recipe ID.",
                            "Recipes with a Recipe ID containing any of these texts will display the marker",
                            " Format: [\"namespace\"] or [\"namespace1\", \"namespace2\"]",
                            " A single text (recipeIdFilter = \"kubejs\") from older versions is still accepted",
                            " Default: [\"kubejs\", \"kjs\"]")
                    .define("recipeIdFilter", (Object) List.of("kubejs", "kjs"), JEMConfig::isValidRecipeIdFilter);
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
            hideTooltipDetails = builder
                    .comment("If true, the tooltip only shows \"JEM\" instead of tooltipLine1, tooltipLine2",
                            "and the \"Just Enough Markers\" credit line.",
                            " Default: false")
                    .define("hideTooltipDetails", false);
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
                    .defineInRange("defaultOffsetX", 0, -1000, 1000);
            defaultOffsetY = builder
                    .comment("Default Y offset for the marker if no category override exists")
                    .defineInRange("defaultOffsetY", 0, -1000, 1000);
        builder.pop();

        // --- Custom marker categories ---
        builder.comment("Custom offsets for specific recipe categories")
                .push("CustomOffsetsMarkerCategories");
            categories = builder
                    .comment("Adjust marker position for specific recipe categories",
                            " Format: \"categoryId;offsetX;offsetY\"",
                            " Example: [\"minecraft:crafting;-45;-14\", \"emi:anvil_repairing;4;10\"]",
                            " Automatically updated by in-game marker move mode (see the key binds)")
                    .defineListAllowEmpty(
                            "categories",
                            () -> List.of(),
                            obj -> obj instanceof String
                    );
        builder.pop();
    }

    /**
     * The texts a recipe ID has to contain to display the marker. The config normally holds a list,
     * but a single text (the format of older versions) is still supported.
     */
    public List<String> getRecipeIdFilters() {
        Object value = recipeIdFilter.get();
        if (value instanceof String text) {
            return List.of(text);
        }
        if (value instanceof List<?> texts) {
            return texts.stream().map(String.class::cast).toList();
        }
        return List.of();
    }

    /**
     * Replaces (or adds) the saved offset for a recipe category, or removes its entry entirely
     * when the new offset matches the default, keeping {@code categories} clean. Used by the
     * in-game marker move mode to save a drag, and to reset a single marker back to default.
     */
    public void setCategoryOffset(String categoryId, int offsetX, int offsetY) {
        List<String> updated = new ArrayList<>();
        for (String entry : categories.get()) {
            String[] parts = entry.split(";");
            if (parts.length == 3 && parts[0].equals(categoryId)) continue; // drop the previous entry, if any
            updated.add(entry);
        }
        if (offsetX != defaultOffsetX.get() || offsetY != defaultOffsetY.get()) {
            updated.add(categoryId + ";" + offsetX + ";" + offsetY);
        }
        categories.set(updated);
    }

    private static boolean isValidRecipeIdFilter(Object value) {
        return value instanceof String
                || (value instanceof List<?> list && list.stream().allMatch(String.class::isInstance));
    }
}
