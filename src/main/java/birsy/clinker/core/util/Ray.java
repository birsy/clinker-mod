package birsy.clinker.core.util;

import net.minecraft.world.phys.Vec3;

public record Ray(Vec3 startPos, Vec3 endPos, Vec3 direction, double distance) {
    public Ray(Vec3 startPos, Vec3 endPos) {
        this(startPos, endPos, endPos.subtract(startPos).normalize(), endPos.distanceTo(startPos));
    }
}
