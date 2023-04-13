package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class BugstalkBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final double HITBOX_HEIGHT = 3.0D;
    private static final VoxelShape X_AXIS_AABB = Block.box(HITBOX_HEIGHT, 0.0D, 0.0D, 16.0D - HITBOX_HEIGHT, 16.0D, 16.0D);
    private static final VoxelShape Y_AXIS_AABB = Block.box(0.0D, HITBOX_HEIGHT, 0.0D, 16.0D, 16.0D - HITBOX_HEIGHT, 16.0D);
    private static final VoxelShape Z_AXIS_AABB = Block.box(0.0D, 6.0D, HITBOX_HEIGHT, 16.0D, 16.0D, 16.0D - HITBOX_HEIGHT);

    public BugstalkBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(WATERLOGGED, false));
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, AXIS);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        pEntity.setDeltaMovement(pEntity.getDeltaMovement().multiply(0.5, 1.0, 0.5));
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (pState.getValue(AXIS)) {
            case X:
                return X_AXIS_AABB;
            case Y:
            default:
                return Y_AXIS_AABB;
            case Z:
                return Z_AXIS_AABB;
        }
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return Shapes.block();
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        for (int i = 0; i < 3; i++) {
            Vec3 particlePosition = MathUtils.cubeNormalize(
                    new Vec3(pRandom.nextDouble(), pRandom.nextDouble(), (pRandom.nextDouble())).multiply(2.0, 2.0, 2.0).subtract(1.0, 1.0, 1.0))
                    .multiply(0.5, 0.5, 0.5)
                    .multiply(1.1, 1.1, 1.1);
            //Clinker.LOGGER.info(particlePosition.toString());
            //pLevel.addParticle(ClinkerParticles.BUG.get(), (double)pPos.getX() + particlePosition.x() + 0.5, (double)pPos.getY() + particlePosition.y() + 0.5, (double)pPos.getZ() + particlePosition.z() + 0.5, 0.0D, 0.0D, 0.0D);
        }
    }
}
