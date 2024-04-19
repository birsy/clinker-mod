package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

// informs our squad that we want to relax.
// sets up a relaxation point for members of our squad!
// TODO: use POIs to find better spots to relax. try to relax around light sources?
public class InitiateRelaxWithSquad<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_ABSENT),
                                                                                                                Pair.of(ClinkerMemoryModules.GNOMADS_IN_SQUAD.get(), MemoryStatus.VALUE_PRESENT));
    protected SquareRadius radius = new SquareRadius(10, 7);
    protected float proportion = 0.75F;

    public InitiateRelaxWithSquad<E> chanceToAskSquadMember(float proportion) {
        this.proportion = proportion;
        return this;
    }

    public InitiateRelaxWithSquad<E> radius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }


    @Override
    protected void start(E entity) {
        super.start(entity);
        GlobalPos relaxationSpot = GlobalPos.of(entity.level().dimension(), BlockPos.containing(LandRandomPos.getPos(entity, (int) this.radius.xzRadius(), (int) this.radius.yRadius())));

        BrainUtils.setForgettableMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get(), relaxationSpot, 1800);
        for (GnomadEntity gnomadEntity : BrainUtils.getMemory(entity, ClinkerMemoryModules.GNOMADS_IN_SQUAD.get())) {
            if (!BrainUtils.hasMemory(gnomadEntity, ClinkerMemoryModules.RELAXATION_SPOT.get()) && gnomadEntity.getRandom().nextFloat() < this.proportion) {
                BrainUtils.setForgettableMemory(gnomadEntity, ClinkerMemoryModules.RELAXATION_SPOT.get(), relaxationSpot, 1800);
            }
        }
    }
}
