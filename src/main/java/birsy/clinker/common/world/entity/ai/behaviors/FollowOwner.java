package birsy.clinker.common.world.entity.ai.behaviors;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;

public class FollowOwner<E extends PathfinderMob & OwnableEntity> extends FollowEntity<E, LivingEntity> {
    protected LivingEntity owner = null;

    public FollowOwner() {
        following(this::getOwner);
        teleportToTargetAfter(12);
    }

    protected LivingEntity getOwner(E entity) {
        if (this.owner == null)
            this.owner = entity.getOwner();

        if (this.owner != null && this.owner.isRemoved())
            this.owner = null;
        
        return this.owner;
    }
}
