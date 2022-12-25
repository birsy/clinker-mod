package birsy.clinker.core.registry.alchemy;

import birsy.clinker.common.alchemy.chemicals.Chemical;
import birsy.clinker.common.alchemy.chemicals.Solvent;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ClinkerChemicals {
    public static final DeferredRegister<Chemical> CHEMICALS = DeferredRegister.create(ClinkerRegistries.CHEMICAL.get(), Clinker.MOD_ID);

    //Solvents
    public static final RegistryObject<Solvent> AIR = createChemical("air", () -> new Solvent(1.0F,
        new Chemical.Properties().meltingPoint(-210.0F).boilingPoint(-194.35F).burningPoint(Float.POSITIVE_INFINITY).color(1.0F, 1.0F, 1.0F).density(0.0F).hardness(0.0F)
    ));
    public static final RegistryObject<Solvent> WATER = createChemical("water", () -> new Solvent(1.0F,
            new Chemical.Properties().meltingPoint(0.0F).boilingPoint(100.0F).burningPoint(160.0F).color(0.1F, 0.5F, 1.0F).density(1.0F).hardness(0.0F)
    ));
    public static final RegistryObject<Solvent> BLOOD = createChemical("blood", () -> new Solvent(1.0F,
            new Chemical.Properties().meltingPoint(-5.0F).boilingPoint(105.0F).burningPoint(100.0F).color(1.0F, 0.0F, 0.0F).hardness(0.0F)
    ));
    public static final RegistryObject<Solvent> BRINE = createChemical("brine", () -> new Solvent(1.0F,
            new Chemical.Properties().meltingPoint(-15.0F).boilingPoint(-115.0F).burningPoint(160.0F).color(0.1F, 0.5F, 1.0F).hardness(0.0F)
    ));

    public static <T extends Chemical> RegistryObject<T> createChemical(String name, final Supplier<T> supplier) {
        RegistryObject<T> element = CHEMICALS.register(name, supplier);
        return element;
    }
}
