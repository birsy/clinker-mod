package birsy.clinker.datagen.providers;

import birsy.clinker.core.registry.ClinkerDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class ClinkerDataMapProvider extends DataMapProvider {
    public ClinkerDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(ClinkerDataMaps.ITEM_CONDUCTIVITY)
                // highly resistice
                // chainmail!
                .add(Items.CHAINMAIL_HELMET.builtInRegistryHolder(), -2F, false)
                .add(Items.CHAINMAIL_CHESTPLATE.builtInRegistryHolder(), -2F, false)
                .add(Items.CHAINMAIL_LEGGINGS.builtInRegistryHolder(), -2F, false)
                .add(Items.CHAINMAIL_BOOTS.builtInRegistryHolder(), -2F, false)
                // also leather boots.
                .add(Items.LEATHER_BOOTS.builtInRegistryHolder(), -2F, false)

                // resistive
                // leather and wood
                .add(Items.WOODEN_SWORD.builtInRegistryHolder(), -1F, false)
                .add(Items.WOODEN_PICKAXE.builtInRegistryHolder(), -1F, false)
                .add(Items.WOODEN_AXE.builtInRegistryHolder(), -1F, false)
                .add(Items.WOODEN_SHOVEL.builtInRegistryHolder(), -1F, false)
                .add(Items.WOODEN_HOE.builtInRegistryHolder(), -1F, false)
                .add(Items.LEATHER_HELMET.builtInRegistryHolder(), -1F, false)
                .add(Items.LEATHER_CHESTPLATE.builtInRegistryHolder(), -1F, false)
                .add(Items.LEATHER_LEGGINGS.builtInRegistryHolder(), -1F, false)
                .add(Items.LEATHER_HORSE_ARMOR.builtInRegistryHolder(), -1F, false)

                // conductive
                // iron stuffs
                .add(Items.IRON_SWORD.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_PICKAXE.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_AXE.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_SHOVEL.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_HOE.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_HELMET.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_CHESTPLATE.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_LEGGINGS.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_BOOTS.builtInRegistryHolder(), 1F, false)
                .add(Items.IRON_HORSE_ARMOR.builtInRegistryHolder(), 1F, false)
                .add(Items.ANVIL.builtInRegistryHolder(), 1F, false)
                // "pure" netherite stuffs
                .add(Items.NETHERITE_SCRAP.builtInRegistryHolder(), 1F, false)
                .add(Items.ANCIENT_DEBRIS.builtInRegistryHolder(), 1F, false)
                // iron tags
                .add(Tags.Items.INGOTS_IRON, 1F, false)
                .add(Tags.Items.NUGGETS_IRON, 1F, false)
                .add(Tags.Items.RAW_MATERIALS_IRON, 1F, false)
                .add(Tags.Items.STORAGE_BLOCKS_IRON, 1F, false)
                .add(Tags.Items.STORAGE_BLOCKS_RAW_IRON, 1F, false)
                .add(Tags.Items.ORES_IRON, 1F, false)

                // highly conductive
                // gold!
                .add(Items.GOLDEN_SWORD.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_PICKAXE.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_AXE.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_SHOVEL.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_HOE.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_HELMET.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_CHESTPLATE.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_LEGGINGS.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_BOOTS.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_HORSE_ARMOR.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_CARROT.builtInRegistryHolder(), 2F, false)
                .add(Items.GOLDEN_APPLE.builtInRegistryHolder(), 2F, false)
                .add(Items.ENCHANTED_GOLDEN_APPLE.builtInRegistryHolder(), 2F, false)
                // netherite is partially made of gold
                .add(Items.NETHERITE_SWORD.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_PICKAXE.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_AXE.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_SHOVEL.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_HOE.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_HELMET.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_CHESTPLATE.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_LEGGINGS.builtInRegistryHolder(), 2F, false)
                .add(Items.NETHERITE_BOOTS.builtInRegistryHolder(), 2F, false)
                // special items
                .add(Items.TRIDENT.builtInRegistryHolder(), 2F, false)
                // gold, netherite, copper tags
                .add(Tags.Items.INGOTS_GOLD, 2F, false)
                .add(Tags.Items.NUGGETS_GOLD, 2F, false)
                .add(Tags.Items.RAW_MATERIALS_GOLD, 2F, false)
                .add(Tags.Items.STORAGE_BLOCKS_GOLD, 2F, false)
                .add(Tags.Items.STORAGE_BLOCKS_RAW_GOLD, 2F, false)
                .add(Tags.Items.ORES_GOLD, 2F, false)
                .add(Tags.Items.INGOTS_NETHERITE, 2F, false)
                .add(Tags.Items.STORAGE_BLOCKS_NETHERITE, 2F, false)
                .add(Tags.Items.INGOTS_COPPER, 2F, false)
                .add(Tags.Items.RAW_MATERIALS_COPPER, 2F, false)
                .add(Tags.Items.STORAGE_BLOCKS_COPPER, 2F, false)
                .add(Tags.Items.STORAGE_BLOCKS_RAW_COPPER, 2F, false)
                .add(Tags.Items.ORES_COPPER, 2F, false)

                .add(Items.LIGHTNING_ROD.builtInRegistryHolder(), 10F, false);

        builder(ClinkerDataMaps.ENTITY_CONDUCTIVITY)
                .add(EntityType.IRON_GOLEM.builtInRegistryHolder(), 3F, false);
    }
}
