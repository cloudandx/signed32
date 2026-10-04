package org.cloudandx.signed32.client.mixin.client.render;


import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ViewArea.class)
public abstract class ViewAreaMixin {

    @Unique
    protected LevelRenderer levelRenderer;

    @Unique
    protected int sectionGridSizeX;

    @Unique
    protected int sectionGridSizeY;

    @Unique
    protected int sectionGridSizeZ;

    @Unique
    private int viewDistance;

    @Unique
    private SectionPos cameraSectionPos;

    @Unique
    public SectionRenderDispatcher.RenderSection[] sections;

    @Unique
    protected void setViewDistance(int renderDistanceChunks) {
        int horizontal = renderDistanceChunks * 2 + 1;
        this.sectionGridSizeX = horizontal;
        this.sectionGridSizeY = 12 * 2 + 1;
        this.sectionGridSizeZ = horizontal;
        this.viewDistance = renderDistanceChunks;
    }

    /**
     * 第 gridY 个槽位装 low + floorMod(gridY - low, gridSizeY) 段，low = camSectionY - half。
     * gridSizeY 个槽位恰好覆盖 [cam-half, cam+half] 连续段，恒含相机所在段，与查表的
     * floorMod(sectionY, gridSizeY) 互为逆运算。
     */
    @Unique
    public void repositionCamera(SectionPos cameraSectionPos) {
        int half = this.sectionGridSizeY / 2;
        int low = cameraSectionPos.y() - half;
        for (int gridX = 0; gridX < this.sectionGridSizeX; gridX++) {
            int lowestX = cameraSectionPos.x() - this.viewDistance;
            int newSectionX = lowestX + Math.floorMod(gridX - lowestX, this.sectionGridSizeX);
            for (int gridZ = 0; gridZ < this.sectionGridSizeZ; gridZ++) {
                int lowestZ = cameraSectionPos.z() - this.viewDistance;
                int newSectionZ = lowestZ + Math.floorMod(gridZ - lowestZ, this.sectionGridSizeZ);
                for (int gridY = 0; gridY < this.sectionGridSizeY; gridY++) {
                    int newSectionY = low + Math.floorMod(gridY - low, this.sectionGridSizeY);
                    SectionRenderDispatcher.RenderSection section = this.sections[farlands$gridIndex(gridX, gridY, gridZ)];
                    long node = SectionPos.asLong(newSectionX, newSectionY, newSectionZ);
                    if (section.getSectionNode() != node) {
                        section.setSectionNode(node);
                    }
                }
            }
        }
        this.cameraSectionPos = cameraSectionPos;
        this.levelRenderer.sectionOcclusionGraph().invalidate();
    }

    /** 竖直窗口判定改成相机段加减半高，XZ 判定与原版一致。 */
    @Unique
    private boolean containsSection(int sectionX, int sectionY, int sectionZ) {
        int half = this.sectionGridSizeY / 2;
        int camSectionY = this.cameraSectionPos.y();
        if (sectionY < camSectionY - half || sectionY > camSectionY + half) {
            return false;
        }
        return sectionX < this.cameraSectionPos.x() - this.viewDistance
                || sectionX > this.cameraSectionPos.x() + this.viewDistance ? false
                        : sectionZ >= this.cameraSectionPos.z() - this.viewDistance
                                && sectionZ <= this.cameraSectionPos.z() + this.viewDistance;
    }

    /** 与 vanilla 的 getSectionIndex 同布局，竖直下标换成 floorMod(sectionY, gridSizeY)。 */
    private SectionRenderDispatcher.RenderSection getRenderSection(int sectionX, int sectionY, int sectionZ) {
        if (!this.containsSection(sectionX, sectionY, sectionZ)) {
            return null;
        }
        int x = Math.floorMod(sectionX, this.sectionGridSizeX);
        int y = Math.floorMod(sectionY, this.sectionGridSizeY);
        int z = Math.floorMod(sectionZ, this.sectionGridSizeZ);
        return this.sections[farlands$gridIndex(x, y, z)];
    }

    @Unique
    private int farlands$gridIndex(int x, int y, int z) {
        return (z * this.sectionGridSizeY + y) * this.sectionGridSizeX + x;
    }
}