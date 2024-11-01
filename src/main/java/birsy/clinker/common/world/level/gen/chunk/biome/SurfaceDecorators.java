package birsy.clinker.common.world.level.gen.chunk.biome;

import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.AshSteppeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.BasicSurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.DefaultSurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.SurfaceDecorator;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;

public class SurfaceDecorators {
    public static final HashMap<ResourceLocation, SurfaceDecorator> BIOME_TO_SURFACE_DECORATOR = new HashMap<>();
    private static final SurfaceDecorator DEFAULT_DECORATOR = new DefaultSurfaceDecorator();
    public static final SurfaceDecorator TEST_DECORATOR = new BasicSurfaceDecorator(Blocks.RED_WOOL.defaultBlockState(), Blocks.GREEN_WOOL.defaultBlockState(), Blocks.BLUE_WOOL.defaultBlockState(), 4);

    static {
        register(new ResourceLocation(Clinker.MOD_ID, "ash_steppe"),
                 new AshSteppeSurfaceDecorator());
    }

    //new BasicSurfaceDecorator(ClinkerBlocks.ASH.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), 0)

    public static void register(ResourceLocation biome, SurfaceDecorator decorator) {
        BIOME_TO_SURFACE_DECORATOR.put(biome, decorator);
    }
    public static SurfaceDecorator getSurfaceDecorator(ResourceLocation biomeLocation) {
        return BIOME_TO_SURFACE_DECORATOR.getOrDefault(biomeLocation, DEFAULT_DECORATOR);
    }
}
