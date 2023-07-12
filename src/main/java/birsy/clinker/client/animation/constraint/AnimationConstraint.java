package birsy.clinker.client.animation.constraint;

import birsy.clinker.client.animation.ModelBone;
import birsy.clinker.client.animation.ModelSkeleton;
import birsy.clinker.core.Clinker;

import javax.annotation.Nullable;

public abstract class AnimationConstraint {
    public final String identifier;
    public final ModelBone bone;
    public final ModelSkeleton skeleton;

    private ConstraintOrder constraintOrder;
    private final boolean dynamicConstraintOrder;

    protected AnimationConstraint(String identifier, ModelBone bone, ModelSkeleton skeleton, @Nullable ConstraintOrder constraintOrder) {
        this.identifier = identifier;

        this.bone = bone;
        this.bone.constraints.put(identifier, this);
        this.skeleton = skeleton;
        this.skeleton.constraintIdentifierMap.put(identifier, this);

        if (constraintOrder == null) {
            this.constraintOrder = ConstraintOrder.PRE_PHYSICS;
            this.skeleton.constraints[0].add(this);
            this.dynamicConstraintOrder = true;
        } else {
            this.constraintOrder = constraintOrder;
            this.skeleton.constraints[this.constraintOrder.index].add(this);
            this.dynamicConstraintOrder = false;
        }
    }

    public ConstraintOrder getConstraintOrder() {
        return constraintOrder;
    }

    public void setConstraintOrder(ConstraintOrder order) {
        if (this.dynamicConstraintOrder) {
            this.constraintOrder = order;
        } else {
            Clinker.LOGGER.warn("Trying to set constraint order of non-dynamic constraint!");
        }
    }

    public abstract void iterate(float deltaTime);

    public abstract boolean isConstraintSatisfied();
}
