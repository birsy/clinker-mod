package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.common.world.level.interactable.CollidableInteractable;
import birsy.clinker.common.world.level.interactable.InteractionContext;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

public class SalamanderBodyInteractable extends CollidableInteractable {
    final NewSalamanderEntity entityParent;
    final NewSalamanderEntity.SalamanderJoint jointParent;

    public SalamanderBodyInteractable(NewSalamanderEntity entityParent, NewSalamanderEntity.SalamanderJoint jointParent, OBBCollisionShape shape) {
        super(shape, true, 5.0, 1.0, false);
        entityParent.addChild(this);
        this.entityParent = entityParent;
        this.jointParent = jointParent;
    }

    public SalamanderBodyInteractable() {
        super();
        this.entityParent = null;
        this.jointParent = null;
    }

    @Override
    public boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide) {
        return false;
    }

    @Override
    public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide) {
        if (entity instanceof Player player) {
            Vec3 hitVector = interactionContext.to().subtract(interactionContext.from()).normalize();
            this.entityParent.lastHitJoint = this.jointParent;
            player.attack(this.entityParent);
            player.resetAttackStrengthTicker();
            this.jointParent.accelerate(hitVector);
        }
        return true;
    }

    @Override
    public boolean onPick(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide) {
        // todo : entity picking.
        //        copy code from Minecraft#pickBlock()
        return false;
    }

    @Override
    public boolean onTouch(InteractionContext context, Entity touchingEntity, boolean clientSide) {
        return true;
    }

    @Override
    public void push(Vec3 movement) {
        jointParent.push(movement);
        super.push(movement);
    }
}
