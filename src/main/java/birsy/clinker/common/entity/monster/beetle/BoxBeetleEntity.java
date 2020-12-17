package birsy.clinker.common.entity.monster.beetle;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BoxBeetleEntity extends MonsterEntity {

	public boolean isBashing;
	public boolean inFlight;
	
	public BoxBeetleEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		this.getNavigator().setCanSwim(true);
	}

	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
	    this.goalSelector.addGoal(4, new RandomWalkingGoal(this, 1.0D));
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
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	@Override
	public boolean canCollide(Entity entity) {
		return true;
	}
	
	@Override
	public boolean canBePushed() {
		return true;
	}
	
	@Override
	public void applyEntityCollision(Entity entityIn) {}
}
