package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.block.BidirectionalPipeBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.common.util.TriPredicate;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class TaprootFeature extends Feature<NoneFeatureConfiguration> {
    public TaprootFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        WorldGenLevel level = context.level();

        BlockPos seedPos = context.origin();
        BlockPos.MutableBlockPos cursor = seedPos.mutable();

        Map<Direction, Float> changeDirectionProbabilities = new HashMap<>();
        for (Direction direction : Direction.values()) {
            float probability;
            if (direction == Direction.DOWN) {
                probability = 0.2F;
            } else if (direction == Direction.UP) {
                probability = 0.9F;
            } else {
                probability = 0.6F;
            }
            changeDirectionProbabilities.put(direction, probability);
        }

        Direction startingGrowthDirection = Direction.DOWN;
        Direction currentGrowthDirection = startingGrowthDirection;

        cursor.set(seedPos);
        List<BlockPos> stemShape = new ArrayList<>();
        stemShape.add(cursor.immutable());
        boolean stemHit = false;
        for (int i = 0; i < 24; i++) {
            cursor.move(currentGrowthDirection);
            stemShape.add(cursor.immutable());

            // randomly change directions
            float changeDirectionProbability = changeDirectionProbabilities.get(currentGrowthDirection);
            if (random.nextFloat() < changeDirectionProbability) {
                if (currentGrowthDirection.getAxis() == Direction.Axis.Y) {
                    currentGrowthDirection = Direction.from2DDataValue(random.nextInt(4));
                } else {
                    if (random.nextFloat() < 0.8) {
                        // rotate vertically
                        currentGrowthDirection = random.nextBoolean() ?
                                Direction.DOWN :
                                Direction.UP;
                    } else {
                        // rotate horizontally
                        currentGrowthDirection = random.nextFloat() < 0.5 ?
                                currentGrowthDirection.getClockWise() :
                                currentGrowthDirection.getCounterClockWise();
                    }
                }
            }

            // stop if we hit a block
            BlockState stateAtPos = level.getBlockState(cursor);
            if (!stateAtPos.canBeReplaced() && !stateAtPos.is(BlockTags.REPLACEABLE_BY_TREES)) {
                stemHit = true;
                break;
            }
        }

        Direction lastGrowthDir = startingGrowthDirection;
        for (int i = 0; i < stemShape.size() - 1; i++) {
            BlockPos currentPos = stemShape.get(i),
                    nextPos = stemShape.get(i + 1);
            Direction nextGrowthDir = Direction.fromDelta(
                    currentPos.getX() - nextPos.getX(),
                    currentPos.getY() - nextPos.getY(),
                    currentPos.getZ() - nextPos.getZ()
            );
            BlockState stateToPlace = ClinkerBlocks.TAPROOT_BURL.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, lastGrowthDir.getAxis());

            float factor = i / (stemShape.size() - 1f);
            if (stemHit) factor = (factor > 0.5 ? 1 - factor : factor) * 2;
            factor = 1 - factor;
            float diameter = Mth.map(factor, 0, 1, 1.3F, 3.5F);
            float radius = diameter / 2f;
            int radBounds = (int) Math.ceil(radius);
            boolean shouldOffsetDiameter = Mth.floor(diameter) % 2 == 0 && Mth.floor(diameter) > 0;
            for (int x = -radBounds; x < radBounds; x++) {
                for (int y = -radBounds; y < radBounds; y++) {
                    for (int z = -radBounds; z < radBounds; z++) {
                        double length = shouldOffsetDiameter ? Mth.length(x + 0.5, y + 0.5, z + 0.5) : Mth.length(x, y, z);
                        cursor.set(currentPos).move(x, y, z);
                        BlockState stateAtPos = level.getBlockState(cursor);
                        if (length < radius) level.setBlock(cursor, stateToPlace, 2);
                    }
                }
            }
            lastGrowthDir = nextGrowthDir;
        }

        // branches
        int branchCount = stemShape.size();
        for (int i = 0; i < branchCount; i++) {
            int posIndex = random.nextInt(stemShape.size() - 1);
            BlockPos branchSeedPos = stemShape.get(posIndex),
                     nextBranchSeedPos = stemShape.get(posIndex + 1);
            cursor.set(branchSeedPos);

            Direction travelDir = Direction.fromDelta(
                    nextBranchSeedPos.getX() - branchSeedPos.getX(),
                    nextBranchSeedPos.getY() - branchSeedPos.getY(),
                    nextBranchSeedPos.getZ() - branchSeedPos.getZ()
            );
            Direction inputDir = travelDir;
            while (inputDir == travelDir) inputDir = Direction.from2DDataValue(random.nextInt(4));
            Direction attachmentDir = inputDir;

            boolean shouldSkip = true;
            for (int j = 1; j < 3; j++) {
                cursor.move(inputDir, j);
                BlockState stateAtPos = level.getBlockState(cursor);
                cursor.move(inputDir, -j);
                if (stateAtPos.canBeReplaced() || stateAtPos.is(BlockTags.REPLACEABLE_BY_TREES)) {
                    shouldSkip = false;
                    cursor.move(inputDir, j);
                    break;
                } else if (!stateAtPos.is(ClinkerBlocks.TAPROOT_BURL.get())) {
                    break;
                }
            }
            if (shouldSkip) continue;

            int branchLength = random.nextIntBetweenInclusive(3, 16);
            for (int j = 0; j < branchLength; j++) {
                // rotation directions and vectors for this segment
                int aX = attachmentDir.getStepX(), aY = attachmentDir.getStepY(), aZ = attachmentDir.getStepZ();
                int bX = travelDir.getStepX(), bY = travelDir.getStepY(), bZ = travelDir.getStepZ();
                int cX = (aY * bZ) - (aZ * bY), cY = (aZ * bX) - (aX * bZ), cZ = (aX * bY) - (aY * bX);
                Direction rotationVector = Direction.fromDelta(cX, cY, cZ);
                if (rotationVector == null) break;
                Direction.Axis rotationAxis = rotationVector.getAxis();

                // first check the current attachment dir
                cursor.move(attachmentDir);
                BlockState attachedState = level.getBlockState(cursor);
                cursor.move(attachmentDir.getOpposite());
                boolean hasAttachment = attachedState.isSolid();

                boolean shouldStop = false;
                if (!hasAttachment) {
                    boolean clockwise = rotationVector.getAxisDirection() == Direction.AxisDirection.POSITIVE;
                    attachmentDir = clockwise ? attachmentDir.getClockWise(rotationAxis) : attachmentDir.getCounterClockWise(rotationAxis);
                    travelDir = clockwise ? travelDir.getClockWise(rotationAxis) : travelDir.getCounterClockWise(rotationAxis);
                } else {
                    shouldStop = true;
                    boolean clockwiseFirst = random.nextBoolean();
                    Direction[] attemptedTravelDirs = {
                            travelDir,
                            clockwiseFirst ?
                                    travelDir.getClockWise(attachmentDir.getAxis()) :
                                    travelDir.getCounterClockWise(attachmentDir.getAxis()),
                            clockwiseFirst ?
                                    travelDir.getCounterClockWise(attachmentDir.getAxis()) :
                                    travelDir.getClockWise(attachmentDir.getAxis()),
                            rotationVector.getAxisDirection() != Direction.AxisDirection.POSITIVE ?
                                    travelDir.getClockWise(rotationAxis) :
                                    travelDir.getCounterClockWise(rotationAxis)
                    };
                    Direction[] attachmentDirs = {
                            attachmentDir, attachmentDir, attachmentDir,
                            rotationVector.getAxisDirection() != Direction.AxisDirection.POSITIVE ?
                                    attachmentDir.getClockWise(rotationAxis) :
                                    attachmentDir.getCounterClockWise(rotationAxis)
                    };

                    boolean attemptTurnFirst = random.nextInt(2) == 0;
                    if (attemptTurnFirst) {
                        Direction temp;
                        temp = attemptedTravelDirs[0];
                        attemptedTravelDirs[0] = attemptedTravelDirs[1];
                        attemptedTravelDirs[1] = attemptedTravelDirs[2];
                        attemptedTravelDirs[2] = temp;

                        temp = attachmentDirs[0];
                        attachmentDirs[0] = attachmentDirs[1];
                        attachmentDirs[1] = attachmentDirs[2];
                        attachmentDirs[2] = temp;
                    }

                    for (int k = 0; k < attemptedTravelDirs.length; k++) {
                        Direction attemptedTravelDir = attemptedTravelDirs[k];
                        cursor.move(attemptedTravelDir);
                        BlockState travelState = level.getBlockState(cursor);
                        cursor.move(attemptedTravelDir.getOpposite());
                        boolean isBlocked = !travelState.canBeReplaced() && !travelState.is(BlockTags.REPLACEABLE_BY_TREES);
                        if (!isBlocked) {
                            shouldStop = false;
                            travelDir = attemptedTravelDir;
                            attachmentDir = attachmentDirs[k];
                            break;
                        }
                    }
                }

                level.setBlock(cursor,
                        ClinkerBlocks.TAPROOTS.get().defaultBlockState()
                                .setValue(BidirectionalPipeBlock.INPUT_FACE, inputDir)
                                .setValue(BidirectionalPipeBlock.OUTPUT_FACE, travelDir),
                        2
                );
                inputDir = travelDir.getOpposite();
                cursor.move(travelDir);
                if (shouldStop) break;
            }
        }

        return true;
    }

    // returns whether we stopped early
    private boolean buildRootShape(BlockPos seedPos, Direction startingDirection, int iterations,
                                   TriFunction<Integer, BlockPos, Direction, Direction> directionChooser,
                                   TriPredicate<Integer, BlockPos, Direction> shouldStop,
                                   List<BlockPos> returnList) {
        BlockPos.MutableBlockPos growthPos = seedPos.mutable();
        Direction currentGrowthDirection = startingDirection;

        returnList.add(growthPos.immutable());
        for (int i = 0; i < iterations; i++) {
            growthPos.move(currentGrowthDirection);

            BlockPos newPos = growthPos.immutable();
            returnList.add(newPos);

            currentGrowthDirection = directionChooser.apply(i, newPos, currentGrowthDirection);
            if (shouldStop.test(i, newPos, currentGrowthDirection)) return true;
        }
        return false;
    }

    private void placeRootShape(List<BlockPos> shape, Direction startingDirection, Direction endingDirection, WorldGenLevel level,
                                TriFunction<BlockPos, Direction, Direction, @Nullable BlockState> stateChooser,
                                Function<Integer, Float> diameterChooser) {
        BlockPos.MutableBlockPos placementPos = new BlockPos.MutableBlockPos();
        Direction lastGrowthDir = startingDirection;
        for (int i = 0; i < shape.size() - 1; i++) {
            BlockPos currentPos = shape.get(i),
                     nextPos = shape.get(i + 1);
            Direction nextGrowthDir = Direction.fromDelta(
                    nextPos.getX() - currentPos.getX(),
                    nextPos.getY() - currentPos.getY(),
                    nextPos.getZ() - currentPos.getZ()
            );
            BlockState state =  stateChooser.apply(currentPos, lastGrowthDir, nextGrowthDir.getOpposite());
            if (state == null) continue;

            float diameter = diameterChooser.apply(i);
            float radius = diameter / 2f;
            int radBounds = (int) Math.ceil(radius);
            boolean shouldOffsetDiameter = Mth.floor(diameter) % 2 == 0 && Mth.floor(diameter) > 0;
            for (int x = -radBounds; x < radBounds; x++) {
                for (int y = -radBounds; y < radBounds; y++) {
                    for (int z = -radBounds; z < radBounds; z++) {
                        double length = shouldOffsetDiameter ? Mth.length(x + 0.5, y + 0.5, z + 0.5) : Mth.length(x, y, z);
                        if (length < radius) {
                            placementPos.set(currentPos).move(x, y, z);
                            level.setBlock(placementPos, state, 2);
                        }
                    }
                }
            }

            lastGrowthDir = nextGrowthDir;
        }
        BlockState lastState = stateChooser.apply(shape.getLast(), lastGrowthDir, endingDirection.getOpposite());
        if (lastState != null) level.setBlock(shape.getLast(), lastState, 2);
    }
}
