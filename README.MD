<div align="center">

  <h1> Signed32: Beyond the Integer Horizon</h1>



  <p>
    <strong>Vanilla Minecraft told you that ±30,000,000 blocks was the edge of the universe.<br>
    We took that as a personal challenge.</strong>
  </p>

  <p>
    <a href="https://fabricmc.net/"><img src="https://img.shields.io/badge/Loader-Fabric-eed582?style=for-the-badge&logo=fabric" alt="Fabric"></a>
    <a href="https://minecraft.net/"><img src="https://img.shields.io/badge/Minecraft-26.3-46c056?style=for-the-badge&logo=minecraft" alt="Minecraft 26.3"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge" alt="GPL-3.0"></a>
    <a href="#-native-multilingual-support"><img src="https://img.shields.io/badge/Languages-EN%20%7C%20%E7%B9%81%E4%B8%AD%20%7C%20%E7%B2%B5%E8%AA%9E%20%7C%20%E7%B0%A1%E4%B8%AD-ff69b4?style=for-the-badge" alt="Localization"></a>
  </p>

</div>

---

## ⚡ What is Signed32?

**Signed32** completely shatters the invisible walls of modern Minecraft. By rebuilding the engine's core coordinate packing, border enforcement, and spatial memory systems, this mod unleashes the absolute limit of 32-bit signed integers:

$$\mathbf{[-2,147,483,648 \;\longleftrightarrow\; +2,147,483,647]}$$

Travel over **2.14 billion blocks** into the void, stand beneath towering sky-scraping **Beta 1.7.3 Far Lands**, and witness the iconic single-precision floating-point visual artifacts from the golden age of Minecraft!

---

## Vanilla vs. Signed32

| Feature | Vanilla Minecraft | With Signed32 |
| :--- | :---: | :---: |
| **Max World Border** | Fixed at $\pm 30,000,000$ | **$\pm 2,147,483,647$ (Full 32-Bit Range)** |
| **33.5M+ Chunk Crash** | 💥 Fatal Overflow Crash | **Fixed & Fully Traversable** |
| **Far Lands Generation** | ❌ Removed in Beta 1.8 | **Restored via Modern Density Routers** |
| **BlockPos Coordinate Limit** | $\pm 33,554,432$ | **Unlimited (Memory Hash Map System)** |
| **Classic Camera Jitter** | ❌ Patched | **Fully Restored (Configurable)** |
| **Block Model Vertex Tearing** | ❌ Removed | **Restored for Non-Full Blocks** |

---

## Key Highlights

### 1. Authentic Beta 1.7.3 Far Lands
* Recreates the legendary **32-bit Perlin noise integer overflow** within Minecraft 26.3's modern density sampling pipeline.
* Towering **Edge Far Lands walls** reaching from $Y=-64$ straight to $Y=320$, riddled with the iconic swiss-cheese caverns.
* **Configurable Threshold**: Want the Far Lands to appear closer to spawn? Tune the distance anywhere from 0 to 2.14B blocks in the config!

### 2. Deep Engine & Coordinate Overhaul
* **No More 33.5M+ Crashes**: Vanilla Minecraft stores chunk block coordinates by multiplying by 16, instantly triggering fatal overflows past block $33,554,432$. Signed32 completely patches this calculation.
* **32-Bit BlockPos & SectionPos**: Bypasses the 22-bit/26-bit bit-shift packaging limits with ultra-fast memory hash mappings, ensuring block entities, lighting, and ticking work seamlessly.
* **Entity Spatial Integrity**: Rewrites entity section storage to prevent mobs and players from vanishing into thin air in deep coordinates.

### 3. The Nostalgic "Glitch" Aesthetic
* **Stepwise Camera Jitter**: Snaps the player's camera position down to single-precision float math ($32$-bit), recreating the jittery walking experience of classic versions.
* **Entity Sync Stutter**: Mobs render aligned to single-precision floating-point grids, stuttering naturally in sync with your viewpoint.
* **Vertex Stretching & Tearing**: Torches, redstone wire, levers, and rails begin to distort, tear, and float off their surfaces as you journey deeper into the boundary.

---

## ⚠️ Multiplayer & Server Notice

> [!WARNING]
> **Extended Network Protocol Toggle**:
> Signed32 contains an optional custom networking protocol for transmitting extreme 32-bit coordinates.
> * **Singleplayer**: Safe with all features enabled.
> * **Modded Server (with Signed32 on server)**: Safe with all features enabled.
> * **Standard Servers (Paper / Purpur / Vanilla)**: **Turn OFF** `extendedBlockPosProtocol` in your client config before joining, otherwise the vanilla server will reject custom packets!

---

## 🛠️ In-Game Configuration

Configure boundaries, threshold coordinates, noise generation, and visual jitter directly in-game using **Cloth Config** and **Mod Menu**!

1. Open **Mod Menu** in the pause screen.
2. Select **Signed32** and click the **Settings (⚙️)** button.
3. Tweak your experience across 4 clean categories:
    * 🌍 **World & Generation**
    * 📐 **Coordinates & Storage**
    * 🌐 **Network & Compatibility**
    * 🎥 **Far Lands Visual Artifacts**

---

## 📋 Requirements

| Dependency | Requirement |
| :--- | :--- |
| **Minecraft** | `26.3` |
| **Fabric Loader** | `>= 0.16.0` |
| **Fabric API** | **Required** |
| **Cloth Config API** | **Required** |
| **Mod Menu** | *Highly Recommended* |

---

## 📜 License

Signed32 is distributed under the terms of the **GNU General Public License v3.0**. Feel free to inspect, modify, and include it in your modpacks!
