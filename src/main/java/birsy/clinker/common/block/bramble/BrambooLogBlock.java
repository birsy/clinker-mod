package birsy.clinker.common.block.bramble;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class BrambooLogBlock extends RotatedPillarBlock {
    public static final IntegerProperty PROPERTY_AGE = BlockStateProperties.AGE_15;

    public BrambooLogBlock() {
        super(Block.Properties.of(Material.WOOD)
                .strength(2.0F)
                .sound(SoundType.BAMBOO)
                .harvestLevel(0)
                .harvestTool(ToolType.AXE)
                .randomTicks());
    }

    public void grow(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == axis) {
                BlockPos blockPos = pos.relative(direction);
                BlockState blockState = worldIn.getBlockState(blockPos);

                int age = 0;
                if (blockState.is(ClinkerBlocks.BRAMBOO_STALK.get()) && blockState.getValue(AXIS) == axis) {
                    worldIn.setBlock(blockPos, ClinkerBlocks.BRAMBOO_LOG.get().defaultBlockState().setValue(AXIS, axis), 2);
                } else if (blockState.isAir()) {
                    worldIn.setBlock(blockPos, ClinkerBlocks.BRAMBOO_STALK.get().defaultBlockState().setValue(AXIS, axis), 2);
                    age += 8;
                }

                worldIn.setBlock(pos, ClinkerBlocks.BRAMBOO_LOG.get().defaultBlockState().setValue(AXIS, axis).setValue(PROPERTY_AGE, age), 4);
            }
        }
    }

    public int getMaxStemLength(BlockPos pos) {
        //gets a random number between one and zero based off a block's position.
        float random = (((float)(Mth.getSeed(pos.getX(), pos.getY(), pos.getZ()) & 15L) / 15.0F));
        //min of 7, max of 10
        return (int) (7 + (random * 3));
    }

    public void age(BlockState state, ServerLevel worldIn, BlockPos pos, int amount) {
        Direction.Axis axis = state.getValue(AXIS);
        int age = Mth.clamp(state.getValue(PROPERTY_AGE) + amount, 0, 15);
        worldIn.setBlock(pos, ClinkerBlocks.BRAMBOO_LOG.get().defaultBlockState().setValue(AXIS, axis).setValue(PROPERTY_AGE, age), 4);

        if (age <= 15) {
            grow(worldIn, pos, state);
        }
    }

    private int getStemLength(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        Direction direction = estimateBrambooDirection(worldIn, pos, state);
        BlockPos blockPos = pos.mutable();
        int length = 0;

        for (int i = 0; i < getMaxStemLength(pos) + 1; i++) {
            if (isBlockBramboo(worldIn, blockPos)) {
                length++;
                blockPos.relative(direction);
            } else {
                return length;
            }
        }

        String string;
        if (length <= getMaxStemLength(pos)) {
            string = "an acceptable length.";
        } else {
            string = "longer than it probably should be.";
        }
        Clinker.LOGGER.info("this bramboo is " + length + " blocks long, which is " + string);

        return length;
    }

    private boolean isBlockBramboo(LevelAccessor worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos);
        return blockState.is(ClinkerBlocks.BRAMBOO_STALK.get()) || blockState.is(ClinkerBlocks.BRAMBOO_LOG.get());
    }

    private boolean isBrambooFacingSameDirection(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        BlockState blockState = worldIn.getBlockState(pos);
        return isBlockBramboo(worldIn, pos) && state.getValue(AXIS) == blockState.getValue(AXIS);
    }

    private Direction estimateBrambooDirection(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction direction;

        switch (axis) {
            case X:
                boolean west = !isBlockBramboo(worldIn, pos.relative(Direction.WEST));
                boolean east = !isBlockBramboo(worldIn, pos.relative(Direction.EAST));
                if (west && east) {
                    direction = Direction.WEST;
                } else if (west) {
                    direction = Direction.WEST;
                } else {
                    direction = Direction.EAST;
                }

                break;
            default:
                boolean up = !isBlockBramboo(worldIn, pos.relative(Direction.UP));
                boolean down = !isBlockBramboo(worldIn, pos.relative(Direction.DOWN));
                if (up && down) {
                    direction = Direction.UP;
                } else if (up) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }

                break;
            case Z:
                boolean north = !isBlockBramboo(worldIn, pos.relative(Direction.NORTH));
                boolean south = !isBlockBramboo(worldIn, pos.relative(Direction.SOUTH));
                if (north && south) {
                    direction = Direction.NORTH;
                } else if (north) {
                    direction = Direction.NORTH;
                } else {
                    direction = Direction.SOUTH;
                }

                break;
        }

        return direction;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        if (random.nextInt(3) == 0) {
            age(state, worldIn, pos, random.nextInt(1) + 1);
        }

        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, PROPERTY_AGE);
    }
}
