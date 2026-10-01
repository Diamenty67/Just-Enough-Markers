package com.diamenty67.justenoughmarkers.client.jei;

import com.diamenty67.justenoughmarkers.JEMConstants;
import com.diamenty67.justenoughmarkers.client.MarkerLogic;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.registration.IAdvancedRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JEIMarkerPlugin implements IModPlugin {

    private static final ResourceLocation ID = JEMConstants.rl("jei_plugin");
    private static final ResourceLocation MARKER_ICON = JEMConstants.rl("textures/marker.png");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.getJeiHelpers().getAllRecipeTypes().forEach(recipeType ->
                registration.addRecipeCategoryDecorator(recipeType, new Decorator<>())
        );
    }

    private static class Decorator<T> implements IRecipeCategoryDecorator<T> {

        @Override
        public void draw(T recipe, IRecipeCategory<T> recipeCategory,
                         IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                         double mouseX, double mouseY) {

            ResourceLocation id = resolveRecipeId(recipeCategory, recipe);

            boolean showMarker = MarkerLogic.shouldShowMarker(id, () -> recipeSlotsView.getSlotViews()
                    .stream()
                    .map(slot -> slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK))
                    .flatMap(Optional::stream)
                    .map(stack -> stack.getItem().builtInRegistryHolder().key().location())
                    .toList());

            if (!showMarker) return;

            // --- Marker position ---
            String categoryId = recipeCategory.getRecipeType().getUid().toString();
            int[] pos = MarkerLogic.computeMarkerPosition(categoryId, recipeCategory.getWidth(), recipeCategory.getHeight());
            int pX = pos[0];
            int pY = pos[1];

            // --- Drawing the marker ---
            guiGraphics.blit(MARKER_ICON, pX, pY, 0, 0, MarkerLogic.SIZE, MarkerLogic.SIZE, MarkerLogic.SIZE, MarkerLogic.SIZE);

            // --- Tooltip when hovering ---
            if (mouseX >= pX && mouseX <= (pX + MarkerLogic.SIZE) &&
                    mouseY >= pY && mouseY <= (pY + MarkerLogic.SIZE)) {

                var font = Minecraft.getInstance().font;
                List<FormattedCharSequence> lines = MarkerLogic.buildTooltip().stream()
                        .flatMap(c -> font.split(c, 200).stream())
                        .toList();

                guiGraphics.renderTooltip(font, lines, (int) mouseX, (int) mouseY);
            }
        }

        @Nullable
        private static <R> ResourceLocation resolveRecipeId(IRecipeCategory<R> recipeCategory, R recipe) {
            return recipeCategory.getRegistryName(recipe);
        }
    }
}
