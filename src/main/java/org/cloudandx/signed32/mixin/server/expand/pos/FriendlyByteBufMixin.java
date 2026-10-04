package org.cloudandx.signed32.mixin.server.expand.pos;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FriendlyByteBuf.class)
public class FriendlyByteBufMixin {

    /**
     * @author CloudAndX
     * @reason 支援 32 位元方塊座標協定切換；關閉時自動還原為原版 64 位元壓縮格式以相容伺服器
     */
    @Overwrite
    public static void writeBlockPos(ByteBuf buffer, BlockPos pos) {
        if (Signed32Config.INSTANCE.extendedBlockPosProtocol) {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        } else {
            buffer.writeLong(pos.asLong());
        }
    }

    /**
     * @author CloudAndX
     * @reason 支援 32 位元方塊座標協定切換；關閉時自動還原為原版 64 位元解碼格式以相容伺服器
     */
    @Overwrite
    public static BlockPos readBlockPos(ByteBuf buffer) {
        if (Signed32Config.INSTANCE.extendedBlockPosProtocol) {
            return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        return BlockPos.of(buffer.readLong());
    }
}