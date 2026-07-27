package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.datagen.custom.CounterTransformOverrideProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.joml.Matrix4f;

import java.util.concurrent.CompletableFuture;

public class ClinkerCounterTransformOverrideProvider extends CounterTransformOverrideProvider {
    public ClinkerCounterTransformOverrideProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Clinker.MOD_ID, lookupProvider);
    }

    @Override
    protected void gather() {
        this.create("bottles")
                .applyTo(
                        Items.GLASS_BOTTLE,
                        Items.EXPERIENCE_BOTTLE,
                        Items.HONEY_BOTTLE,
                        Items.OMINOUS_BOTTLE
                ).transform(new Matrix4f().translate(0F, 0.1F, 0F))
                .stackOffset(0F)
                .stackOffsetRandom(0.05F, 0.03F, 0.05F)
                .build();
        this.create("potion_bottles")
                .parent("bottles")
                .applyTo(Tags.Items.POTION_BOTTLE)
                .build();

        this.create("buckets")
                .applyTo(Tags.Items.BUCKETS)
                .transform(new Matrix4f().translate(0F, 0.1F, 0F))
                .stackOffset(0F)
                .stackOffsetRandom(0.05F, 0.03F, 0.05F)
                .build();

        this.create("bowls")
                .applyTo(
                        Items.BOWL,
                        Items.MUSHROOM_STEW,
                        Items.SUSPICIOUS_STEW,
                        Items.RABBIT_STEW,
                        Items.BEETROOT_SOUP
                )
                .stackOffset(0F)
                .stackOffsetRandom(0.05F, 0.03F, 0.05F)
                .build();

        this.create("beds")
                .applyTo(ItemTags.BEDS)
                .transform(new Matrix4f().translate(0F, 0.05F, 0F))
                .stackOffset(0F)
                .stackOffsetRandom(0F, 0.02F, 0F)
                .build();
    }
}
