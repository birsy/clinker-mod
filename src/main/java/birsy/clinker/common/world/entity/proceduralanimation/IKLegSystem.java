package birsy.clinker.common.world.entity.proceduralanimation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IKLegSystem<E extends LivingEntity & IKLocomotionEntity> {
    final E entity;
    final IKLeg[] legs;

    protected IKLegSystem(E entity, IKLeg[] legs) {
        this.entity = entity;
        this.legs = legs;
    }

    public void update() {
        if (!this.entity.level().isClientSide()) {
            for (int i = 0; i < legs.length; i++) {
                if (legs[i].refreshTimer > 0) legs[i].refreshTimer--;
            }
        }
        for (int i = 0; i < legs.length; i++) {
            legs[i].update(this.entity, this.entity.level());
        }
    }

    public IKLeg getLeg(int legIndex) {
        return legs[legIndex];
    }

    public void setAttachmentDirection(Vector3d direction) {
        for (IKLeg leg : this.legs) {
            leg.setAttachmentDirection(direction.x, direction.y, direction.z);
        }
    }

    public static class SymmetricalBuilder {
        float defaultGait = 1F / (20F * 0.35F);
        float defaultStepSize = 0.75F;
        float defaultStillStepSize = 0.2F;

        float defaultXBaseOffset = 0.5F;
        float defaultYBaseOffset = 1.0F;

        float defaultXRestOffset = 1.0F;
        float defaultZRestOffset = 0.0F;

        List<LegBuilder> legs = new ArrayList<>();

        private SymmetricalBuilder() {}

        public static SymmetricalBuilder create() {
            return new SymmetricalBuilder();
        }

        public SymmetricalBuilder stepSize(float num) {
            this.defaultStepSize = num;
            return this;
        }

        public SymmetricalBuilder stillStepSize(float num) {
            this.defaultStillStepSize = num;
            return this;
        }

        public SymmetricalBuilder gait(float num) {
            this.defaultGait = num;
            return this;
        }

        public SymmetricalBuilder baseOffset(float x, float y) {
            this.defaultXBaseOffset = x;
            this.defaultYBaseOffset = y;
            return this;
        }

        public SymmetricalBuilder restOffset(float x, float y) {
            this.defaultXRestOffset = x;
            this.defaultZRestOffset = y;
            return this;
        }

        public SymmetricalBuilder addLeg(float zBaseOffset) {
            return this.addLeg(zBaseOffset, defaultXRestOffset, defaultZRestOffset);
        }

        public SymmetricalBuilder addLeg(float zBaseOffset, float xRestPosOffset, float zRestPosOffset) {
            return this.addLeg(defaultXBaseOffset, defaultYBaseOffset, zBaseOffset, xRestPosOffset, zRestPosOffset);
        }

        public SymmetricalBuilder addLeg(float xBaseOffset, float yBaseOffset, float zBaseOffset, float xRestPosOffset, float zRestPosOffset) {
            this.legs.add(new LegBuilder(xBaseOffset, yBaseOffset, zBaseOffset, xRestPosOffset, zRestPosOffset, defaultGait, defaultStepSize, defaultStillStepSize));
            return this;
        }

        public SymmetricalBuilder addLeg(float xBaseOffset, float yBaseOffset, float zBaseOffset, float xRestPosOffset, float zRestPosOffset, float gait, float stepSize, float stillStepSize) {
            this.legs.add(new LegBuilder(xBaseOffset, yBaseOffset, zBaseOffset, xRestPosOffset, zRestPosOffset, gait, stepSize, stillStepSize));
            return this;
        }

        public <E extends LivingEntity & IKLocomotionEntity> IKLegSystem  build(E entity) {
            IKLeg[] iklegs = new IKLeg[legs.size() * 2];

            // sort the legs based off their local z position
            this.legs.sort(Comparator.comparingDouble((leg) -> leg.zBaseOffset));

            // create the legs
            for (int i = 0; i < this.legs.size(); i++) {
                LegBuilder legBuilder = this.legs.get(i);

                IKLeg leg0 = new IKLeg<>(i * 2 + 0)
                    .setBaseOffset(legBuilder.xBaseOffset, legBuilder.yBaseOffset, legBuilder.zBaseOffset)
                    .setRestPosOffset(legBuilder.xRestPosOffset, 0, legBuilder.zBaseOffset)
                    .setGait(legBuilder.gait)
                    .setStepSize(legBuilder.stepSize)
                    .setStepSize(legBuilder.stillStepSize);

                IKLeg leg1 = new IKLeg<>(i * 2 + 1)
                        .setBaseOffset(-legBuilder.xBaseOffset, legBuilder.yBaseOffset, legBuilder.zBaseOffset)
                        .setRestPosOffset(-legBuilder.xRestPosOffset, 0, legBuilder.zBaseOffset)
                        .setGait(legBuilder.gait)
                        .setStepSize(legBuilder.stepSize)
                        .setStepSize(legBuilder.stillStepSize);

                iklegs[i * 2 + 0] = leg0;
                iklegs[i * 2 + 1] = leg1;
            }

            // assign leg adjacencies
            for (int i = 0; i < this.legs.size(); i++) {
                int legIndex = i * 2;
                if (i > 0) {
                    int pLegIndex = (i-1) * 2;
                    iklegs[legIndex + 0].addAdjacentLegs(iklegs[pLegIndex + 0]);
                    iklegs[legIndex + 1].addAdjacentLegs(iklegs[pLegIndex + 1]);
                }
                if (i < this.legs.size() - 1) {
                    int nLegIndex = (i+1) * 2;
                    iklegs[legIndex + 0].addAdjacentLegs(iklegs[nLegIndex + 0]);
                    iklegs[legIndex + 1].addAdjacentLegs(iklegs[nLegIndex + 1]);
                }

                iklegs[legIndex + 0].addAdjacentLegs(iklegs[legIndex + 1]);
                iklegs[legIndex + 1].addAdjacentLegs(iklegs[legIndex + 0]);
            }

            IKLegSystem system = new IKLegSystem(entity, iklegs);
            return system;
        }
    }

    private record LegBuilder(float xBaseOffset, float yBaseOffset, float zBaseOffset, float xRestPosOffset, float zRestPosOffset, float gait, float stepSize, float stillStepSize) {}
}
