package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.shape.SurfaceShaper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public record BiomeGenerationInfo(ResourceKey<Biome> biomeKey,
                                  BlockState seaBlock, int seaLevel,
                                  SurfaceShaper shaper,
                                  SurfaceDecorator decorator) {

}
