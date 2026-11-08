package birsy.clinker.common.alchemy.recipe;

import birsy.clinker.common.item.AlchemistsCrossbowItem;
import birsy.clinker.common.item.components.CrossbowAttachment;
import birsy.clinker.core.registry.ClinkerDataComponents;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class AlchemistsCrossbowAttachmentRecipe extends CustomRecipe {
    public final Ingredient attachmentIngredient;
    public final CrossbowAttachment attachmentComponent;

    public AlchemistsCrossbowAttachmentRecipe(CraftingBookCategory category, Ingredient attachmentIngredient, CrossbowAttachment attachmentComponent) {
        super(category);
        this.attachmentIngredient = attachmentIngredient;
        this.attachmentComponent = attachmentComponent;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.size() != 2)
            return false;

        int crossbowIndex = searchForItem(input,
                stack -> stack.is(ClinkerItems.ALCHEMISTS_CROSSBOW) && !AlchemistsCrossbowItem.hasAttachment(stack));
        if (crossbowIndex == -1) return false;

        int attachmentIndex = searchForItem(input, attachmentIngredient);
        if (attachmentIndex == -1) return false;

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int crossbowIndex = searchForItem(input,
                stack -> stack.is(ClinkerItems.ALCHEMISTS_CROSSBOW) && !AlchemistsCrossbowItem.hasAttachment(stack));
        ItemStack crossbow = input.getItem(crossbowIndex);

        ItemStack result = crossbow.copy();
        result.set(ClinkerDataComponents.CROSSBOW_ATTACHMENT.get(), attachmentComponent);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ClinkerRecipes.Serializers.ALCHEMISTS_CROSSBOW_ATTACHMENT.get();
    }

    private static int searchForItem(CraftingInput input, Predicate<ItemStack> predicate) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (predicate.test(stack)) return i;
        }
        return -1;
    }

    public record Serializer() implements RecipeSerializer<AlchemistsCrossbowAttachmentRecipe> {
        public static final MapCodec<AlchemistsCrossbowAttachmentRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category),
                Ingredient.CODEC_NONEMPTY.fieldOf("attachment_item").forGetter(recipe -> recipe.attachmentIngredient),
                CrossbowAttachment.CODEC.fieldOf("attachment_type").forGetter(recipe -> recipe.attachmentComponent)
        ).apply(inst, AlchemistsCrossbowAttachmentRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, AlchemistsCrossbowAttachmentRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
                        Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.attachmentIngredient,
                        CrossbowAttachment.STREAM_CODEC, recipe -> recipe.attachmentComponent,
                        AlchemistsCrossbowAttachmentRecipe::new
                );

        @Override
        public MapCodec<AlchemistsCrossbowAttachmentRecipe> codec() { return CODEC; }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlchemistsCrossbowAttachmentRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
