package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerEnglishLanguageProvider extends LanguageProvider {
    public ClinkerEnglishLanguageProvider(PackOutput output) {
        super(output, Clinker.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.clinker", "Clinker Items");
        for (DeferredHolder<Block, ? extends Block> entry : ClinkerBlocks.BLOCKS.getEntries()) {
            this.addBlock(entry, localizedNameFromRegistryName(entry.getId().getPath()));
        }
        for (DeferredHolder<Item, ? extends Item> entry : ClinkerItems.ITEMS.getEntries()) {
            this.addItem(entry, localizedNameFromRegistryName(entry.getId().getPath()));
        }
        for (DeferredHolder<EntityType<?>, ? extends EntityType> entry : ClinkerEntities.ENTITY_TYPES.getEntries()) {
            this.addEntityType(entry, localizedNameFromRegistryName(entry.getId().getPath()));
        }

    }

    private String localizedNameFromRegistryName(String registryName) {
        String[] words = registryName.split("_");
        StringBuilder localizedName = new StringBuilder();
        for (String word : words) localizedName.append(Character.toTitleCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");

        return localizedName.toString().trim();
    }
}
