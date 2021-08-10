package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.gen.carver.OreVein;
import birsy.clinker.common.world.gen.carver.OreVeinConfig;
import birsy.clinker.core.Clinker;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerOreVeins {
    public static final DeferredRegister<WorldCarver<?>> ORE_VEINS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, Clinker.MOD_ID);

    public static final RegistryObject<WorldCarver<OreVeinConfig>> LEAD_ORE_VEIN = ORE_VEINS.register("lead_ore_vein", () -> new OreVein(OreVeinConfig.CODEC, 256));
}
