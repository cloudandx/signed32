# Signed32

**Signed32** is a Fabric mod designed to push Minecraft to the absolute mathematical limits of a **Signed 32-bit Integer** ($\pm 2,147,483,647$ blocks), faithfully restoring the iconic **Far Lands** and **Farther Lands** from Beta 1.7.3 while ensuring structural stability and crash prevention at extreme coordinates.

---

## 🛠️ Most Important: Crash Prevention & Rendering Fixes

Navigating beyond vanilla boundaries usually causes immediate server crashes and graphical tearing. Signed32 solves these core issues directly:

* **Preventing Integer Overflow Deadlocks (Watchdog Crash Fix):**
   * At extreme coordinates ($\pm 2,147,483,647$), standard `for` loops inside AABB box collision checks and block iterations experience integer wrapping (e.g., `x++` turns negative), creating an infinite loop that freezes the server thread and triggers a Watchdog crash. Signed32 intercepts and blocks this wrapping behavior.


* **Fixing Worldgen Lattice Lock:**
   * Replaces standard Java type-casting deadlocks in Perlin noise generation (`(int) Math.floor(x)` clamping at `2,147,483,647`) to keep lattice indices rolling smoothly.


* **Mitigating Single-Precision Float Rendering Artifacts:**
   * Handles large-coordinate floating-point degradation gracefully to prevent full-screen vertex tearing and GPU rendering pipeline crashes.

---

## 🌍 Core World Generation Features

### 1. Authentic Beta 1.7.3 Far Lands ($\pm 12,550,821$ Blocks)
* Triggered when `minLimit` and `maxLimit` Perlin noise octaves overflow the 32-bit integer limit.
* The selector noise remains smooth, blending extreme extremes to carve out majestic **3D Swiss Cheese Cave Walls** soaring into the sky.

### 2. Farther Lands ($\pm 1,004,065,920$ Blocks)
* Triggered when the selector noise (`mainNoise`) overflows its threshold.
* The noise grid locks along the primary axis, stretching the Swiss cheese cavities into infinite, parallel **Solid Stripe Mountains and Deep Abyssal Rifts**.

### 3. Corner Lands Enhancement
* **Corner Far Lands:** When both $X$ and $Z$ axes exceed the threshold simultaneously, orthogonal 3D cheese tunnels intersect to form **Loop Far Lands** (square monolithic vertical shafts).
* **Corner Farther Lands:** Intersecting parallel stripes form precise **Checkerboard Monolith Grids**.

### 4. Multi-Dimension Support
* **Nether Far Lands:** Generates colossal Netherrack Swiss-cheese walls piercing through the bedrock ceiling ($Y=128$), accompanied by massive vertical lava falls.
* **The End Far Lands:** Tears outer End islands into floating End Stone rings and monolithic pillars stretching across the void.

---

## Configuration

Configurable via **Cloth Config GUI** or by editing `config/signed32.json`:

```json
{
  "expandWorldBorder": true,
  "fixChunkOverflow": true,
  "farLandsNoise": true,
  "farLandsThreshold": 12550821,
  "fartherLandsThreshold": 1004065920,
  "expandBlockPos": true,
  "expandSectionPos": true,
  "expandEntitySections": true,
  "cameraJitter": true,
  "entityJitter": true,
  "blockTearing": true,
  "jitterThreshold": 0
}
```
---
## Dependencies
* **Fabric Loader** (>= 0.15.0)
* **Fabric API**
* **Cloth Config API** (Optional, for in-game configuration screen)
* **Mod Menu** (Optional, for mod options integration)

---
## Licence
Licence under the **LGPL 3.0** License