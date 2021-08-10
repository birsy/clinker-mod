package birsy.clinker.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractSoundCaster extends Entity {
	private int age;
	public int lifespan = 40;
	
	public int range = 10;
	
	public UUID caster;
	public int casterID;
	
	public AbstractSoundCaster(EntityType<? extends AbstractSoundCaster> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
	}
	
	public AbstractSoundCaster(EntityType<? extends AbstractSoundCaster> type, double x, double y, double z, World worldIn) {
		super(type, worldIn);
		this.setPosition(x, y, z);
	}

	public AbstractSoundCaster(EntityType<? extends AbstractSoundCaster> type, LivingEntity livingEntityIn, World worldIn) {
		this(type, livingEntityIn.getPosX(), livingEntityIn.getPosYEye() - (double)0.1F, livingEntityIn.getPosZ(), worldIn);
		this.setCaster(livingEntityIn);
	}
	
	public void writeAdditional(CompoundNBT compound) {
		compound.putShort("Age", (short)this.age);
		compound.putInt("Lifespan", lifespan);
		compound.putInt("Range", range);
		if (this.getCasterId() != null) {
			compound.putUniqueId("caster", this.getCasterId());
		}
	}

	public void readAdditional(CompoundNBT compound) {
		this.age = compound.getShort("Age");
		if (compound.contains("Lifespan")) lifespan = compound.getInt("Lifespan");
		if (compound.contains("Range")) range = compound.getInt("Range");
		if (compound.hasUniqueId("Caster")) {
			this.caster = compound.getUniqueId("Caster");
		}
	}
	
	public void tick() {
		++this.age;
		if(this.age > this.lifespan) {
			this.remove();
		}
	}
	
	@Nullable
	public UUID getCasterId() {
		return this.caster;
	}
	
	@Nullable
	public Entity getCaster() {
		if (this.caster != null && this.world instanceof ServerWorld) {
			return ((ServerWorld)this.world).getEntityByUuid(this.caster);
		} else {
			return this.casterID != 0 ? this.world.getEntityByID(this.casterID) : null;
		}
	}
	
	public void setCaster(@Nullable Entity entityIn) {
		if (entityIn != null) {
			this.caster = entityIn.getUniqueID();
			this.casterID = entityIn.getEntityId();
		}
	}
	
	@Override
	protected void registerData(){}
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
}
