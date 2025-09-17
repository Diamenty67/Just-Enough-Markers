# Just Enough Markers

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![Loader](https://img.shields.io/badge/Loader-NeoForge-orange)
![Requires](https://img.shields.io/badge/Requires-JEI%20%7C%20KubeJS-blue)
![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red)

**Just Enough Markers (JEM)** is a lightweight Minecraft mod for **NeoForge** that enhances **JEI (Just Enough Items)** by visually highlighting **modified recipes**.  
It is designed for **modpack creators** and **technical players** who want a clear view of recipe changes, whether by **KubeJS** scripts or custom modifications.

---

## 📋 Table of Contents

- [Features](#-features)
- [How It Works](#-how-it-works)
- [Configuration](#-configuration)
- [Installation](#-installation)
- [Usage](#-usage)
- [Support](#-support)
- [License](#-license)
- [Notes](#-notes)

---

## 🌟 Features

- Highlights recipes in **JEI** based on:
    - **Recipe IDs** matching a specific filter (`recipeIdFilter`).
    - **Outputs of recipes without an ID** (`outputFilter`).
- Supports **dynamic marker positions** per recipe category.
- Tooltip displays **custom messages** when hovering over a marker.
- **Debug mode** to show markers on all recipes for testing.
- Lightweight, stable, and compatible with **NeoForge 1.21.1**.

---

## ⚙️ How It Works

1. Recipes are checked for a **recipe ID**:
    - If the recipe ID contains the `recipeIdFilter` text, a **marker** is displayed.
2. Recipes **without a recipe ID** are checked against `outputFilter`:
    - If the output matches an item in the list, a **marker** is displayed.
    - Recipes with a recipe ID are **ignored** for outputFilter.
3. **Marker position** can be adjusted per recipe category via `categories`.
4. **Tooltips** appear when hovering over a marker, showing configurable lines.
5. **Debug mode** (`onlySpecificRecipeID`) can force markers on all recipes regardless of filters.

---

## ⚙️ Configuration

**Configurable options in `JEMConfig.java`:**

| Option | Description | Default                            |
|--------|-------------|------------------------------------|
| `recipeIdFilter` | Text to match recipe IDs for displaying the marker | `kubejs`                           |
| `outputFilter` | List of item IDs for recipes **without recipe IDs** to display markers | `[]`                               |
| `tooltipLine1` | First line of the tooltip | `Modified recipe`                  |
| `tooltipLine2` | Second line of the tooltip | `According to the modpack creator` |
| `onlySpecificRecipeID` | Debug flag to show markers on **all recipes** | `false`                            |
| `defaultOffsetX` | Default X offset of marker | `19`                               |
| `defaultOffsetY` | Default Y offset of marker | `-25`                              |
| `categories` | List of recipe categories with custom marker positions (`categoryId;offsetX;offsetY`) | Predefined examples included       |

---

## 📦 Installation

1. Install **Minecraft 1.21.1** with **NeoForge**.
2. Add **JEI** and **KubeJS** to your `mods` folder.
3. Download the latest **JEM** `.jar`.
4. Launch Minecraft using **NeoForge**.

---

## 🎮 Usage

- Open **JEI** in-game.
- Recipes matching **recipeIdFilter** or outputs in **outputFilter** (for recipes without IDs) will show markers.
- Hover over a marker to see tooltip information.
- Use debug mode to **force all markers** for testing or modpack development.

---

## 🛠 Support

- Report issues on [https://github.com/Diamenty67/Just-Enough-Markers/issues](#).

---

## 📄 License

**All Rights Reserved.**  
No part of this mod may be copied, redistributed, or modified without explicit permission from the author.

---

## 📌 Notes

- **NeoForge only** – Forge or Fabric are **not supported**.
- Works best with the **latest JEI and KubeJS versions**.
- Intended primarily for **modpack developers** and **technical players**.
- Provides **visual clarity** on modified recipes without affecting JEI functionality.