package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Function;

public class ClinkerItemModelProvider extends ItemModelProvider {
    public ClinkerItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Clinker.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(ClinkerItems.VITRIOL_BUCKET.get());
    }
}
