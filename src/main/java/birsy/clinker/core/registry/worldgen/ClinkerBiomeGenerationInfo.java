package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.content.surface.decoration.BrineSwampSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.decoration.HeathSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.shape.CrackleSurfaceShape;
import birsy.clinker.common.world.level.gen.content.surface.shape.HeathSurfaceShape;
import birsy.clinker.common.world.level.gen.system.biome.BiomeGenerationInfo;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerBiomeGenerationInfo {
    public static final DeferredRegister<BiomeGenerationInfo> BIOME_GENERATION_INFO =
            DeferredRegister.create(ClinkerRegistries.BIOME_GENERATION_INFO_REGISTRY, Clinker.MOD_ID);

    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> UPPER_SHELF_PLATEAU =
            register(ClinkerBiomes.TEMPLATE_UPPER_SHELF_PLATEAU,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(
                                    OthershoreGenerationConstants.UPPER_SHELF_HEIGHT + 40,
                                    OthershoreGenerationConstants.UPPER_SHELF_HEIGHT
                            )
            );

    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> UPPER_SHELF =
            register(ClinkerBiomes.TEMPLATE_UPPER_SHELF,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(
                                    OthershoreGenerationConstants.UPPER_SHELF_HEIGHT,
                                    OthershoreGenerationConstants.LOWER_SHELF_HEIGHT
                            )
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> ASH_STEPPE =
            register(ClinkerBiomes.ASH_STEPPE,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(
                                    OthershoreGenerationConstants.UPPER_SHELF_HEIGHT,
                                    OthershoreGenerationConstants.LOWER_SHELF_HEIGHT
                            )
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> HEATH =
            register(ClinkerBiomes.HEATH,
                    BiomeGenerationInfo.builder()
                            .surfaceShape(new HeathSurfaceShape(OthershoreGenerationConstants.UPPER_SHELF_HEIGHT - 10, OthershoreGenerationConstants.LOWER_SHELF_HEIGHT))
                            .decorator(new HeathSurfaceDecorator())
            );

    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> LOWER_SHELF =
            register(ClinkerBiomes.TEMPLATE_LOWER_SHELF,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(OthershoreGenerationConstants.LOWER_SHELF_HEIGHT)
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> SHELF_BORDER =
            register(ClinkerBiomes.TEMPLATE_SHELF_BORDER,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(OthershoreGenerationConstants.LOWER_SHELF_HEIGHT + 15)
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> SHELF_BORDER_CRACKLE =
            register(ClinkerBiomes.TEMPLATE_SHELF_BORDER_CRACKLE,
                    BiomeGenerationInfo.builder()
                            .surfaceShape(new CrackleSurfaceShape(
                                    OthershoreGenerationConstants.LOWER_SHELF_HEIGHT + 15,
                                    OthershoreGenerationConstants.UPPER_SHELF_HEIGHT - 10,
                                    OthershoreGenerationConstants.UPPER_SHELF_HEIGHT
                            ))
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> HEATH_THICKET =
            register(ClinkerBiomes.HEATH_THICKET,
                    BiomeGenerationInfo.builder()
                            .surfaceShape(new HeathSurfaceShape(OthershoreGenerationConstants.LOWER_SHELF_HEIGHT, OthershoreGenerationConstants.SEA_HEIGHT))
                            .decorator(new HeathSurfaceDecorator())
            );


    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> BEACH =
            register(ClinkerBiomes.TEMPLATE_BEACH,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(OthershoreGenerationConstants.SEA_HEIGHT + 5)
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> BRINE_SWAMP =
            register(ClinkerBiomes.BRINE_SWAMP,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(OthershoreGenerationConstants.SEA_HEIGHT)
                            .decorator(new BrineSwampSurfaceDecorator(OthershoreGenerationConstants.SEA_HEIGHT))
            );
    public static final DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> SEA =
            register(ClinkerBiomes.TEMPLATE_SEA,
                    BiomeGenerationInfo.builder()
                            .placeholderSurfaceShape(OthershoreGenerationConstants.SEA_HEIGHT - 8)
            );
    
    public static DeferredHolder<BiomeGenerationInfo, BiomeGenerationInfo> register(ResourceKey<Biome> biome, BiomeGenerationInfo.Builder builder) {
        return BIOME_GENERATION_INFO.register(biome.location().getPath(), () -> builder.build(biome));
    }
}
