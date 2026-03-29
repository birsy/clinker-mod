package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

import birsy.clinker.common.networking.packet.debug.ClientboundSquadDebugPacket;
import birsy.clinker.core.Clinker;
import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class SquadManager extends SavedData {
    private final HashMap<UUID, Squad> squads = Maps.newHashMap();
    private final ServerLevel level;

    public SquadManager(ServerLevel level) {
        this.level = level;
    }

    public static SquadManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(() -> new SquadManager(level), (data, registry) -> new SquadManager(level), null),
                "SquadManager"
        );
    }

    public Squad getOrCreate(UUID squadId) {
        return squads.computeIfAbsent(squadId, (id) -> new Squad(id, level));
    }

    public void tick() {
        Collection<Squad> squads = this.squads.values();
        for (Squad squad : squads) {
            squad.tick();
            if (squad.shouldBeRemoved()) {
                squad.ticksUntilRemoval--;
                if (squad.ticksUntilRemoval <= 0) squad.cleanup();
            } else {
                squad.ticksUntilRemoval = Squad.TIME_UNTIL_REMOVAL;
            }
        }
        squads.removeIf(squad -> squad.ticksUntilRemoval <= 0);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return tag;
    }

    @SubscribeEvent
    public static void tickSquads(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("tickSquads");

            SquadManager managerForLevel = get(level);
            managerForLevel.tick();

            if (SharedConstants.IS_RUNNING_IN_IDE && level.getGameTime() % 5 == 0) {
                PacketDistributor.sendToPlayersInDimension(level,
                        ClientboundSquadDebugPacket.of(managerForLevel.squads.values())
                );
            }

            profiler.pop();
        }
    }
}
