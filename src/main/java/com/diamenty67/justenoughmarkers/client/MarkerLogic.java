package com.diamenty67.justenoughmarkers.client;

import com.diamenty67.justenoughmarkers.JEMConfig;
import com.diamenty67.justenoughmarkers.JEMConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Decides whether a recipe should display the marker, and where. Shared by the JEI and EMI
 * integrations so both recipe viewers stay in sync and behave identically.
 */
public final class MarkerLogic {
    public static final int SIZE = 12;

    private MarkerLogic() {}

    /**
     * @param recipeId  the ID of the underlying recipe, or null if this recipe/display has no
     *                  real backing recipe (e.g. a purely visual/info entry)
     * @param outputIds the registry IDs of the recipe's outputs; only evaluated when needed
     *                  (no recipe ID and outputFilter is not empty), same as before
     */
    public static boolean shouldShowMarker(@Nullable ResourceLocation recipeId, Supplier<List<ResourceLocation>> outputIds) {
        if (JEMConfig.COMMON.onlySpecificRecipeID.get()) {
            // Displays the marker on all recipes
            return true;
        }

        if (recipeId != null) {
            // Recipes with ID → only recipeIdFilter
            String idString = recipeId.toString();
            return JEMConfig.COMMON.getRecipeIdFilters().stream().anyMatch(idString::contains);
        }

        // Recipes without ID → only outputFilter
        List<? extends String> outputFilter = JEMConfig.COMMON.outputFilter.get();
        if (outputFilter.isEmpty()) {
            return false;
        }
        for (ResourceLocation outputId : outputIds.get()) {
            if (outputId != null && outputFilter.contains(outputId.toString())) {
                return true;
            }
        }
        return false;
    }

    /** Top-left corner of the marker, in the recipe display's own coordinate space. */
    public static int[] computeMarkerPosition(String categoryId, int width, int height) {
        int x = width - SIZE + JEMConfig.COMMON.defaultOffsetX.get();
        int y = height - SIZE + JEMConfig.COMMON.defaultOffsetY.get();

        // Checks if a category has been configured
        for (String entry : JEMConfig.COMMON.categories.get()) {
            String[] parts = entry.split(";");
            if (parts.length == 3 && parts[0].equals(categoryId)) {
                try {
                    x = width - SIZE + Integer.parseInt(parts[1]);
                    y = height - SIZE + Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {}
            }
        }
        return new int[]{x, y};
    }

    public static List<Component> buildTooltip() {
        return List.of(
                Component.literal(JEMConfig.COMMON.tooltipLine1.get()),
                Component.literal(JEMConfig.COMMON.tooltipLine2.get()),
                Component.literal(JEMConstants.MOD_NAME)
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)
        );
    }
}
