package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class ClinkerDimensions
{	
    public static final RegistryKey<DimensionType> OTHERSHORE_TYPE = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, name("othershore_type"));
    public static final RegistryKey<World> OTHERSHORE = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, name("othershore"));

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}
