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

JEM also supports **EMI**: when EMI is installed alongside JEI, markers are shown in EMI's recipe screen too, using the exact same configuration.

> ⚠️ **If EMI is installed, run `/reload` after changing JEM's configuration.** EMI only rebuilds its recipe list — and re-applies JEM's markers — on `/reload`, on world join, or on a resource pack reload. Configuration changes made mid-session won't appear in EMI's recipe screen until then. JEI does not have this limitation: its markers update live as soon as the configuration file is saved.
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
| <code>recipeIdFilter</code> |List of texts to match recipe IDs for displaying the marker |<code>["kubejs"]</code>          |
| <code>outputFilter</code> |Item IDs used for output-based filtering                    |<code>[]</code>                  |
| <code>tooltipLine1</code> |First line of the marker tooltip                            |<code>Modified recipe</code>     |
| <code>tooltipLine2</code> |Second line of the marker tooltip                           |<code>According to the modpack creator</code> |
| <code>onlySpecificRecipeID</code> |Debug option for displaying markers on all recipes          |<code>false</code>               |
| <code>defaultOffsetX</code> |Default horizontal marker offset                            |<code>19</code>                  |
| <code>defaultOffsetY</code> |Default vertical marker offset                              |<code>-25</code>                 |
| <code>categories</code> |Custom positions for individual recipe categories           |Predefined examples              |

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
