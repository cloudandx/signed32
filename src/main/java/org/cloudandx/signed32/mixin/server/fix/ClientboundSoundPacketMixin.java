package org.cloudandx.signed32.mixin.server.fix;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 聲音包座標編碼從 int 加寬到 long。
 * 支援透過 Signed32Config.INSTANCE.extendedBlockPosProtocol 進行動態切換，
 * 關閉時自動還原為原版 4 位元組格式以相容各類外部伺服器。
 */
@SuppressWarnings("deprecation")
@Mixin(ClientboundSoundPacket.class)
public abstract class ClientboundSoundPacketMixin {

    @Shadow
    @Final
    private Holder<SoundEvent> sound;

    @Shadow
    @Final
    private SoundSource source;

    @Shadow
    @Final
    private float volume;

    @Shadow
    @Final
    private float pitch;

    @Shadow
    @Final
    private long seed;

    @Unique
    private long signed32$x8;

    @Unique
    private long signed32$y8;

    @Unique
    private long signed32$z8;

    @Unique
    private int signed32$coordIndex;

    /**
     * 伺服端構造：座標為 double，乘 8 後直接放 long。
     */
    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V", at = @At("RETURN"))
    private void signed32$storeLongCoords(Holder<SoundEvent> sound, SoundSource source, double x, double y,
                                          double z, float volume, float pitch, long seed, CallbackInfo ci) {
        this.signed32$x8 = (long) (x * 8.0);
        this.signed32$y8 = (long) (y * 8.0);
        this.signed32$z8 = (long) (z * 8.0);
    }

    /**
     * 客戶端構造：
     * - 開啟協定：讀取 8 位元組 long。
     * - 關閉協定（原版相容）：讀取原版 4 位元組 int，防止封包緩衝區溢出崩潰。
     */
    @Redirect(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;readInt()I"))
    private int signed32$readLongAsInt(RegistryFriendlyByteBuf buffer) {
        long v;
        if (Signed32Config.INSTANCE.extendedBlockPosProtocol) {
            v = buffer.readLong();
        } else {
            v = buffer.readInt();
        }

        switch (this.signed32$coordIndex) {
            case 0 -> this.signed32$x8 = v;
            case 1 -> this.signed32$y8 = v;
            default -> this.signed32$z8 = v;
        }
        this.signed32$coordIndex++;
        return (int) v;
    }

    /**
     * 編碼：根據協定設定寫入 long 或原版 int。
     */
    @Overwrite
    private void write(RegistryFriendlyByteBuf buffer) {
        SoundEvent.STREAM_CODEC.encode(buffer, this.sound);
        buffer.writeEnum(this.source);
        if (Signed32Config.INSTANCE.extendedBlockPosProtocol) {
            buffer.writeLong(this.signed32$x8);
            buffer.writeLong(this.signed32$y8);
            buffer.writeLong(this.signed32$z8);
        } else {
            buffer.writeInt((int) this.signed32$x8);
            buffer.writeInt((int) this.signed32$y8);
            buffer.writeInt((int) this.signed32$z8);
        }
        buffer.writeFloat(this.volume);
        buffer.writeFloat(this.pitch);
        buffer.writeLong(this.seed);
    }

    @Overwrite
    public double getX() {
        return (double) this.signed32$x8 / 8.0;
    }

    @Overwrite
    public double getY() {
        return (double) this.signed32$y8 / 8.0;
    }

    @Overwrite
    public double getZ() {
        return (double) this.signed32$z8 / 8.0;
    }
}