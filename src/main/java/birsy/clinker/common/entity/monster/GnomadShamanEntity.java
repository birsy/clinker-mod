package birsy.clinker.common.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

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
	
	public GnomadShamanEntity(EntityType<? extends AbstractGnomadEntity> type, World worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
	    this.getNavigator().setCanSwim(true);
	    this.setCanPickUpLoot(true);
	    this.gnomadRank = 1;
	}	
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setCallsForHelp(AbstractGnomadEntity.class));
		this.goalSelector.addGoal(3, new GnomadShamanEntity.BuffGnomadsGoal(this));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, PlayerEntity.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, GnomeEntity.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeletonEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));
		this.goalSelector.addGoal(9, new LookAtGoal(this, GnomeEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, MobEntity.class, 8.0F));
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
	public boolean hitByEntity(Entity entityIn) {
		this.interuptionTicks = 5;
		return super.hitByEntity(entityIn);
	}

	public class BuffGnomadsGoal extends SpellcastGoal {
		private final GnomadShamanEntity gnomad;

		public BuffGnomadsGoal(GnomadShamanEntity gnomadIn) {
			super(gnomadIn, SpellType.BUFF, 25, true);
			this.gnomad = gnomadIn;
		}

		@Override
		boolean spellcastPredicate() {
			if (gnomad.getAttackTarget() != null) {
				return gnomad.getNearbyGnomads(4.0D, 2.0D, 4.0D).size() > 2 && gnomad.getDistanceSq(gnomad.getAttackTarget()) > 3;
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
						LOGGER.info(gnomad.getEntityId() + " has cast a " + spellType.toString() + " spell!");
						if (rand.nextInt(1) == 0) {
							if (((GnomadAxemanEntity) livingentity).getShieldNumber() > 1 && ((GnomadAxemanEntity) livingentity).getShieldNumber() < 2) {
								((GnomadAxemanEntity) livingentity).setShielded(((GnomadAxemanEntity) livingentity).getShieldNumber() + 2);
								livingentity.getDataManager().set(GnomadAxemanEntity.SHIELDS, ((GnomadAxemanEntity) livingentity).getShieldNumber() + 2);
							}
							((GnomadAxemanEntity) livingentity).setShielded(4);
						} else {
							((GnomadAxemanEntity) livingentity).setBuffed(true);
							livingentity.getDataManager().set(GnomadAxemanEntity.BUFFED, true);
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
		public boolean shouldExecute() {
			if (gnomad.isAlive() && spellDelay == 0) {
				return spellcastPredicate();
			} else {
				return spellTicks == castingTime + 1;
			}
		}

		@Override
		public void startExecuting() {
			gnomad.spellType = spellType;
			gnomad.isSpellcasting = true;
			gnomad.spellTicks = 0;
			gnomad.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
			this.spell();
		}

		@Override
		public void tick() {
			gnomad.spellTicks++;
			if (spellTicks == castingTime) {
				gnomad.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0F, 1.0F);
				this.spell();
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			if (interuptable) {
				return gnomad.interuptionTicks > 0;
			} else {
				return spellTicks == castingTime + 1;
			}
		}

		@Override
		public void resetTask() {
			gnomad.spellType = SpellType.NONE;
			gnomad.isSpellcasting = false;
			gnomad.spellTicks = 0;
			gnomad.interuptionTicks = 0;
			gnomad.spellDelay = 40;
			super.resetTask();
		}

		//Called to determine when the spell is cast.
		abstract boolean spellcastPredicate();

		//Called to determine what the spell actually does.
		abstract void spell();
	}

	private List<AbstractGnomadEntity> getNearbyGnomads(double rangeX, double rangeY, double rangeZ) {
		AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(rangeX, rangeY, rangeZ);
		List<AbstractGnomadEntity> list = this.world.getEntitiesWithinAABB(AbstractGnomadEntity.class, axisalignedbb);
		
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
