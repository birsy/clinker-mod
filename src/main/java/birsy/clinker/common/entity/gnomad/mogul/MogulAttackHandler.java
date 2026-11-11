package birsy.clinker.common.entity.gnomad.mogul;

import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulAnimator;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MogulAttackHandler {
    private static final byte
            ANIMATION_FLAG_LABEL = 0b0100_0000,
            ANIMATION_TYPE_MASK = 0b0000_1111,
            ANIMATION_ACTION_MASK =   0b0011_0000,
            ANIMATION_ACTION_PAUSE =  0b0001_0000,
            ANIMATION_ACTION_STOP =   0b0010_0000,
            ANIMATION_ACTION_REWIND = 0b0011_0000;

    public static final MogulAttackType SWING_UP = new MogulAttackType((byte) 0)
            .windupLength(16).swingLength(16).recoveryLength(20)
            .hitboxTick(16).hitboxOffset(0, 0, 3F).hitboxExtents(0.8F, 1F)
            .hitFunction((mogul, entity) -> mogul.attack(entity, 4, 0, 0.5F, 1));
    public static final MogulAttackType SWING_LEFT = new MogulAttackType((byte) 1)
            .windupLength(16).swingLength(16).recoveryLength(16)
            .hitboxTick(23).hitboxOffset(0, 1, 2.6F).hitboxExtents(1.2F, 0.1F)
            .hitFunction((mogul, entity) -> mogul.attack(entity, 4,  1.5F, 0.0F, 0.2F));
    public static final MogulAttackType SWING_RIGHT = new MogulAttackType((byte) 2)
            .windupLength(20).swingLength(10).recoveryLength(20)
            .hitboxTick(20).hitboxOffset(0, 1, 2.6F).hitboxExtents(1.1F, 0.1F)
            .hitFunction((mogul, entity) -> mogul.attack(entity, 4, -1.5F, 0.1F, 0.2F));

    private final GnomadMogulEntity mogul;
    private MogulAttack activeAttack = null;

    public MogulAttackHandler(GnomadMogulEntity mogul) {
        this.mogul = mogul;
    }

    public void tick() {
        if (activeAttack != null) activeAttack.update();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleAnimationEvent(byte event, GnomadMogulAnimator animator) {
        if ((event & ANIMATION_FLAG_LABEL) == 0) return;

        Animator.TimedAnimationEntry<?, ?> entry = null;
        byte animationId = (byte) (event & ANIMATION_TYPE_MASK);
        // todo: make this more generic somehow
        if (animationId == SWING_UP.animationId) { entry = animator.upSwingAnim; }
        else if (animationId == SWING_LEFT.animationId) { entry = animator.leftSwingAnim; }
        else if (animationId == SWING_RIGHT.animationId) { entry = animator.rightSwingAnim; }

        if (entry == null) return;

        byte action = (byte) (event & ANIMATION_ACTION_MASK);
        if (action == 0) entry.begin();
        else if (action == ANIMATION_ACTION_PAUSE) entry.stop();
        else if (action == ANIMATION_ACTION_STOP) entry.stop();
        else if (action == ANIMATION_ACTION_REWIND) entry.rewind();
    }

    public void beginAttack(MogulAttackType attackType) {
        if (activeAttack != null) activeAttack.cancel();
        activeAttack = new MogulAttack(this, attackType);
        activeAttack.start();
    }
    public void cancelAttack() {
        if (activeAttack != null) activeAttack.cancel();
    }
    public float getWalkSpeedMultiplier() {
        if (activeAttack == null) return 1.0F;
        if (activeAttack.isWindingUp()) return 0.5F;
        if (activeAttack.isSwinging()) return 0.1F;
        return 0.8F;
    }
    public boolean isAttacking() {
        if (activeAttack == null) return false;
        return !activeAttack.isRecovering();
    }

    public static class MogulAttackType {
        public final byte animationId;
        public int windupLength = 16, swingLength = 16, recoveryLength = 16, hitboxTick = 20, length = 48;
        public Vector3f hitboxOffset = new Vector3f(0, 1, 2);
        public Vector2f hitboxExtents = new Vector2f(1,1);
        public BiConsumer<GnomadMogulEntity, LivingEntity> onHit;

        protected MogulAttackType(byte animationId) {
            this.animationId = animationId;
        }

        public MogulAttackType windupLength(int windupLength) {
            this.windupLength = windupLength;
            this.length = windupLength + swingLength + recoveryLength;
            return this;
        }
        public MogulAttackType swingLength(int swingLength) {
            this.swingLength = swingLength;
            this.length = windupLength + swingLength + recoveryLength;
            return this;
        }
        public MogulAttackType recoveryLength(int recoveryLength) {
            this.recoveryLength = recoveryLength;
            this.length = windupLength + swingLength + recoveryLength;
            return this;
        }
        public MogulAttackType hitboxTick(int hitboxTick) {
            this.hitboxTick = hitboxTick;
            return this;
        }
        public MogulAttackType hitFunction(BiConsumer<GnomadMogulEntity, LivingEntity> onHit) {
            this.onHit = onHit;
            return this;
        }
        public MogulAttackType hitboxOffset(float x, float y, float z) {
            this.hitboxOffset.set(x, y, z);
            return this;
        }
        public MogulAttackType hitboxExtents(float horizontal, float vertical) {
            this.hitboxExtents.set(horizontal, vertical);
            return this;
        }
    }

    protected static class MogulAttack {
        final GnomadMogulEntity mogul;
        final MogulAttackHandler handler;
        final MogulAttackType type;
        protected int tickCount = 0;

        protected MogulAttack(MogulAttackHandler handler, MogulAttackType type) {
            this.handler = handler;
            this.mogul = handler.mogul;
            this.type = type;
        }

        protected void start() {
            if (mogul.level() instanceof ServerLevel server) {
                server.broadcastEntityEvent(mogul, (byte)(ANIMATION_FLAG_LABEL | type.animationId));
            }
        }
        protected void cancel() {
            if (mogul.level() instanceof ServerLevel server) {
                server.broadcastEntityEvent(mogul, (byte) (ANIMATION_FLAG_LABEL | ANIMATION_ACTION_STOP | type.animationId));
            }
            this.handler.activeAttack = null;
        }

        protected boolean isWindingUp() {
            return tickCount < type.windupLength;
        }
        protected boolean isSwinging() {
            return tickCount >= type.windupLength && tickCount < (type.windupLength + type.swingLength);
        }
        protected boolean isRecovering() {
            return tickCount >= (type.windupLength + type.swingLength);
        }

        private final Vector3d offsetHitboxPos = new Vector3d();
        protected void update() {
            tickCount++;
            if (tickCount == type.hitboxTick) {
                offsetHitboxPos.set(type.hitboxOffset)
                        .rotateY(-mogul.yBodyRot * Mth.DEG_TO_RAD)
                        .add(mogul.getX(), mogul.getY(), mogul.getZ());
                AABB aabb = new AABB(
                        offsetHitboxPos.x - type.hitboxExtents.x(),
                        offsetHitboxPos.y - 0.5,
                        offsetHitboxPos.z - type.hitboxExtents.x(),
                        offsetHitboxPos.x + type.hitboxExtents.x(),
                        offsetHitboxPos.y + type.hitboxExtents.y() * 2 + 0.5,
                        offsetHitboxPos.z + type.hitboxExtents.x());
                List<LivingEntity> hits = EntityRetrievalUtil.getEntities(mogul.level(), aabb,
                        (otherEntity) -> otherEntity instanceof LivingEntity);
                for (LivingEntity hit : hits) {
                    if (hit == mogul) continue;
                    if (mogul.canAttack(hit)) type.onHit.accept(mogul, hit);
                }
            }
            if (tickCount > type.length) {
                this.cancel();
            }
        }
    }
}
