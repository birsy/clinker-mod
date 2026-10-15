package birsy.clinker.common.world.level.gen.content.structure;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TownRuinsStructure extends Structure {
    public static final MapCodec<TownRuinsStructure> CODEC = simpleCodec(TownRuinsStructure::new);

    protected TownRuinsStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        RandomSource random = context.random();
        int x = context.chunkPos().getBlockX(context.random().nextInt(15)),
            z = context.chunkPos().getBlockZ(context.random().nextInt(15));
        int centerY = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());

        return Optional.of(new GenerationStub(new BlockPos(x, centerY, z), (builder) -> {
            int buildingCount = random.nextIntBetweenInclusive(3, 12);
            List<BoundingBox> boundingBoxes = new ArrayList<>(buildingCount);
            for (int b = 0; b < buildingCount; b++) {
                TRY_AGAIN:
                for (int tries = 0; tries < 2; tries++) {
                    int buildingX = x + random.nextIntBetweenInclusive(-24, 24),
                        buildingZ = z + random.nextIntBetweenInclusive(-24, 24);
                    int minY = context.heightAccessor().getMaxBuildHeight(), maxY = context.heightAccessor().getMinBuildHeight(), totalY = 0;
                    for (int ySamples = 0; ySamples < 4; ySamples++) {
                        int ySample = context.chunkGenerator().getFirstOccupiedHeight(
                                buildingX + random.nextIntBetweenInclusive(-6, 6), buildingZ + random.nextIntBetweenInclusive(-6, 6),
                                Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
                        minY = Math.min(ySample, minY); maxY = Math.max(ySample, maxY);
                        if (maxY - minY > 5) continue TRY_AGAIN;
                        totalY += ySample;
                    }

                    int buildingY = totalY / 4;
                    if (Math.abs(centerY - buildingY) > 5) continue TRY_AGAIN;

                    int width1 = (int) Math.round(random.triangle(4.5, 2)),
                        width2 = width1 + random.nextIntBetweenInclusive(0, 3);
                    boolean swapAxis = random.nextBoolean();
                    int xWidth = swapAxis ? width1 : width2,
                        zWidth = swapAxis ? width2 : width1;

                    int[] lengths = new int[4], widths = new int[4];
                    for (int i = 0; i < 4; i++) {
                        Direction direction = Direction.from2DDataValue(i);
                        boolean hasHall = random.nextInt(2) != 0;
                        int maxWidth = direction.getAxis() == Direction.Axis.X ? zWidth : xWidth;
                        lengths[i] = hasHall ? random.nextIntBetweenInclusive(width1 - 1, width2 + 1) : 0;
                        widths[i] = random.nextIntBetweenInclusive(3, maxWidth);
                    }

                    StructurePiece piece = new CrossBuilding(
                            buildingY - 1, random.nextIntBetweenInclusive(3, 5),
                            buildingX, buildingZ, buildingX + width1, buildingZ + zWidth,
                            lengths, widths, random.nextInt(4)
                    );
                    for (BoundingBox boundingBox : boundingBoxes) {
                        if (boundingBox.intersects(piece.getBoundingBox())) continue TRY_AGAIN;
                    }

                    boundingBoxes.add(piece.getBoundingBox());
                    builder.addPiece(piece);
                    break;
                }
            }

        }));
    }

    @Override
    public StructureType<?> type() {
        return ClinkerStructures.Types.TOWN_RUINS.get();
    }

    // each building is a 'cross' with a centerpiece room and halls extending outwards.
    public static class CrossBuilding extends StructurePiece {
        final int y, height;
        final int minCenterX, minCenterZ, maxCenterX, maxCenterZ;
        // indexed by Direction.2DDataValue
        final int[] hallLengths, hallWidths;
        final int entranceDirectionIndex;

        public CrossBuilding(int y, int height,
                        int x1, int z1, int x2, int z2,
                        int[] lengths, int[] widths, int entranceDirectionIndex) {
            super(ClinkerStructures.Pieces.TOWN_RUINS_CROSS.get(), 0, new BoundingBox(
                    Math.min(x1, x2) - lengths[1], y - 32,
                    Math.min(z1, z2) - lengths[2],
                    Math.max(x1, x2) + lengths[3], y + height + 32,
                    Math.max(z1, z2) + lengths[0]));
            this.y = y;
            this.height = height;
            this.minCenterX = Math.min(x1, x2); this.minCenterZ = Math.min(z1, z2);
            this.maxCenterX = Math.max(x1, x2); this.maxCenterZ = Math.max(z1, z2);
            this.hallLengths = lengths;
            this.hallWidths = widths;
            this.entranceDirectionIndex = entranceDirectionIndex;
            this.setOrientation(null);
        }

        public CrossBuilding(StructurePieceSerializationContext context, CompoundTag tag) {
            super(ClinkerStructures.Pieces.TOWN_RUINS_CROSS.get(), tag);
            this.y = tag.getInt("Y");
            this.height = tag.getInt("Height");
            this.minCenterX = tag.getInt("MinCenterX");
            this.minCenterZ = tag.getInt("MinCenterZ");
            this.maxCenterX = tag.getInt("MaxCenterX");
            this.maxCenterZ = tag.getInt("MaxCenterZ");
            this.hallLengths = tag.getIntArray("HallLengths");
            this.hallWidths = tag.getIntArray("HallWidths");
            this.entranceDirectionIndex = tag.getInt("EntranceDirectionIndex");
            this.setOrientation(null);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putInt("Y", y);
            tag.putInt("Height", height);
            tag.putInt("MinCenterX", minCenterX);
            tag.putInt("MinCenterZ", minCenterZ);
            tag.putInt("MaxCenterX", maxCenterX);
            tag.putInt("MaxCenterZ", maxCenterZ);
            tag.putIntArray("HallLengths", hallLengths);
            tag.putIntArray("HallWidths", hallWidths);
            tag.putInt("EntranceDirectionIndex", entranceDirectionIndex);
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

            NormalNoise noise = NormalNoise.create(RandomSource.create(level.getSeed()), -3, 1.0, 2.0, 1.0);

            BlockPos min = new BlockPos(minCenterX, y + 1, minCenterZ), max = new BlockPos(maxCenterX, y + height, maxCenterZ);
            fillFloor(level, box, min, max, noise);
            fillVoid(level, box, min, max);

            for (int i = 0; i < 4; i++) generateFace(level, box, Direction.from2DDataValue(i), noise, i == entranceDirectionIndex);

            // pillars at the corners
            BlockState pillar = ClinkerBlocks.BRIMSTONE_PILLAR.get().defaultBlockState();
            this.generateBox(level, box,
                    minCenterX, y, minCenterZ,
                    minCenterX, y + height, minCenterZ,
                    pillar, pillar, true);
            this.generateBox(level, box,
                    maxCenterX, y, minCenterZ,
                    maxCenterX, y + height, minCenterZ,
                    pillar, pillar, true);
            this.generateBox(level, box,
                    minCenterX, y, maxCenterZ,
                    minCenterX, y + height, maxCenterZ,
                    pillar, pillar, true);
            this.generateBox(level, box,
                    maxCenterX, y, maxCenterZ,
                    maxCenterX, y + height, maxCenterZ,
                    pillar, pillar, true);

        }

        private void generateFace(WorldGenLevel level, BoundingBox box, Direction normalDir, NormalNoise noise, boolean isEntrance) {
            Direction wallDir = normalDir.getClockWise(); // direction we walk along the face
            int wallSpan = normalDir.getAxis() == Direction.Axis.X ? maxCenterZ - minCenterZ : maxCenterX - minCenterX;

            BlockPos start = roomCorner(normalDir, wallDir.getOpposite()), end = start.relative(wallDir, wallSpan);

            int hallLength = hallLengths[normalDir.get2DDataValue()];
            int hallSpan = hallWidths[normalDir.get2DDataValue()];
            int top = y + height;

            boolean hasNoHall = hallLength <= 0 || hallSpan <= 0;
            if ((hasNoHall && !isEntrance) || hallSpan > wallSpan) {
                // no hall: one solid wall
                fillWall(level, box, start, end, noise);
                return;
            }

            int before = (wallSpan - hallSpan) / 2;
            int hallEnd = before + hallSpan;

            // room wall on either side of the opening
            fillWall(level, box, start, start.relative(wallDir, before), noise);
            fillWall(level, box, start.relative(wallDir, hallEnd), start.relative(wallDir, wallSpan), noise);

            // lintel over the opening is skipped with "seamless" corridors
            boolean isSeamless = !(hallSpan > 1 && hallSpan != wallSpan);
//            if (hallSpan > 1 && hallSpan != wallSpan) {
//                fillWall(level, box,
//                        start.relative(wallDir, before + 1), start.relative(wallDir, hallEnd - 1),
//                        noise);
//            }

            if (!hasNoHall) {
                // hall gen
                BlockPos nearRow = start.relative(normalDir), farRow = start.relative(normalDir, hallLength);
                BlockPos nearA = nearRow.relative(wallDir, before), nearB = nearRow.relative(wallDir, hallEnd);
                BlockPos farA = farRow.relative(wallDir, before), farB = farRow.relative(wallDir, hallEnd);

                // floor
                this.fillFloor(level, box, nearA, farB, noise);
                // funky floor decor
                if (!isSeamless) {
                    BlockState sidewaysPillar = ClinkerBlocks.BRIMSTONE_PILLAR.get().defaultBlockState().setValue(
                            RotatedPillarBlock.AXIS, wallDir.getAxis()
                    );
                    BlockPos decorPosA = nearA.relative(normalDir, -1).relative(wallDir, 1),
                            decorPosB = nearB.relative(normalDir, -1).relative(wallDir, -1);
                    this.generateBox(level, box,
                            Math.min(decorPosA.getX(), decorPosB.getX()), y, Math.min(decorPosA.getZ(), decorPosB.getZ()),
                            Math.max(decorPosA.getX(), decorPosB.getX()), y, Math.max(decorPosA.getZ(), decorPosB.getZ()),
                            sidewaysPillar, sidewaysPillar, false);
                    BlockState pointyPointyPointy = ClinkerBlocks.CHISELED_BRIMSTONE.get().defaultBlockState().setValue(
                            GlazedTerracottaBlock.FACING, isEntrance ? normalDir.getOpposite() : normalDir
                    );
                    int decorOffset = (hallSpan - 2);
                    decorOffset = decorOffset % 2 == 0 ? decorOffset - (decorOffset / 2) + 1 : decorOffset - (decorOffset / 2);
                    decorPosA = nearA.relative(normalDir, -1).relative(wallDir, decorOffset);
                    decorPosB = nearB.relative(normalDir, -1).relative(wallDir, -decorOffset);
                    this.generateBox(level, box,
                            Math.min(decorPosA.getX(), decorPosB.getX()), y, Math.min(decorPosA.getZ(), decorPosB.getZ()),
                            Math.max(decorPosA.getX(), decorPosB.getX()), y, Math.max(decorPosA.getZ(), decorPosB.getZ()),
                            pointyPointyPointy, pointyPointyPointy, false);
                }

                fillVoid(level, box, nearA.atY(y + 1), farB.atY(top));
                fillWall(level, box, nearA, farA, noise); // side wall
                fillWall(level, box, nearB, farB, noise); // other side wall
                if (!isEntrance) fillWall(level, box, farA, farB, noise); // end cap, only if this hall isnt an entrance
            }
        }

        private BlockPos roomCorner(Direction a, Direction b) {
            Direction xDir = a.getAxis() == Direction.Axis.X ? a : b;
            Direction zDir = a.getAxis() == Direction.Axis.Z ? a : b;
            return new BlockPos(
                    xDir.getStepX() > 0 ? maxCenterX : minCenterX, y,
                    zDir.getStepZ() > 0 ? maxCenterZ : minCenterZ);
        }

        private void fillVoid(WorldGenLevel level, BoundingBox box, BlockPos a, BlockPos b) {
            int xMin = Math.min(a.getX(), b.getX()), yMin = Math.min(a.getY(), b.getY()), zMin = Math.min(a.getZ(), b.getZ()),
                xMax = Math.max(a.getX(), b.getX()), yMax = Math.max(a.getY(), b.getY()), zMax = Math.max(a.getZ(), b.getZ());
            BlockPos.MutableBlockPos pos = a.mutable();
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    boolean foundSurface = false;
                    int surfaceHeight = yMin;
                    for (int y = yMin; y <= yMax; y++) {
                        pos.set(x, y, z);
                        if (!level.getBlockState(pos).isSolid()) {
                            surfaceHeight = y;
                            foundSurface = true;
                            break;
                        }
                    }
                    if (!foundSurface) continue;

                    int offset = 2;
                    for (int y = yMin; y <= surfaceHeight; y++) {
                        pos.set(x, y + offset, z);
                        // scoot blocks down, kind of...
                        BlockState state = level.getBlockState(pos);
                        this.placeBlock(level, state, x, y, z, box);
                    }
                    // fill the rest with air
                    this.generateAirBox(level, box, x, surfaceHeight + 1, z, x, yMax, z);
                }
            }
        }

        private void fillWall(WorldGenLevel level, BoundingBox box, BlockPos a, BlockPos b, NormalNoise noise) {
            BlockState main = ClinkerBlocks.BRIMSTONE_BRICKS.get().defaultBlockState(), rough = ClinkerBlocks.BRIMSTONE.get().defaultBlockState(),
                       mainSlab = ClinkerBlocks.BRIMSTONE_BRICK_SLAB.get().defaultBlockState();
            int xMin = Math.min(a.getX(), b.getX()), zMin = Math.min(a.getZ(), b.getZ()), yMin = y + 1, yMax = y + height,
                xMax = Math.max(a.getX(), b.getX()), zMax = Math.max(a.getZ(), b.getZ());
            int wallHeight = yMax - yMin;
            BlockPos.MutableBlockPos pos = a.mutable();
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    double ruinNoise = noise.getValue(x, 0, z);
                    double ruinedWallHeight = (ruinNoise * 0.5 + 0.5) * wallHeight;
                    int ruinedYMax = yMin + (int) Math.floor(ruinedWallHeight);
                    for (int y = yMin; y <= ruinedYMax; y++) {
                        double blockVariantNoise = noise.getValue(x, y * 0.25, z);
                        BlockState state = blockVariantNoise > -0.2 ? main : rough;
                        this.placeBlock(level, state, x, y, z, box);
                    }
                    if (Mth.frac(ruinedWallHeight) >= 0.5) {
                        pos.set(x, ruinedYMax + 1, z);
                        if (level.getBlockState(pos).isSolid()) continue;

                        double blockVariantNoise = noise.getValue(x, y * 0.25, z);
                        if (blockVariantNoise > -0.2) {
                            this.placeBlock(level, mainSlab, x, ruinedYMax + 1, z, box);
                        }
                    }
                }
            }
        }

        private void fillFloor(WorldGenLevel level, BoundingBox box, BlockPos a, BlockPos b, NormalNoise noise) {
            BlockState main = ClinkerBlocks.POLISHED_BRIMSTONE.get().defaultBlockState(), rough = ClinkerBlocks.BRIMSTONE.get().defaultBlockState();
            int xMin = Math.min(a.getX(), b.getX()), zMin = Math.min(a.getZ(), b.getZ()),
                xMax = Math.max(a.getX(), b.getX()), zMax = Math.max(a.getZ(), b.getZ());
            BlockPos.MutableBlockPos pos = a.mutable();
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    double blockVariantNoise = noise.getValue(x, y, z);
                    boolean isEdge = x == xMin || x == xMax || z == zMin || z == zMax;
                    BlockState state = blockVariantNoise > -0.6 ? main : rough;
                    this.placeBlock(level, state, x, this.y, z, box);
                    state = rough;

                    int solidCount = 0;
                    for (int i = this.y - 1; i > level.getMinBuildHeight(); i--) {
                        pos.set(x, i, z);
                        if (isEdge) {
                            blockVariantNoise = noise.getValue(x, i, z);
                            state = blockVariantNoise > -0.3 ? main : rough;
                        }
                        if (level.getBlockState(pos).isSolid()) solidCount++;
                        this.placeBlock(level, state, x, i, z, box);
                        if (solidCount >= 2) break;
                    }
                }
            }
        }
    }
}
