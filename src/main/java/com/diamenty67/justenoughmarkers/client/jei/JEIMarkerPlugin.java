package com.diamenty67.justenoughmarkers.client.jei;

import com.diamenty67.justenoughmarkers.JEMConfig;
import com.diamenty67.justenoughmarkers.JEMConstants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.registration.IAdvancedRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
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
    private static final int SIZE = 12;

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

            // --- Fetching the recipe ID ---
            ResourceLocation id = resolveRecipeId(recipeCategory, recipe);

            // --- User configuration ---
            boolean debugMode = JEMConfig.COMMON.onlySpecificRecipeID.get(); // debug/dev mode
            String recipeFilter = JEMConfig.COMMON.recipeIdFilter.get();
            List<? extends String> outputFilter = JEMConfig.COMMON.outputFilter.get();

            boolean showMarker = false;

            if (debugMode) {
                // Always display the marker for debugging
                showMarker = true;
            } else if (id != null) {
                // Recipes with ID → only recipeIdFilter
                showMarker = id.toString().contains(recipeFilter);
            } else {
                // Recipes without ID → only outputFilter
                if (!outputFilter.isEmpty()) {
                    var outputs = recipeSlotsView.getSlotViews()
                            .stream()
                            .map(slot -> slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK))
                            .flatMap(Optional::stream)
                            .toList();

                    for (ItemStack stack : outputs) {
                        var itemId = stack.getItem().builtInRegistryHolder().key().location();
                        if (outputFilter.contains(itemId.toString())) {
                            showMarker = true;
                            break;
                        }
                    }
                }
            }

            if (!showMarker) return;

            // --- Marker position ---
            int pX = recipeCategory.getWidth() - SIZE + JEMConfig.COMMON.defaultOffsetX.get();
            int pY = recipeCategory.getHeight() - SIZE + JEMConfig.COMMON.defaultOffsetY.get();

            // Checks if a category has been configured
            List<? extends String> configuredCategories = JEMConfig.COMMON.categories.get();
            for (String entry : configuredCategories) {
                String[] parts = entry.split(";");
                if (parts.length == 3 && parts[0].equals(recipeCategory.getRecipeType().getUid().toString())) {
                    try {
                        int offsetX = Integer.parseInt(parts[1]);
                        int offsetY = Integer.parseInt(parts[2]);
                        pX = recipeCategory.getWidth() - SIZE + offsetX;
                        pY = recipeCategory.getHeight() - SIZE + offsetY;
                    } catch (NumberFormatException ignored) {}
                }
            }

            // --- Drawing the marker ---
            guiGraphics.blit(MARKER_ICON, pX, pY, 0, 0, SIZE, SIZE, SIZE, SIZE);

            // --- Tooltip when hovering ---
            if (mouseX >= pX && mouseX <= (pX + SIZE) &&
                    mouseY >= pY && mouseY <= (pY + SIZE)) {

                var font = Minecraft.getInstance().font;
                var tooltip = List.of(
                        Component.literal(JEMConfig.COMMON.tooltipLine1.get()),
                        Component.literal(JEMConfig.COMMON.tooltipLine2.get()),
                        Component.literal(JEMConstants.MOD_NAME)
                                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)
                );

                List<FormattedCharSequence> lines = tooltip.stream()
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
