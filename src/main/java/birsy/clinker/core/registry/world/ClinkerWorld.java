package birsy.clinker.core.registry.world;

import birsy.clinker.client.render.world.OthershoreDimensionEffects;
import birsy.clinker.common.world.level.gen.chunk.CaveChunkGenerator;
import birsy.clinker.common.world.level.gen.chunk.OthershoreChunkGenerator;
import birsy.clinker.common.world.level.gen.chunk.TestChunkGenerator;
import birsy.clinker.common.world.level.gen.chunk.TestChunkGenerator2;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerWorld {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR, Clinker.MOD_ID);

    public static final ResourceKey<Level> OTHERSHORE = ResourceKey.create(Registries.DIMENSION, name("othershore"));
    public static final Supplier<Codec<OthershoreChunkGenerator>> OTHERSHORE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("othershore_chunk_generator", () -> OthershoreChunkGenerator.CODEC);
    public static final Supplier<Codec<TestChunkGenerator>> TEST_CHUNK_GENERATOR = CHUNK_GENERATORS.register("test_chunk_generator", () -> TestChunkGenerator.CODEC);
    public static final Supplier<Codec<CaveChunkGenerator>> CAVE_CHUNK_GENERATOR = CHUNK_GENERATORS.register("cave_chunk_generator", () -> CaveChunkGenerator.CODEC);

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(Clinker.MOD_ID, "othershore"), new OthershoreDimensionEffects());
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}
