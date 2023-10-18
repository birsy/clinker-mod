package birsy.clinker.core.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class JomlConversions {
    public static Vector3d toJOML(Vec3 in) {
        return new Vector3d(in.x, in.y, in.z);
    }
    public static Vec3 toMojang(Vector3d in) {
        return new Vec3(in.x, in.y, in.z);
    }
}
