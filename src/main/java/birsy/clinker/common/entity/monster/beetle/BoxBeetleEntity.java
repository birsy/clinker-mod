package birsy.clinker.common.entity.monster.beetle;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.ShoggothHeadEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class BoxBeetleEntity extends MonsterEntity {
	private boolean isBashing;
	private boolean inFlight;
	private boolean wasInFlightLastTick;

	public int ticksInFlight;
	public int ticksAirborne;

	public int attackTimer;
	private int attackDelay;
	private float rollAmount;
	private float rollAmountO;

	public int flightOpenTransitionTicks;
	public int flightCloseTransitionTicks;

	private final MovementController walkingMoveController = new MovementController(this);
	private final MovementController flyingMoveController = new FlyingMovementController(this, 50, true);

	public BoxBeetleEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		this.getNavigator().setCanSwim(true);
	}

	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new BoxBeetleEntity.GroundAttackGoal(this));
		this.goalSelector.addGoal(3, new BoxBeetleEntity.FlyingWanderGoal(this));
	    this.goalSelector.addGoal(4, new BoxBeetleEntity.WalkingWanderGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
	}
	
	//func_233666_p_ --> registerAttributes
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 17.0)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
				.createMutableAttribute(Attributes.FLYING_SPEED, 0.6F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
				.createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 3.0D);
	}

	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		if (this.inFlight) {
			return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
		} else {
			return super.getBlockPathWeight(pos, worldIn);
		}
	}

	@Override
	public void livingTick() {
		//If it's not currently transitioning from flying, then it may start to fly.
		if (this.rand.nextInt(100) == 0 && this.flightCloseTransitionTicks == 0 && this.flightOpenTransitionTicks == 0 ) {
			if (this.getFlying()) {
				this.setFlying(false);
			} else {
				this.setFlying(true);
			}
		}

		super.livingTick();
	}

	@Override
	public void tick() {
		//Sets the transition ticks for the elytra unfullering animation.
		if (flightOpenTransitionTicks > 0) {
			flightOpenTransitionTicks--;
		}

		if (flightCloseTransitionTicks > 0) {
			flightCloseTransitionTicks--;
		}

		if(this.isBashing) {
			this.attackTimer++;
		} else {
			this.attackTimer = 0;
		}

		if (this.attackDelay > 0) {
			this.attackDelay--;
		}

		if(this.getFlying()) {
			updateBodyPitch();
		}

		super.tick();
	}

	@OnlyIn(Dist.CLIENT)
	public float getBodyPitch(float p_226455_1_) {
		return MathHelper.lerp(p_226455_1_, this.rollAmountO, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.rollAmountO = this.rollAmount;
		if (this.isBashing) {
			this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
		} else {
			this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
		}
	}

	public void setBashing(Boolean bool) {
		this.isBashing = bool;
	}
	
	public boolean getBashing() {
		return this.isBashing;
	}

	public void setFlying(Boolean bool) {
		this.inFlight = bool;
		if (bool) {
			this.moveController = flyingMoveController;
			this.setNoGravity(true);
			this.flightOpenTransitionTicks = 10;
		} else {
			this.moveController = walkingMoveController;
			this.setNoGravity(false);
			this.flightCloseTransitionTicks = 10;
		}
	}
	
	public boolean getFlying() {
		return this.inFlight;
	}

	public class GroundAttackGoal extends Goal {
		private final BoxBeetleEntity beetle;
		private Path path;
		private int bashTimer;

		public GroundAttackGoal(BoxBeetleEntity mobIn) {
			this.beetle = mobIn;
		}

		@Override
		public boolean shouldExecute() {
			LivingEntity livingentity = this.beetle.getAttackTarget();
			if (livingentity == null) {
				return false;
			} else return livingentity.isAlive();
		}

		public boolean shouldContinueExecuting() {
			LivingEntity livingentity = this.beetle.getAttackTarget();

			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else if (!this.beetle.isWithinHomeDistanceFromPosition(livingentity.getPosition())) {
				return false;
			} else {
				return !(livingentity instanceof PlayerEntity) || !livingentity.isSpectator() && !((PlayerEntity)livingentity).isCreative();
			}
		}

		public void startExecuting() {
			//Sets it to track the player, and makes it mad while it does so.
			this.path = this.beetle.getNavigator().getPathToEntity(this.beetle.getAttackTarget(), 0);

			this.beetle.getNavigator().setPath(this.path, 1.0D);
			this.beetle.setAggroed(true);

			this.beetle.attackDelay = 0;
			this.bashTimer = 0;
		}

		@Override
		public void tick() {
			LivingEntity livingentity = this.beetle.getAttackTarget();

			//If it's not already bashing, within 9 blocks of the player, and isn't recovering from an attack, then it has a 1 in 3 chance to begin bashing.
			if (!this.beetle.getBashing() && this.beetle.getDistance(livingentity) < 4.0D && this.beetle.attackDelay <= 0) {
				this.beetle.setBashing(this.beetle.rand.nextInt(3) == 0);
			}

			if (this.beetle.getBashing() && this.beetle.attackDelay <= 0) {
				if (this.beetle.getBoundingBox().intersects(livingentity.getBoundingBox())) {
					this.beetle.attackEntityAsMob(livingentity);

					this.beetle.attackDelay = 25;
					this.bashTimer = 0;
				} else {
					Vector3d targetPos = livingentity.getPositionVec();
					this.beetle.moveController.setMoveTo(targetPos.x, targetPos.y, targetPos.z, 2.0D);
					this.bashTimer++;

					//If it's been trying to bash for more than half a second, but hasn't succeeded, then something's clearly wrong. It stops bashing.
					if(bashTimer >= 10) {
						this.beetle.setBashing(false);
					}
				}
			}

			super.tick();
		}

		public void resetTask() {
			this.beetle.setAggroed(false);
			this.beetle.setBashing(false);
			this.beetle.getNavigator().clearPath();

			this.beetle.attackDelay = 0;
			this.bashTimer = 0;
		}
	}

	class FlyingWanderGoal extends Goal {
		private final BoxBeetleEntity beetle;

		FlyingWanderGoal(BoxBeetleEntity mobIn) {
			this.beetle = mobIn;
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean shouldExecute() {
			return this.beetle.inFlight && this.beetle.navigator.noPath();
		}

		public boolean shouldContinueExecuting() {
			return this.beetle.inFlight && this.beetle.navigator.hasPath() && !this.beetle.isAggressive();
		}

		public void startExecuting() {
			Vector3d vector3d = this.getRandomLocation();
			if (vector3d != null) {
				this.beetle.navigator.setPath(this.beetle.navigator.getPathToPos(new BlockPos(vector3d), 1), 1.0D);
			}
		}

		@Nullable
		private Vector3d getRandomLocation() {
			Vector3d vector3d;
			vector3d = this.beetle.getLook(0.0F);

			int i = 8;
			Vector3d vector3d2 = RandomPositionGenerator.findAirTarget(this.beetle, 8, 7, vector3d, ((float)Math.PI / 2F), 2, 1);
			return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(this.beetle, 8, 4, -2, vector3d, (double)((float)Math.PI / 2F));
		}
	}

	public class WalkingWanderGoal extends RandomWalkingGoal {
		private final BoxBeetleEntity beetle;

		public WalkingWanderGoal(BoxBeetleEntity creatureIn, double speedIn) {
			super(creatureIn, speedIn);
			beetle = creatureIn;
		}

		@Override
		public boolean shouldExecute() {
			if (beetle.getFlying()) {
				return false;
			} else {
				return super.shouldExecute();
			}
		}
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		if ((super.attackEntityFrom(source, amount) && source.isProjectile() && this.inFlight) || (super.attackEntityFrom(source, amount) && this.rand.nextInt(3) == 0 && this.inFlight)) {
			this.setFlying(false);
			this.setBashing(false);
			this.attackDelay = 50;
		}
		return super.attackEntityFrom(source, amount);
	}

	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected boolean makeFlySound() {
		return this.inFlight;
	}

	protected void collideWithEntity(Entity entityIn) {
		if (entityIn == this.getAttackTarget() && this.getBashing()) {
			return;
		} else {
			super.collideWithEntity(entityIn);
		}
	}
}
