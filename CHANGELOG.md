# Changelog

All notable changes to Just Enough Markers (JEM) — Minecraft 1.20.1 / Forge — are documented in this file.

## [1.0.0-mc1.20.1] - 2026-10-02

### Fixed

- **EMI: markers didn't update when toggling move mode on an already-open recipe screen; changing page "fixed" it.** EMI builds a recipe's marker widget once, when that recipe's display is first built, and reuses it afterwards without asking again — unlike JEI, which re-decides every single frame. Whether to show a marker at all was being decided at that one-time build, so: entering move mode on a page with no customized recipe couldn't make a marker appear (none had ever been built for those recipes), and leaving move mode couldn't make one disappear either (once built, it just kept drawing). That decision now happens every frame inside the marker's own rendering, the same as JEI, so it reacts immediately either way. This was the same root cause behind the `/reload`-after-config-change limitation noted in 0.0.6 below, not a separate EMI limitation as that entry assumed at the time — ordinary configuration changes (`recipeIdFilter`, `outputFilter`, `categories`, etc.) should now also apply live in EMI, with no `/reload` needed, for any recipe EMI is already displaying.

## [0.0.9-mc1.20.1] - 2026-10-02

### Fixed

- **The move mode key bind did nothing while a JEI or EMI screen was open** — exactly the screens it is meant to be used in. Vanilla only tracks a key bind's click (`KeyMapping.consumeClick()`) while no screen is open at all, to stop gameplay hotkeys from also firing while typing in a text field; that includes JEI's and EMI's own screens. The key is now detected from Forge's raw `InputEvent.Key` instead, which fires regardless of what screen (if any) is open, while still not firing while actually typing in a text field (chat, a sign, a search box).
- **Dragging always grabbed the last (bottom-most) marker on the page, regardless of which one was clicked**, whenever several displayed recipes shared the same category — a very common case (e.g. several different crafting recipes are all `minecraft:crafting`). The in-progress drag was tracked by category id alone, so every marker sharing that category answered to the same drag. It is now tracked by the identity of the specific on-screen marker that was actually grabbed, so each one can be dragged independently; they still share one saved position per category, same as before, since that is how `categories` has always worked.

## [0.0.8-mc1.20.1] - 2026-10-02

### Fixed

- **Marker move mode: dragging and right-click reset did not actually work.** The mouse-button polling used `InputConstants.isKeyDown(...)`, which always queries GLFW's *keyboard* state regardless of the code passed in — mouse buttons need the separate `GLFW.glfwGetMouseButton(...)` call. The marker never registered a press, so a drag could never start. Fixed in both the JEI and EMI integrations. The default (grayscale) texture was unaffected and already worked correctly, which is why it was the only one of the three move-mode textures that could be seen in testing before this fix.
- **A config file Forge cannot parse at all (e.g. a typo while hand-editing `categories`, such as a missing pair of quotes) crashed the entire game on startup** instead of just that one mod failing gracefully. JEM now checks the file before handing it to Forge; if it cannot be parsed, it is renamed to `jem-common.toml.broken-<timestamp>` (so nothing already typed is lost) and a fresh default config is created, so the game still starts.

### Note

- A browser/sign-in window opening on first launch is **not related to JEM** — JEM makes no network or browser calls anywhere in its code. That window is the Microsoft/Xbox account sign-in flow CurseForge's own launcher uses (visible in the launch log as `--userType msa`, `--xuid`, `--accessToken`), the same one used to start any modded instance through CurseForge.
- A hand-written entry in `categories` must be a quoted string, like the examples already in the file's comments: `categories = ["minecraft:crafting;0;0"]`, not `categories = [minecraft:crafting;0;0]`. The second form is invalid TOML and is exactly what caused the crash above. With move mode now actually working, hand-editing this list should rarely be necessary.

## [0.0.7-mc1.20.1] - 2026-10-02

### Added

- **In-game marker move mode.** A new key bind (Controls → Just Enough Markers (JEM), unbound by default) toggles a mode where markers can be dragged with the mouse directly in JEI's or EMI's recipe screen, instead of editing `categories` by hand. Right-click a marker to reset it to the default position. Markers change color while move mode is active to show their state: grayscale at the default position, green when customized, red while being dragged.
- **`hideTooltipDetails` config option.** When `true`, the marker tooltip only shows "JEM" instead of `tooltipLine1`, `tooltipLine2`, and the full "Just Enough Markers" credit line. Also applied automatically (without changing the saved setting) while move mode is active.

### Changed

- New default values for fresh installs: `recipeIdFilter = ["kubejs", "kjs"]` (was `["kubejs"]`), `defaultOffsetX = 0` and `defaultOffsetY = 0` (were `19` and `-25`), `categories = []` (was a long list of predefined third-party examples). Existing configs are unaffected: Forge only applies a new default to a key that is missing or invalid, never to a value a player (or a modpack) already has on disk.

### Known limitation

- <span style="color:#e03e2d">**Dragging and right-click reset detect the mouse by polling its raw state once per frame**</span>, since neither JEI's recipe decorator API nor EMI's widget API exposes a mouse-release event. If a recipe screen is closed while actively dragging a marker, that drag is dropped (not committed) the next time a marker is drawn, after a short delay — it does not get stuck permanently, but the in-progress position is lost rather than saved.

## [0.0.6-mc1.20.1] - 2026-09-22

### Added

- **EMI compatibility.** Markers are now also shown in EMI's recipe screen when EMI is installed alongside JEI, with the exact same configuration (`recipeIdFilter`, `outputFilter`, `categories`, tooltip lines, debug mode).

### Known limitation

- **If EMI is installed, run `/reload` after changing JEM's configuration.** EMI only rebuilds its recipe list (and re-applies JEM's markers) on `/reload`, on world join, or on a resource pack reload — not live while the config file changes, unlike JEI. JEI itself is unaffected and keeps updating its markers automatically.
- JEM tries to enable EMI's **"Show Recipe Decorators"** setting (`dev.show-recipe-decorators`) automatically on startup. <span style="color:#e03e2d">**This setting must be set to `true` in EMI's own configuration, or the marker will not appear in EMI at all.**</span> If markers still don't show after a full restart, check that value manually in EMI's config.

## [0.0.5-mc1.20.1] - 2026-09-20

### Changed

- `recipeIdFilter` now accepts a list of texts (e.g. `["kubejs", "example"]`) instead of a single text, so multiple namespaces can be matched at once. A single text (`recipeIdFilter = "kubejs"`, the previous format) is still accepted, so existing configs keep working unchanged.

## [0.0.4-mc1.20.1] - 2026-09-19

### Added

- Initial port to **Minecraft 1.20.1 / Forge**, from the Minecraft 1.21.1 / NeoForge version. Same features, configuration, and visual behavior.
- The configuration file now reloads automatically while the game is running — no restart needed after editing it.
