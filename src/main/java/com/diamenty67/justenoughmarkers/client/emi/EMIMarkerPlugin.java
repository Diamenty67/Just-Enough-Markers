package com.diamenty67.justenoughmarkers.client.emi;

import com.diamenty67.justenoughmarkers.JEMConstants;
import com.diamenty67.justenoughmarkers.client.MarkerLogic;
import com.diamenty67.justenoughmarkers.client.MoveMode;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.config.EmiConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

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
        // EMI builds a recipe display's widgets once (not every frame, like JEI's draw() does), and
        // then reuses that same display across many frames without calling decorate() again. So a
        // widget is always added here, for every recipe, and whether the marker should actually be
        // visible right now is decided fresh every frame inside the widget's own render() instead.
        // Deciding it here once, like an earlier version of this plugin did, meant a recipe that
        // didn't qualify when its display was first built could never show a marker later just
        // because move mode got turned on afterwards — and conversely, a marker added while move
        // mode was on stayed on screen after turning move mode back off, until the display was
        // rebuilt (e.g. by navigating to a different page).
        String categoryId = recipe.getCategory().getId().toString();
        int width = recipe.getDisplayWidth();
        int height = recipe.getDisplayHeight();
        widgets.add(new MarkerWidget(recipe, categoryId, width, height));
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

    private static class MarkerWidget extends Widget {
        private final EmiRecipe recipe;
        private final String categoryId;
        private final int recipeWidth;
        private final int recipeHeight;

        // Updated every render() call, i.e. every frame: whether the marker currently has anything
        // to show, and if so where, for getBounds()/getTooltip() to agree with what was just drawn.
        private boolean visible;
        private int currentX;
        private int currentY;

        MarkerWidget(EmiRecipe recipe, String categoryId, int recipeWidth, int recipeHeight) {
            this.recipe = recipe;
            this.categoryId = categoryId;
            this.recipeWidth = recipeWidth;
            this.recipeHeight = recipeHeight;
        }

        @Override
        public Bounds getBounds() {
            return visible ? new Bounds(currentX, currentY, MarkerLogic.SIZE, MarkerLogic.SIZE) : Bounds.EMPTY;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            ResourceLocation id = resolveRecipeId(recipe);
            visible = MarkerLogic.shouldShowMarker(id, () -> recipe.getOutputs()
                    .stream()
                    .map(EmiStack::getId)
                    .toList());

            if (!visible) return;

            int[] pos = MarkerLogic.computeMarkerPosition(categoryId, recipeWidth, recipeHeight);
            int x = pos[0];
            int y = pos[1];

            ResourceLocation texture = MARKER_ICON;
            if (MoveMode.isActive()) {
                // `this` identifies this specific on-screen marker widget; several recipes shown
                // together often share the same categoryId, so that alone can't tell the dragged
                // marker apart from the others. EMI constructs one MarkerWidget per recipe display,
                // so `this` is a stable, unique identity for the whole drag gesture.
                MoveMode.Result result = MoveMode.update(this, categoryId, x, y, mouseX, mouseY, recipeWidth, recipeHeight);
                x = result.x();
                y = result.y();
                texture = MoveMode.textureFor(result.state());
            }

            currentX = x;
            currentY = y;

            // Whole-image blit: see JEIMarkerPlugin for why width/height/regionWidth/regionHeight/
            // textureWidth/textureHeight all equal SIZE here instead of the real texture dimensions.
            guiGraphics.blit(texture, x, y, 0, 0, MarkerLogic.SIZE, MarkerLogic.SIZE, MarkerLogic.SIZE, MarkerLogic.SIZE);
        }

        @Override
        public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
            if (!visible) return List.of();
            return MarkerLogic.buildTooltip().stream()
                    .map(Component::getVisualOrderText)
                    .map(ClientTooltipComponent::create)
                    .toList();
        }
    }
}
