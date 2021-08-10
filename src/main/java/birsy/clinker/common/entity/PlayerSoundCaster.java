package birsy.clinker.common.entity;

//import birsy.clinker.init.ClinkerEntityTypes;
import net.minecraft.world.entity.EntityType;
//import net.minecraft.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PlayerSoundCaster extends AbstractSoundCaster {
	public int lifespan = 100;
	public int range = 30;
	
	public PlayerSoundCaster(EntityType<? extends AbstractSoundCaster> type, Level worldIn) {
		super(type, worldIn);
	}
	
	//public PlayerSoundCaster(World worldIn, LivingEntity casterIn) {
	//	super(ClinkerEntityTypes.PLAYER_SOUND.get(), casterIn, worldIn);
	//}

	//public PlayerSoundCaster(World worldIn, double x, double y, double z) {
	//	super(ClinkerEntityTypes.PLAYER_SOUND.get(), x, y, z, worldIn);
	//}
}
