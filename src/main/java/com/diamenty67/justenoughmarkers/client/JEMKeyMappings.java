package com.diamenty67.justenoughmarkers.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/** JEM's key binds. Registered from {@link com.diamenty67.justenoughmarkers.JustEnoughMarkers}, client-only. */
public final class JEMKeyMappings {

    /** Toggles {@link MoveMode}. Unbound by default; the player assigns a key in Controls. */
    public static final KeyMapping TOGGLE_MOVE_MODE = new KeyMapping(
            "key.jem.toggle_move_mode",
            KeyConflictContext.UNIVERSAL,
            InputConstants.UNKNOWN,
            "key.categories.jem"
    );

    private JEMKeyMappings() {}

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_MOVE_MODE);
    }

    /**
     * {@code KeyMapping.consumeClick()} (the usual way to react to a key bind) only tracks a press
     * while no screen is open: vanilla deliberately gates it that way so gameplay hotkeys don't
     * also fire while typing in a text field or clicking GUI buttons (see
     * {@code KeyboardHandler.keyPress}, which only calls {@code KeyMapping.click(...)} inside its
     * {@code if (this.minecraft.screen == null)} branch). Since this key must work while JEI's or
     * EMI's recipe screen is open, it is detected here instead, from Forge's raw
     * {@code InputEvent.Key}, which fires unconditionally regardless of what screen (if any) is
     * open or whether that screen already consumed the key press.
     */
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;
        if (!TOGGLE_MOVE_MODE.matches(event.getKey(), event.getScanCode())) return;

        // Still must not fire while the player is actually typing (chat, a sign, JEI/EMI's own
        // search box...), the same guard vanilla uses for its own always-on narrator toggle key.
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null && screen.getFocused() instanceof EditBox editBox && editBox.canConsumeInput()) {
            return;
        }

        MoveMode.toggle();
    }
}
