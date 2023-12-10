package birsy.clinker.common.world.block.blockentity.fairyfruit;

import birsy.clinker.common.world.physics.particle.CollidingParticle;
import birsy.clinker.core.util.JomlConversions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class FairyFruitJoint extends CollidingParticle {
    public final int index;
    public double roll;
    public boolean isTip;
    public boolean isRoot;
    public boolean shouldCollide = true;
    public boolean shouldBeRemoved = false;
    public float fruitGrowth = 0;

    FairyFruitSegment topAttachment;
    FairyFruitSegment bottomAttachment;

    @OnlyIn(Dist.CLIENT)
    Vec3 clientPos;
    Vec3 pClientPos;

    public FairyFruitJoint(FairyFruitBlockEntity parent, double radius) {
        super(parent, radius);
        this.roll = 0.0;
        this.index = parent.joints.indexOf(this);
        this.clientPos = this.position;
        this.pClientPos = this.clientPos;
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        this.pPosition = this.position;
        this.pClientPos = this.clientPos.scale(1);
        this.clientPos = this.clientPos.lerp(this.position, 0.8);
    }
    @OnlyIn(Dist.CLIENT)
    public Vec3 getClientPosition(double partialTick) {
        return this.pClientPos.lerp(this.clientPos, partialTick);
    }

    public void tick() {
        if (this.isTip) {
            this.fruitGrowth += Math.min(this.fruitGrowth + 0.0001, 1);
        } else {
            this.fruitGrowth = 0;
        }

        this.shouldCollide = false;
        if (this.topAttachment == null && this.bottomAttachment == null) return;
        if (this.topAttachment == null) {
            this.radius = this.bottomAttachment.getLength(1.0) * 0.25;
        } else if (this.bottomAttachment == null) {
            this.radius = this.topAttachment.getLength(1.0) * 0.25;
        } else {
            this.radius = Math.min(this.topAttachment.getLength(1.0), this.bottomAttachment.getLength(1.0)) * 0.25;
        }
    }

    protected void accelerate(Vec3 amount) {
        if (this.isRoot) return;
        super.accelerate(amount);
    }

    protected void push(double x, double y, double z) {
        if (this.isRoot) return;
        super.push(x, y, z);
    }

    public Quaterniond getOrientation(double partialTick) {
        Quaterniond roll = new Quaterniond();
        if (this.topAttachment == null || this.isRoot) return roll;
        Vec3 topCenter = this.topAttachment.getCenter(partialTick);
        Vec3 bottomCenter = this.bottomAttachment == null ? this.getPosition(partialTick) : this.bottomAttachment.getCenter(partialTick);
        return new Quaterniond().lookAlong(JomlConversions.toJOML(topCenter.subtract(bottomCenter)), new Vector3d(0, 1, 0));
    }

    protected void destroy() {
        this.getParent().joints.remove(this.index);
        this.bottomAttachment.destroy();
    }

    public FairyFruitBlockEntity getParent() {
        return (FairyFruitBlockEntity) this.parent;
    }

    public CompoundTag serialize() {
        CompoundTag jointTag = new CompoundTag();

        jointTag.putDouble("x", this.position.x);
        jointTag.putDouble("y", this.position.y);
        jointTag.putDouble("z", this.position.z);

        jointTag.putDouble("dX", this.physicsPrevPosition.x);
        jointTag.putDouble("dY", this.physicsPrevPosition.y);
        jointTag.putDouble("dZ", this.physicsPrevPosition.z);

        jointTag.putDouble("roll", this.roll);
        jointTag.putDouble("radius", this.radius);
        jointTag.putBoolean("root", this.isRoot);
        jointTag.putBoolean("tip", this.isTip);
        jointTag.putBoolean("collision", this.shouldCollide);

        jointTag.putInt("id", this.index);

        return jointTag;
    }

    public FairyFruitJoint deserialize(CompoundTag jointTag) {
        Vec3 position = new Vec3(jointTag.getDouble("x"), jointTag.getDouble("y"), jointTag.getDouble("z"));
        Vec3 physicsPrevPosition = new Vec3(jointTag.getDouble("dX"), jointTag.getDouble("dY"), jointTag.getDouble("dZ"));
        double roll = jointTag.getDouble("roll");
        double radius = jointTag.getDouble("radius");
        boolean tip = jointTag.getBoolean("tip");
        boolean root = jointTag.getBoolean("root");
        boolean hasCollision = jointTag.getBoolean("collision");

        this.radius = radius;
        this.position = position;
        this.physicsPrevPosition = physicsPrevPosition;
        this.roll = roll;
        this.shouldCollide = hasCollision;
        this.isTip = tip;
        this.isRoot = tip;
        if (parent instanceof FairyFruitBlockEntity fruit) {
            if (root) fruit.root = this;
            if (tip) fruit.tip = this;
        }

        return this;
    }

    public static FairyFruitJoint deserialize(FairyFruitBlockEntity parent, CompoundTag jointTag) {
        return parent.joints.get(jointTag.getInt("id")).deserialize(jointTag);
    }

    public static FairyFruitJoint deserializeNew(FairyFruitBlockEntity parent, CompoundTag jointTag) {
        FairyFruitJoint joint = new FairyFruitJoint(parent, jointTag.getDouble("radius"));
        return joint.deserialize(jointTag);
    }

    public FruitStage getFruitStage() {
        return FruitStage.ROOT;
    }

    public enum FruitStage {
        ROOT, BUD, FRUIT;
        public static FruitStage[] fruitByInt = FruitStage.values();

        static FruitStage growthToFruitStage(double growth) {
            return fruitByInt[ (int) Math.floor(Math.min(growth, 0.98) * 3) ];
        }
    }
}
