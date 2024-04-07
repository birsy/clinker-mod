package birsy.clinker.common.world.entity.ordnance.modifer;

import birsy.clinker.common.world.entity.ordnance.OrdnanceEntity;

public abstract class VariableIntensityModifier extends OrdnanceModifier {
    protected final float amount;

    public VariableIntensityModifier(OrdnanceEntity entity, float amount) {
        super(entity);
        this.amount = amount;
    }
}
