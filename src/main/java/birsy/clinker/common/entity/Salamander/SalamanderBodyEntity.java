package birsy.clinker.common.entity.Salamander;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;

public class SalamanderBodyEntity extends AbstractSalamanderPartEntity {
    public SalamanderHeadEntity head;

    public SalamanderBodyEntity(EntityType<? extends AbstractSalamanderPartEntity> entity, Level level) {
        super(entity, level);
    }

    @Override
    protected boolean shouldDropExperience() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if ((head == null || head.isDeadOrDying()) && !this.isNoAi()) {
            this.hurt(DamageSource.STARVE, Float.MAX_VALUE);
        }
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        boolean isHeadAlive = !((head == null || head.isDeadOrDying()) && !this.isNoAi());
        if (isHeadAlive) {
            head.split(this.getSegmentID() - 1);
        }
    }

    @Override
    public boolean isSteppingCarefully() {
        if (ahead != null) {
            if (!ahead.isOnGround() && this.hasLegs() && this.isOnGround()) {
                return true;
            }
        }
        if (behind != null) {
            if (!ahead.isOnGround() && this.hasLegs() && this.isOnGround()) {
                return true;
            }
        }

        return super.isSteppingCarefully();
    }

    public SalamanderHeadEntity turnToHead() {
        //creates a head to replace this entity
        SalamanderHeadEntity newHead = ClinkerEntities.SALAMANDER_HEAD.get().create(this.level);

        //transfers all nbt data from this entity to the head
        //probably a more efficient way to do this but idkkkk
        CompoundTag transferTag = new CompoundTag();
        this.saveWithoutId(transferTag);
        newHead.load(transferTag);
        //set new link relationships
        if (this.behind != null) {
            newHead.behind = this.behind;
            this.behind.ahead = newHead;
        }
        newHead.setDecaptiatedStump(true);

        //yeets this entity
        this.discard();

        return newHead;
    }

    @Override
    public void handleNetherPortal() {
        if (this.head != null) {
            this.head.handleNetherPortal();
        }
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level, ITeleporter teleporter) {
        if (this.head != null) {
            return this.head.changeDimension(level, teleporter);
        } else {
            return super.changeDimension(level, teleporter);
        }
    }

    public Entity changeDimensionBody(ServerLevel level, ITeleporter teleporter) {
        return super.changeDimension(level, teleporter);
    }

    @Override
    public void setPortalCooldown() {
        if (this.head != null) {
            this.head.setPortalCooldown();
        } else {
            super.setPortalCooldown();
        }
    }

    public void setPortalCooldownBody() {
        super.setPortalCooldown();
    }

    @Override
    public boolean hasCustomName() {
        if (this.head != null) {
            return head.hasCustomName();
        }

        return false;
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        if (this.head != null) {
            head.setCustomName(pName);
        }
    }

    @Override
    public void setCustomNameVisible(boolean pAlwaysRenderNameTag) {
        if (this.head != null) {
            head.setCustomNameVisible(pAlwaysRenderNameTag);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public int decreaseAirSupply(int pAir) {
        return pAir;
    }

    @Override
    public boolean onClimbable() {
        return this.hasLegs() || this.isInWater() || super.onClimbable();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.head != null) {
            boolean hurtHead = this.head.hurtFromBody(this, this.getSegmentID(), pSource, pAmount * 0.25F);
        }
        return super.hurt(pSource, pAmount);
    }

    public boolean hurtFromHead(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }
    public boolean is(Entity pEntity) { return this == pEntity || this.head == pEntity; }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        if (hasLegs()) {
            super.playStepSound(pPos, pBlock);
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
