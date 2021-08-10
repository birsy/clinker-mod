package birsy.clinker.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public abstract class AbstractSoundCaster extends Entity {
	private int age;
	public int lifespan = 40;
	
	public int range = 10;
	
	public UUID caster;
	public int casterID;
	
	public AbstractSoundCaster(EntityType<? extends AbstractSoundCaster> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}
	
	public AbstractSoundCaster(EntityType<? extends AbstractSoundCaster> type, double x, double y, double z, Level worldIn) {
		super(type, worldIn);
		this.setPos(x, y, z);
	}

	public AbstractSoundCaster(EntityType<? extends AbstractSoundCaster> type, LivingEntity livingEntityIn, Level worldIn) {
		this(type, livingEntityIn.getX(), livingEntityIn.getEyeY() - (double)0.1F, livingEntityIn.getZ(), worldIn);
		this.setCaster(livingEntityIn);
	}
	
	public void addAdditionalSaveData(CompoundTag compound) {
		compound.putShort("Age", (short)this.age);
		compound.putInt("Lifespan", lifespan);
		compound.putInt("Range", range);
		if (this.getCasterId() != null) {
			compound.putUUID("caster", this.getCasterId());
		}
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		this.age = compound.getShort("Age");
		if (compound.contains("Lifespan")) lifespan = compound.getInt("Lifespan");
		if (compound.contains("Range")) range = compound.getInt("Range");
		if (compound.hasUUID("Caster")) {
			this.caster = compound.getUUID("Caster");
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
		if (this.caster != null && this.level instanceof ServerLevel) {
			return ((ServerLevel)this.level).getEntity(this.caster);
		} else {
			return this.casterID != 0 ? this.level.getEntity(this.casterID) : null;
		}
	}
	
	public void setCaster(@Nullable Entity entityIn) {
		if (entityIn != null) {
			this.caster = entityIn.getUUID();
			this.casterID = entityIn.getId();
		}
	}
	
	@Override
	protected void defineSynchedData(){}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
