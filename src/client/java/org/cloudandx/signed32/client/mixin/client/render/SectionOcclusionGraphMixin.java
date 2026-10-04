package org.cloudandx.signed32.client.mixin.client.render;

import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.ViewArea;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionOcclusionGraph.class)
public class SectionOcclusionGraphMixin {

    @Unique
    private static final int VERTICAL_SIMULATION_DISTANCE = 8;

    @Redirect(method = "getRelativeFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;getViewDistance()I"))
    private static int signed32$gridHalfSpan(ViewArea viewArea) {
        return VERTICAL_SIMULATION_DISTANCE;
    }
}