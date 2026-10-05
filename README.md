# Just Enough Markers (JEM)

**Visual markers for modified recipes in Just Enough Items (JEI) and EMI.**

Just Enough Markers (JEM) is a lightweight, client-side Minecraft mod designed for **modpack developers, creators, and technical players**. It adds configurable visual markers to recipes displayed in **Just Enough Items (JEI)** and **EMI**, making customized recipes easier to identify.

JEM is particularly useful for modpacks that use **KubeJS** or other tools to modify recipes.

***

## ✨ Features

*   **Recipe ID filtering** — Mark recipes whose IDs match the configured `recipeIdFilter`.
*   **Output filtering** — Identify recipes by their outputs using the `outputFilter` configuration.
*   **Customizable markers** — Configure marker positions, offsets, and tooltip messages.
*   **Category-specific positioning** — Define custom marker positions for individual recipe categories.
*   **In-game move mode** — Drag markers to a new position directly in JEI or EMI instead of editing the configuration by hand.
*   **Simplified tooltip option** — Show just "JEM" instead of the full tooltip text.
*   **Debug mode** — Display markers on all recipes for testing and development.
*   **Client-side and lightweight** — JEM only modifies the recipe viewer's visual presentation and does not alter recipes.

***

## 🔌 Compatibility

### Minecraft Versions

| Minecraft |Mod Loader |Support     |
| --------- |---------- |----------- |
| <strong>1.21.1</strong> |<strong>NeoForge</strong> |✅ Supported |
| <strong>1.20.1</strong> |<strong>Forge</strong> |✅ Supported |

### Recipe Viewer Compatibility

JEM is built specifically for **Just Enough Items (JEI)** and is compatible with the **majority of JEI plugins and integrations**. JEI is required for JEM to load.

JEM also supports **EMI**: when EMI is installed alongside JEI, markers are shown in EMI's recipe screen too, using the exact same configuration, and update live as the configuration file changes — no `/reload` needed, the same as JEI. If markers still seem stuck after a configuration change on a recipe EMI is already displaying, treat it as a bug and report it.
>
> JEM tries to enable EMI's **"Show Recipe Decorators"** setting (`dev.show-recipe-decorators`) automatically on startup. <span style="color:#e03e2d">**This setting must be set to `true` in EMI's own configuration, or the marker will not appear in EMI at all.**</span> If markers still don't show after a full restart, check that value manually in EMI's config.

> ⚠️ **<span style="color:#e03e2d">JEM is not compatible with Roughly Enough Items (REI).</span>**

***

## ⚙️ How It Works

JEM checks recipes displayed by JEI or EMI against your configured filters.

1.  The recipe ID is checked against `recipeIdFilter`.
2.  If no matching ID is found, the recipe output can be checked against `outputFilter`.
3.  Matching recipes receive a visual marker.
4.  Marker appearance and position are controlled through the configuration.
5.  Debug mode can be used to display markers regardless of the configured filters.

## ⚙️ Configuration

| Option               |Description                                                 |Default                          |
| -------------------- |----------------------------------------------------------- |-------------------------------- |
| <code>recipeIdFilter</code> |List of texts to match recipe IDs for displaying the marker |<code>["kubejs", "kjs"]</code>   |
| <code>outputFilter</code> |Item IDs used for output-based filtering                    |<code>[]</code>                  |
| <code>tooltipLine1</code> |First line of the marker tooltip                            |<code>Modified recipe</code>     |
| <code>tooltipLine2</code> |Second line of the marker tooltip                           |<code>According to the modpack creator</code> |
| <code>hideTooltipDetails</code> |If true, the tooltip only shows "JEM" instead of the two lines above and the full credit line |<code>false</code> |
| <code>onlySpecificRecipeID</code> |Debug option for displaying markers on all recipes          |<code>false</code>               |
| <code>defaultOffsetX</code> |Default horizontal marker offset                            |<code>0</code>                   |
| <code>defaultOffsetY</code> |Default vertical marker offset                              |<code>0</code>                   |
| <code>categories</code> |Custom positions for individual recipe categories, format <code>"categoryId;offsetX;offsetY"</code> — written automatically by move mode, but can also be edited by hand |<code>[]</code> |

***

## 🖱️ In-Game Marker Move Mode

Instead of editing `categories` by hand, markers can be repositioned directly in JEI's or EMI's recipe screen (hand-editing is still supported, but each entry must be a quoted string, e.g. `categories = ["minecraft:crafting;0;0"]` — a missing pair of quotes is invalid TOML. The game does not crash: the broken file is backed up next to the configuration file and a fresh default one is created, so the mistake would still have to be fixed by hand in that backup):

1.  Bind a key to **"Toggle Marker Move Mode"** in **Controls → Just Enough Markers (JEM)** (unbound by default). The key works even while JEI's or EMI's recipe screen is open — that is in fact when it matters.
2.  Press it to enter move mode. A chat message confirms it, and briefly explains the controls.
3.  **Hold left-click** on a marker and drag it to reposition it; release to confirm. The new offset is saved to `categories` immediately. If several recipes on the page share the same category (e.g. several different crafting recipes), each marker can still be dragged independently — they just all end up at the one position saved for that category, same as before move mode existed.
4.  **Right-click** a marker to reset it back to the default position (removes its entry from `categories`).
5.  Press the key again to exit move mode.

While move mode is active, markers are shown on every recipe (as if `onlySpecificRecipeID` were `true`) and tooltips are simplified (as if `hideTooltipDetails` were `true`), so every marker is easy to find and nothing blocks the view while dragging. Both changes are temporary and purely visual: they are **not** written to the configuration file and automatically stop applying the moment move mode is turned back off, regardless of what those two options are actually set to.

Markers also change color to show their state while move mode is active:

| State | Appearance |
| ----- | ---------- |
| At the default position | Grayscale |
| Customized (moved from default) | Green |
| Currently being dragged | Red |

Outside move mode, markers always use the normal full-color icon — this is purely an in-editor aid and does not change anything for players who never use it.

***

## 🛠️ Support & Issues

For bug reports, compatibility issues, or feature requests, please use the GitHub issue tracker:

**[GitHub Issues](https://github.com/Diamenty67/Just-Enough-Markers/issues)**

When reporting an issue, please provide:

*   Minecraft version
*   Mod loader and version
*   JEM version
*   JEI version (and EMI version, if installed)
*   Relevant mods
*   Description of the issue
*   Logs or screenshots, if applicable

## 📜 License

**All Rights Reserved**

This project may not be redistributed, copied, modified, or republished without explicit permission from the author.

***

**Just Enough Markers** — _Make your recipe changes visible._
