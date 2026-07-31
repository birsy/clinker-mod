package birsy.clinker.common.block;

import birsy.clinker.common.world.level.heat.HeatSystem;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SulfurCoreBlock extends Block {
    public SulfurCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        HeatSystem system = HeatSystem.get(level);
        BlockPos.MutableBlockPos mPos = pos.mutable();
        for (Direction dir : Direction.values()) {
            mPos.set(pos).move(dir);
            if (level.getBlockState(mPos).is(ClinkerBlocks.SULFUR_DUCT.get()))
                system.spawnPacket(mPos);
        }
    }
}
