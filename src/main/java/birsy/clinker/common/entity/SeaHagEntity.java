package birsy.clinker.common.entity;

import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SeaHagEntity extends Monster {
    //0 - Front
    //1 - Right
    //2 - Back
    //3 - Left
    private final Vec3[] legPosOffsets = {new Vec3(0.0, 1.53125, -1.09375), new Vec3(-1.09375, 1.53125, 0.0), new Vec3(0.0, 1.53125, 1.09375), new Vec3(1.09375, 1.53125, 0.0)};
    private final Vec3[] footPosOffsets = Util.make(new Vec3[4], (array) -> { for (int i = 0; i < array.length; i++) { array[i] = legPosOffsets[i].multiply(1.5, 0.0, 1.5); }});
    public final float legLength = 1.8125F;
    private Vec3[] feetPositions = new Vec3[4];

    public SeaHagEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        for (int leg = 0; leg < 4; leg++) {
            feetPositions[leg] = getDesiredFootPosition(leg, 1.0F);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.01F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();
        this.yBodyRot = 1.0f;
        this.yBodyRotO = 1.0f;
        for (int leg = 0; leg < 4; leg++) {
            if (getLegPosition(leg, 1.0F).distanceTo(feetPositions[leg]) > legLength * 1.5) {
                updateFootPosition(leg, 1.0F);
            }
        }
    }

    public Vec3 getLegPosition(int leg, float partialTick) {
        Vec3 legPos = legPosOffsets[leg].yRot(Mth.lerp(this.yBodyRotO, this.yBodyRot, partialTick));
        Vec3 entityPos = this.position();
        return entityPos.add(legPos);
    }
    public Vec3 getDesiredFootPosition(int leg, float partialTick) {
        Vec3 legPos = footPosOffsets[leg].yRot(Mth.lerp(this.yBodyRotO, this.yBodyRot, partialTick));
        Vec3 entityPos = this.position();
        return entityPos.add(legPos);
    }

    public void updateFootPosition(int leg, float partialTicks) {
        Vec3 footPosition = getDesiredFootPosition(leg, partialTicks);
        feetPositions[leg] = footPosition;
    }

    public Vec3 getFootPosition(int leg, float partialTicks) {
        return feetPositions[leg];
    }

    private Vec3 rayCast(ClipBlockStateContext pContext) {
        return BlockGetter.traverseBlocks(pContext.getFrom(), pContext.getTo(), pContext, (context, endPos) -> {
            BlockState blockstate = this.level.getBlockState(endPos);
            Vec3 vec3 = context.getFrom().subtract(context.getTo());
            return context.isTargetBlock().test(blockstate) ? new Vec3(vec3.x, vec3.y, vec3.z) : null;
        }, (context) -> {
            Vec3 vec3 = context.getFrom().subtract(context.getTo());
            return new Vec3(vec3.x, vec3.y, vec3.z);
        });
    }
}
