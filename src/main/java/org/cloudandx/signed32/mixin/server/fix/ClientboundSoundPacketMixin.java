package org.cloudandx.signed32.mixin.fix;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

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
 *
 * <p>原版的構造器把世界座標寫成 {@code (int)(x * 8.0)}，{@code d2i} 對超範圍飽和，
 * 於是 |coord| 超過 2^28（即約 2.68 億）時三個座標全部變成 {@code Integer.MAX_VALUE}。
 * 客戶端解回的位置與真實位置無關，且距離被算成 0，表現為無聲或方位全錯。
 * 本 mod 的可玩範圍是 ±2^31，這一段必須在範圍內。
 *
 * <p>線上格式與原版唯一差別是三個座標各佔 8 位元組，包增大 12 位元組。兩端必須同為
 * 本 mod，否則解碼錯位。
 *
 * <p>三條路徑都要落到自己的 long 欄位，缺一條都會讓 getX/getY/getZ 給出錯值：伺服端構造器
 * 由 {@link #signed32$storeLongCoords} 寫入，客戶端反序列化由 {@link #signed32$readLongAsInt}
 * 寫入。客戶端那條尤其容易漏，因為它不經過伺服端構造器：只讀不寫會讓三個欄位恆為 0，
 * 聲音被算在世界原點，表現為方位與衰減全錯。
 *
 * <p>舊的 {@code int x/y/z} 欄位保留但不再被讀，{@code @Shadow} 與描述符因此不必更動。
 */
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

    /**
     * 解碼時的座標序號：0 = x，1 = y，2 = z。
     *
     * <p>構造器裡三次 {@code readInt} 的字節碼順序固定為 x、y、z，且三次調用之間沒有其它
     * 對同一緩衝區的讀取，所以按調用序分派即可。不在構造器 HEAD 處歸零：HEAD 位於
     * {@code super()} 之前，Mixin 禁止在那裡觸碰實例欄位。每個新實例的欄位預設即 0。
     */
    @Unique
    private int signed32$coordIndex;

    /**
     * 伺服端構造：座標為 double，乘 8 後直接放 long，不做飽和。
     *
     * <p>不 @Shadow 私有 int x/y/z：真實座標就在本構造器的形參裡，{@code (int)(x * 8.0)}
     * 的飽和已經在原版構造器體內發生，從欄位讀回來拿到的只會是飽和值。
     */
    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V", at = @At("RETURN"))
    private void signed32$storeLongCoords(Holder<SoundEvent> sound, SoundSource source, double x, double y,
            double z, float volume, float pitch, long seed, CallbackInfo ci) {
        this.signed32$x8 = (long) (x * 8.0);
        this.signed32$y8 = (long) (y * 8.0);
        this.signed32$z8 = (long) (z * 8.0);
    }

    /**
     * 客戶端構造：座標從 4 位元組改讀 8 位元組，並寫進自己的 long 欄位。
     *
     * <p>target 的 owner 必須是 {@code RegistryFriendlyByteBuf}：字節碼裡三次調用寫作
     * {@code invokevirtual RegistryFriendlyByteBuf.readInt:()I}，owner 是編譯期類型而不是該
     * 方法真正的聲明類 {@code FriendlyByteBuf}。寫成聲明類會掃描到 0 個目標。
     *
     * <p>解碼結果必須寫進 long 欄位。客戶端反序列化只走本構造器，不經過伺服端那條路，不寫
     * 就恆為 0，聲音會被算在世界原點。
     *
     * <p>不在構造器 HEAD 處歸零 {@code coordIndex}：HEAD 位於 {@code super()} 之前，Mixin
     * 禁止在那裡觸碰實例欄位。每個新實例的欄位預設即 0，無需顯式重置。
     */
    @Redirect(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;readInt()I"))
    private int signed32$readLongAsInt(RegistryFriendlyByteBuf buffer) {
        long v = buffer.readLong();
        switch (this.signed32$coordIndex) {
            case 0 -> this.signed32$x8 = v;
            case 1 -> this.signed32$y8 = v;
            default -> this.signed32$z8 = v;
        }
        this.signed32$coordIndex++;
        return (int) v;
    }

    /** 編碼：三個座標寫 long，其餘欄位逐字照抄原版。 */
    @Overwrite
    private void write(RegistryFriendlyByteBuf buffer) {
        SoundEvent.STREAM_CODEC.encode(buffer, this.sound);
        buffer.writeEnum(this.source);
        buffer.writeLong(this.signed32$x8);
        buffer.writeLong(this.signed32$y8);
        buffer.writeLong(this.signed32$z8);
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