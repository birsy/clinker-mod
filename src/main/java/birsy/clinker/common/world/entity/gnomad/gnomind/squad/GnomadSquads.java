package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

import birsy.clinker.common.networking.packet.debug.GnomadSquadRemovalDebugPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

// keeps track of squads in a given ServerLevel
// not actually saved data - squads are ephemeral. just wanted an easy way to attach it to a serverlevel
@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GnomadSquads extends SavedData {
    private final HashMap<UUID, GnomadSquad> squadMap = Maps.newHashMap();
    private final ServerLevel level;

    public GnomadSquads(ServerLevel pLevel) {
        this.level = pLevel;
        this.setDirty();
    }
    public static Factory<GnomadSquads> factory(ServerLevel pLevel) {
        return new Factory<>(() -> new GnomadSquads(pLevel), (data, registry) -> load(pLevel, data), DataFixTypes.SAVED_DATA_RAIDS);
    }

    public void tick() {
        Iterator<Map.Entry<UUID, GnomadSquad>> iterator = squadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, GnomadSquad> entry = iterator.next();
            GnomadSquad squad = entry.getValue();
            if (squad.markedForRemoval) {
                PacketDistributor.sendToAllPlayers(new GnomadSquadRemovalDebugPacket(squad.id));
                iterator.remove();
                continue;
            }
            squad.tick();
        }
    }

    public void createSquad(GnomadEntity... members) {
        this.createSquad(List.of(members));
    }

    public void createSquad(Collection<GnomadEntity> members) {
        GnomadSquad squad = new GnomadSquad(UUID.randomUUID(), level);
        for (GnomadEntity member : members) squad.addMember(member);
        squadMap.put(squad.id, squad);
    }

    public static GnomadSquads load(ServerLevel pLevel, CompoundTag pTag) {
        GnomadSquads data = new GnomadSquads(pLevel);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider registries) {
        // don't do anything LOL
        return pCompoundTag;
    }

    @SubscribeEvent
    public static void tick(LevelTickEvent.Post event) {
        Minecraft.getInstance().getProfiler().push("tickGnomadSquads");
        if (event.getLevel() instanceof ServerLevel level) {
            getInstance(level).tick();
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    public static GnomadSquads getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(factory(level), "GnomadSquads");
    }
}
