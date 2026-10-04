package org.cloudandx.signed32.client.mixin.client.misc;

import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.cloudandx.signed32.config.Signed32Config;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    @Shadow
    @Final
    private QuadInstance quadInstance;

    @Shadow
    private int getTintColor(BlockAndTintGetter level, BlockState state, BlockPos pos, int tintIndex) {
        throw new AssertionError();
    }

    @Inject(method = "putQuadWithTint", at = @At("HEAD"), cancellable = true)
    private void distortPutQuadWithTint(
            BlockQuadOutput output,
            float x,
            float y,
            float z,
            BlockAndTintGetter level,
            BlockState state,
            BlockPos pos,
            BakedQuad quad,
            CallbackInfo ci
    ) {
        if (!Signed32Config.INSTANCE.blockTearing) {
            return;
        }

        int bx = pos.getX();
        int bz = pos.getZ();
        int threshold = Signed32Config.INSTANCE.jitterThreshold;

        // 小於閾值時直接返回原版邏輯，不產生任何計算開銷
        if (Math.abs(bx) < threshold && Math.abs(bz) < threshold) {
            return;
        }

        int by = pos.getY();
        BakedQuad tornQuad = signed32$distortQuad(quad, bx, by, bz);

        int tintIndex = tornQuad.materialInfo().tintIndex();
        if (tintIndex != -1) {
            this.quadInstance.multiplyColor(this.getTintColor(level, state, pos, tintIndex));
        }

        output.put(x, y, z, tornQuad, this.quadInstance);
        ci.cancel();
    }

    @Unique
    private static BakedQuad signed32$distortQuad(BakedQuad quad, int bx, int by, int bz) {
        Vector3fc p0 = signed32$distortVertex(quad.position0(), bx, by, bz);
        Vector3fc p1 = signed32$distortVertex(quad.position1(), bx, by, bz);
        Vector3fc p2 = signed32$distortVertex(quad.position2(), bx, by, bz);
        Vector3fc p3 = signed32$distortVertex(quad.position3(), bx, by, bz);

        return new BakedQuad(
                p0, p1, p2, p3,
                quad.packedUV0(),
                quad.packedUV1(),
                quad.packedUV2(),
                quad.packedUV3(),
                quad.direction(),
                quad.materialInfo()
        );
    }

    @Unique
    private static Vector3fc signed32$distortVertex(Vector3fc v, int bx, int by, int bz) {
        float nx = (float) ((double) ((float) ((double) bx + (double) v.x())) - (double) bx);
        float ny = (float) ((double) ((float) ((double) by + (double) v.y())) - (double) by);
        float nz = (float) ((double) ((float) ((double) bz + (double) v.z())) - (double) bz);

        return new Vector3f(nx, ny, nz);
    }
}
