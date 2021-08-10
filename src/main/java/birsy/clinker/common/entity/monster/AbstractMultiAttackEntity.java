package birsy.clinker.common.entity.monster;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;

@SuppressWarnings("unused")
public abstract class AbstractMultiAttackEntity extends Monster
{
	private static final EntityDataAccessor<Byte> ATTACK = SynchedEntityData.defineId(AbstractMultiAttackEntity.class, EntityDataSerializers.BYTE);
	public int attackTicks;
	private AbstractMultiAttackEntity.AttackType activeAttack = AbstractMultiAttackEntity.AttackType.NONE;

	
	public AbstractMultiAttackEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACK, (byte)0);
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.attackTicks = compound.getInt("AttackTicks");
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("AttackTicks", this.attackTicks);
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		if (this.attackTicks > 0) {
			--this.attackTicks;
		}
	}
	

	
	
	
	
	public static enum AttackType {
		NONE(0, 0.0D, 0.0D, 0.0D, 0);

		public final int id;
		public final int timeInTicks;
		public final double[] particleSpeed;
		
		private AttackType(int idIn, double xParticleSpeed, double yParticleSpeed, double zParticleSpeed, int timeInTicksIn) {
			this.id = idIn;
			this.timeInTicks = timeInTicksIn;
			this.particleSpeed = new double[]{xParticleSpeed, yParticleSpeed, zParticleSpeed};
		}

		public static AbstractMultiAttackEntity.AttackType getFromId(int idIn) {
			for(AbstractMultiAttackEntity.AttackType spellcastingillagerentity$spelltype : values()) {
				if (idIn == spellcastingillagerentity$spelltype.id) {
					return spellcastingillagerentity$spelltype;
				}
			}

			return NONE;
		}
	}
}
