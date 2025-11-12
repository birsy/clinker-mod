package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ClinkerItemModelProvider extends ItemModelProvider {
    public ClinkerItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Clinker.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(ClinkerItems.VITRIOL_BUCKET);
        this.basicItem(ClinkerItems.PAGE);
        this.basicItem(ClinkerItems.FISTFUL_OF_MAGGOTS);
        this.basicItem(ClinkerItems.CROSSBOW_REPEATER_ATTACHMENT);

        this.basicItem(ClinkerItems.SULFUR);
        this.basicItem(ClinkerItems.SALTPETRE);

        this.handheldItem(ClinkerItems.PECULIAR_MIRROR.get());
    }

    public ItemModelBuilder basicItem(DeferredItem<? extends Item> item) {
        return super.basicItem(item.get());
    }
}
