package birsy.clinker.common.world.block.fluid;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerFluids;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class VitriolFluid extends BaseFlowingFluid {
    private static final BaseFlowingFluid.Properties VITRIOL_PROPERTIES =
            new BaseFlowingFluid.Properties(ClinkerFluids.VITRIOL_TYPE, ClinkerFluids.VITRIOL, ClinkerFluids.FLOWING_VITRIOL)
                    .bucket(ClinkerItems.VITRIOL_BUCKET)
                    .block(ClinkerBlocks.VITRIOL_BLOCK);

    protected VitriolFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
    }

    protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {

    }

    public static class Flowing extends VitriolFluid {
        public Flowing() {
            super(VITRIOL_PROPERTIES);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends VitriolFluid {
        public Source() {
            super(VITRIOL_PROPERTIES);
        }

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
