package birsy.clinker.common.world.entity.homunculoids;

import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.ArrayList;
import java.util.List;

public class MotherHomunculoid extends HomunculoidEntity implements SmartBrainOwner<MotherHomunculoid> {
    public MotherHomunculoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MotherHomunculoid>> getSensors() {
        return null;
    }



//    protected List<EntityType<?>> birthableMobs() {
//        List<Holder<EntityType<?>>> list = new ArrayList<>();
//        BuiltInRegistries.ENTITY_TYPE.getOrCreateTag(ClinkerTags.BIRTHABLE_HOMUNCULOIDS).bind(list);
//        List<EntityType<?>> returnList = new ArrayList<>();
//        for (Holder<EntityType<?>> entityTypeHolder : list) returnList.add(entityTypeHolder.value());
//        return returnList;
//    }
//
//    public void giveBirth() {
//        List<EntityType<?>> birthableMobs = birthableMobs();
//
//        // get a random birthable mob
//        int index = this.random.nextInt(birthableMobs.size());
//        EntityType<?> mobType = birthableMobs.get(index);
//
//        Entity entityToBirth = mobType.create(this.level());
//        if (entityToBirth == null) return;
//        entityToBirth.setPos(this.position());
//        entityToBirth.setDeltaMovement(random.nextFloat() * 2 - 1, 1, random.nextFloat() * 2 - 1);
//        this.level().addFreshEntity(entityToBirth);
//    }
}
