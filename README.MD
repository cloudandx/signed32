# Signed32

**Signed32** is a Fabric Mod that let players explore their world to 32-bit maximum number.

It faithfully restores the iconic **Far Lands** and **Farther Lands** from Beta 1.7.3 while performing deep architectural overhauls to ensure that exploring extreme coordinates remains stable, smooth, and completely crash-free.

---

## 1. Crash Prevention & Critical Bug Fixes (Top Priority)

Exploring beyond standard coordinates in vanilla Minecraft inevitably leads to server crashes, infinite loops, and data corruption. Signed32 resolves these deep engine flaws directly:

* **Watchdog Crash & Infinite Loop Prevention:**
  * At extreme coordinates (2,147,483,647), standard `for` loops inside AABB box collision checks and block iterations experience integer wrapping (e.g., `x++` turns negative), creating an infinite loop that freezes the server thread and triggers a Watchdog crash. Signed32 intercepts and blocks this wrapping behavior.
* **Worldgen Lattice Lock Fix:**
  * Replaces standard Java type-casting deadlocks in Perlin noise generation (`(int) Math.floor(x)` clamping at `2,147,483,647`) to keep lattice indices rolling smoothly.
* **Single-Precision Float & Rendering Degradation Handling:**
  * Handles large-coordinate floating-point precision loss gracefully to mitigate extreme vertex tearing and graphical glitches.

---

## 2. Expanded World Border & Position Reconstruction

To support seamless traversal across billions of blocks without breaking core game mechanics, the fundamental coordinate systems have been entirely rebuilt:

* **Expanded World Border:**
  * Thoroughly removes the vanilla 30,000,000-block hard barrier, unlocking unrestricted player and entity movement up to the 32-bit signed integer limit `(2.14B blocks)`.
* **Position System Reconstruction (`BlockPos`, `ChunkPos`, `SectionPos`):**
  * Re-engineers spatial mapping and chunk-section indexing structures to prevent overflow clipping during large-scale block queries and entity section management.
* **Custom Hashmath Integration:**
  * Implements specialized hashing mathematics to handle extreme coordinate keys efficiently, preventing dictionary collisions and memory leaks in chunk caching.

---

## 3. Authentic Beta 1.7.3 Terrain Generation & Features

### Far Lands (12,550,821 Blocks)
* Triggered when `minLimit` and `maxLimit` Perlin noise octaves overflow the 32-bit integer limit.
* The selector noise remains smooth, blending extremes to carve out majestic **3D Swiss Cheese Cave Walls** soaring into the sky.

### Farther Lands (1,004,065,920 Blocks)
* Triggered when the selector noise (`mainNoise`) overflows its threshold.
* The noise grid locks along the primary axis, The original Far Lands became more stretched, flattened at the top, and extended infinitely.
* 
---

## Quick Teleport Commands

* **Edge Far Lands:** `/tp @s 12550821 ~ 0`
* **Corner Far Lands:** `/tp @s 12550821 ~ 12550821`
* **Edge Farther Lands:** `/tp @s 1004065920 ~ 0`
* **Corner Farther Lands:** `/tp @s 1004065920 ~ 1004065920`
* **Nether Far Lands:** `/execute in minecraft:the_nether run tp @s 12550821 ~ 0`
* **32-Bit Integer Limit:** `/tp @s 2147483631 100 0`

---

## Dependencies

* **Fabric Loader** ( >= 0.15.0 )
* **Fabric API**
* **Cloth Config** (Optional, for in-game configuration screen)
* **Mod Menu** (Optional, for mod options integration)

---

## License

Licensed under the **GNU GPL 3.0 License**.
