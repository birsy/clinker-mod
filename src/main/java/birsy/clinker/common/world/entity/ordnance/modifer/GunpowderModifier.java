package birsy.clinker.common.world.entity.ordnance.modifer;

import birsy.clinker.common.world.entity.ordnance.OrdnanceEntity;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class GunpowderModifier extends VariableIntensityModifier {
    @OnlyIn(Dist.CLIENT)
    public PointLight light;
    protected int maxFuseTime = 120;

    public GunpowderModifier(OrdnanceEntity entity, float amount) {
        super(entity, amount);
        if (entity.level().isClientSide()) {
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                this.light = new PointLight();
                this.light.setRadius(1.0F);
                this.light.setColor(0, 0, 0);
                this.light.setPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ());
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entity.tickCount >= this.maxFuseTime || this.entity.isOnFire()) {
            this.entity.detonate();
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (this.entity.level().isClientSide()) {
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                if (this.light != null) VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            }
        }
    }


    @Override
    public boolean reusable() {
        return false;
    }

    @Override
    public boolean createParticles() {
        return true;
    }
}
