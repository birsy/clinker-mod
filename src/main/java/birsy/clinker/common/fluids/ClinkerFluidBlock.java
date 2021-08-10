package birsy.clinker.common.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ClinkerFluidBlock extends LiquidBlock {

    public ClinkerFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }
}
