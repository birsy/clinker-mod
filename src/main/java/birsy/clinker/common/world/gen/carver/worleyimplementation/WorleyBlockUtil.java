package birsy.clinker.common.world.gen.carver.worleyimplementation;

import birsy.clinker.core.Clinker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import BlockState;

public class WorleyBlockUtil
{
    //Sets block to passed in string. If string is not found to be a valid block, sets to fallback
    public static BlockState getStateFromString(String block, BlockState fallback)
    {
        BlockState blocky;

        blocky = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(block)).defaultBlockState();

        if(blocky == null)
        {
            Clinker.LOGGER.warn("Unable to find block: " + block + "   Using fallback block: " + fallback.toString());
            blocky = fallback;
        }

        return blocky;

    }

}
