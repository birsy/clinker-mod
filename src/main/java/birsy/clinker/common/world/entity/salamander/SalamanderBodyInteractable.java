package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundPushPacket;
import birsy.clinker.common.world.level.interactable.CollidableInteractable;
import birsy.clinker.common.world.level.interactable.InteractionContext;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SalamanderBodyInteractable extends CollidableInteractable {
    final NewSalamanderEntity entityParent;
    final NewSalamanderEntity.SalamanderJoint jointParent;

    public SalamanderBodyInteractable(NewSalamanderEntity entityParent, NewSalamanderEntity.SalamanderJoint jointParent, OBBCollisionShape shape) {
        super(shape, false, 1.0);
        this.entityParent = entityParent;
        this.jointParent = jointParent;
    }

    public SalamanderBodyInteractable() {
        super();
        this.entityParent = null;
        this.jointParent = null;
    }

    @Override
    public boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean onPick(InteractionContext interactionContext, @Nullable Entity entity) {
        // todo : entity picking.
        //        copy code from Minecraft#pickBlock()
        return false;
    }

    @Override
    public boolean onTouch(InteractionContext context, Entity touchingEntity) {
//        if (touchingEntity == this.entityParent) return false;
//
//        float salamanderMass = 5.0F;
//        float entityMass = touchingEntity.getBbHeight() * touchingEntity.getBbWidth() * touchingEntity.getBbWidth();
//        float totalMass = entityMass + salamanderMass;
//
//        double collisionDepth = context.from().distanceTo(context.to());
//        Vec3 collisionDirection = context.from().subtract(context.to()).normalize();
//
//        Vec3 entityMovement = collisionDirection.scale(collisionDepth * (salamanderMass / totalMass));
//        Vec3 segmentMovement = collisionDirection.scale(-collisionDepth * (entityMass / totalMass));
//
//        touchingEntity.push(entityMovement.x, entityMovement.y, entityMovement.z);
//        if (touchingEntity instanceof ServerPlayer player) ClinkerPacketHandler.sendToClient(player, new ClientboundPushPacket(entityMovement));
//
//        this.jointParent.push(segmentMovement);
        return true;
    }
}
