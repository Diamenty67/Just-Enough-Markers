package com.diamenty67.justenoughmarkers.client;

import com.diamenty67.justenoughmarkers.JEMConfig;
import com.diamenty67.justenoughmarkers.JEMConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

/**
 * In-game "move mode": lets a player drag a marker to a new position with the mouse instead of
 * editing {@code categories} by hand. Shared by the JEI and EMI integrations.
 * <p>
 * Neither JEI's {@code IRecipeCategoryDecorator} nor EMI's {@code Widget} exposes a mouse-release
 * (or even a mouse-press) callback — JEI's decorator is draw-only, and EMI's {@code Widget} base
 * class has no {@code mouseReleased}. Both backends do hand us the current mouse position every
 * frame (draw()/render() runs continuously), so instead of needing global screen click events,
 * this polls the raw mouse button state directly (GLFW) once per real frame and does its own
 * press/drag/release edge detection from that — self-contained, and works identically for both
 * recipe viewers.
 */
public final class MoveMode {

    public enum State { DEFAULT, MOVED, DRAGGING }

    public record Result(int x, int y, State state) {}

    /** Below this, a gap between two polls is "two markers in the same frame", not two frames. */
    private static final long SAME_FRAME_NANOS = 1_000_000L; // 1 ms
    /** Above this, a gap means no marker was drawn for a while (screen closed/changed mid-drag). */
    private static final long STALE_DRAG_NANOS = 300_000_000L; // 300 ms

    private static boolean active = false;

    /**
     * Identity of the marker currently being dragged. Several recipes shown on the same page often
     * share one category (e.g. several different crafting recipes are all "minecraft:crafting"),
     * so tracking the drag by category id alone would make every marker of that category answer
     * to the same drag. This must be an identity unique to one on-screen marker instance — the
     * recipe object itself for JEI, the widget instance itself for EMI — compared by reference
     * ({@code ==}), not {@code equals()} (a recipe's own equals(), if it has one, is irrelevant here).
     */
    private static Object draggingInstanceKey = null;
    private static int grabDX, grabDY;
    private static int lastDragX, lastDragY;

    private static boolean leftDown = false;
    private static boolean leftWasDown = false;
    private static boolean leftJustPressed = false;
    private static boolean rightWasDown = false;
    private static boolean rightJustPressed = false;
    private static long lastPollNanos = 0L;

    private MoveMode() {}

    public static boolean isActive() {
        return active;
    }

    public static void toggle() {
        active = !active;
        if (!active) {
            draggingInstanceKey = null;
        }

        var player = Minecraft.getInstance().player;
        if (player == null) return;

        if (active) {
            player.displayClientMessage(Component.translatable("jem.message.move_mode.enabled")
                    .withStyle(ChatFormatting.GREEN), false);
            player.displayClientMessage(Component.translatable("jem.message.move_mode.hint")
                    .withStyle(ChatFormatting.GRAY), false);
        } else {
            player.displayClientMessage(Component.translatable("jem.message.move_mode.disabled")
                    .withStyle(ChatFormatting.RED), false);
        }
    }

    /** {@code onlySpecificRecipeID}, with move mode's temporary, in-memory-only override applied. */
    public static boolean effectiveOnlySpecificRecipeID() {
        return active || JEMConfig.COMMON.onlySpecificRecipeID.get();
    }

    /** {@code hideTooltipDetails}, with move mode's temporary, in-memory-only override applied. */
    public static boolean effectiveHideTooltipDetails() {
        return active || JEMConfig.COMMON.hideTooltipDetails.get();
    }

