package birsy.clinker.mixin.common;

//import birsy.clinker.common.world.level.interactable.*;
//import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
//import birsy.clinker.core.util.rigidbody.gjkepa.GJKEPA;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.AABB;
//import net.minecraft.world.phys.Vec3;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Mixin(Entity.class)
//public abstract class EntityMixin {
//    @Inject(method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
//    private void collide(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {
//        Vec3 velocityVector = cir.getReturnValue();
//        Entity me = (Entity)(Object)this;
//
//        InteractableManager manager = me.getLevel().isClientSide ? InteractableManager.clientInteractableManager : InteractableManager.serverInteractableManagers.get(me.getLevel());
//        List<Interactable> interactables = new ArrayList();
//        interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.floor(me.getX() / 16.0D), (int) Math.floor(me.getZ() / 16.0D))));
//        interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.ceil(me.getX() / 16.0D),  (int) Math.floor(me.getZ() / 16.0D))));
//        interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.floor(me.getX() / 16.0D), (int) Math.ceil(me.getZ() / 16.0D))));
//        interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.ceil(me.getX() / 16.0D), ( int) Math.ceil(me.getZ() / 16.0D))));
//
//        for (Interactable interactable : interactables) {
//            if (interactable instanceof CollidableInteractable cI) {
//                AABB entityAABB = me.getBoundingBox().move(velocityVector);
//                Vec3 center = entityAABB.getCenter();
//                OBBCollisionShape entityBB = new OBBCollisionShape(entityAABB.maxX - center.x(), entityAABB.maxY - center.y(), entityAABB.maxZ - center.z());
//                entityBB.transform.setPosition(center);
//
//                GJKEPA.Manifold m = GJKEPA.collisionTest(cI.shape, entityBB, 5);
//                if (m != null) {
//                    velocityVector = velocityVector.add(m.normal().scale(m.depth()));
//                }
//            }
//        }
//        cir.setReturnValue(velocityVector);
//    }
//}
