package birsy.clinker.core.registry.world;

import birsy.clinker.common.level.chunk.gen.OthershoreChunkGenerator;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ClinkerBiomes {
    public static final ResourceKey<Biome> ASH_STEPPE = register("ash_steppe");

    private static ResourceKey<Biome> register(String pKey) {
        return ResourceKey.create(Registry.BIOME_REGISTRY, name(pKey));
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}
