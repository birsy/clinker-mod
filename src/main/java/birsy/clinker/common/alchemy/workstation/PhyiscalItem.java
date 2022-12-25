package birsy.clinker.common.alchemy.workstation;

import birsy.clinker.core.Clinker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PhyiscalItem implements ItemLike {
    private final AlchemicalWorkstation workstation;
    private final ItemStack itemStack;
    private Vec3 previousPosition;
    private Vec3 position;
    private Vec3 velocity;
    private float elasticity;
    private float friction;
    private boolean updatePhysics;

    public PhyiscalItem(AlchemicalWorkstation workstation, ItemStack itemStack, Vec3 position) {
        this.workstation = workstation;
        this.itemStack = itemStack;

        this.previousPosition = position;
        this.position = position;
        this.velocity = Vec3.ZERO;

        this.elasticity = 0.2F;
        this.friction = 1.0F;
        this.updatePhysics = false;
    }

    public void tick(float deltaTime) {
        this.previousPosition = this.position;
        this.updatePhysics = true;

        if (updatePhysics) {
            handleStaticCollisionsAndUpdatePosition(deltaTime);
            handleDynamicCollisions(deltaTime);
        }
    }

    //Collisions with blocks
    private void handleStaticCollisionsAndUpdatePosition(float deltaTime) {
        Vec3 currentPosition = this.position;

        Vec3 nextPosition = this.position.add(velocity.scale(deltaTime));
        Level level = workstation.level;

        BlockHitResult collision = level.clip(new ClipContext(currentPosition, nextPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

        if (collision.getType() != HitResult.Type.MISS) {
            Vec3 collisionLocation = collision.getLocation();
            Vec3 faceNormal = new Vec3(collision.getDirection().getNormal().getX(), collision.getDirection().getNormal().getY(), collision.getDirection().getNormal().getZ());
            Vec3 reflectionNormal = new Vec3(faceNormal.x() < 0 ? 1 : -1, faceNormal.y() < 0 ? 1 : -1, faceNormal.z() < 0 ? 1 : -1);
            double progress = currentPosition.distanceTo(collisionLocation) / currentPosition.distanceTo(nextPosition);
            double collideTime = deltaTime * progress;
            Vec3 collisionPosition = this.position.add(velocity.scale(collideTime));
            nextPosition = collisionPosition;//collisionPosition.add(faceNormal.scale(deltaTime - collideTime).scale(elasticity));

            Vec3 velocityMultiplier = new Vec3(reflectionNormal.x() < 0 ? elasticity : friction, reflectionNormal.y() < 0 ? elasticity : friction, reflectionNormal.z() < 0 ? elasticity : friction);

            this.velocity = this.velocity.multiply(reflectionNormal).multiply(velocityMultiplier);
        } else {
            this.velocity = this.velocity.add(0, -2 * deltaTime, 0);
        }

        this.position = nextPosition;
    }

    //Collisions with entities and other items
    private void handleDynamicCollisions(float deltaTime) {
        //fuck this im doing it later
    }

    public Vec3 getPosition(float partialTick) {
        return this.previousPosition.lerp(this.position, partialTick);
    }

    @Override
    public Item asItem() {
        return itemStack.getItem();
    }

    public ItemStack asItemStack() {
        return itemStack;
    }
}
