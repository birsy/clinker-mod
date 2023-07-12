package birsy.clinker.client.animation;

import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.Quaternionf;
import net.minecraft.world.phys.Vec3;

public interface ModelSkeletonParent {
    Vec3 getSkeletonPosition();
    Vec3 getPreviousSkeletonPosition();
    default Vec3 getSkeletonPosition(float partialTicks) {
        return this.getPreviousSkeletonPosition().lerp(this.getSkeletonPosition(), partialTicks);
    }
    void setPreviousSkeletonPosition(Vec3 value);

    Quaternionf getSkeletonOrientation();
    Quaternionf getPreviousSkeletonOrientation();
    default Quaternionf getSkeletonOrientation(float partialTicks) {
        return this.getPreviousSkeletonOrientation().slerp(this.getSkeletonOrientation(), partialTicks);
    }
    void setPreviousSkeletonOrientation(Quaternionf value);
}
