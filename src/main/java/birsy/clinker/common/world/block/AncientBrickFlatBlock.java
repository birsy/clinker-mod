package birsy.clinker.common.world.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;

public class AncientBrickFlatBlock extends DirectionalBlock {
	public static final MapCodec<AncientBrickFlatBlock> CODEC = simpleCodec(AncientBrickFlatBlock::new);

	public AncientBrickFlatBlock(BlockBehaviour.Properties p_52664_) {
		super(p_52664_);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}

	public AncientBrickFlatBlock() {
		this(Block.Properties.of().mapColor(MapColor.SAND)
				.requiresCorrectToolForDrops()
				.strength(25.0F, 1200.0F));
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getClickedFace();
		BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction.getOpposite()));
		return blockstate.is(this) && blockstate.getValue(FACING) == direction ? this.defaultBlockState().setValue(FACING, direction.getOpposite()) : this.defaultBlockState().setValue(FACING, direction);
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return null;
	}
}
