package birsy.clinker.common.fluids;

import birsy.clinker.core.registry.ClinkerFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BrineFluidBlock extends ClinkerFluidBlock {
    public BrineFluidBlock() {
        super(ClinkerFluids.BRINE_SOURCE, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().tickRandomly());
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }
}
