package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MothBallBlock extends Block {
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, 3);
    protected static final VoxelShape
            ONE_AABB = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0),
            TWO_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0),
            THREE_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);

    public MothBallBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(level, pos);
        return switch (state.getValue(COUNT)) {
            case 2 -> TWO_AABB.move(offset.x, offset.y, offset.z);
            case 3 -> THREE_AABB.move(offset.x, offset.y, offset.z);
            default -> ONE_AABB.move(offset.x, offset.y, offset.z);
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(5 - state.getValue(COUNT)) == 0) {
            BlockPos particlePos = new BlockPos(
                    pos.getX() + random.nextIntBetweenInclusive(-6, 6),
                    pos.getY() + random.nextIntBetweenInclusive(0, 1),
                    pos.getZ() + random.nextIntBetweenInclusive(-6, 6)
            );
            if (particlePos.equals(pos)) return;
            BlockState particleState = level.getBlockState(particlePos);
            if (!particleState.isCollisionShapeFullBlock(level, particlePos)) {
                level.addParticle(
                        ClinkerParticles.MOTH.get(),
                        particlePos.getX() + random.nextDouble(),
                        particlePos.getY() + random.nextDouble(),
                        particlePos.getZ() + random.nextDouble(),
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.setValue(COUNT, Math.min(3, blockstate.getValue(COUNT) + 1));
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return !useContext.isSecondaryUseActive() && useContext.getItemInHand().is(this.asItem()) && state.getValue(COUNT) < 3 || super.canBeReplaced(state, useContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COUNT);
    }
}
