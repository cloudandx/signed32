package org.cloudandx.signed32.client.mixin.client.render;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionCopy")
public class SectionCopyMixin {

    /**
     * 渲染快取防護：在邊界處構建網格時，若遇到尚未分配或為 null 的區段，
     * 視為純空氣區段處理，徹底杜絕 SectionCopy 內部調用 hasOnlyAir() 時拋出 NPE。
     */
    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")
    )
    private static boolean farlands$nullSectionIsAir(LevelChunkSection section) {
        return section == null || section.hasOnlyAir();
    }
}