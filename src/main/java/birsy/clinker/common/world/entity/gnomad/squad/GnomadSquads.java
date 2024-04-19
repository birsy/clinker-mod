package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.debug.GnomadSquadRemovalDebugPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

import java.util.*;

// keeps track of squads in a given ServerLevel
// not actually saved data - squads are ephemeral. just wanted an easy way to attach it to a serverlevel
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GnomadSquads extends SavedData {
    private final HashMap<Integer, GnomadSquad> squadMap = Maps.newHashMap();
    private final ServerLevel level;
    private int nextAvailableID;

    public GnomadSquads(ServerLevel pLevel) {
        this.level = pLevel;
        this.nextAvailableID = 1;
        this.setDirty();
    }
    public static SavedData.Factory<GnomadSquads> factory(ServerLevel pLevel) {
        return new SavedData.Factory<>(() -> new GnomadSquads(pLevel), p_294039_ -> load(pLevel, p_294039_), DataFixTypes.SAVED_DATA_RAIDS);
    }

    public void tick() {
        Iterator<Map.Entry<Integer, GnomadSquad>> iterator = squadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, GnomadSquad> entry = iterator.next();
            GnomadSquad squad = entry.getValue();

            if (squad.markedForRemoval) {
                ClinkerPacketHandler.sendToAllClients(new GnomadSquadRemovalDebugPacket(squad));
                iterator.remove();
                break;
            }
            squad.tick();
        }
    }

    public void createSquad(GnomadEntity... members) {
        GnomadSquad squad = new GnomadSquad(nextAvailableID++, level);
        for (GnomadEntity member : members) {
            squad.addMember(member);
        }
    }

    public void createSquad(Collection<GnomadEntity> members) {
        GnomadSquad squad = new GnomadSquad(nextAvailableID++, level);
        for (GnomadEntity member : members) {
            squad.addMember(member);
        }

        squadMap.put(squad.id, squad);
    }

    public static GnomadSquads load(ServerLevel pLevel, CompoundTag pTag) {
        GnomadSquads data = new GnomadSquads(pLevel);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        // don't do anything LOL
        return pCompoundTag;
    }


    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.level instanceof ServerLevel level) {
            level.getDataStorage().computeIfAbsent(factory(level), "GnomadSquads").tick();
        }
    }
}
