package birsy.clinker.common.entity.module.modules;

import birsy.clinker.common.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.common.entity.gnomad.mogul.MogulAttackHandler;
import birsy.clinker.common.entity.module.Module;
import birsy.clinker.common.entity.module.ModuleHolder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

// todo: finish this...
public interface AnimatedAttackHandler<E extends Entity & ModuleHolder<E> & AnimatedAttackHandler<E> & SkeletonParent<E, ?>> {
    byte ANIMATION_FLAG_LABEL = 0b0100_0000,
         ANIMATION_TYPE_MASK =  0b0000_1111,
         ANIMATION_ACTION_MASK =   0b0011_0000,
         ANIMATION_ACTION_PAUSE =  0b0001_0000,
         ANIMATION_ACTION_STOP =   0b0010_0000,
         ANIMATION_ACTION_REWIND = 0b0011_0000;

    AnimatedAttackHandler.AnimatedAttackHandlerModule<E> attackModule();

    class AnimatedAttackHandlerModule<E extends Entity & ModuleHolder<E> & AnimatedAttackHandler<E> & SkeletonParent<E, ?>> extends Module<E> {
        protected AnimatedAttackHandlerModule(E self) {
            super(self);
        }
    }

    record HurtBox<E extends Entity & ModuleHolder<E> & AnimatedAttackHandler<E> & SkeletonParent<E, ?>>
            (Vector3fc offset, float width, float height, int startTick, int endTick, BiConsumer<E, LivingEntity> onHit) {}

    record AttackType<E extends Entity & ModuleHolder<E> & AnimatedAttackHandler<E> & SkeletonParent<E, ?>>
            (int length, ImmutableList<HurtBox> hurtBoxes, BiConsumer<E, AttackInstance> onTick) {}

    class AttackInstance<E extends Entity & ModuleHolder<E> & AnimatedAttackHandler<E> & SkeletonParent<E, ?>> {
        final E entity;
        final AttackType<E> type;
        final long startTime, endTime;

        protected int tickCount = 0;
        private final Vector3d offsetHitboxPos = new Vector3d();

        private AttackInstance(E entity, AttackType<E> type, long startTime, long endTime) {
            this.entity = entity;
            this.type = type;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        protected void start() {
//            if (mogul.level() instanceof ServerLevel server) {
//                server.broadcastEntityEvent(mogul, (byte)(ANIMATION_FLAG_LABEL | type.animationId));
//            }
        }

        protected void tick() {
            tickCount++;

            if (tickCount > type.length) {
                this.stop();
                return;
            }

            for (HurtBox hurtBox : type.hurtBoxes) {
                offsetHitboxPos.set(hurtBox.offset)
                        .rotateY(-entity.getYRot() * Mth.DEG_TO_RAD)
                        .add(entity.getX(), entity.getY(), entity.getZ());
//                AABB aabb = new AABB(
//                        offsetHitboxPos.x - type.hitboxExtents.x(),
//                        offsetHitboxPos.y - 0.5,
//                        offsetHitboxPos.z - type.hitboxExtents.x(),
//                        offsetHitboxPos.x + type.hitboxExtents.x(),
//                        offsetHitboxPos.y + type.hitboxExtents.y() * 2 + 0.5,
//                        offsetHitboxPos.z + type.hitboxExtents.x()
//                );
//
//                List<LivingEntity> hits = EntityRetrievalUtil.getEntities(mogul.level(), aabb,
//                        (otherEntity) -> otherEntity instanceof LivingEntity);
//                for (LivingEntity hit : hits) {
//                    if (hit == mogul) continue;
//                    if (mogul.canAttack(hit)) type.onHit.accept(mogul, hit);
//                }
            }
        }

        protected void stop() {
        }
    }
}
