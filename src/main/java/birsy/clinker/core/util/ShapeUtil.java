package birsy.clinker.core.util;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.HashMap;

public class ShapeUtil {
    public static HashMap<Direction.Axis, Vec3> AXIS_TO_VECTOR = Util.make(() -> {
        HashMap<Direction.Axis, Vec3> map = new HashMap<>();
        map.put(Direction.Axis.X, new Vec3(1, 0, 0));
        map.put(Direction.Axis.Y, new Vec3(0, 1, 0));
        map.put(Direction.Axis.Z, new Vec3(0, 0, 1));
        return map;
    });

    public static VoxelShape rotate(VoxelShape shape, int turns, Direction.Axis axis) {
        VoxelShape[] newShapes = new VoxelShape[]{Shapes.empty()};

        double rotation = turns * Mth.HALF_PI;
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            Quaterniond quaternion = new Quaterniond(new AxisAngle4d(rotation, JomlConversions.toJOML(AXIS_TO_VECTOR.get(axis))));
            Vec3 c1 = JomlConversions.toMoj(quaternion.transform(new Vector3d(x1, y1, z1).sub(0.5, 0.5, 0.5)));
            Vec3 c2 = JomlConversions.toMoj(quaternion.transform(new Vector3d(x2, y2, z2).sub(0.5, 0.5, 0.5)));
            c1 = c1.add(0.5, 0.5, 0.5);
            c2 = c2.add(0.5, 0.5, 0.5);

            VoxelShape newBox = Shapes.box(Math.min(c1.x(), c2.x()), Math.min(c1.y(), c2.y()), Math.min(c1.z(), c2.z()),
                                           Math.max(c1.x(), c2.x()), Math.max(c1.y(), c2.y()), Math.max(c1.z(), c2.z()));
            newShapes[0] = Shapes.join(newShapes[0], newBox, BooleanOp.OR);
        });

        return newShapes[0];
    }

    public static VoxelShape flip(VoxelShape shape, Direction.Axis axis) {
        VoxelShape[] newShapes = new VoxelShape[]{Shapes.empty()};

        Vec3 flipVector = AXIS_TO_VECTOR.get(axis).scale(-2).add(1, 1, 1);
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            Vec3 c1 = new Vec3(x1, y1, z1).subtract(0.5, 0.5, 0.5).multiply(flipVector);
            Vec3 c2 = new Vec3(x2, y2, z2).subtract(0.5, 0.5, 0.5).multiply(flipVector);
            c1 = c1.add(0.5, 0.5, 0.5);
            c2 = c2.add(0.5, 0.5, 0.5);

            VoxelShape newBox = Shapes.box(Math.min(c1.x(), c2.x()), Math.min(c1.y(), c2.y()), Math.min(c1.z(), c2.z()),
                    Math.max(c1.x(), c2.x()), Math.max(c1.y(), c2.y()), Math.max(c1.z(), c2.z()));
            newShapes[0] = Shapes.join(newShapes[0], newBox, BooleanOp.OR);
        });

        return newShapes[0];
    }
}
