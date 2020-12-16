package birsy.clinker.common.block.belwithstone;

import java.util.Random;

import birsy.clinker.common.block.util.PillarCap;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class BelwithPillarBlock extends RotatedPillarBlock
{
	public static final EnumProperty<PillarCap> PILLAR_CAP = EnumProperty.create("pillarcap", PillarCap.class);
	
	public BelwithPillarBlock()
	{
		super(((Block.Properties.create(Material.ROCK)
			  .hardnessAndResistance(10F, 6.0F)
			  .sound(SoundType.BONE)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE))));
		this.setDefaultState(this.stateContainer.getBaseState().with(PILLAR_CAP, PillarCap.NONE));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		switch((Direction.Axis)state.get(AXIS)) {
			case Y:
				if(worldIn.getBlockState(pos.down()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get() && worldIn.getBlockState(pos.up()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.BOTH));
				} else if (worldIn.getBlockState(pos.down()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.DOWN));
				} else if (worldIn.getBlockState(pos.up()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.UP));
				}
			case Z:
				if(worldIn.getBlockState(pos.north()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get() && worldIn.getBlockState(pos.south()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.BOTH));
				} else if (worldIn.getBlockState(pos.north()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.DOWN));
				} else if (worldIn.getBlockState(pos.south()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.UP));
				}
			case X:
				if(worldIn.getBlockState(pos.east()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get() && worldIn.getBlockState(pos.west()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.BOTH));
				} else if (worldIn.getBlockState(pos.east()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.DOWN));
				} else if (worldIn.getBlockState(pos.west()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockState(pos, state.with(PILLAR_CAP, PillarCap.UP));
				}
		}
		
		super.tick(state, worldIn, pos, rand);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AXIS, PILLAR_CAP);
	}
}
