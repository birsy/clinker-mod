package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.RestWithFriendsTask;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

// informs our squad that we want to relax.
// sets up a relaxation point for members of our squad!
// TODO: use POIs to find better spots to relax. try to relax around light sources?
public class InitiateRelaxWithSquad<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_ABSENT),
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_ABSENT),
            Pair.of(ClinkerMemoryModules.GNOMADS_IN_SQUAD.get(), MemoryStatus.VALUE_PRESENT)
    );
    protected SquareRadius radius = new SquareRadius(10, 7);
    private BlockPos relaxationSpot;

    public InitiateRelaxWithSquad<E> radius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Vec3 pos = LandRandomPos.getPos(entity, (int) this.radius.xzRadius(), (int) this.radius.yRadius());
        if (pos == null) return false;
        this.relaxationSpot = BlockPos.containing(pos);

        List<GnomadEntity> squadmates = BrainUtils.getMemory(entity, ClinkerMemoryModules.GNOMADS_IN_SQUAD.get());
        return squadmates.size() > 1;
    }

    @Override
    protected void start(E entity) {
        Clinker.LOGGER.info("{} decided to rest.", DebugEntityNameGenerator.getEntityName(entity));
        entity.squad.postTask(new RestWithFriendsTask(this.relaxationSpot, entity));
    }

    @Override
    protected void stop(E entity) {
        this.relaxationSpot = null;
    }
}
