package birsy.clinker.core.registry.entity;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, Clinker.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> CONDUCTIVITY = ATTRIBUTES.register("generic.conductivity",
            () -> new RangedAttribute("attribute.clinker.name.generic.conductivity", 1.0, -100.0, 100.0).setSyncable(true).setSentiment(Attribute.Sentiment.NEGATIVE)
    );
}
