package birsy.clinker.common.block.sulfur;

import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractSulfurBlock extends Block {
    public AbstractSulfurBlock(Properties properties) {
        super(properties);
    }

    public void discharge(LevelAccessor worldIn, BlockPos pos, float strength) {
        worldIn.playSound(null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
