package birsy.clinker.common.world.block.blockentity.fairyfruit;

import birsy.clinker.common.world.physics.particle.LinkConstraint;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FairyFruitSegment extends LinkConstraint<FairyFruitJoint> {
    public final int index;
    protected FairyFruitInteractable interactable;
    public boolean shouldBeRemoved = false;

    public FairyFruitSegment(FairyFruitBlockEntity parent, FairyFruitJoint topJoint, FairyFruitJoint bottomJoint, double length) {
        super(parent, topJoint, bottomJoint, length);
        topJoint.bottomAttachment = this;
        bottomJoint.topAttachment = this;
        this.index = parent.segments.indexOf(this);
    }

    public void tick() {
        super.tick();
        if (this.length < this.getMaxLength()) {
            this.length = Math.min(this.length + 0.05, this.getMaxLength());
        } else if (this.length > this.getMaxLength()) {
            this.length = this.getMaxLength();
        }
        this.getBottomJoint().shouldCollide = true;

        if (this.length != this.pLength && this.interactable != null) {
            this.interactable.shape.size = new Vec3(this.interactable.shape.size.x(), this.length * 0.5, this.interactable.shape.size.z());
            this.interactable.shape.recalculateVertices();
            this.interactable.updateShape();
        }
    }

    public float getMaxLength() {
        return this.isTip() ? 0.4375F : 1.0F;
    }

    public void addDestroyEffects() {
        if (this.parent instanceof FairyFruitBlockEntity parent) {
            Level level = this.parent.getLevel();
            if (level == null) return;
            if (interactable == null) return;

            Vec3 size = this.interactable.shape.size;
            double boundX = size.x, boundY = size.y, boundZ = size.z;

            Vec3 segmentPosition = this.getCenter(1.0);
            float particleDensity = 0.1F;
            for (double x = -boundX; x < boundX; x += particleDensity) {
                for (double y = -boundY; y < boundY; y += particleDensity) {
                    for (double z = -boundZ; z < boundZ; z += particleDensity) {
                        Vec3 particlePos = new Vec3(x, y, z);
                        particlePos = this.getOrientation(1.0, FairyFruitBlockEntity.ORIENTATION_FORWARD).transform(particlePos);
                        particlePos = particlePos.add(segmentPosition);

                        parent.getLevel().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, parent.getBlockState()).setPos(parent.getBlockPos()),
                                particlePos.x(), particlePos.y(), particlePos.z(),
                                0, 0, 0);
                    }
                }
            }
            level.playSound(null, MathUtils.blockPosFromVec3(this.getCenter(1.0)), SoundEvents.HANGING_ROOTS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void addBoneMealEffects() {
        if (this.parent instanceof FairyFruitBlockEntity parent) {
            Level level = this.parent.getLevel();
            if (level == null) return;

            Vec3 segmentPosition = this.getCenter(1.0);
            for (int i = 0; i < 15; i++) {
                Vec3 particlePos = new Vec3(level.random.nextGaussian() * 0.25, level.random.nextGaussian() * 0.25, level.random.nextGaussian() * 0.25);
                particlePos = particlePos.add(segmentPosition);

                parent.getLevel().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        0, 0, 0);
            }
            level.playSound(null, MathUtils.blockPosFromVec3(this.getCenter(1.0)), SoundEvents.HANGING_ROOTS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void remove() {
        this.joint1.removeConstraint(this);
        this.joint1.bottomAttachment = null;
        this.joint2.removeConstraint(this);
        this.joint2.topAttachment = null;
        this.getParent().segments.remove(this.index);
    }

    protected void destroy() {
        this.addDestroyEffects();
        this.remove();
        this.interactable.markForRemoval();
        this.getBottomJoint().destroy();
    }

    public FairyFruitBlockEntity getParent() {
        return (FairyFruitBlockEntity) this.parent;
    }

    public FairyFruitJoint getTopJoint() {
        return (FairyFruitJoint) joint1;
    }

    public FairyFruitJoint getBottomJoint() {
        return (FairyFruitJoint) joint2;
    }

    public boolean isTip() {
        return this.getBottomJoint().isTip;
    }



    public CompoundTag serialize() {
        CompoundTag segmentTag = new CompoundTag();

        segmentTag.putInt("topId", this.getTopJoint().index);
        segmentTag.putInt("bottomId", this.getBottomJoint().index);
        segmentTag.putDouble("length", this.length);
        segmentTag.putInt("id", this.index);

        return segmentTag;
    }

    public FairyFruitSegment deserialize(CompoundTag segmentTag) {
        double length = segmentTag.getDouble("length");
        this.length = length;
        return this;
    }

    public static FairyFruitSegment deserialize(FairyFruitBlockEntity parent, CompoundTag segmentTag) {
        return parent.segments.get(segmentTag.getInt("id")).deserialize(segmentTag);
    }

    public static FairyFruitSegment deserializeNew(FairyFruitBlockEntity parent, CompoundTag segmentTag) {
        int topIndex = segmentTag.getInt("topId");
        int bottomIndex = segmentTag.getInt("bottomId");
        double length = segmentTag.getDouble("length");

        FairyFruitSegment segment = new FairyFruitSegment(parent, parent.joints.get(topIndex), parent.joints.get(bottomIndex), length);
        return segment;
    }


}
