package birsy.clinker.common.alchemy.recipe;

import birsy.clinker.common.alchemy.matter.Matter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;

public record AlchemyRecipe(Matter input, Matter output, AlchemyRecipeRequirements recipeRequirements) {
//    public static final Codec<AlchemyRecipe> CODEC = RecordCodecBuilder.mapCodec((codec) -> codec.group(
//            Matter.CODEC.fieldOf("input").forGetter(AlchemyRecipe::input),
//            Matter.CODEC.fieldOf("output").forGetter(AlchemyRecipe::output),
//            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("output_quantity", 1.0F).forGetter(AlchemyRecipe::outputQuantity),
//            Matter.CODEC.optionalFieldOf("catalyst", null).forGetter(AlchemyRecipe::catalyst),
//            Codec.either()
//    ).apply(codec, AlchemyRecipe::new));
}
