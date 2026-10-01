package com.diamenty67.justenoughmarkers.client.emi;

import com.diamenty67.justenoughmarkers.JEMConstants;
import com.diamenty67.justenoughmarkers.client.MarkerLogic;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.config.EmiConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * EMI is a separate recipe viewer from JEI, with its own plugin API: JEI's
 * {@code IRecipeCategoryDecorator} (used by {@link com.diamenty67.justenoughmarkers.client.jei.JEIMarkerPlugin})
 * is never called when EMI's own recipe screen is the one being shown, which is why the markers
 * disappeared when EMI was installed. This plugin adds the same marker through EMI's equivalent,
 * {@code EmiRecipeDecorator}, so both viewers behave the same way.
 * <p>
 * Discovered by EMI itself (not Forge) by scanning mod files for this annotation, exactly like
 * {@code @JeiPlugin} is discovered by JEI. If EMI is not installed, EMI never runs that scan, so
 * this class is simply never touched: no error, nothing else in the mod depends on it.
 */
@EmiEntrypoint
@SuppressWarnings("unused")
public class EMIMarkerPlugin implements EmiPlugin {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation MARKER_ICON = JEMConstants.rl("textures/marker.png");

    @Override
    public void register(EmiRegistry registry) {
        enableRecipeDecorators();
        registry.addRecipeDecorator(EMIMarkerPlugin::decorate);
    }

    /**
     * EMI only calls registered {@code EmiRecipeDecorator}s (the mechanism used below) when its
     * own {@code EmiConfig.showRecipeDecorators} setting is on, which EMI defaults to off for every
     * player ("dev.show-recipe-decorators" — its own comment calls this "typically developer facing
     * ... not useful for players"). Without this, the marker is silently never drawn in EMI's screen,
     * even though it registers successfully. This flips that setting on so the marker actually
     * appears; {@code EmiConfig} is an internal EMI class, not part of its stable public API, so this
     * is wrapped defensively in case a future EMI version renames or removes the field.
     */
    private static void enableRecipeDecorators() {
        try {
            EmiConfig.showRecipeDecorators = true;
        } catch (Throwable t) {
            LOGGER.warn("Could not enable EMI's recipe decorators; markers may not appear in EMI's recipe screen", t);
        }
    }

    private static void decorate(EmiRecipe recipe, WidgetHolder widgets) {
        ResourceLocation id = resolveRecipeId(recipe);

        boolean showMarker = MarkerLogic.shouldShowMarker(id, () -> recipe.getOutputs()
                .stream()
                .map(EmiStack::getId)
                .toList());

        if (!showMarker) return;

        // --- Marker position ---
        String categoryId = recipe.getCategory().getId().toString();
        int width = recipe.getDisplayWidth();
        int height = recipe.getDisplayHeight();
        int[] pos = MarkerLogic.computeMarkerPosition(categoryId, width, height);

        // --- Drawing the marker + tooltip when hovering ---
        // EMI hit-tests the widget bounds itself, unlike JEI where the mouse position had to be
        // checked manually against the marker's rectangle.
        //
        // The 6-int addTexture(...) overload isn't used here: it hardcodes the *assumed* source
        // texture to 256x256 (meant for picking an icon out of a big shared sprite sheet), which
        // would sample only a tiny corner of our actual, standalone 12x12 marker.png and render
        // essentially nothing. Passing SIZE for width/height/regionWidth/regionHeight/textureWidth/
        // textureHeight alike (all equal, so the ratio is 1:1) instead means "sample the whole
        // source image", exactly matching JEI's guiGraphics.blit(..., SIZE, SIZE, SIZE, SIZE) call.
        widgets.addTexture(MARKER_ICON, pos[0], pos[1], MarkerLogic.SIZE, MarkerLogic.SIZE, 0, 0,
                        MarkerLogic.SIZE, MarkerLogic.SIZE, MarkerLogic.SIZE, MarkerLogic.SIZE)
                .tooltipText(MarkerLogic.buildTooltip());
    }

    /**
     * Mirrors JEI's {@code IRecipeCategoryDecorator} semantics: a recipe "has an ID" only when it
     * is genuinely backed by a registered {@link Recipe} (e.g. a KubeJS recipe), as opposed to a
     * purely visual/info entry that EMI still has to give some internal ID to.
     */
    @Nullable
    private static ResourceLocation resolveRecipeId(EmiRecipe recipe) {
        Recipe<?> backing = recipe.getBackingRecipe();
        return backing != null ? backing.getId() : null;
    }
}
