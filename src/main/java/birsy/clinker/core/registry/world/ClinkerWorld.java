package birsy.clinker.core.registry.world;

import birsy.clinker.common.level.chunk.gen.OthershoreChunkGenerator;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ClinkerWorld {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registry.CHUNK_GENERATOR_REGISTRY, Clinker.MOD_ID);

    public static final ResourceKey<DimensionType> OTHERSHORE_TYPE = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, name("othershore"));
    public static final ResourceKey<Level> OTHERSHORE = ResourceKey.create(Registry.DIMENSION_REGISTRY, name("othershore"));
    public static final RegistryObject<Codec<OthershoreChunkGenerator>> OTHERSHORE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("othershore_chunk_generator", () -> OthershoreChunkGenerator.CODEC);

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}
