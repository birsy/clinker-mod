package birsy.clinker.common.world.gen.carver.worleyimplementation;

import birsy.clinker.core.Clinker;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class WorleyBlockUtil
{
    //Sets block to passed in string. If string is not found to be a valid block, sets to fallback
    public static BlockState getStateFromString(String block, BlockState fallback)
    {
        BlockState blocky;

        blocky = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(block)).getDefaultState();

        if(blocky == null)
        {
            Clinker.LOGGER.warn("Unable to find block: " + block + "   Using fallback block: " + fallback.toString());
            blocky = fallback;
        }

        return blocky;

    }

}
