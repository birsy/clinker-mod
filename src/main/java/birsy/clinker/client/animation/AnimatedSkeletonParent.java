package birsy.clinker.client.animation;

import birsy.clinker.core.util.Quaterniond;
import net.minecraft.world.phys.Vec3;

public interface AnimatedSkeletonParent {
    Vec3 getSkeletonPosition();
    Vec3 getPreviousSkeletonPosition();
    default Vec3 getSkeletonPosition(float partialTicks) {
        return this.getPreviousSkeletonPosition().lerp(this.getSkeletonPosition(), partialTicks);
    }
    void setPreviousSkeletonPosition(Vec3 value);

    Quaterniond getSkeletonOrientation();
    Quaterniond getPreviousSkeletonOrientation();
    default Quaterniond getSkeletonOrientation(float partialTicks) {
        return this.getPreviousSkeletonOrientation().slerp(this.getSkeletonOrientation(), partialTicks);
    }
    void setPreviousSkeletonOrientation(Quaterniond value);
}
