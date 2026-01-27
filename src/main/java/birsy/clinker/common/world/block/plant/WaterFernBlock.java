package birsy.clinker.common.world.block.plant;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WaterFernBlock extends BushBlock {
    public static final MapCodec<WaterFernBlock> CODEC = simpleCodec(WaterFernBlock::new);
    public static final BooleanProperty
            NORTHEAST = BooleanProperty.create("northeast"),
            SOUTHEAST = BooleanProperty.create("southeast"),
            SOUTHWEST = BooleanProperty.create("southwest"),
            NORTHWEST = BooleanProperty.create("northwest");
    public static final BooleanProperty[] CORNER_PROPERTIES = new BooleanProperty[]{NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST};
    public static final ImmutableMap<BooleanProperty, Integer> PROPERTY_TO_KEY = ImmutableMap.<BooleanProperty, Integer>builder()
            .put(NORTHEAST, 0b0001).put(SOUTHEAST, 0b0010).put(SOUTHWEST, 0b0100).put(NORTHWEST, 0b1000).build();
    private static final VoxelShape[] stateToShape = Util.make(() -> {
        VoxelShape[] shapeByPropertyIndex = new VoxelShape[]{
                Block.box(8.0, 0.0, 0.0, 16.0, 1.5,  8.0),
                Block.box(8.0, 0.0, 8.0, 16.0, 1.5, 16.0),
                Block.box(0.0, 0.0, 8.0,  8.0, 1.5, 16.0),
                Block.box(0.0, 0.0, 0.0,  8.0, 1.5,  8.0)};
        VoxelShape[] shapes = new VoxelShape[16];
        for (int i = 0; i < shapes.length; i++) {
            int key = 0;
            VoxelShape shape = Shapes.empty();
            for (int j = 0; j < CORNER_PROPERTIES.length; j++) {
                int propertyKey = PROPERTY_TO_KEY.get(CORNER_PROPERTIES[j]);
                if ((propertyKey & i) > 0) {
                    key |= propertyKey;
                    shape = Shapes.or(shape, shapeByPropertyIndex[j]);
                }
            }
            shapes[key] = shape;
        }
        return shapes;
    });

    //Stolen Citadels

    public WaterFernBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> definition) {
        definition.add(NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST);
    }

    @Override
    protected MapCodec<? extends WaterFernBlock> codec() {
        return CODEC;
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int key = 0;
        for (BooleanProperty property : CORNER_PROPERTIES) if (state.getValue(property)) key |= PROPERTY_TO_KEY.get(property);
        return stateToShape[key];
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return (!context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && !isFull(state)) || super.canBeReplaced(state, context);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        // place in a particular corner of a block
        BooleanProperty corner;
        Vec3 placementPos = context.getClickLocation();
        double x = Mth.frac(placementPos.x()), z = Mth.frac(placementPos.z());
        if (x > 0.5 && z > 0.5) corner = SOUTHEAST;
        else if (x > 0.5) corner = NORTHEAST;
        else if (z > 0.5) corner = SOUTHWEST;
        else corner = NORTHWEST;

        // add corner if there's a block currently there
        BlockState currentState = context.getLevel().getBlockState(context.getClickedPos());
        if (currentState.is(this)) return currentState.setValue(corner, true);

        BlockState state = this.defaultBlockState();
        for (BooleanProperty cornerProperty : CORNER_PROPERTIES) state = state.setValue(cornerProperty, false);
        return state.setValue(corner, true);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape belowShape = level.getBlockState(pos).getShape(level, pos);
        if (belowShape.max(Direction.Axis.Y) > 0.9) return false;
        // only place above water
        FluidState fluidstate = level.getFluidState(pos);
        FluidState fluidstate1 = level.getFluidState(pos.above());
        return (fluidstate.getType() == Fluids.WATER) && fluidstate1.getType() == Fluids.EMPTY;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // break if no corner state is "true"
        boolean hasPart = false;
        for (BooleanProperty cornerProperty : CORNER_PROPERTIES) {
            if (state.getValue(cornerProperty)) {
                hasPart = true;
                break;
            }
        }
        VoxelShape belowShape = level.getBlockState(pos.below()).getShape(level, pos);
        if (belowShape.max(Direction.Axis.Y) > 0.9) return false;
        return hasPart && super.canSurvive(state, level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // scoop!
        if ((stack.is(Items.BUCKET) || stack.is(Items.WATER_BUCKET)) && player.getAbilities().mayBuild) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            if (!player.getAbilities().instabuild) {
                int count = 0;
                for (BooleanProperty cornerProperty : CORNER_PROPERTIES) if (state.getValue(cornerProperty)) count++;
                player.addItem(new ItemStack(this, count));
            }
            level.playSound(player, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1, 1);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.FAIL;
    }

    private static boolean isFull(BlockState state) {
        for (BooleanProperty cornerProperty : CORNER_PROPERTIES) {
            if (!state.getValue(cornerProperty)) return false;
        }
        return true;
    }

    @Override
    public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        if (mob != null) if (mob.getNavigation().getNodeEvaluator() instanceof SwimNodeEvaluator) return PathType.BREACH;
        return PathType.OPEN;
    }
}
