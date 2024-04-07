package birsy.clinker.core.registry;

import birsy.clinker.common.world.entity.ordnance.modifer.GunpowderModifier;
import birsy.clinker.common.world.entity.ordnance.modifer.OrdnanceModifier;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerOrdnanceModifiers {
    public static final DeferredRegister<OrdnanceModifier> ORDNANCE_MODIFIERS = DeferredRegister.create(ClinkerRegistries.ORDNANCE_MODIFIERS, Clinker.MOD_ID);

    //public static final Supplier<OrdnanceModifier> GUNPOWDER_MODIFIER = ORDNANCE_MODIFIERS.register("gunpowder", () -> new GunpowderModifier());
}
