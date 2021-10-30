package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClinkerDimensions
{
    public static final ResourceKey<DimensionType> OTHERSHORE_TYPE = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, name("othershore"));
    public static final ResourceKey<Level> OTHERSHORE = ResourceKey.create(Registry.DIMENSION_REGISTRY, name("othershore"));

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}