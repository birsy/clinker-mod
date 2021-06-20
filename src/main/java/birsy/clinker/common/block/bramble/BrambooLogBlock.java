package birsy.clinker.common.block.bramble;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class BrambooLogBlock extends RotatedPillarBlock {
    public static final IntegerProperty PROPERTY_AGE = BlockStateProperties.AGE_0_15;

    public BrambooLogBlock() {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(2.0F)
                .sound(SoundType.BAMBOO)
                .harvestLevel(0)
                .harvestTool(ToolType.AXE)
                .tickRandomly());
    }

    public void grow(IWorld worldIn, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.get(AXIS);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == axis) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = worldIn.getBlockState(blockPos);

                int age = 0;
                if (blockState.matchesBlock(ClinkerBlocks.BRAMBOO_STALK.get()) && blockState.get(AXIS) == axis) {
                    worldIn.setBlockState(blockPos, ClinkerBlocks.BRAMBOO_LOG.get().getDefaultState().with(AXIS, axis), 2);
                } else if (blockState.isAir()) {
                    worldIn.setBlockState(blockPos, ClinkerBlocks.BRAMBOO_STALK.get().getDefaultState().with(AXIS, axis), 2);
                    age += 8;
                }

                worldIn.setBlockState(pos, ClinkerBlocks.BRAMBOO_LOG.get().getDefaultState().with(AXIS, axis).with(PROPERTY_AGE, age), 4);
            }
        }
    }

    public int getMaxStemLength(BlockPos pos) {
        //gets a random number between one and zero based off a block's position.
        float random = (((float)(MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()) & 15L) / 15.0F));
        //min of 7, max of 10
        return (int) (7 + (random * 3));
    }

    public void age(BlockState state, ServerWorld worldIn, BlockPos pos, int amount) {
        Direction.Axis axis = state.get(AXIS);
        int age = MathHelper.clamp(state.get(PROPERTY_AGE) + amount, 0, 15);
        worldIn.setBlockState(pos, ClinkerBlocks.BRAMBOO_LOG.get().getDefaultState().with(AXIS, axis).with(PROPERTY_AGE, age), 4);

        if (age <= 15) {
            grow(worldIn, pos, state);
        }
    }

    private int getStemLength(IWorld worldIn, BlockPos pos, BlockState state) {
        Direction direction = estimateBrambooDirection(worldIn, pos, state);
        BlockPos blockPos = pos.toMutable();
        int length = 0;

        for (int i = 0; i < getMaxStemLength(pos) + 1; i++) {
            if (isBlockBramboo(worldIn, blockPos)) {
                length++;
                blockPos.offset(direction);
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

    private boolean isBlockBramboo(IWorld worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos);
        return blockState.matchesBlock(ClinkerBlocks.BRAMBOO_STALK.get()) || blockState.matchesBlock(ClinkerBlocks.BRAMBOO_LOG.get());
    }

    private boolean isBrambooFacingSameDirection(IWorld worldIn, BlockPos pos, BlockState state) {
        BlockState blockState = worldIn.getBlockState(pos);
        return isBlockBramboo(worldIn, pos) && state.get(AXIS) == blockState.get(AXIS);
    }

    private Direction estimateBrambooDirection(IWorld worldIn, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.get(AXIS);
        Direction direction;

        switch (axis) {
            case X:
                boolean west = !isBlockBramboo(worldIn, pos.offset(Direction.WEST));
                boolean east = !isBlockBramboo(worldIn, pos.offset(Direction.EAST));
                if (west && east) {
                    direction = Direction.WEST;
                } else if (west) {
                    direction = Direction.WEST;
                } else {
                    direction = Direction.EAST;
                }

                break;
            default:
                boolean up = !isBlockBramboo(worldIn, pos.offset(Direction.UP));
                boolean down = !isBlockBramboo(worldIn, pos.offset(Direction.DOWN));
                if (up && down) {
                    direction = Direction.UP;
                } else if (up) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }

                break;
            case Z:
                boolean north = !isBlockBramboo(worldIn, pos.offset(Direction.NORTH));
                boolean south = !isBlockBramboo(worldIn, pos.offset(Direction.SOUTH));
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
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (random.nextInt(3) == 0) {
            age(state, worldIn, pos, random.nextInt(1) + 1);
        }

        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS, PROPERTY_AGE);
    }
}
