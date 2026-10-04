package org.cloudandx.signed32.mixin.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class LightingMixin {

    // =========================================================================
    // 1. 光照層讀寫安全防禦 (LayerLightSectionStorage)
    // =========================================================================
    @Mixin(LayerLightSectionStorage.class)
    public static abstract class LayerLightSectionStorageMixin {

        @Shadow
        protected abstract DataLayer getDataLayer(long sectionNode, boolean updating);

        /**
         * 讀取防禦：當查詢的區段尚未分配光照層時，安全回傳 0 級光照，徹底消除 layer.get(...) NPE 崩潰
         */
        @Inject(method = "getStoredLevel", at = @At("HEAD"), cancellable = true)
        private void safeGetStoredLevel(long blockNode, CallbackInfoReturnable<Integer> cir) {
            long sectionPos = SectionPos.blockToSection(blockNode);
            DataLayer layer = this.getDataLayer(sectionPos, true);
            if (layer == null) {
                cir.setReturnValue(0);
            }
        }

        /**
         * 寫入防禦：若該區段尚未分配光照層，直接取消寫入，防止 propagateIncreases 觸發後續空指針
         */
        @Inject(method = "setStoredLevel", at = @At("HEAD"), cancellable = true)
        private void safeSetStoredLevel(long blockNode, int level, CallbackInfo ci) {
            long sectionPos = SectionPos.blockToSection(blockNode);
            if (this.getDataLayer(sectionPos, true) == null) {
                ci.cancel();
            }
        }
    }

    // =========================================================================
    // 2. 光照映射表防禦 (DataLayerStorageMap)
    // =========================================================================
    @Mixin(DataLayerStorageMap.class)
    public static abstract class DataLayerStorageMapMixin {

        @Shadow @Final
        protected Long2ObjectOpenHashMap<DataLayer> map;

        @Shadow
        public abstract void clearCache();

        /**
         * 複製防禦：防止 copyDataLayer 對 null 呼叫 .copy()，自動填補空白 DataLayer
         */
        @Inject(method = "copyDataLayer", at = @At("HEAD"), cancellable = true)
        private void safeCopyDataLayer(long sectionNode, CallbackInfoReturnable<DataLayer> cir) {
            DataLayer existing = this.map.get(sectionNode);
            if (existing == null) {
                DataLayer newLayer = new DataLayer();
                this.map.put(sectionNode, newLayer);
                this.clearCache();
                cir.setReturnValue(newLayer);
            }
        }
    }
}