package birsy.clinker.common.fluids;

import birsy.clinker.core.registry.ClinkerFluids;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BrineFluidBlock extends ClinkerFluidBlock {
    public BrineFluidBlock() {
        super(ClinkerFluids.BRINE_SOURCE, BlockBehaviour.Properties.of(Material.WATER).noCollission().randomTicks());
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
    }
}
