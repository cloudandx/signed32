package org.cloudandx.signed32.mixin.server.fix;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings({"OverwriteAuthorRequired", "OptionalUsedAsFieldOrParameterType"})
@Mixin(JigsawPlacement.class)
public class JigsawPlacementMixin {

        @Shadow
        @Final
        private static Logger LOGGER;

        @Overwrite
        public static Optional<Structure.GenerationStub> addPieces(
                        Structure.GenerationContext context,
                        Holder<StructureTemplatePool> startPool,
                        Optional<Identifier> startJigsaw,
                        int maxDepth,
                        BlockPos position,
                        boolean doExpansionHack,
                        Optional<Heightmap.Types> projectStartToHeightmap,
                        JigsawStructure.MaxDistance maxDistanceFromCenter,
                        PoolAliasLookup poolAliasLookup,
                        DimensionPadding dimensionPadding,
                        LiquidSettings liquidSettings) {
                RegistryAccess registryAccess = context.registryAccess();
                ChunkGenerator chunkGenerator = context.chunkGenerator();
                StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
                LevelHeightAccessor heightAccessor = context.heightAccessor();
                WorldgenRandom random = context.random();
                Registry<StructureTemplatePool> pools = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL);
                Rotation centerRotation = Rotation.getRandom(random);
                StructureTemplatePool centerPool = startPool.unwrapKey()
                                .flatMap(key -> pools.getOptional(
                                                poolAliasLookup.lookup((ResourceKey<StructureTemplatePool>) key)))
                                .orElse(startPool.value());
                StructurePoolElement centerElement = centerPool.getRandomTemplate(random);
                if (centerElement == EmptyPoolElement.INSTANCE) {
                        return Optional.empty();
                }

                BlockPos anchoredPosition;
                if (startJigsaw.isPresent()) {
                        Identifier targetJigsawId = startJigsaw.get();
                        Optional<BlockPos> anchor = getRandomNamedJigsaw(centerElement, targetJigsawId, position,
                                        centerRotation,
                                        structureTemplateManager, random);
                        if (anchor.isEmpty()) {
                                LOGGER.error("No starting jigsaw {} found in start pool {}", targetJigsawId,
                                                startPool.unwrapKey().map(key -> key.identifier().toString())
                                                                .orElse("<unregistered>"));
                                return Optional.empty();
                        }
                        anchoredPosition = anchor.get();
                } else {
                        anchoredPosition = position;
                }

                Vec3i localAnchorPosition = anchoredPosition.subtract(position);
                BlockPos adjustedPosition = position.subtract(localAnchorPosition);
                PoolElementStructurePiece centerPiece = new PoolElementStructurePiece(
                                structureTemplateManager,
                                centerElement,
                                adjustedPosition,
                                centerElement.getGroundLevelDelta(),
                                centerRotation,
                                centerElement.getBoundingBox(structureTemplateManager, adjustedPosition,
                                                centerRotation),
                                liquidSettings);
                BoundingBox box = centerPiece.getBoundingBox();
                int centerX = (int) (((long) box.maxX() + (long) box.minX()) / 2L);
                int centerZ = (int) (((long) box.maxZ() + (long) box.minZ()) / 2L);
                int bottomY = projectStartToHeightmap.isEmpty()
                                ? adjustedPosition.getY()
                                : position.getY() + chunkGenerator.getFirstFreeHeight(centerX, centerZ,
                                                projectStartToHeightmap.get(), heightAccessor, context.randomState());
                int oldAbsoluteGroundY = box.minY() + centerPiece.getGroundLevelDelta();
                centerPiece.move(0, bottomY - oldAbsoluteGroundY, 0);
                if (isStartTooCloseToWorldHeightLimits(heightAccessor, dimensionPadding,
                                centerPiece.getBoundingBox())) {
                        LOGGER.debug("Center piece {} with bounding box {} does not fit dimension padding {}",
                                        centerElement,
                                        centerPiece.getBoundingBox(), dimensionPadding);
                        return Optional.empty();
                }

                int centerY = bottomY + localAnchorPosition.getY();
                return Optional.of(new Structure.GenerationStub(
                                new BlockPos(centerX, centerY, centerZ),
                                builder -> {
                                        List<PoolElementStructurePiece> pieces = Lists.newArrayList();
                                        pieces.add(centerPiece);
                                        if (maxDepth > 0) {
                                                AABB aabb = new AABB(
                                                                (double) ((long) centerX - (long) maxDistanceFromCenter
                                                                                .horizontal()),
                                                                (double) Math.max(
                                                                                centerY - maxDistanceFromCenter
                                                                                                .vertical(),
                                                                                heightAccessor.getMinY()
                                                                                                + dimensionPadding
                                                                                                                .bottom()),
                                                                (double) ((long) centerZ - (long) maxDistanceFromCenter
                                                                                .horizontal()),
                                                                (double) ((long) centerX + (long) maxDistanceFromCenter
                                                                                .horizontal() + 1L),
                                                                (double) Math.min(
                                                                                centerY + maxDistanceFromCenter
                                                                                                .vertical() + 1,
                                                                                heightAccessor.getMaxY() + 1
                                                                                                - dimensionPadding
                                                                                                                .top()),
                                                                (double) ((long) centerZ + (long) maxDistanceFromCenter
                                                                                .horizontal() + 1L));
                                                VoxelShape shape = Shapes.join(Shapes.create(aabb),
                                                                Shapes.create(AABB.of(box)),
                                                                BooleanOp.ONLY_FIRST);
                                                addPieces(context.randomState(), maxDepth, doExpansionHack,
                                                                chunkGenerator,
                                                                structureTemplateManager, heightAccessor, random, pools,
                                                                centerPiece, pieces, shape,
                                                                poolAliasLookup, liquidSettings);
                                                pieces.forEach(builder::addPiece);
                                        }
                                }));
        }

        @Shadow
        private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement element, Identifier name,
                        BlockPos pos,
                        Rotation rotation, StructureTemplateManager structureTemplateManager, WorldgenRandom random) {
                throw new AssertionError();
        }

        @Shadow
        private static void addPieces(RandomState randomState, int maxDepth, boolean useExpansionHack,
                        ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager,
                        LevelHeightAccessor heightAccessor, RandomSource random, Registry<StructureTemplatePool> pools,
                        PoolElementStructurePiece startPiece, List<PoolElementStructurePiece> pieces, VoxelShape free,
                        PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
                throw new AssertionError();
        }

        @Shadow
        private static boolean isStartTooCloseToWorldHeightLimits(LevelHeightAccessor heightAccessor,
                        DimensionPadding dimensionPadding, BoundingBox centerPieceBb) {
                throw new AssertionError();
        }
}