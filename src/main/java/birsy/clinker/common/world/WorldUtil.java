package birsy.clinker.common.world;

import birsy.clinker.common.tileentity.HeaterTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.biome.BiomeManager;

public class WorldUtil {
    /**
     * Gets the "heat" of a block. Currently gets the ambient temperature and then checks for lava/fire underneath.
     * TODO: Create Heater and Heat Duct functionality.
     * @param world The world you're measuring the temperature from.
     * @param pos The position you want the temperature at.
     * @return A temperature of the block, taking ambient temperature and local heat sources into account.
     */
    public static float getAmbientHeat(LevelAccessor world, BlockPos pos) {
        float worldTemp = 0;
        BlockState blockstate = world.getBlockState(pos.below());

        if (blockstate.getFluidState().getType() == Fluids.LAVA || blockstate.getFluidState().getType() == Fluids.FLOWING_LAVA || blockstate.is(BlockTags.FIRE) || blockstate.is(BlockTags.CAMPFIRES) || blockstate.is(ClinkerBlocks.HEATER.get())) {
            worldTemp += 30;
        }



        if (world.getBlockEntity(pos) instanceof HeaterTileEntity) {
            worldTemp += ((HeaterTileEntity) world.getBlockEntity(pos)).getHeatOverlayStrength(0.5F);
        }

        float ambientTemp = world.getBiome(pos).getTemperature(pos);
        if (new ResourceLocation("the_nether").equals(DimensionType.NETHER_LOCATION.getRegistryName())) {
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
    public static float getFogDensityMultiplier(Level world, BlockPos pos) {
        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            Biome biome = world.getBiome(pos);
        }

        return 1.0f;
    }
}
