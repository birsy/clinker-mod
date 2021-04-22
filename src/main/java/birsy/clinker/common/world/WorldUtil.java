package birsy.clinker.common.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;

public class WorldUtil {
    /**
     * Gets the "heat" of a block. Currently gets the ambient temperature and then checks for lava/fire underneath.
     * TODO: Create Heater and Heat Duct functionality.
     * @param world The world you're measuring the temperature from.
     * @param pos The position you want the temperature at.
     * @return A temperature of the block, taking ambient temperature and local heat sources into account.
     */
    public static float getAmbientHeat(IWorld world, BlockPos pos) {
        float worldTemp = 0;
        BlockState blockstate = world.getBlockState(pos.down());

        if (blockstate.getFluidState().getFluid() == Fluids.LAVA || blockstate.getFluidState().getFluid() == Fluids.FLOWING_LAVA || blockstate.isIn(BlockTags.FIRE) || blockstate.isIn(BlockTags.CAMPFIRES) || blockstate.isIn(Blocks.MAGMA_BLOCK)) {
            worldTemp += 30;
        }

        float ambientTemp = world.getBiome(pos).getTemperature(pos);
        if (new ResourceLocation("the_nether").equals(DimensionType.THE_NETHER.getRegistryName())) {
            ambientTemp += 10;
        }

        return worldTemp + ambientTemp;
    }

    /**
     * Gets the biome-based fog density multiplier. ONLY WORKS IN THE OTHERSHORE.
     * TODO: Make this less hardcoded.
     * @param world The world you want the fog density multiplier in.
     * @param pos The position you want the fog density multiplier at.
     * @return A fog multiplier starting from 1.0 to infinity. Changes depending on the biome.
     */
    public static float getFogDensityMultiplier(World world, BlockPos pos) {
        if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
            Biome biome = world.getBiome(pos);
        }

        return 1.0f;
    }
}
