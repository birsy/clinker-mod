package birsy.clinker.common.entity.projectile;

import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class PhospherBallEntity extends ProjectileItemEntity implements IRendersAsItem {
	public int explosionPower = 1;
	
	public double accelerationX;
	public double accelerationY;
	public double accelerationZ;
	
	public PhospherBallEntity(EntityType<? extends PhospherBallEntity> type, World worldIn) {
		super(type, worldIn);
	}

	//public PhospherBallEntity(World worldIn, LivingEntity throwerIn) {
		//super(ClinkerEntityTypes.PHOSPHER_BALL.get(), throwerIn, worldIn);
	//}

	@Override
	protected Item getDefaultItem() {
		return ClinkerItems.PHOSPHER_BALL_ITEM.get();
	}

	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!this.world.isRemote) {
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.func_234616_v_());
			this.world.createExplosion((Entity)null, this.getPosX(), this.getPosY(), this.getPosZ(), (float)this.explosionPower, flag, flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
			this.remove();
		}
	}

	/**
	 * Called when the arrow hits an entity
	 */
	protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
		super.onEntityHit(p_213868_1_);
		if (!this.world.isRemote) {
			Entity entity = p_213868_1_.getEntity();
			Entity entity1 = this.func_234616_v_();
			if (entity1 instanceof LivingEntity) {
				this.applyEnchantments((LivingEntity)entity1, entity);
			}
		}
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("ExplosionPower", this.explosionPower);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("ExplosionPower", 99)) {
			this.explosionPower = compound.getInt("ExplosionPower");
		}
	}
	
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.markVelocityChanged();
			Entity entity = source.getTrueSource();
			if (entity != null) {
				Vector3d vector3d = entity.getLookVec();
				this.setMotion(vector3d);
				this.accelerationX = vector3d.x * 0.1D;
	            this.accelerationY = vector3d.y * 0.1D;
	            this.accelerationZ = vector3d.z * 0.1D;
	            this.setShooter(entity);
	            return true;
			} else {
				return false;
			}
		}
	}
}
