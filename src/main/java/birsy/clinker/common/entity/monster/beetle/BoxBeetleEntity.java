package birsy.clinker.common.entity.monster.beetle;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.ShoggothHeadEntity;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.EnumSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class BoxBeetleEntity extends Monster {
	public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(BoxBeetleEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> BASHING = SynchedEntityData.defineId(BoxBeetleEntity.class, EntityDataSerializers.BOOLEAN);

	private boolean wasInFlightLastTick;

	public int ticksInFlight;
	public int ticksAirborne;

	public int attackTimer;
	private int attackDelay;
	private float rollAmount;
	private float rollAmountO;

	@OnlyIn(Dist.CLIENT)
	private int prevFlightOpenTicks;
	public static final EntityDataAccessor<Integer> FLIGHT_OPEN_TICKS = SynchedEntityData.defineId(BoxBeetleEntity.class, EntityDataSerializers.INT);

	@OnlyIn(Dist.CLIENT)
	private int prevFlightCloseTicks;
	public static final EntityDataAccessor<Integer> FLIGHT_CLOSE_TICKS = SynchedEntityData.defineId(BoxBeetleEntity.class, EntityDataSerializers.INT);

	public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(BoxBeetleEntity.class, EntityDataSerializers.FLOAT);

	private final MoveControl walkingMoveController = new MoveControl(this);
	private final MoveControl flyingMoveController = new FlyingMoveControl(this, 50, true);

	public BoxBeetleEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
		this.getNavigation().setCanFloat(true);
	}

	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}

	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new BoxBeetleEntity.GroundAttackGoal(this));
		this.goalSelector.addGoal(3, new BoxBeetleEntity.FlyingWanderGoal(this));
	    this.goalSelector.addGoal(4, new BoxBeetleEntity.WalkingWanderGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
	    this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
	}
	
	//createMobAttributes --> registerAttributes
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 17.0)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.FLYING_SPEED, 0.6F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 3.0D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(FLYING, false);
		this.entityData.define(BASHING, false);

		this.entityData.define(FLIGHT_OPEN_TICKS, 0);
		this.entityData.define(FLIGHT_CLOSE_TICKS, 0);

		this.entityData.define(SIZE, 1.0F);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("Flying", this.inFlight());
		compound.putBoolean("Bashing", this.isBashing());

		compound.putInt("Flight_Open_Ticks", this.getFlightOpenTransitionTicks());
		compound.putInt("Flight_Close_Ticks", this.getFlightCloseTransitionTicks());

		compound.putFloat("Size", this.getSize());
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.entityData.set(FLYING, compound.getBoolean("Flying"));
		this.entityData.set(BASHING, compound.getBoolean("Bashing"));

		this.entityData.set(FLIGHT_OPEN_TICKS, compound.getInt("Flight_Open_Ticks"));
		this.entityData.set(FLIGHT_CLOSE_TICKS, compound.getInt("Flight_Close_Ticks"));

		this.entityData.set(SIZE, compound.getFloat("Size"));
	}

	public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
		if (this.inFlight()) {
			return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
		} else {
			return super.getWalkTargetValue(pos, worldIn);
		}
	}

	@Override
	public void aiStep() {
		//If it's not currently transitioning from flying, then it may start to fly.
		if (this.random.nextInt(600) == 0 && this.getFlightCloseTransitionTicks() == 0 && this.getFlightOpenTransitionTicks() == 0 ) {
			if (!this.inFlight()) {
				this.setFlying(true);
			}
		} else if (this.random.nextInt(100) == 0 && this.getFlightCloseTransitionTicks() == 0 && this.getFlightOpenTransitionTicks() == 0) {
			if (this.inFlight()) {
				this.setFlying(false);
			}
		}

		super.aiStep();
	}

	@Override
	public void tick() {
		this.prevFlightOpenTicks = this.getFlightOpenTransitionTicks();
		this.prevFlightCloseTicks = this.getFlightCloseTransitionTicks();

		//Sets the transition ticks for the elytra unfullering animation.
		if (this.getFlightCloseTransitionTicks() > 0) {
			this.setFlightCloseTransitionTicks(this.getFlightCloseTransitionTicks() - 1);
		}

		if (this.getFlightOpenTransitionTicks() > 0) {
			this.setFlightOpenTransitionTicks(this.getFlightOpenTransitionTicks() - 1);
		}


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
		}

		super.tick();
	}

	@OnlyIn(Dist.CLIENT)
	public float getBodyPitch(float partialTicks) {
		return Mth.lerp(partialTicks, this.rollAmountO, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.rollAmountO = this.rollAmount;
		if (this.isBashing()) {
			this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
		} else {
			this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
		}
	}

	public void setBashing(Boolean bool) {
		this.entityData.set(BASHING, bool);
	}
	public boolean isBashing() {
		return this.entityData.get(FLYING);
	}

	public void setFlying(Boolean bool) {
		this.entityData.set(FLYING, bool);
		if (bool) {
			this.moveControl = flyingMoveController;
			this.setNoGravity(true);
			this.setFlightOpenTransitionTicks(10);
		} else {
			this.moveControl = walkingMoveController;
			this.setNoGravity(false);
			this.setFlightCloseTransitionTicks(10);
		}
	}
	public boolean inFlight() {
		return this.entityData.get(FLYING);
	}

	@OnlyIn(Dist.CLIENT)
	public float getFlightOpenTransitionTicks(float partialTicks) {
		return Mth.lerp(partialTicks, prevFlightOpenTicks, this.entityData.get(FLIGHT_OPEN_TICKS));
	}
	public int getFlightOpenTransitionTicks() {
		return this.entityData.get(FLIGHT_OPEN_TICKS);
	}
	public void setFlightOpenTransitionTicks(int time) {
		this.entityData.set(FLIGHT_OPEN_TICKS, time);
	}

	@OnlyIn(Dist.CLIENT)
	public float getFlightCloseTransitionTicks(float partialTicks) {
		return Mth.lerp(partialTicks, prevFlightCloseTicks, this.entityData.get(FLIGHT_CLOSE_TICKS));
	}
	public int getFlightCloseTransitionTicks() {
		return this.entityData.get(FLIGHT_CLOSE_TICKS);
	}
	public void setFlightCloseTransitionTicks(int time) {
		this.entityData.set(FLIGHT_CLOSE_TICKS, time);
	}

	public float getSize() {
		return this.entityData.get(SIZE);
	}
	public void setSize(float size) {
		this.entityData.set(SIZE, size);
	}

	public class GroundAttackGoal extends Goal {
		private final BoxBeetleEntity beetle;
		private Path path;
		private int bashTimer;

		public GroundAttackGoal(BoxBeetleEntity mobIn) {
			this.beetle = mobIn;
		}

		@Override
		public boolean canUse() {
			LivingEntity livingentity = this.beetle.getTarget();
			if (livingentity == null) {
				return false;
			} else return livingentity.isAlive();
		}

		public boolean canContinueToUse() {
			LivingEntity livingentity = this.beetle.getTarget();

			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else if (!this.beetle.isWithinRestriction(livingentity.blockPosition())) {
				return false;
			} else {
				return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
			}
		}

		public void start() {
			//Sets it to track the player, and makes it mad while it does so.
			this.path = this.beetle.getNavigation().createPath(this.beetle.getTarget(), 0);

			this.beetle.getNavigation().moveTo(this.path, 1.0D);
			this.beetle.setAggressive(true);

			this.beetle.attackDelay = 0;
			this.bashTimer = 0;
		}

		@Override
		public void tick() {
			LivingEntity livingentity = this.beetle.getTarget();

			//If it's not already bashing, within 9 blocks of the player, and isn't recovering from an attack, then it has a 1 in 3 chance to begin bashing.
			if (!this.beetle.isBashing() && this.beetle.distanceTo(livingentity) < 4.0D && this.beetle.attackDelay <= 0) {
				this.beetle.setBashing(this.beetle.random.nextInt(3) == 0);
			}

			if (this.beetle.isBashing() && this.beetle.attackDelay <= 0) {
				if (this.beetle.getBoundingBox().intersects(livingentity.getBoundingBox())) {
					this.beetle.doHurtTarget(livingentity);

					this.beetle.attackDelay = 25;
					this.bashTimer = 0;
				} else {
					Vec3 targetPos = livingentity.position();
					this.beetle.moveControl.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 2.0D);
					this.bashTimer++;

					//If it's been trying to bash for more than half a second, but hasn't succeeded, then something's clearly wrong. It stops bashing.
					if(bashTimer >= 10) {
						this.beetle.setBashing(false);
					}
				}
			}

			super.tick();
		}

		public void stop() {
			this.beetle.setAggressive(false);
			this.beetle.setBashing(false);
			this.beetle.getNavigation().stop();

			this.beetle.attackDelay = 0;
			this.bashTimer = 0;
		}
	}

	class FlyingWanderGoal extends Goal {
		private final BoxBeetleEntity beetle;

		FlyingWanderGoal(BoxBeetleEntity mobIn) {
			this.beetle = mobIn;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canUse() {
			return this.beetle.inFlight() && this.beetle.navigation.isDone();
		}

		public boolean canContinueToUse() {
			return this.beetle.inFlight() && this.beetle.navigation.isInProgress() && !this.beetle.isAggressive();
		}

		public void start() {
			Vec3 vector3d = this.getRandomLocation();
			if (vector3d != null) {
				this.beetle.navigation.moveTo(this.beetle.navigation.createPath(new BlockPos(vector3d), 1), 1.0D);
			}
		}

		@Nullable
		private Vec3 getRandomLocation() {
			Vec3 vector3d;
			vector3d = this.beetle.getViewVector(0.0F);

			int i = 8;
			Vec3 vector3d2 = RandomPos.getAboveLandPos(this.beetle, 8, 7, vector3d, ((float)Math.PI / 2F), 2, 1);
			return vector3d2 != null ? vector3d2 : RandomPos.getAirPos(this.beetle, 8, 4, -2, vector3d, (double)((float)Math.PI / 2F));
		}
	}

	public class WalkingWanderGoal extends RandomStrollGoal {
		private final BoxBeetleEntity beetle;

		public WalkingWanderGoal(BoxBeetleEntity creatureIn, double speedIn) {
			super(creatureIn, speedIn);
			beetle = creatureIn;
		}

		@Override
		public boolean canUse() {
			if (beetle.inFlight()) {
				return false;
			} else {
				return super.canUse();
			}
		}
	}

	public boolean hurt(DamageSource source, float amount) {
		if ((super.hurt(source, amount) && source.isProjectile() && this.inFlight()) || (super.hurt(source, amount) && this.random.nextInt(3) == 0 && this.inFlight())) {
			this.setFlying(false);
			this.setBashing(false);
			this.attackDelay = 50;
		}
		return super.hurt(source, amount);
	}

	public boolean causeFallDamage(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected boolean makeFlySound() {
		return this.inFlight();
	}

	protected void doPush(Entity entityIn) {
		if (entityIn == this.getTarget() && this.isBashing()) {
			return;
		} else {
			super.doPush(entityIn);
		}
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		if (reason != MobSpawnType.COMMAND) {
			this.setSize(MathUtils.getRandomFloatBetween(random, 0.8F,1.3F));
		}
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
}
