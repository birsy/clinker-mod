package birsy.clinker.common.world.block.blockentity.fairyfruit;

import birsy.clinker.common.world.level.interactable.InteractableParent;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;


public class FairyFruitBlockEntity extends BlockEntity implements InteractableParent {
    public FairyFruitVine vine;
    
    public float lengthOffset;
    
    public PointLight light;

    public FairyFruitBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.FAIRY_FRUIT.get(), pPos, pBlockState);
    }

    @Override
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
        this.lengthOffset = p_155231_.random.nextFloat() * 2;
        this.vine = new FairyFruitVine(level, this.getBlockPos().getCenter().add(0, 0.5, 0));
        for (int i = 0; i < Math.round(5 + p_155231_.random.nextGaussian() * 2); i++) {
            this.vine.grow();
        }

        if (level.isClientSide()) {
            this.light = new PointLight();
            this.light.setColor(0xffaf59);
            this.light.setRadius(12.0F);
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level.isClientSide) {
            if (this.light == null) return;
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            }
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.level.isClientSide) {
            if (this.light == null) return;
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            }
        }
    }

    public void useBoneMeal() {
        if (this.level == null) return;
        if (this.vine == null) return;
        this.vine.grow();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, FairyFruitBlockEntity entity) {
        if (entity.vine == null) return;
        entity.vine.tick();
    }
}
