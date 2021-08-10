package birsy.clinker.common.entity.monster;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class AbstractMultiAttackEntity extends MonsterEntity
{
	private static final DataParameter<Byte> ATTACK = EntityDataManager.createKey(AbstractMultiAttackEntity.class, DataSerializers.BYTE);
	public int attackTicks;
	private AbstractMultiAttackEntity.AttackType activeAttack = AbstractMultiAttackEntity.AttackType.NONE;

	
	public AbstractMultiAttackEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(ATTACK, (byte)0);
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.attackTicks = compound.getInt("AttackTicks");
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("AttackTicks", this.attackTicks);
	}

	protected void updateAITasks() {
		super.updateAITasks();
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
