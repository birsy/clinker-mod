package birsy.clinker.datagen.custom;

import birsy.clinker.common.alchemy.recipe.AlchemistsCrossbowAttachmentRecipe;
import birsy.clinker.common.alchemy.recipe.MortarAndPestleRecipe;
import birsy.clinker.common.item.components.CrossbowAttachment;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlchemistsCrossbowAttachmentRecipeBuilder implements RecipeBuilder {
    protected final Ingredient attachmentIngredient;
    protected final CrossbowAttachment attachmentType;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public AlchemistsCrossbowAttachmentRecipeBuilder(Ingredient attachmentIngredient, CrossbowAttachment attachmentType) {
        this.attachmentIngredient = attachmentIngredient;
        this.attachmentType = attachmentType;
    }

    @Override
    public AlchemistsCrossbowAttachmentRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }
    @Override
    public AlchemistsCrossbowAttachmentRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }
    @Override
    public Item getResult() {
        return ClinkerItems.ALCHEMISTS_CROSSBOW.asItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        AlchemistsCrossbowAttachmentRecipe recipe = new AlchemistsCrossbowAttachmentRecipe(CraftingBookCategory.EQUIPMENT, attachmentIngredient, attachmentType);
        output.accept(id.withPrefix("alchemists_crossbow_attachment/"), recipe, advancement.build(id.withPrefix("recipes/" + RecipeCategory.COMBAT.getFolderName() + "/")));
    }
}
