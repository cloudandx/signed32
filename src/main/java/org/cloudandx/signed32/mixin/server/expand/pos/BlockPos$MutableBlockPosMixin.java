package org.cloudandx.signed32.mixin.server.expand.pos;

import net.minecraft.core.BlockPos;
import org.cloudandx.signed32.config.Signed32Config;
import org.cloudandx.signed32.util.pos.IntBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPos.MutableBlockPos.class)
public class BlockPos$MutableBlockPosMixin {

    /**
     * @author CloudAndX
     * @reason 支援 32 位元方塊座標設定；關閉時使用原生解析
     */
    @Overwrite
    public BlockPos.MutableBlockPos set(long packedPos) {
        BlockPos.MutableBlockPos self = (BlockPos.MutableBlockPos) (Object) this;
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
            return self.set(pos.x, pos.y, pos.z);
        }
        return self.set(BlockPos.getX(packedPos), BlockPos.getY(packedPos), BlockPos.getZ(packedPos));
    }
}