package org.cloudandx.signed32.mixin.fix;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FillCommand.class)
public abstract class FillCommandMixin {

    @Shadow
    @Final
    private static Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE;

    @Redirect(method = "fillBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private static Object signed32$rejectHugeSpan(GameRules rules, GameRule<Integer> rule, CommandSourceStack source,
            BoundingBox region) throws CommandSyntaxException {
        int limit = rules.get(rule);
        if (span(region.minX(), region.maxX()) > limit
                || span(region.minY(), region.maxY()) > limit
                || span(region.minZ(), region.maxZ()) > limit) {
            throw ERROR_AREA_TOO_LARGE.create(limit, (int) Math.min(maxSpan(region), Integer.MAX_VALUE));
        }
        return limit;
    }

    @Unique
    private static long span(int a, int b) {
        return Math.abs((long) b - a) + 1;
    }

    @Unique
    private static long maxSpan(BoundingBox box) {
        return Math.max(span(box.minX(), box.maxX()),
                Math.max(span(box.minY(), box.maxY()), span(box.minZ(), box.maxZ())));
    }
}