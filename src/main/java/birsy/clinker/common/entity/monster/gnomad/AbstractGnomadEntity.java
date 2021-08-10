package birsy.clinker.common.entity.monster.gnomad;

import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGnomadEntity extends Monster
{
	public int gnomadRank = 0;

	private final double maxSquadDistance = 20.0F;
	private final double squadAddDistance = maxSquadDistance * 0.5F;
	private List<AbstractGnomadEntity> squadron = new ArrayList<>();

	protected AbstractGnomadEntity(EntityType<? extends AbstractGnomadEntity> type, Level worldIn)
	{
		super(type, worldIn);
	}
	
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 25.0F)
				.add(Attributes.MOVEMENT_SPEED, 0.3F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D)
				.add(Attributes.ARMOR, 0.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
	}

	@Override
	public void tick() {
		//Updates the gnomad's squadron.
		squadron.removeIf(gnomad -> this.blockPosition().distManhattan(gnomad.blockPosition()) >= maxSquadDistance);
		if (squadron.size() > 10) {
			for (Entity entity : this.level.getEntitiesOfClass(AbstractGnomadEntity.class, new AABB(this.position().add(squadAddDistance, squadAddDistance, squadAddDistance), this.position().add(-squadAddDistance, -squadAddDistance, -squadAddDistance)))) {
				if (entity instanceof AbstractGnomadEntity && !squadron.contains(entity)) {
					squadron.add((AbstractGnomadEntity) entity);
				}
			}
		}
		super.tick();
	}

	protected Vec3 getSquadCenter() {
		Vec3 squadCenter = new Vec3(this.getX(), this.getY(), this.getZ());

		if (squadron.size() > 1) {
			for (AbstractGnomadEntity gnomad : squadron) {
				squadCenter.add(gnomad.getX(), gnomad.getY(), gnomad.getZ());
			}
		}
		return new Vec3(squadCenter.x() / squadron.size(), squadCenter.y() / squadron.size(), squadCenter.z() / squadron.size());
	}

	//Sounds
	protected SoundEvent getAmbientSound() {
		return ClinkerSounds.ENTITY_GNOME_AMBIENT.get();
	}
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ClinkerSounds.ENTITY_GNOME_HURT.get();
	}
	protected SoundEvent getDeathSound() {
		return ClinkerSounds.ENTITY_GNOME_DEATH.get();
	}

	@Override
	protected float getSoundVolume() {
		if (Minecraft.getInstance().player != null) {
			return MathUtils.mapRange(0, 60, 1, 0, (float) this.distanceToSqr(Minecraft.getInstance().player));
		} else {
			return 1.0f;
		}
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.PIGLIN_STEP, 0.15F, 1.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	public AbstractGnomadEntity.ArmPose getArmPose()
	{
		return AbstractGnomadEntity.ArmPose.NEUTRAL;
	}

	@OnlyIn(Dist.CLIENT)
	public static enum ArmPose {
		NEUTRAL,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		TOSSING,
		THROWN,
		CELEBRATING,
		SHAKING;
	}
}
