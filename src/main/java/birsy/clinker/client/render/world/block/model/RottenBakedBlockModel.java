package birsy.clinker.client.render.world.block.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class RottenBakedBlockModel extends BakedBlockModelProxy{
    protected RottenBakedBlockModel(BakedModel baseBakedModel) {
        super(baseBakedModel);
    }

    private static class WigglyModel extends BakedBlockModelProxy {
        private final BlockState state;
        private final Map<BlockState, BakedModel> modelCache = new HashMap<>();
        private final Map<RenderType, Map<Direction, List<BakedQuad>>> quadCache = new HashMap<>();

        protected WigglyModel(BlockState state, BakedModel baseBakedModel) {
            super(baseBakedModel);
            this.state = state;
        }

    }
}
