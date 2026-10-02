package birsy.clinker.common.entity.module.modules;

import birsy.clinker.common.entity.module.Module;
import birsy.clinker.common.entity.module.ModuleHolder;
import birsy.clinker.core.Clinker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public interface Sitter<E extends LivingEntity & ModuleHolder<E> & Sitter<E>> {
    Sitter.SitterModule<E> sitModule();

    class SitterModule<E extends LivingEntity & ModuleHolder<E> & Sitter<E>> extends Module<E> {
        private static final int NOT_SITTING = -1;

        private final int poseCount;
        private final EntityDataAccessor<Integer> sitPoseData;
        private static final AttributeModifier SPEED_MODIFIER_SITTING = new AttributeModifier(
                Clinker.resource("sitting"), -1000.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        public SitterModule(E self, EntityDataAccessor<Integer> sitPoseData, int poseCount) {
            super(self);
            this.sitPoseData = sitPoseData;
            this.poseCount = poseCount;
        }

        // sometimes we can sit in a different pose. just leave this for the animation driver to handle...
        public int getSitPose() {
            return self.getEntityData().get(sitPoseData);
        }

        public boolean isSitting() {
            return getSitPose() != NOT_SITTING;
        }

        public void setSitting(boolean sitting) {
            int pose = sitting ? self.getRandom().nextInt(poseCount) : NOT_SITTING;
            self.getEntityData().set(sitPoseData, pose);

            // can't move when sitting!
            AttributeInstance speedAttribute = self.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttribute != null) {
                if (sitting) speedAttribute.addTransientModifier(SPEED_MODIFIER_SITTING);
                else speedAttribute.removeModifier(SPEED_MODIFIER_SITTING);
            }
        }

        @Override
        public void defineSyncedData(SynchedEntityData.Builder builder) {
            builder.define(sitPoseData, NOT_SITTING);
        }
        @Override
        public void save(CompoundTag tag) {
            tag.putInt("SitPose", this.getSitPose());
        }
        @Override
        public void load(CompoundTag tag) {
            self.getEntityData().set(sitPoseData, tag.getInt("SitPose"));
        }
        @Override
        public boolean isPushable() {
            return !this.isSitting();
        }
    }
}