    /**
     * Must be called once per visible marker, every frame, only while {@link #isActive()}.
     * Handles press/drag/release and the per-marker reset click, and returns where to actually
     * draw the marker this frame (it follows the cursor while being dragged) and which visual
     * state it is in.
     *
     * @param instanceKey  identity of this specific on-screen marker (see {@link #draggingInstanceKey}) —
     *                     the recipe object for JEI, the marker widget itself for EMI
     * @param categoryId   the recipe category's id, as used in the {@code categories} config list
     * @param x            the marker's position this frame before any drag, local to the recipe display
     * @param y
     * @param mouseX       mouse position in that same local coordinate space
     * @param mouseY
     * @param recipeWidth  the recipe display's width/height, needed to convert a screen position
     * @param recipeHeight back into the offsetX/offsetY format {@code categories} is stored in
     */
    public static Result update(Object instanceKey, String categoryId, int x, int y, int mouseX, int mouseY, int recipeWidth, int recipeHeight) {
        pollInputOnce();

        boolean hovering = mouseX >= x && mouseX <= x + MarkerLogic.SIZE
                && mouseY >= y && mouseY <= y + MarkerLogic.SIZE;

        if (instanceKey == draggingInstanceKey) {
            if (leftDown) {
                lastDragX = mouseX - grabDX;
                lastDragY = mouseY - grabDY;
                return new Result(lastDragX, lastDragY, State.DRAGGING);
            }

            // The button was released since the last frame this marker was drawn: commit it.
            int offsetX = lastDragX - recipeWidth + MarkerLogic.SIZE;
            int offsetY = lastDragY - recipeHeight + MarkerLogic.SIZE;
            JEMConfig.COMMON.setCategoryOffset(categoryId, offsetX, offsetY);
            int committedX = lastDragX;
            int committedY = lastDragY;
            draggingInstanceKey = null;
            return new Result(committedX, committedY, resolveRestState(categoryId));
        }

        if (draggingInstanceKey == null && hovering) {
            if (leftJustPressed) {
                draggingInstanceKey = instanceKey;
                grabDX = mouseX - x;
                grabDY = mouseY - y;
                lastDragX = x;
                lastDragY = y;
                return new Result(x, y, State.DRAGGING);
            }
            if (rightJustPressed) {
                JEMConfig.COMMON.setCategoryOffset(categoryId, JEMConfig.COMMON.defaultOffsetX.get(), JEMConfig.COMMON.defaultOffsetY.get());
                return new Result(x, y, State.DEFAULT);
            }
        }

        return new Result(x, y, resolveRestState(categoryId));
    }

    public static ResourceLocation textureFor(State state) {
        return switch (state) {
            case DRAGGING -> JEMConstants.rl("textures/marker_dragging.png");
            case MOVED -> JEMConstants.rl("textures/marker_moved.png");
            case DEFAULT -> JEMConstants.rl("textures/marker_default.png");
        };
    }

    private static State resolveRestState(String categoryId) {
        for (String entry : JEMConfig.COMMON.categories.get()) {
            String[] parts = entry.split(";");
            if (parts.length == 3 && parts[0].equals(categoryId)) {
                return State.MOVED;
            }
        }
        return State.DEFAULT;
    }

    /** Polls the raw mouse button state at most once per real frame, however many markers ask. */
    private static void pollInputOnce() {
        long now = System.nanoTime();
        long gap = now - lastPollNanos;
        if (gap < SAME_FRAME_NANOS) {
            return;
        }
        if (draggingInstanceKey != null && gap > STALE_DRAG_NANOS) {
            // Nothing polled for a while: the recipe screen was likely closed or changed mid-drag.
            // Drop the stale drag instead of silently resuming it later.
            draggingInstanceKey = null;
        }
        lastPollNanos = now;

        // Mouse buttons, unlike keyboard keys, need GLFW's own glfwGetMouseButton: Mojang's
        // InputConstants.isKeyDown(window, code) always calls glfwGetKey (keyboard) regardless of
        // what code is passed, so it never reflects real mouse button state.
        long window = Minecraft.getInstance().getWindow().getWindow();
        boolean leftNow = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightNow = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        leftJustPressed = leftNow && !leftWasDown;
        rightJustPressed = rightNow && !rightWasDown;
        leftDown = leftNow;
        leftWasDown = leftNow;
        rightWasDown = rightNow;
    }
}
