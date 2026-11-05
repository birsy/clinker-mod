package birsy.clinker.common.entity.lungwyrm;

import birsy.clinker.common.entity.SegmentedEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class LungWyrmEntity extends SegmentedEntity<LungWyrmEntity, LungWyrmEntity.LungWyrmJoint> implements Enemy {
    public LungWyrmEntity(EntityType<? extends SegmentedEntity<LungWyrmEntity, LungWyrmJoint>> entityType, Level level) {
        super(entityType, level, 32);
        this.moveControl = new LungWyrmMoveControl(this);
    }

    @Override
    protected LungWyrmJoint createJoint(int index, int length) {
        float scale = Mth.clampedMap((float) index / length, 0, 1, 1, 0.3F);
        return new LungWyrmJoint(this, this.parts[index], index, scale);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data) {
        data = super.finalizeSpawn(level, difficulty, reason, data);
        int count = maxJointCount;//level.getRandom().nextInt(maxJointCount / 2, maxJointCount);
        Vector3d pos = new Vector3d(this.getX(), this.getY(), this.getZ()), offset = new Vector3d();
        for (int i = 0; i < count; i++) {
            LungWyrmJoint joint = createJoint(i, count);
            float length = joint.length;
            float fac = Mth.clampedMap((float) i / count, 0.0F, 0.1F, 0.0F, 1.0F);

            joint.x = pos.x();
            joint.y = pos.y();
            joint.z = pos.z();
            this.addJoint(joint);

            offset.set(0, 0, 0.5)
                  .add(Math.sin(i * 0.5) * fac, Math.cos(i * 0.5) * fac, 0)
                  .normalize(length);
            pos.add(offset);
        }
        return data;
    }

    private void updateDebugMovement() {
        Player player = EntityRetrievalUtil.getNearestPlayer(this, 200F);
        if (player == null) return;
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.CARROT_ON_A_STICK)) return;
        this.moveControl.setWantedPosition(player.getX(), player.getY(), player.getZ(), 0.1F);
    }

    @Override
    protected void customServerAiStep() {
        updateDebugMovement();
        super.customServerAiStep();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ELDER_GUARDIAN_HURT;
    }
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ELDER_GUARDIAN_DEATH;
    }
    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(sound, volume * 5.0F, pitch);
    }

    @Override
    public LungWyrmMoveControl getMoveControl() {
        return (LungWyrmMoveControl) super.getMoveControl();
    }

    public static class LungWyrmJoint extends Joint<LungWyrmEntity, LungWyrmEntity.LungWyrmJoint> {
        protected LungWyrmJoint(LungWyrmEntity parent, SegmentPartEntity part, int index, float scale) {
            super(parent, part, index, 4F * scale, 4F * scale, 4F);
        }

        @Override
        public void tick() {
            super.tick();

        }
    }
}
