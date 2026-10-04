package org.cloudandx.signed32.mixin.server.fix;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.datafixers.util.Either;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gamerules.GameRules;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FillBiomeCommand.class)
public abstract class FillBiomeCommandMixin {

    @Shadow
    @Final
    private static Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE;

    @Inject(method = "fill(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Holder;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)Lcom/mojang/datafixers/util/Either;", at = @At("HEAD"), cancellable = true)
    private static void signed32$rejectHugeSpan(ServerLevel level, BlockPos rawFrom, BlockPos rawTo, Holder<Biome> biome,
            Predicate<Holder<Biome>> filter, Consumer<Supplier<Component>> messageOutput,
            CallbackInfoReturnable<Either<Integer, CommandSyntaxException>> cir) {
        long limit = level.getGameRules().get(GameRules.MAX_BLOCK_MODIFICATIONS);
        if (span(rawFrom.getX(), rawTo.getX()) > limit
                || span(rawFrom.getY(), rawTo.getY()) > limit
                || span(rawFrom.getZ(), rawTo.getZ()) > limit) {
            cir.setReturnValue(Either.right(ERROR_VOLUME_TOO_LARGE.create(limit,
                    (int) Math.min(maxSpan(rawFrom, rawTo), Integer.MAX_VALUE))));
        }
    }

    @Unique
    private static long span(int a, int b) {
        return Math.abs((long) b - a) + 1;
    }

    @Unique
    private static long maxSpan(BlockPos a, BlockPos b) {
        return Math.max(span(a.getX(), b.getX()),
                Math.max(span(a.getY(), b.getY()), span(a.getZ(), b.getZ())));
    }
}