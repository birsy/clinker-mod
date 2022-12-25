package birsy.clinker.common.entity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//testing coords = /tp -2001.63 210.65 1609.50
public class SeaHagEntity extends Monster {
    //0 - Front
    //1 - Right
    //2 - Back
    //3 - Left
    private final Vec3[] legPosOffsets = {new Vec3(0.0, 1.53125, -1.09375), new Vec3(-1.09375, 1.53125, 0.0), new Vec3(0.0, 1.53125, 1.09375), new Vec3(1.09375, 1.53125, 0.0)};
    private final Vec3[] footPosOffsets = Util.make(new Vec3[4], (array) -> { for (int i = 0; i < array.length; i++) { array[i] = legPosOffsets[i].subtract(0, 3, 0); }});
    public final float legLength = 1.8125F;
    private Vec3[] footPositions = new Vec3[4];
    private Vec3[] pCurrentFootPositions = new Vec3[4];
    private Vec3[] currentFootPositions = new Vec3[4];

    public final SeaHagPartEntity head;
    public final SeaHagPartEntity bodyTop;
    public final SeaHagPartEntity bodyMiddle;
    public final SeaHagPartEntity bodyBottom;
    private final SeaHagPartEntity[] subEntities;
    private final Map<String, SeaHagPartEntity> parts;
    private final Map<String, Integer> partIds;
    //Offsets of the segments from the BASE of the hitbox
    private final double[] segmentHeights = {5.1, 3.4, 2.0, 0.0};
    //Offsets of the segments from the LAST SEGMENT
    private final double[] segmentOffsets = {1.7, 1.4, 2.0, 0.0};

    public SeaHagEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        for (int leg = 0; leg < 4; leg++) {
            footPositions[leg] = getDesiredFootPosition(leg, 1.0F);
            pCurrentFootPositions[leg] = getDesiredFootPosition(leg, 1.0F);
            currentFootPositions[leg] = getDesiredFootPosition(leg, 1.0F);
        }

        head = new SeaHagPartEntity(this, "head", 1.5F, 1.5F);
        bodyTop = new SeaHagPartEntity(this, "bodyTop", 2.0F, 2.0F);
        bodyMiddle = new SeaHagPartEntity(this, "bodyMiddle", 2.2F, 2.2F);
        bodyBottom = new SeaHagPartEntity(this, "bodyBottom", 2.625F, 2.625F);

        subEntities = new SeaHagPartEntity[]{head, bodyTop, bodyMiddle, bodyBottom};
        parts = new HashMap<>();
        parts.put("head", head);
        parts.put("bodyTop", bodyTop);
        parts.put("bodyMiddle", bodyMiddle);
        parts.put("bodyBottom", bodyBottom);
        partIds = new HashMap<>();
        partIds.put("head", 0);
        partIds.put("bodyTop", 1);
        partIds.put("bodyMiddle", 2);
        partIds.put("bodyBottom", 3);
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
        for (int leg = 0; leg < 4; leg++) {
            pCurrentFootPositions[leg] = currentFootPositions[leg];

            Vec3 legPos = getLegPosition(leg, 1.0F);
            Vec3 footPos = getDesiredFootPosition(leg, 1.0F).subtract(0, 3, 0);
            BlockHitResult raycast = this.getLevel().clip(new ClipContext(legPos, footPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this));
            if (leg == 0) {
                //Clinker.LOGGER.info(footPos);
            }
            footPositions[leg] = raycast.getLocation();
            currentFootPositions[leg] = currentFootPositions[leg].lerp(footPositions[leg], 0.3);
        }

        for (SeaHagPartEntity subEntity : subEntities) {
            subEntity.baseTick();
            subEntity.xOld = subEntity.getX();
            subEntity.yOld = subEntity.getY();
            subEntity.zOld = subEntity.getZ();

            subEntity.collideWithNearbyEntities();
        }

        Vec3 vect = new Vec3(0, 1, 0).normalize();
        float offset = (float) (vect.y() * 0.0F);
        setPartDir("bodyBottom", vect.x(), vect.y(), vect.z() + (offset * 1));
        setPartDir("bodyMiddle", vect.x(), vect.y(), vect.z() + (offset * 2));
        setPartDir("bodyTop",    vect.x(), vect.y(), vect.z() + (offset * 3));
        setPartDir("head",       vect.x(), vect.y(), vect.z() + (offset * 4));
    }

    public boolean hurt(SeaHagPartEntity pPart, DamageSource pSource, float pAmount) {
        return this.hurt(pSource, pAmount);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket entityPacket) {
        super.recreateFromPacket(entityPacket);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return subEntities;
    }

    private void setPartDir(String name, double dirX, double dirY, double dirZ) {
        SeaHagPartEntity part = parts.get(name);
        int partId = partIds.get(name);

        double size = segmentOffsets[partId];
        Vec3 vector = MathUtils.rotateVectorY(new Vec3(dirX, dirY, dirZ).normalize(), (float)Math.toRadians(this.yBodyRot)).scale(size);
        if (partId + 1 < subEntities.length) {
            SeaHagPartEntity previousPart = subEntities[partId + 1];
            part.setPos(previousPart.getX() + vector.x, previousPart.getY() + vector.y, previousPart.getZ() + vector.z);
        } else {
            part.setPos(this.getX() + vector.x, this.getY() + vector.y, this.getZ() + vector.z);
        }
    }

    public Vec3 getLegPosition(int leg, float partialTick) {
        Vec3 legPos = MathUtils.rotateVectorY(legPosOffsets[leg], (float) Math.toRadians(this.yBodyRot));
        Vec3 entityPos = this.getPosition(partialTick);
        return entityPos.add(legPos);
    }
    public Vec3 getDesiredFootPosition(int leg, float partialTick) {
        Vec3 footPos = MathUtils.rotateVectorY(footPosOffsets[leg], (float) Math.toRadians(this.yBodyRot));
        Vec3 entityPos = this.getPosition(partialTick);
        return entityPos.add(footPos);
    }

    public Vec3 getFootPosition(int leg, float partialTicks) {
        return pCurrentFootPositions[leg].lerp(currentFootPositions[leg], partialTicks);
    }
}
