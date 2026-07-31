package birsy.clinker.datagen.custom;

import birsy.clinker.common.alchemy.recipe.MortarAndPestleRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MortarAndPestleRecipeBuilder implements RecipeBuilder {
    protected final ItemStack result;
    protected final List<Ingredient> ingredients = new ArrayList<>(4);
    protected int grindTime = 60;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public MortarAndPestleRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    public MortarAndPestleRecipeBuilder ingredients(Ingredient... ingredients) {
        for (Ingredient ingredient : ingredients)
            this.ingredient(ingredient);
        return this;
    }

    public MortarAndPestleRecipeBuilder ingredient(Ingredient ingredient) {
        if (ingredients.size() >= 4) throw new IllegalStateException("tried to add more than 4 ingredients to mortar and pestle recipe");
        ingredients.add(ingredient);
        return this;
    }

    public MortarAndPestleRecipeBuilder grindTime(int grindTime) {
        this.grindTime = grindTime;
        return this;
    }

    @Override
    public MortarAndPestleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }
    @Override public MortarAndPestleRecipeBuilder group(@Nullable String group) { return this; }
    @Override public Item getResult() { return this.result.getItem(); }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        MortarAndPestleRecipe recipe = new MortarAndPestleRecipe(this.result, this.ingredients, this.grindTime);
        output.accept(id.withPrefix("mortar_and_pestle/"), recipe, advancement.build(id.withPrefix("recipes/mortar_and_pestle/")));
    }
}
