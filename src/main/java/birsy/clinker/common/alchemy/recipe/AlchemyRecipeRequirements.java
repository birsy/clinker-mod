package birsy.clinker.common.alchemy.recipe;

import birsy.clinker.common.alchemy.matter.Matter;

import javax.annotation.Nullable;

public record AlchemyRecipeRequirements(@Nullable Matter catalyst, float heat) {
}
