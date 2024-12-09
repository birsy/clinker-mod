package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;

public class ClinkerTags {
    public static final TagKey<Block> WORKSTATION = BlockTags.create(Clinker.resource("valid_workstation_blocks"));
    public static final TagKey<GameEvent> GNOMAD_CALLOUTS = TagKey.create(Registries.GAME_EVENT, Clinker.resource("gnomad_callouts"));
    public static final TagKey<EntityType<?>> DOESNT_SCARE_GNOMAD_RUNTS = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("doesnt_scare_gnomad_runts"));
    public static final TagKey<EntityType<?>> GNOMADS = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("gnomads"));
    public static final TagKey<EntityType<?>> BIRTHABLE_HOMUNCULOIDS = TagKey.create(Registries.ENTITY_TYPE, Clinker.resource("birthable_homunculoids"));

    public static final TagKey<Item> NOT_BUOYANT = ItemTags.create(Clinker.resource("not_buoyant"));
    public static final TagKey<Item> ALCHEMISTS_CROSSBOW_REPAIRABLE = ItemTags.create(Clinker.resource("alchemists_crossbow_repairable"));

}
