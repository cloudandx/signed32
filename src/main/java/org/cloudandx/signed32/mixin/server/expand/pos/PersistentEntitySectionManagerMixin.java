package org.cloudandx.signed32.mixin.expand.pos;

import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import org.cloudandx.signed32.util.pos.IntSectionPos;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x(J)I"))
    private int redirectSectionX(long sectionNode) {
        return IntSectionPos.getSectionPos(sectionNode).x;
    }

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;y(J)I"))
    private int redirectSectionY(long sectionNode) {
        return IntSectionPos.getSectionPos(sectionNode).y;
    }

    @Redirect(method = "lambda$dumpSections$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;z(J)I"))
    private int redirectSectionZ(long sectionNode) {
        return IntSectionPos.getSectionPos(sectionNode).z;
    }
}