package birsy.clinker.core.registry;

import birsy.clinker.common.world.entity.homunculoids.HomunculoidEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;

public class ClinkerTags {
    public static final TagKey<Block> WORKSTATION = BlockTags.create(new ResourceLocation(Clinker.MOD_ID, "valid_workstation_blocks"));
    public static final TagKey<GameEvent> GNOMAD_CALLOUTS = TagKey.create(Registries.GAME_EVENT, new ResourceLocation(Clinker.MOD_ID, "gnomad_callouts"));
    public static final TagKey<EntityType<?>> DOESNT_SCARE_GNOMAD_RUNTS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Clinker.MOD_ID, "doesnt_scare_gnomad_runts"));
    public static final TagKey<EntityType<?>> GNOMADS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Clinker.MOD_ID, "gnomads"));
    public static final TagKey<EntityType<?>> BIRTHABLE_HOMUNCULOIDS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Clinker.MOD_ID, "birthable_homunculoids"));

    public static final TagKey<Item> NOT_BUOYANT = ItemTags.create(new ResourceLocation(Clinker.MOD_ID, "not_buoyant"));

}
