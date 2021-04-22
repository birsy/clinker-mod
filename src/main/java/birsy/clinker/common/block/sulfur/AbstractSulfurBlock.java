package birsy.clinker.common.block.sulfur;

import net.minecraft.block.Block;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class AbstractSulfurBlock extends Block {
    public AbstractSulfurBlock(Properties properties) {
        super(properties);
    }

    public void discharge(IWorld worldIn, BlockPos pos, float strength) {
        worldIn.playSound(null, pos, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
