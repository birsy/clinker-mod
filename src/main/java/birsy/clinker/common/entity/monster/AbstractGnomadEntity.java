package birsy.clinker.common.entity.monster;

import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractGnomadEntity extends MonsterEntity
{
	public int gnomadRank = 0;
	
	protected AbstractGnomadEntity(EntityType<? extends AbstractGnomadEntity> type, World worldIn)
	{
		super(type, worldIn);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 25.0F)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}

	@Override
	public void tick() {

		super.tick();
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
