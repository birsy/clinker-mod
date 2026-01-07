package birsy.clinker.core.registry.worldgen;

import birsy.clinker.client.render.world.OthershoreDimensionEffects;
import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreChunkGenerator;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerWorld {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR, Clinker.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE, Clinker.MOD_ID);

    public static final ResourceKey<Level> OTHERSHORE = ResourceKey.create(Registries.DIMENSION, Clinker.resource("othershore"));

    public static final Supplier<MapCodec<OthershoreChunkGenerator>> OTHERSHORE_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("othershore", () -> OthershoreChunkGenerator.CODEC);
    public static final Supplier<MapCodec<OthershoreBiomeSource>> OTHERSHORE_BIOME_SOURCE =
            BIOME_SOURCES.register("othershore", () -> OthershoreBiomeSource.CODEC);

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(Clinker.resource("othershore"), new OthershoreDimensionEffects());
    }
}
