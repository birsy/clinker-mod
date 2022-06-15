package birsy.clinker.core.registry.alchemy;

import birsy.clinker.common.alchemy.chemicals.Element;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ClinkerElements {
    /*public static final DeferredRegister<Element> ELEMENTS = DeferredRegister.create(ClinkerRegistries.ELEMENT, Clinker.MOD_ID);


    public static final RegistryObject<Element> FOUNDATION = createElement("foundation", () -> new Element(Integer.MIN_VALUE));

    public static final RegistryObject<Element> EARTH = createElement("earth", () -> new Element(-1));
    public static final RegistryObject<Element> WATER = createElement("water", () -> new Element(0));
    public static final RegistryObject<Element> AIR = createElement("air", () -> new Element(1));
    public static final RegistryObject<Element> FIRE = createElement("fire", () -> new Element(2));

    public static final RegistryObject<Element> FLUX = createElement("flux", () -> new Element(Integer.MAX_VALUE));


    public static RegistryObject<Element> createElement(String name, final Supplier<? extends Element> supplier) {
        RegistryObject<Element> element = ELEMENTS.register(name, supplier);
        return element;
    }*/
}
