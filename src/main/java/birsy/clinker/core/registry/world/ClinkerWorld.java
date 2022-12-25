package birsy.clinker.core.registry.world;

import birsy.clinker.client.render.world.OthershoreDimensionEffects;
import birsy.clinker.common.level.chunk.gen.CaveChunkGenerator;
import birsy.clinker.common.level.chunk.gen.OthershoreChunkGenerator;
import birsy.clinker.common.level.chunk.gen.TestChunkGenerator;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerWorld {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registry.CHUNK_GENERATOR_REGISTRY, Clinker.MOD_ID);

    public static final ResourceKey<Level> OTHERSHORE = ResourceKey.create(Registry.DIMENSION_REGISTRY, name("othershore"));
    public static final RegistryObject<Codec<OthershoreChunkGenerator>> OTHERSHORE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("othershore_chunk_generator", () -> OthershoreChunkGenerator.CODEC);
    public static final RegistryObject<Codec<TestChunkGenerator>> TEST_CHUNK_GENERATOR = CHUNK_GENERATORS.register("test_chunk_generator", () -> TestChunkGenerator.CODEC);
    public static final RegistryObject<Codec<CaveChunkGenerator>> CAVE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("cave_chunk_generator", () -> CaveChunkGenerator.CODEC);

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(name("othershore"), new OthershoreDimensionEffects());
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}
