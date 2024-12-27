package birsy.clinker.client.book.recipemap;

import birsy.clinker.common.alchemy.matter.Matter;
import birsy.clinker.common.alchemy.recipe.AlchemyRecipe;
import birsy.clinker.common.alchemy.recipe.AlchemyRecipeRequirements;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;

@SuppressWarnings("UnstableApiUsage")
public class RecipeMapPage {
    final ImmutableValueGraph<MatterNode, AlchemyRecipeRequirements> nodeGraph;

    public RecipeMapPage() {
        this.nodeGraph = ValueGraphBuilder.directed()
                .allowsSelfLoops(true)
                .<MatterNode, AlchemyRecipeRequirements> immutable()
                .addNode(new MatterNode("Lead", 0, 0))
                .build();
    }

    record MatterNode(String name, float x, float y) {}
}
