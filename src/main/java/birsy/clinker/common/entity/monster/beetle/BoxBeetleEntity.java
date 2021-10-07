package birsy.clinker.common.entity.monster.beetle;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.ShoggothHeadEntity;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class BoxBeetleEntity extends MonsterEntity {
	public static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(BoxBeetleEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> BASHING = EntityDataManager.createKey(BoxBeetleEntity.class, DataSerializers.BOOLEAN);

	private boolean wasInFlightLastTick;
	private boolean attemptTransitionNextTick;

	public int ticksInFlight;
	public int ticksAirborne;

	public int attackTimer;
	private int attackDelay;
	private float rollAmount;
	private float prevRollAmount;

	@OnlyIn(Dist.CLIENT)
	private int prevFlightOpenTicks;
	public static final DataParameter<Integer> FLIGHT_OPEN_TICKS = EntityDataManager.createKey(BoxBeetleEntity.class, DataSerializers.VARINT);

	@OnlyIn(Dist.CLIENT)
	private int prevFlightCloseTicks;
	public static final DataParameter<Integer> FLIGHT_CLOSE_TICKS = EntityDataManager.createKey(BoxBeetleEntity.class, DataSerializers.VARINT);

	public static final DataParameter<Float> SIZE = EntityDataManager.createKey(BoxBeetleEntity.class, DataSerializers.FLOAT);

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

	protected void registerData() {
		super.registerData();
		this.dataManager.register(FLYING, false);
		this.dataManager.register(BASHING, false);

		this.dataManager.register(FLIGHT_OPEN_TICKS, 0);
		this.dataManager.register(FLIGHT_CLOSE_TICKS, 0);

		this.dataManager.register(SIZE, 1.0F);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("Flying", this.inFlight());
		compound.putBoolean("Bashing", this.isBashing());

		compound.putInt("FlightOpenTicks", this.getFlightOpenTransitionTicks());
		compound.putInt("FlightCloseTicks", this.getFlightCloseTransitionTicks());

		compound.putFloat("Size", this.getSize());
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(FLYING, compound.getBoolean("Flying"));
		this.dataManager.set(BASHING, compound.getBoolean("Bashing"));

		this.dataManager.set(FLIGHT_OPEN_TICKS, compound.getInt("FlightOpenTicks"));
		this.dataManager.set(FLIGHT_CLOSE_TICKS, compound.getInt("FlightCloseTicks"));

		this.dataManager.set(SIZE, compound.getFloat("Size"));
	}

	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		if (this.inFlight()) {
			return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
		} else {
			return super.getBlockPathWeight(pos, worldIn);
		}
	}

	@Override
	public void livingTick() {
		//If it's not currently transitioning from flying, then it may start to fly.
		if (this.rand.nextInt(600) == 0) {
			if (!this.inFlight()) {
				this.setFlying(true, true);
			}
		} else if (this.rand.nextInt(100) == 0) {
			if (this.inFlight()) {
				this.setFlying(false, true);
			}
		}

		super.livingTick();
	}

	@Override
	public void tick() {
		updateFlightTicks();

		if(this.isBashing()) {
			this.attackTimer++;
		} else {
			this.attackTimer = 0;
		}

		if (this.attackDelay > 0) {
			this.attackDelay--;
		}

		if (this.inFlight()) {
			updateBodyPitch();
			this.moveController = this.flyingMoveController;
		} else {
			this.moveController = this.walkingMoveController;
		}

		if (this.attemptTransitionNextTick) {
			this.setFlying(!this.inFlight(), true);
		}

		super.tick();
	}
	
	public void updateFlightTicks() {
		this.prevFlightOpenTicks = this.getFlightOpenTransitionTicks();
		this.prevFlightCloseTicks = this.getFlightCloseTransitionTicks();

		//Sets the transition ticks for the elytra unfullering animation.
		if (this.getFlightCloseTransitionTicks() > 0) {
			this.setFlightCloseTransitionTicks(this.getFlightCloseTransitionTicks() - 1);
		}

		if (this.getFlightOpenTransitionTicks() > 0) {
			this.setFlightOpenTransitionTicks(this.getFlightOpenTransitionTicks() - 1);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public float getBodyPitch(float partialTicks) {
		return MathHelper.lerp(partialTicks, this.prevRollAmount, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.prevRollAmount = this.rollAmount;
		if (this.isBashing()) {
			this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
		} else {
			this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
		}
	}

	public void setBashing(Boolean bool) {
		this.dataManager.set(BASHING, bool);
	}
	public boolean isBashing() {
		return this.dataManager.get(BASHING);
	}

	public void setFlying(boolean state, boolean checkTransitionTicks) {
		if (!checkTransitionTicks || (this.getFlightCloseTransitionTicks() == 0 && this.getFlightOpenTransitionTicks() == 0)) {
			this.dataManager.set(FLYING, state);
			if (state) {
				this.moveController = flyingMoveController;
				this.setNoGravity(true);
				this.setFlightOpenTransitionTicks(10);
			} else {
				this.moveController = walkingMoveController;
				this.setNoGravity(false);
				this.setFlightCloseTransitionTicks(10);
			}

			this.attemptTransitionNextTick = false;
		} else {
			this.attemptTransitionNextTick = true;
		}
	}
	public boolean inFlight() { return this.dataManager.get(FLYING); }

	@OnlyIn(Dist.CLIENT)
	public float getFlightOpenTransitionTicks(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevFlightOpenTicks, this.dataManager.get(FLIGHT_OPEN_TICKS));
	}
	public int getFlightOpenTransitionTicks() {
		return this.dataManager.get(FLIGHT_OPEN_TICKS);
	}
	public void setFlightOpenTransitionTicks(int time) {
		this.dataManager.set(FLIGHT_OPEN_TICKS, time);
	}

	@OnlyIn(Dist.CLIENT)
	public float getFlightCloseTransitionTicks(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevFlightCloseTicks, this.dataManager.get(FLIGHT_CLOSE_TICKS));
	}
	public int getFlightCloseTransitionTicks() {
		return this.dataManager.get(FLIGHT_CLOSE_TICKS);
	}
	public void setFlightCloseTransitionTicks(int time) {
		this.dataManager.set(FLIGHT_CLOSE_TICKS, time);
	}

	public float getSize() {
		return this.dataManager.get(SIZE);
	}
	public void setSize(float size) {
		this.dataManager.set(SIZE, size);
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
			this.path = this.beetle.getNavigator().pathfind(this.beetle.getAttackTarget(), 0);

			this.beetle.getNavigator().setPath(this.path, 1.0D);
			this.beetle.setAggroed(true);

			this.beetle.attackDelay = 0;
			this.bashTimer = 0;
		}

		@Override
		public void tick() {
			LivingEntity livingentity = this.beetle.getAttackTarget();

			//If it's not already bashing, within 9 blocks of the player, and isn't recovering from an attack, then it has a 1 in 3 chance to begin bashing.
			if (!this.beetle.isBashing() && this.beetle.getDistance(livingentity) < 4.0D && this.beetle.attackDelay <= 0) {
				this.beetle.setBashing(this.beetle.rand.nextInt(3) == 0);
			}

			if (this.beetle.isBashing() && this.beetle.attackDelay <= 0) {
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
			return this.beetle.inFlight() && this.beetle.navigator.noPath();
		}

		public boolean shouldContinueExecuting() {
			return this.beetle.inFlight() && this.beetle.navigator.hasPath() && !this.beetle.isAggressive();
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
			if (beetle.inFlight()) {
				return false;
			} else {
				return super.shouldExecute();
			}
		}
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (super.attackEntityFrom(source, amount) && this.inFlight() && (source.isProjectile() || this.rand.nextInt(3) == 0)) {
			this.setFlying(false, true);
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
		return this.inFlight();
	}

	protected void collideWithEntity(Entity entityIn) {
		if (entityIn == this.getAttackTarget() && this.isBashing()) {
			return;
		} else {
			super.collideWithEntity(entityIn);
		}
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		if (reason != SpawnReason.COMMAND) {
			this.setSize(MathUtils.getRandomFloatBetween(rand, 0.8F,1.3F));
		}
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
}
