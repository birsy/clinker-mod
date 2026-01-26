package birsy.clinker.common.world.block.plant;

import birsy.clinker.common.world.block.AbstractDirectionalStemBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;
import org.jetbrains.annotations.Nullable;

public class ThornyStemBlock extends AbstractDirectionalStemBlock implements IShearable {
    public static final MapCodec<ThornyStemBlock> CODEC = simpleCodec(ThornyStemBlock::new);

    public ThornyStemBlock(Properties properties) {
        super(0.3F, properties);
    }

    @Override
    public boolean shouldConnect(LevelAccessor level, BlockPos pos, BlockState currentState, Direction neighborDirection, BlockPos neighborPos, BlockState neighborState) {
        if (neighborState.is(ClinkerTags.BRAMBLE_FLOWERS))
            return neighborDirection == Direction.UP;
        return neighborState.is(ClinkerTags.BRAMBLES);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();

        boolean connected = false;
        BlockPos.MutableBlockPos neighborPos = context.getClickedPos().mutable();
        for (Direction direction : Direction.values()) {
            neighborPos = neighborPos.set(context.getClickedPos()).move(direction);
            boolean shouldConnect = shouldConnect(context.getLevel(), context.getClickedPos(), state, direction, neighborPos, context.getLevel().getBlockState(neighborPos));
            if (shouldConnect) connected = true;
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), shouldConnect);
        }

        BlockPos clickedPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState clickedState = context.getLevel().getBlockState(clickedPos);
        if (clickedState.isFaceSturdy(context.getLevel(), clickedPos, context.getClickedFace(), SupportType.CENTER)) {
            connected = true;
            state = state.setValue(PROPERTY_BY_DIRECTION.get(context.getClickedFace().getOpposite()), true);
        }

        if (!connected)
            return null;

        state = state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        return state;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        boolean inside = false;
        AABB entityBounds = entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ());
        VoxelShape shape = this.getShape(state, level, pos, CollisionContext.of(entity));
        for (AABB aabb : shape.toAabbs()) {
            if (entityBounds.intersects(aabb)) {
                inside = true;
                break;
            }
        }
        if (inside) {
            entity.hurt(level.damageSources().cactus(), 1);
            boolean onClimbable = entity instanceof LivingEntity livingEntity && livingEntity.onClimbable();
            if (entity.getDeltaMovement().y < 0 || onClimbable) {
                entity.makeStuckInBlock(state, new Vec3(1, 0.8, 1));
            }
        }
    }

    @Override
    protected MapCodec<? extends ThornyStemBlock> codec() {
        return CODEC;
    }
}
