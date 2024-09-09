package birsy.clinker.common.world.entity;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.common.world.entity.rope.RopeEntitySegment;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

public class TestRopeEntity extends RopeEntity<RopeEntitySegment> {
    public enum SegmentType {
        HEAD(0), BODY(1), BODY_LEGS(2), TAIL_START(3), TAIL_MIDDLE(4), TAIL_END(5);
        private final int index;
        private static final SegmentType[] fromIndex = SegmentType.values();
        SegmentType(int index) { this.index = index; }
        public int index() { return this.index; }
        public static SegmentType fromIndex(int index) { return fromIndex[index]; }
    }

    public TestRopeEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!pLevel.isClientSide) {
            int segmentCount = 18;
            this.addSegmentToEnd(SegmentType.HEAD.index(), false);
            for (int i = 0; i < segmentCount - 4; i++) {
                if (i % 3 == 0) {
                    this.addSegmentToEnd(SegmentType.BODY_LEGS.index(), false);
                } else {
                    this.addSegmentToEnd(SegmentType.BODY.index(), false);
                }
            }
            this.addSegmentToEnd(SegmentType.TAIL_START.index(), false);
            this.addSegmentToEnd(SegmentType.TAIL_MIDDLE.index(), false);
            this.addSegmentToEnd(SegmentType.TAIL_END.index(), false);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.debugMove();
    }

    private void debugMove() {
        float maxSpeed = 2.0F;
        Player target = EntityRetrievalUtil.getNearestEntity(this, 40.0F, (entity -> entity instanceof Player));

        if (target == null) {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
            return;
        }

        // approach player
        if (target.getMainHandItem().is(Items.CARROT_ON_A_STICK)) {
            this.moveTowardsPosition(target.getX(), target.getY() + target.getEyeHeight(), target.getZ(), maxSpeed, 4);
            return;
        }
        // approach point
        if (target.getMainHandItem().is(Items.ARROW)) {
            BlockHitResult result = this.level().clip(new ClipContext(
                    target.getEyePosition(),
                    target.getEyePosition().add(target.getLookAngle().scale(40.0F)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    CollisionContext.empty()
            ));
            if (result.getType() == HitResult.Type.BLOCK) {
                Vec3i normal = result.getDirection().getNormal();
                float radius = this.segments.get(0).radius;
                this.moveTowardsPosition(result.getLocation().x() + normal.getX()*radius, result.getLocation().y() + normal.getY()*radius, result.getLocation().z() + normal.getZ()*radius, maxSpeed, 1.5);
                return;
            }
        }
        // strafe
        if (target.getMainHandItem().is(Items.STRING)) {
            Vec3 targetPos = target.getEyePosition();
            this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            //Mth.cos(this.tickCount * 0.08F)
            this.getMoveControl().strafe(1.2F, Mth.clamp(Mth.sin(this.tickCount * 0.008F) * 10, -1, 1) * 1.5F);
        } else {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
        }

    }
    private void moveTowardsPosition(double x, double y, double z, double maxSpeed, double completionRadius) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
        if (this.position().distanceToSqr(x, y, z) > completionRadius*completionRadius) {
            //this.getNavigation().moveTo(x, y, z, maxSpeed);
            this.getMoveControl().setWantedPosition(x, y, z, maxSpeed);
        } else {
            //this.getNavigation().stop();
            this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        float z = 0;
        for (RopeEntitySegment segment : this.segments) {
            segment.setInitialPosition(this.getX(), this.getY() + segment.radius, this.getZ() + z);
            z += segment.length;
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected RopeEntitySegment createSegment(int segmentType, int index) {
        SegmentType type = SegmentType.fromIndex(segmentType);
        return switch (type) {
            case HEAD, BODY, BODY_LEGS -> new RopeEntitySegment(this, segmentType, 0.5F, 1.0F, index);
            case TAIL_START -> new RopeEntitySegment(this, segmentType, 0.5F * 0.85F, 1.0F, index);
            case TAIL_MIDDLE -> new RopeEntitySegment(this, segmentType, 4.0F / 16.0F, 1.0F, index);
            case TAIL_END -> new RopeEntitySegment(this, segmentType, 2.5F / 16.0F, 1.0F, index);
        };
    }
}
