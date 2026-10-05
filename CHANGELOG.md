# Changelog

All notable changes to Just Enough Markers (JEM) — Minecraft 1.21.1 / NeoForge — are documented in this file. Versions before 1.0.0 (0.0.1 – 0.0.4) predate this changelog.

## [1.0.0-mc1.21.1] - 2026-10-03

Brings the NeoForge version up to date with everything the Minecraft 1.20.1 / Forge version gained since 0.0.4. Changes since `0.0.4-mc1.21.1`:

### Added

- **EMI compatibility.** Markers are now also shown in EMI's recipe screen when EMI is installed alongside JEI, with the exact same configuration (`recipeIdFilter`, `outputFilter`, `categories`, tooltip lines, debug mode). Configuration changes and move mode apply live in EMI as well, with no `/reload` needed, the same as JEI. EMI stays optional: without it, nothing changes.
- **In-game marker move mode.** A new key bind (Controls → Just Enough Markers (JEM), unbound by default, works even while a JEI or EMI recipe screen is open) toggles a mode where markers can be dragged with the mouse directly in the recipe screen, instead of editing `categories` by hand. Right-click a marker to reset it to the default position. Markers change color while move mode is active to show their state: grayscale at the default position, green when customized, red while being dragged. If several recipes on the page share the same category, each marker can still be dragged independently; they share one saved position per category, as `categories` always worked.
- **`hideTooltipDetails` config option.** When `true`, the marker tooltip only shows "JEM" instead of `tooltipLine1`, `tooltipLine2`, and the full "Just Enough Markers" credit line. Also applied automatically (without changing the saved setting) while move mode is active.

### Changed

- `recipeIdFilter` now accepts a list of texts (e.g. `["kubejs", "example"]`) instead of a single text, so multiple namespaces can be matched at once. A single text (`recipeIdFilter = "kubejs"`, the previous format) is still accepted, so existing configs keep working unchanged.
- New default values for fresh installs: `recipeIdFilter = ["kubejs", "kjs"]` (was `["kubejs"]`), `defaultOffsetX = 0` and `defaultOffsetY = 0` (were `19` and `-25`), `categories = []` (was a long list of predefined third-party examples). Existing configs are unaffected: NeoForge only applies a new default to a key that is missing or invalid, never to a value a player (or a modpack) already has on disk.
- The `categories` example in the configuration comments was replaced by a correct one: `["minecraft:crafting;-45;-14", "emi:anvil_repairing;4;10"]`.
- The jar is now named `jem-neoforge-<version>.jar` (it was `jem-<version>.jar`), so it can be told apart from the Forge build.

### Fixed

- Removed an `[[accessTransformers]]` entry in `neoforge.mods.toml` that pointed to a file that does not exist, which logged an error line at every startup.

### Notes

- JEM enables EMI's **"Show Recipe Decorators"** setting (`dev.show-recipe-decorators`) automatically on startup. <span style="color:#e03e2d">**This setting must be set to `true` in EMI's own configuration, or the marker will not appear in EMI at all.**</span> If markers still don't show after a full restart, check that value manually in EMI's config.
- A hand-written entry in `categories` must be a quoted string: `categories = ["minecraft:crafting;0;0"]`, not `categories = [minecraft:crafting;0;0]`. The second form is invalid TOML. NeoForge itself recovers from it without crashing (the broken file is backed up as `jem-common-1.toml.bak` and a fresh default configuration is created), so unlike the Forge build, JEM needs no extra handling for this. With move mode, hand-editing this list should rarely be necessary.
- Likewise, NeoForge reloads the configuration file on its own when it changes on disk, however the editor saves it, so JEM has no separate reloader on this version.
