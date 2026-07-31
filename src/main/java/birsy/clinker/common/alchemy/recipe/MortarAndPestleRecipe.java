package birsy.clinker.common.alchemy.recipe;

import birsy.clinker.core.registry.ClinkerRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public class MortarAndPestleRecipe implements Recipe<MortarAndPestleRecipe.Input> {
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    public final int grindTime;
    private final List<Ingredient>[] expansions;

    public MortarAndPestleRecipe(ItemStack result, List<Ingredient> ingredients, int grindTime) {
        // reduces any ingredients list to the smallest possible amount
        Map<String, Ingredient> byKey = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            String key = ingredientKey(ingredient);
            byKey.putIfAbsent(key, ingredient);
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        int gcd = counts.values().stream().reduce(0, MortarAndPestleRecipe::gcd);

        this.result = result.copyWithCount(Math.max(result.getCount() / gcd, 1));
        this.grindTime = grindTime / gcd;

        NonNullList<Ingredient> reduced = NonNullList.create();
        for (var entry : counts.entrySet()) {
            int reducedCount = entry.getValue() / gcd;
            Ingredient ing = byKey.get(entry.getKey());
            for (int i = 0; i < reducedCount; i++) reduced.add(ing);
        }
        this.ingredients = reduced;

        // cached size multipliers
        this.expansions = new List[5];
        for (int total = reduced.size(); total <= 4; total += reduced.size()) {
            int multiplier = total / reduced.size();
            List<Ingredient> expanded = new ArrayList<>(total);
            for (Ingredient ingredient : reduced)
                for (int i = 0; i < multiplier; i++) expanded.add(ingredient);
            expansions[total] = expanded;
        }
    }

    // ingredients are Weird and don't compare nicely so i turn them into nicer strings first
    private static String ingredientKey(Ingredient ingredient) {
        return Arrays.stream(ingredient.getItems())
                .map(s -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString())
                .reduce("", (a, b) -> a + " " + b);
    }
    private static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }
    // "Maximum Bipartite Matching" thanks google
    private static boolean backtrack(List<ItemStack> stacks, int index, List<Ingredient> ingredients, boolean[] used) {
        if (index == stacks.size()) return true;
        ItemStack stack = stacks.get(index);
        for (int i = 0; i < ingredients.size(); i++) {
            if (!used[i] && ingredients.get(i).test(stack)) {
                used[i] = true;
                if (backtrack(stacks, index + 1, ingredients, used)) return true;
                used[i] = false;
            }
        }
        return false;
    }

    @Override
    public boolean matches(Input input, Level level) {
        // skip all recipes where the GCD doesn't match
        if (input.size() % ingredients.size() != 0) return false;
        List<Ingredient> expanded = expansions[input.size()];
        if (expanded == null) return false;

        return backtrack(input.stacks, 0, expanded, new boolean[expanded.size()]);
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        int count = 0;
        for (int i = 0; i < input.size(); i++)
            if (!input.getItem(i).isEmpty()) count++;
        int resultMultiplier = count / ingredients.size();
        return result.copyWithCount(result.getCount() * resultMultiplier);
    }

    @Override public NonNullList<Ingredient> getIngredients() { return ingredients; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }

    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public RecipeType<?> getType() { return ClinkerRecipes.Types.MORTAR_AND_PESTLE.get(); }
    @Override public RecipeSerializer<?> getSerializer() { return ClinkerRecipes.Serializers.MORTAR_AND_PESTLE.get(); }

    public record Input(List<ItemStack> stacks) implements RecipeInput {
        @Override public ItemStack getItem(int index) { return stacks.get(index); }
        @Override public int size() { return stacks.size(); }
    }

    public record Serializer() implements RecipeSerializer<MortarAndPestleRecipe> {
        public static final MapCodec<MortarAndPestleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.listOf(1, 4).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                Codec.INT.fieldOf("grind_time").forGetter(recipe -> recipe.grindTime)
        ).apply(inst, MortarAndPestleRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, MortarAndPestleRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, recipe -> recipe.result,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(4)), recipe -> recipe.ingredients,
                        ByteBufCodecs.INT, recipe -> recipe.grindTime,
                        MortarAndPestleRecipe::new
                );

        @Override
        public MapCodec<MortarAndPestleRecipe> codec() { return CODEC; }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MortarAndPestleRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
