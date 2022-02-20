package birsy.clinker.core.registry.world;

import birsy.clinker.common.level.CaveSurfaceBuilder;
import birsy.clinker.common.level.GenerationTestBuilder;
import birsy.clinker.core.Clinker;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerSurfaceBuilder {
    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, Clinker.MOD_ID);

    public static final RegistryObject<SurfaceBuilder<SurfaceBuilderBaseConfiguration>> CLINKER = SURFACE_BUILDERS.register("clinker", () -> new CaveSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
    public static final RegistryObject<SurfaceBuilder<SurfaceBuilderBaseConfiguration>> GEN_TEST = SURFACE_BUILDERS.register("gentest", () -> new GenerationTestBuilder(SurfaceBuilderBaseConfiguration.CODEC));

}
