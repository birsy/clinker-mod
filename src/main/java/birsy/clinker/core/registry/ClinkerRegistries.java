package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.chemicals.Element;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class ClinkerRegistries {
    public static IForgeRegistry<Element> ELEMENT = new RegistryBuilder<Element>()
            .setIDRange(0, 0x0FFFFF)
            .setName(new ResourceLocation(Clinker.MOD_ID, "element"))
            .setType(Element.class)
            .create();
}
