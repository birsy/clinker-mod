package birsy.clinker.client.render.world.block.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class BakedBlockModelProxy implements BakedModel {
    protected final BakedModel baseBakedModel;

    protected BakedBlockModelProxy(BakedModel baseBakedModel) { this.baseBakedModel = baseBakedModel; }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pSide, Random pRand) {
        return baseBakedModel.getQuads(pState, pSide, pRand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseBakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return baseBakedModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseBakedModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return baseBakedModel.getOverrides();
    }

    @Override
    public ItemTransforms getTransforms() { return baseBakedModel.getTransforms(); }
}
