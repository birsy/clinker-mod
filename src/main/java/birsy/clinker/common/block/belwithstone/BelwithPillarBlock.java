package birsy.clinker.common.block.belwithstone;

import java.util.Random;

import birsy.clinker.common.block.util.PillarCap;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolType;

public class BelwithPillarBlock extends RotatedPillarBlock
{
	public static final EnumProperty<PillarCap> PILLAR_CAP = EnumProperty.create("pillarcap", PillarCap.class);
	
	public BelwithPillarBlock()
	{
		super(((Block.Properties.of(Material.STONE)
			  .strength(10F, 6.0F)
			  .sound(SoundType.BONE_BLOCK)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE))));
		this.registerDefaultState(this.stateDefinition.any().setValue(PILLAR_CAP, PillarCap.NONE));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		switch((Direction.Axis)state.getValue(AXIS)) {
			case Y:
				if(worldIn.getBlockState(pos.below()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get() && worldIn.getBlockState(pos.above()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.BOTH));
				} else if (worldIn.getBlockState(pos.below()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.DOWN));
				} else if (worldIn.getBlockState(pos.above()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.UP));
				}
			case Z:
				if(worldIn.getBlockState(pos.north()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get() && worldIn.getBlockState(pos.south()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.BOTH));
				} else if (worldIn.getBlockState(pos.north()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.DOWN));
				} else if (worldIn.getBlockState(pos.south()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.UP));
				}
			case X:
				if(worldIn.getBlockState(pos.east()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get() && worldIn.getBlockState(pos.west()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.BOTH));
				} else if (worldIn.getBlockState(pos.east()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.DOWN));
				} else if (worldIn.getBlockState(pos.west()).getBlock() != ClinkerBlocks.BELWITH_PILLAR.get()) {
					worldIn.setBlockAndUpdate(pos, state.setValue(PILLAR_CAP, PillarCap.UP));
				}
		}
		
		super.tick(state, worldIn, pos, rand);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS, PILLAR_CAP);
	}
}
