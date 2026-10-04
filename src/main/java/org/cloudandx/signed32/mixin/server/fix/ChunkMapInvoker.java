package org.cloudandx.signed32.mixin.server.fix;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapInvoker {

    @Invoker("getVisibleChunkIfPresent")
    ChunkHolder signed32$getVisibleChunkIfPresent(long key);
}