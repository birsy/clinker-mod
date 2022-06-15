package birsy.clinker.client.render.entity.model;

import birsy.clinker.common.entity.Salamander.AbstractSalamanderPartEntity;
import birsy.clinker.common.entity.Salamander.SalamanderHeadEntity;
import net.minecraft.client.model.EntityModel;

public abstract class AbstractSalamanderModel<T extends AbstractSalamanderPartEntity> extends EntityModel<T> {
    protected float rotationX;
    protected float rotationY;

    public void setBodyRotation(float rotationX, float rotationY) {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
    }
}
