package org.cloudandx.signed32.mixin.server.expand.pos;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.cloudandx.signed32.config.Signed32Config;
import org.cloudandx.signed32.util.pos.IntSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x(J)I"))
    private int redirectSectionX(long sectionNode) {
        return Signed32Config.INSTANCE.expandEntitySections ? IntSectionPos.getSectionPos(sectionNode).x : SectionPos.x(sectionNode);
    }

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;y(J)I"))
    private int redirectSectionY(long sectionNode) {
        return Signed32Config.INSTANCE.expandEntitySections ? IntSectionPos.getSectionPos(sectionNode).y : SectionPos.y(sectionNode);
    }

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;z(J)I"))
    private int redirectSectionZ(long sectionNode) {
        return Signed32Config.INSTANCE.expandEntitySections ? IntSectionPos.getSectionPos(sectionNode).z : SectionPos.z(sectionNode);
    }
}