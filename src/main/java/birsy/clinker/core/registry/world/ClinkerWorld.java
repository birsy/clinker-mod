package birsy.clinker.core.registry.world;

import birsy.clinker.client.render.world.OthershoreDimensionEffects;
import birsy.clinker.common.world.level.gen.chunk.TerrainBuilderTestChunkGenerator;
import birsy.clinker.common.world.level.gen.legacy.CaveChunkGenerator;
import birsy.clinker.common.world.level.gen.legacy.OthershoreChunkGenerator;
import birsy.clinker.common.world.level.gen.chunk.TestChunkGenerator;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClinkerWorld {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR, Clinker.MOD_ID);

    public static final ResourceKey<Level> OTHERSHORE = ResourceKey.create(Registries.DIMENSION, Clinker.resource("othershore"));

    public static final Supplier<MapCodec<OthershoreChunkGenerator>> OTHERSHORE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("othershore_chunk_generator", () -> OthershoreChunkGenerator.CODEC);
    public static final Supplier<MapCodec<TestChunkGenerator>> TEST_CHUNK_GENERATOR = CHUNK_GENERATORS.register("test_chunk_generator", () -> TestChunkGenerator.CODEC);
    public static final Supplier<MapCodec<TerrainBuilderTestChunkGenerator>> TERRAIN_BUILDER_TEST_CHUNK_GENERATOR = CHUNK_GENERATORS.register("terrain_builder_test_chunk_generator", () -> TerrainBuilderTestChunkGenerator.CODEC);
    public static final Supplier<MapCodec<CaveChunkGenerator>> CAVE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("cave_chunk_generator", () -> CaveChunkGenerator.CODEC);

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(Clinker.resource("othershore"), new OthershoreDimensionEffects());
    }
}
