package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import birsy.clinker.common.world.physics.rigidbody.colliders.OBBCollisionShape;
import birsy.clinker.common.world.physics.rigidbody.gjkepa.GJKEPA;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.JomlConversions;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.*;

public class WorkstationEnvironment {
    protected Workstation workstation;
    public List<WorkstationPhysicsObject> objects;
    public Map<BlockPos, List<AABB>> blockShapeCache;

    public WorkstationEnvironment(Workstation station) {
        this.workstation = station;
        this.objects = new ArrayList<>();
        this.blockShapeCache = new HashMap<>();
    }

    public void tick() {
        Vec3 gravity = new Vec3(0, -0.001, 0);
        for (WorkstationPhysicsObject object : this.objects) {
            //object.push(JomlConversions.toJOML(object.position), JomlConversions.toJOML(gravity));
            object.position = object.position.add(gravity);
            object.integrate();
        }
        this.resolveCollisions();

        if (workstation.containedBlocks.blocks.size() > 0) {
            double radius = 16;
            BlockPos pos = (BlockPos) workstation.containedBlocks.blocks.toArray()[0];
            for (int i = this.objects.size() - 1; i > -1; i--) {
                if (pos.distSqr(BlockPos.containing(this.objects.get(i).position)) > radius * radius) {
                    this.objects.remove(i);
                    Clinker.LOGGER.info("removed object as exceeded maximum distance.");
                }
            }
        }


        //this.resolveCollisions();
    }

    public void addObject(WorkstationPhysicsObject object) {
        this.objects.add(object);
    }

    private void resolveCollisions() {
        for (WorkstationPhysicsObject object : this.objects) {
            List<AABB> potentialCollisionShapes = new ArrayList<>();
            BlockPos pos = BlockPos.containing(object.position);
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        potentialCollisionShapes.addAll(this.getCollisionShapesAtBlock(pos.offset(x, y, z)));
                    }
                }
            }

            for (int i = 0; i < 1; i++) {
                for (AABB colliders : potentialCollisionShapes) {
                    WorkstationPhysicsObject.CollisionManifold collisionManifold = object.collider.collide(colliders);
                    if (collisionManifold != null) {
                        //object.position = object.position.add(collisionManifold.adjustment());
                        //Clinker.LOGGER.info(collisionManifold.adjustment());
                        object.push(JomlConversions.toJOML(collisionManifold.point()), JomlConversions.toJOML(collisionManifold.adjustment()));
                        object.collider.updateTransform(object.position, object.rotation);
                    }
                }
            }

        }
    }

    private List<AABB> getCollisionShapesAtBlock(BlockPos pos) {
        if (blockShapeCache.containsKey(pos)) return this.blockShapeCache.get(pos);
        if (workstation.level.getBlockState(pos).isAir()) return Collections.emptyList();

        List<AABB> shapes = workstation.level.getBlockState(pos).getCollisionShape(workstation.level, pos).toAabbs();
        for (int i = 0; i < shapes.size(); i++) {
            shapes.set(i, shapes.get(i).move(pos));
        }
        this.blockShapeCache.put(pos, shapes);
        return shapes;
    }
}
