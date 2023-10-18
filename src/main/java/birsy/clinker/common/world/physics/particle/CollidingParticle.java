package birsy.clinker.common.world.physics.particle;

import birsy.clinker.core.util.JomlConversions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class CollidingParticle {
    public Vec3 physicsPrevPosition;
    public Vec3 pPosition;
    public Vec3 position;
    public Vec3 physicsNextPosition;

    private Vec3 pushes;
    private Vec3 acceleration;
    public double radius;
    public final List<Constraint> constraints;
    public final List<LinkConstraint> linkConstraints;
    public final ParticleParent parent;

    public CollidingParticle(ParticleParent parent, double radius) {
        this.radius = radius;
        this.constraints = new ArrayList<>();
        this.linkConstraints = new ArrayList<>();
        this.pushes = Vec3.ZERO;
        this.parent = parent;
        Vec3 position = parent.getPosition();
        if (position == null) position = Vec3.ZERO;
        this.pPosition = position;
        this.position = position;
        this.physicsPrevPosition = position;
        this.physicsNextPosition = position;
        this.acceleration = Vec3.ZERO;
        parent.addParticle(this);
    }

    public void tick() {}

    public void remove() {
        parent.removeParticle(this);
    }

    public Vec3 getPosition(double partialTick) {
        return pPosition.lerp(position, partialTick);
    }

    public Vec3 getSmoothedPosition(double partialTick) {
        if (linkConstraints.isEmpty()) return this.pPosition.lerp(this.position, partialTick);

        Vec3 center = Vec3.ZERO;
        for (LinkConstraint links : linkConstraints) {
            center = center.add(links.getCenter(partialTick));
        }
        center = center.scale(1.0D / (double)(constraints.size()));

        return center;
    }

    protected void beginTick(float deltaTime) {
        this.pPosition = this.position;
        Vec3 velocity = this.getDeltaMovement();

        velocity = velocity.add(pushes);
        this.pushes = Vec3.ZERO;

        this.physicsPrevPosition = this.position;
        this.physicsNextPosition = this.position.add(velocity).add(this.acceleration.scale(deltaTime * deltaTime));

        this.acceleration = Vec3.ZERO;
    }

    protected void finalizeTick() {
        applyCollisionConstraints();
        this.position = this.physicsNextPosition;
    }

    protected void applyCollisionConstraints() {
        this.physicsNextPosition = this.position.add(constrainVelocity(this.physicsNextPosition.subtract(position)));
    }

    // ripped from entity code. not entirely sure how it works!
    private Vec3 constrainVelocity(Vec3 velocity) {
        Level level = parent.getLevel();
        if (level == null) return velocity;

        AABB aabb = new AABB(this.position.add(radius, 2 * radius, radius), this.position.subtract(radius, 0, radius));

        List<VoxelShape> list = level.getEntityCollisions(null, aabb.expandTowards(velocity));

        return Entity.collideBoundingBox(null, velocity, aabb, level, list);
    }

    protected void accelerate(Vec3 amount) {
        this.acceleration = this.acceleration.add(amount);
    }

    protected void push(Vec3 amount) {
        this.push(amount.x, amount.y, amount.z);
    }

    protected void push(double x, double y, double z) {
        pushes = pushes.add(x, y, z);
    }

    protected Vec3 getDeltaMovement() {
        return this.position.subtract(this.physicsPrevPosition);
    }

    public Quaterniond getOrientation(double partialTick, Vec3 forward) {
        if (this.constraints.isEmpty()) return new Quaterniond();
        
        Vec3 directionVector = Vec3.ZERO;
        for (LinkConstraint constraint : linkConstraints) {
            directionVector.add(constraint.getVector(partialTick));
        }
        directionVector = directionVector.scale(1.0D / (double)linkConstraints.size());

        return new Quaterniond().lookAlong(JomlConversions.toJOML(directionVector), new Vector3d(0, 1, 0));
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
        if (constraint instanceof LinkConstraint link) linkConstraints.add(link);
    }

    public void removeConstraint(Constraint constraint) {
        constraints.remove(constraint);
        if (constraint instanceof LinkConstraint link) linkConstraints.remove(link);
    }
}
