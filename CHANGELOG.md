# Changelog

All notable changes to Just Enough Markers (JEM) — Minecraft 1.20.1 / Forge — are documented in this file.

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
