package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.relax;

import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.PostSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.RelaxWithSquadTask;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.Optional;

public class TryInitiateRelaxation<E extends LivingEntity & SquadMember<E>> extends PostSquadTask<E, RelaxWithSquadTask> {
    public TryInitiateRelaxation() {
        super(RelaxWithSquadTask.class, TryInitiateRelaxation::createTask);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        if (RandomUtil.oneInNChance(20 * 10)) return super.checkExtraStartConditions(level, mob);
        return false;
    }

    static <E extends LivingEntity & SquadMember<E>> RelaxWithSquadTask createTask(E mob) {
        BlockPos entityBlockPos = mob.blockPosition();
        Level level = mob.level();
        if (level instanceof ServerLevel serverLevel) {
            Optional<BlockPos> attempt = serverLevel.getPoiManager()
                    .findClosest(
                            typeHolder -> typeHolder.is(PoiTypes.LODESTONE),
                            pos -> true,
                            entityBlockPos,
                            32,
                            PoiManager.Occupancy.ANY);
            return attempt
                    .map(pos -> new RelaxWithSquadTask(mob, new GlobalPos(serverLevel.dimension(), pos)))
                    .orElse(null);
        }
        return null;
    }
}
