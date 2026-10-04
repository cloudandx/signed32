package org.cloudandx.signed32.client.mixin.client.render;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.renderer.Octree$Branch")
public abstract class OctreeBranchMixin {

    @Shadow @Final private BoundingBox boundingBox;

    /**
     * 強制將葉子節點終止條件放寬為 "<= 32"。
     * 只要跨度小於等於 32，立刻判定為葉子節點終止遞迴，
     * 絕對不允許八叉樹一路切到 1 引發 1 <-> 2 彈跳死循環！
     */
    @Inject(method = "areChildrenLeaves", at = @At("HEAD"), cancellable = true)
    private void enforceLeafTermination(CallbackInfoReturnable<Boolean> cir) {
        if (this.boundingBox.getXSpan() <= 32) {
            cir.setReturnValue(true);
        }
    }
}