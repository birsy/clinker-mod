package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.recipe.MortarAndPestleRecipe;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerRecipes {
    public static class Types {
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
                DeferredRegister.create(Registries.RECIPE_TYPE, Clinker.MOD_ID);

        public static final DeferredHolder<RecipeType<?>, RecipeType<MortarAndPestleRecipe>> MORTAR_AND_PESTLE =
                register("mortar_and_pestle");

        private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String name) {
            return RECIPE_TYPES.register(name, () -> RecipeType.simple(Clinker.resource(name)));
        }
    }

    public static class Serializers {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
                DeferredRegister.create(Registries.RECIPE_SERIALIZER, Clinker.MOD_ID);

        public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarAndPestleRecipe>> MORTAR_AND_PESTLE =
                RECIPE_SERIALIZERS.register("mortar_and_pestle", MortarAndPestleRecipe.Serializer::new);
    }
}
