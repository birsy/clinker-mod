package birsy.clinker.common.world.entity.ordnance.modifer;

import birsy.clinker.common.world.entity.ordnance.OrdnanceEntity;
import net.minecraft.world.damagesource.DamageSource;

public abstract class OrdnanceModifier {
    protected final OrdnanceEntity entity;

    public OrdnanceModifier(OrdnanceEntity entity) {
        this.entity = entity;
    }

    public void tick() {}

    public void onCollide() {}

    public void onCollideWithEntity() {}

    public void onCollideWithBlock() {}

    // called when the ordnance is hit by a player or entity.
    public void onHit(DamageSource source) {}

    public void onDetonate() {}

    public void onRemove() {}

    public boolean reusable() {
        return true;
    }

    public boolean createParticles() {
        return false;
    }
}
