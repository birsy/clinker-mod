package birsy.clinker.core.registry;

import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;

public class ClinkerTags {
    public static final class Blocks {
        public static final TagKey<Block> WORKSTATION = BlockTags.create(Clinker.resource("valid_workstation_blocks"));
        public static final TagKey<Block> BRAMBLE_FLOWERS = BlockTags.create(Clinker.resource("bramble_flowers"));
        public static final TagKey<Block> OTHERSHORE_SOIL = BlockTags.create(Clinker.resource("othershore_soil"));
        public static final TagKey<Block> BRAMBLES = BlockTags.create(Clinker.resource("brambles"));
    }

    public static final class Items {
        public static final TagKey<Item> NOT_BUOYANT = ItemTags.create(Clinker.resource("not_buoyant"));
        public static final TagKey<Item> ALCHEMISTS_CROSSBOW_REPAIRABLE = ItemTags.create(Clinker.resource("alchemists_crossbow_repairable"));
        public static final TagKey<Item> BASALT = ItemTags.create(Clinker.resource("basalt"));
        public static final TagKey<Item> SALTMOSS_PLANTS = ItemTags.create(Clinker.resource("saltmoss_plants"));
        public static final TagKey<Item> HERBS = ItemTags.create(Clinker.resource("herbs"));

    }

    public static class Entities {
        public static final TagKey<EntityType<?>> DOESNT_SCARE_GNOMAD_RUNTS = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("doesnt_scare_gnomad_runts"));
        public static final TagKey<EntityType<?>> GNOMADS = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("gnomads"));
        public static final TagKey<EntityType<?>> BIRTHABLE_HOMUNCULOIDS = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("birthable_homunculoids"));
        public static final TagKey<EntityType<?>> THORN_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("thorn_immune"));
    }

    public static final class DamageTypes {
        public static final TagKey<DamageType> THORNY = TagKey.create(Registries.DAMAGE_TYPE, Clinker.resource("thorny"));
    }

    public static class OrdnanceModifiers {
        public static final TagKey<OrdnanceModifierType<?>> DETONATES = TagKey.create(ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY, Clinker.resource("detonates"));
        public static final TagKey<OrdnanceModifierType<?>> CAUSES_DETONATION = TagKey.create(ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY, Clinker.resource("causes_detonation"));
    }
}
