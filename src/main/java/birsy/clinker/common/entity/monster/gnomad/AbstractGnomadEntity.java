package birsy.clinker.common.entity.monster.gnomad;

import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGnomadEntity extends MonsterEntity
{
	public int gnomadRank = 0;

	private final double maxSquadDistance = 20.0F;
	private final double squadAddDistance = maxSquadDistance * 0.5F;
	private List<AbstractGnomadEntity> squadron = new ArrayList<>();

	protected AbstractGnomadEntity(EntityType<? extends AbstractGnomadEntity> type, World worldIn)
	{
		super(type, worldIn);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 25.0F)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
				.createMutableAttribute(Attributes.ARMOR, 0.0D)
				.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
	}

	@Override
	public void tick() {
		//Updates the gnomad's squadron.
		squadron.removeIf(gnomad -> this.getPosition().manhattanDistance(gnomad.getPosition()) >= maxSquadDistance);
		if (squadron.size() > 10) {
			for (Entity entity : this.world.getEntitiesWithinAABB(AbstractGnomadEntity.class, new AxisAlignedBB(this.getPositionVec().add(squadAddDistance, squadAddDistance, squadAddDistance), this.getPositionVec().add(-squadAddDistance, -squadAddDistance, -squadAddDistance)))) {
				if (entity instanceof AbstractGnomadEntity && !squadron.contains(entity)) {
					squadron.add((AbstractGnomadEntity) entity);
				}
			}
		}
		super.tick();
	}

	protected Vector3d getSquadCenter() {
		Vector3d squadCenter = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

		if (squadron.size() > 1) {
			for (AbstractGnomadEntity gnomad : squadron) {
				squadCenter.add(gnomad.getPosX(), gnomad.getPosY(), gnomad.getPosZ());
			}
		}
		return new Vector3d(squadCenter.getX() / squadron.size(), squadCenter.getY() / squadron.size(), squadCenter.getZ() / squadron.size());
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
			return MathUtils.mapRange(0, 60, 1, 0, (float) this.getDistanceSq(Minecraft.getInstance().player));
		} else {
			return 1.0f;
		}
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15F, 1.0F);
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
