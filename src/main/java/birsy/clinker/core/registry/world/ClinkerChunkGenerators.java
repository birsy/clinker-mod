package birsy.clinker.core.registry.world;

import birsy.clinker.common.level.chunk.OthershoreChunkGenerator;
import birsy.clinker.common.level.feature.AshBuildupFeature;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ClinkerChunkGenerators {
    static {
        registerChunkGenerator("othershore", OthershoreChunkGenerator.CODEC);
    }

    public static void registerChunkGenerator(String registryName, Codec<? extends ChunkGenerator> codec) {
        ResourceLocation resourceLocation = new ResourceLocation(Clinker.MOD_ID, registryName);

        if (Registry.CHUNK_GENERATOR.keySet().contains(resourceLocation)) {
            throw new IllegalStateException("Configured Feature ID: \"" + resourceLocation.toString() + "\" is already in the registry!");
        }

        Registry.register(Registry.CHUNK_GENERATOR, resourceLocation, codec);
    }
}
