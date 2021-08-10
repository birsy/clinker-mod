package birsy.clinker.common.entity.monster.gnomad;

import java.util.List;
import java.util.function.Predicate;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.HyenaEntity;
import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

@SuppressWarnings("unused")
public class GnomadShamanEntity extends AbstractGnomadEntity
{	
	private int spellDelay;
	private int spellTicks;
	public boolean isSpellcasting;
	private int interuptionTicks;
	private float spellcastFactors;

	public AbstractGnomadEntity.ArmPose armPose;
	public GnomadShamanEntity.SpellType spellType;
	
	public static final Predicate<? super Entity> SUPPORT_ENTITIES = (entity) -> {
		EntityType<?> entitytype = entity.getType();
		return entitytype == ClinkerEntities.GNOMAD_AXEMAN.get();
	};
	
	public static final Predicate<? super Entity> TARGET_ENTITIES = (entity) -> {
		EntityType<?> entitytype = entity.getType();
		return entitytype == ClinkerEntities.GNOME.get() || entitytype == ClinkerEntities.GNOME_BRAT.get() || entitytype == ClinkerEntities.HYENA.get() || entitytype == EntityType.WITHER_SKELETON || entitytype == EntityType.ENDERMAN || entitytype == EntityType.PLAYER;
	};
	
	public GnomadShamanEntity(EntityType<? extends AbstractGnomadEntity> type, Level worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
	    this.getNavigation().setCanFloat(true);
	    this.setCanPickUpLoot(true);
	    this.gnomadRank = 1;
	}	
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers(AbstractGnomadEntity.class));
		this.goalSelector.addGoal(3, new GnomadShamanEntity.BuffGnomadsGoal(this));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, GnomeEntity.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EnderMan.class, true));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, GnomeEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Mob.class, 8.0F));
	}

	@Override
	public void tick() {
		if (interuptionTicks > 0) {
			interuptionTicks--;
		}

		if (spellDelay > 0) {
			spellDelay--;
		}

		super.tick();

		//LOGGER.info(this.getEntityId() + " says that there are " + getNearbyGnomads(4.0D, 2.0D, 4.0D).size() + " gnomads around it!");
	}

	@Override
	public boolean skipAttackInteraction(Entity entityIn) {
		this.interuptionTicks = 5;
		return super.skipAttackInteraction(entityIn);
	}

	public class BuffGnomadsGoal extends SpellcastGoal {
		private final GnomadShamanEntity gnomad;

		public BuffGnomadsGoal(GnomadShamanEntity gnomadIn) {
			super(gnomadIn, SpellType.BUFF, 25, true);
			this.gnomad = gnomadIn;
		}

		@Override
		boolean spellcastPredicate() {
			if (gnomad.getTarget() != null) {
				return gnomad.getNearbyGnomads(4.0D, 2.0D, 4.0D).size() > 2 && gnomad.distanceToSqr(gnomad.getTarget()) > 3;
			} else {
				return gnomad.getNearbyGnomads(4.0D, 2.0D, 4.0D).size() > 2;
			}
		}

		@Override
		void spell() {
			List<AbstractGnomadEntity> list = getNearbyGnomads(4.0D, 2.0D, 4.0D);
			if (!list.isEmpty()) {
				for (LivingEntity livingentity : list) {
					if (livingentity instanceof GnomadAxemanEntity) {
						LOGGER.info(gnomad.getId() + " has cast a " + spellType.toString() + " spell!");
						if (random.nextInt(1) == 0) {
							if (((GnomadAxemanEntity) livingentity).getShieldNumber() > 1 && ((GnomadAxemanEntity) livingentity).getShieldNumber() < 2) {
								((GnomadAxemanEntity) livingentity).setShielded(((GnomadAxemanEntity) livingentity).getShieldNumber() + 2);
								livingentity.getEntityData().set(GnomadAxemanEntity.SHIELDS, ((GnomadAxemanEntity) livingentity).getShieldNumber() + 2);
							}
							((GnomadAxemanEntity) livingentity).setShielded(4);
						} else {
							((GnomadAxemanEntity) livingentity).setBuffed(true);
							livingentity.getEntityData().set(GnomadAxemanEntity.BUFFED, true);
						}
					}
				}
			}
		}
	}

	public abstract class SpellcastGoal extends Goal {
		private final GnomadShamanEntity gnomad;
		private final SpellType spellType;
		private final int castingTime;
		private boolean interuptable;

		public SpellcastGoal(GnomadShamanEntity gnomadIn, SpellType spellTypeIn, int castingTimeIn, boolean interuptableIn) {
			this.gnomad = gnomadIn;
			this.spellType = spellTypeIn;
			this.castingTime = castingTimeIn;
			this.interuptable = interuptableIn;
		}

		@Override
		public boolean canUse() {
			if (gnomad.isAlive() && spellDelay == 0) {
				return spellcastPredicate();
			} else {
				return spellTicks == castingTime + 1;
			}
		}

		@Override
		public void start() {
			gnomad.spellType = spellType;
			gnomad.isSpellcasting = true;
			gnomad.spellTicks = 0;
			gnomad.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
			this.spell();
		}

		@Override
		public void tick() {
			gnomad.spellTicks++;
			if (spellTicks == castingTime) {
				gnomad.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
				this.spell();
			}
		}

		@Override
		public boolean canContinueToUse() {
			if (interuptable) {
				return gnomad.interuptionTicks > 0;
			} else {
				return spellTicks == castingTime + 1;
			}
		}

		@Override
		public void stop() {
			gnomad.spellType = SpellType.NONE;
			gnomad.isSpellcasting = false;
			gnomad.spellTicks = 0;
			gnomad.interuptionTicks = 0;
			gnomad.spellDelay = 40;
			super.stop();
		}

		//Called to determine when the spell is cast.
		abstract boolean spellcastPredicate();

		//Called to determine what the spell actually does.
		abstract void spell();
	}

	private List<AbstractGnomadEntity> getNearbyGnomads(double rangeX, double rangeY, double rangeZ) {
		AABB axisalignedbb = this.getBoundingBox().inflate(rangeX, rangeY, rangeZ);
		List<AbstractGnomadEntity> list = this.level.getEntitiesOfClass(AbstractGnomadEntity.class, axisalignedbb);
		
		return list;
	}

	public static enum SpellType {
		KNOCKBACK,
		BOW,
		BUFF,
		HEAL,
		NONE;
	}
}
