package birsy.clinker.common.world.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CorpseLilyBudBlock extends CorpseLilyCenterBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    protected static final VoxelShape SHAPE = Block.box(5.5, 0.0, 5.5, 10.5, 5.0, 10.5);

    public CorpseLilyBudBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(DOWN, false)
                .setValue(AGE, 0)
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandomSource, BlockPos pPos, BlockState pState) {
        this.grow(pLevel, pPos, pState);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(0, 5) == 0) this.grow(level, pos, state);
    }

    private boolean grow(Level level, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        age++;
        if (age > 4) {
            CorpseLilyBulbBlock.place(level, pos);
            return true;
        } else {
            level.setBlock(pos, state.setValue(AGE, age), 2);
            return false;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(AGE);
    }
}
