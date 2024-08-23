package birsy.clinker.common.world.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface CollisionParent {
    boolean hurt(ColliderEntity damageReceiver, DamageSource pSource, float pAmount);

    void push(ColliderEntity pushReceiver, double xAmount, double yAmount, double zAmount);

    void push(ColliderEntity pushReceiver, Entity pusher);
}
