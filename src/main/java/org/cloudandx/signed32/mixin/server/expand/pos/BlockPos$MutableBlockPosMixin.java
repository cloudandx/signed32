package org.cloudandx.signed32.mixin.server.expand.pos;

import net.minecraft.core.BlockPos;
import org.cloudandx.signed32.config.Signed32Config;
import org.cloudandx.signed32.util.maps.BlockUtil;
import org.cloudandx.signed32.util.pos.IntBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPos.MutableBlockPos.class)
public class BlockPos$MutableBlockPosMixin {

    @Overwrite
    public BlockPos.MutableBlockPos set(long packedPos) {
        BlockPos.MutableBlockPos self = (BlockPos.MutableBlockPos) (Object) this;

        // 使用現有的 size() 取代 hasEntries()
        if (Signed32Config.INSTANCE.expandBlockPos) {
            if (BlockUtil.size() > 0) {
                IntBlockPos pos = BlockUtil.get(packedPos);
                if (pos != null) {
                    return self.set(pos.x, pos.y, pos.z);
                }
            }
        }

        return self.set(BlockPos.getX(packedPos), BlockPos.getY(packedPos), BlockPos.getZ(packedPos));
    }
}