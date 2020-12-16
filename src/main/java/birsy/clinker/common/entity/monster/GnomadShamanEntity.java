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
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class GnomadShamanEntity extends AbstractGnomadEntity
{	
	private int spellDelay;
	protected int spellTicks;
	public boolean isSpellcasting;
	
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
	}	
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		//this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
		//this.goalSelector.addGoal(2, new GnomadAxemanEntity.ChargeAttackGoal(this));
		//this.goalSelector.addGoal(3, new GnomadShamanEntity.MoveToGnomadsGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setCallsForHelp(AbstractGnomadEntity.class));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeletonEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));
		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
		this.goalSelector.addGoal(9, new LookAtGoal(this, GnomeEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, MobEntity.class, 8.0F));
	}
	
	public static enum SpellType {
		KNOCKBACK,
		HEALING,
		STALAGMITE,
		BITEFISH,
		NONE;
	}
}
