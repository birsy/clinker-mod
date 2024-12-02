package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationChangeBlockPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationLoadPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationMergePacket;
import birsy.clinker.common.networking.packet.workstation.ServerboundWorkstationLoadRequestPacket;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class WorkstationManager {
    public static final Map<ServerLevel, WorkstationManager> managerByLevel = new HashMap<>();
    public static final Map<ResourceKey<Level>, WorkstationManager> managerByDimension = new HashMap<>();
    
    public static WorkstationManager clientWorkstationManager = new WorkstationManager();

    //TODO: diagonals
    public static final Vec3i[] DIRECTIONAL_OFFSETS = Util.make(() -> {
        Vec3i[] o = new Vec3i[7];
        o[0] = Vec3i.ZERO;
        int i = 0;
        for (Direction value : Direction.values()) {
            o[++i] = value.getNormal();
        }
        return o;
    });

    public Level level;
    private final boolean isClientSide;
    public final Map<UUID, Workstation> workstationStorage;
    public WorkstationManager(Level level) {
        this.level = level;
        this.isClientSide= this.level.isClientSide();
        this.workstationStorage = new HashMap<>();
    }

    public WorkstationManager() {
        this.isClientSide= true;
        this.workstationStorage = new HashMap<>();
    }

    public void tick() {
        for (Workstation workstation : workstationStorage.values()) {
            workstation.tick();
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        WorkstationManager manager = managerByLevel.get(event.getLevel());
        if (manager == null) return;
        Workstation workstationAtPos = manager.getWorkstationAtBlock(event.getPos());
        // doing this so it doesnt keep crashing while im rebuilding the fucking mod
        // todo: remove this before release
        try {
            if (event.getState().is(ClinkerTags.WORKSTATION) && workstationAtPos == null) {
                manager.addWorkstationBlock(event.getPos());
            } else if (!event.getState().is(ClinkerTags.WORKSTATION) && workstationAtPos != null) {
                manager.removeWorkstationBlockFromUUID(event.getPos(), workstationAtPos.uuid);
            }
        } catch (NoClassDefFoundError e) {
            //do nothing
            return;
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        WorkstationManager manager = managerByLevel.get(event.getLevel());
        if (manager == null) return;
        Workstation workstationAtPos = manager.getWorkstationAtBlock(event.getPos());
        // doing this so it doesnt keep crashing while im rebuilding the fucking mod
        // todo: remove this before release
        try {
            if (event.getState().is(ClinkerTags.WORKSTATION) && workstationAtPos == null) {
                manager.addWorkstationBlock(event.getPos());
            } else if (!event.getState().is(ClinkerTags.WORKSTATION) && workstationAtPos != null) {
                manager.removeWorkstationBlockFromUUID(event.getPos(), workstationAtPos.uuid);
            }
        } catch (NoClassDefFoundError e) {
            //do nothing
            return;
        }
    }

    @Nullable
    public Workstation getWorkstationAtBlock(BlockPos pos) {
        for (Workstation workstation : workstationStorage.values()) {
            if (workstation.containedBlocks.containsBlock(pos)) return workstation;
        }
        return null;
    }

    private void addWorkstationBlock(BlockPos pos) {
        if (this.isClientSide) return;

        List<Workstation> adjacentWorkstations = new ArrayList<>();
        Workstation nearestWorkstation = null;

        for (Workstation workstation : workstationStorage.values()) {
            for (Vec3i directionalOffset : DIRECTIONAL_OFFSETS) {
                if (workstation.containedBlocks.containsBlock(pos.offset(directionalOffset))) {
                    if (nearestWorkstation == null) {
                        nearestWorkstation = workstation;
                    } else if (nearestWorkstation != workstation) {
                        adjacentWorkstations.add(workstation);
                    }
                }
            }
        }

        boolean hasMerged = false;
        if (nearestWorkstation == null) {
            nearestWorkstation = new Workstation(this.level, UUID.randomUUID());
            this.workstationStorage.put(nearestWorkstation.uuid, nearestWorkstation);
        } else {
            if (!adjacentWorkstations.isEmpty()) {
                for (Workstation adjacentWorkstation : adjacentWorkstations) {
                    nearestWorkstation.merge(adjacentWorkstation);
                    ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(pos), new ClientboundWorkstationMergePacket(nearestWorkstation.uuid, adjacentWorkstation.uuid));
                    workstationStorage.remove(adjacentWorkstation.uuid);
                }
            }
        }

        nearestWorkstation.addBlock(pos);
        ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(pos), new ClientboundWorkstationChangeBlockPacket(pos, true, nearestWorkstation.uuid));
    }

    public void loadWorkstationToClient(UUID id, ServerPlayer client) {
        if (this.isClientSide) return;
        if (!this.workstationStorage.containsKey(id)) {
            Clinker.LOGGER.warn("Cannot find Workstation with id " + id.toString() + " !");
            return;
        }
        Workstation workstation = this.workstationStorage.get(id);
        Vec3i workstationCenter = workstation.containedBlocks.getCenter();

        ServerLevel level = (ServerLevel) this.level;
        if (Math.sqrt(workstationCenter.distToCenterSqr(client.position())) - workstation.containedBlocks.getRoughMaxRadius() > (level.getServer().getPlayerList().getViewDistance() + 1) * 16) return;
        ClinkerPacketHandler.sendToClient(client, new ClientboundWorkstationLoadPacket(id, workstation));
    }

    
    public void addWorkstationBlockToUUID(BlockPos pos, UUID id) {
        if (!this.workstationStorage.containsKey(id)) {
            this.workstationStorage.put(id, new Workstation(this.level, id));
            Clinker.LOGGER.info("creating workstation " + id);
        }
        this.workstationStorage.get(id).addBlock(pos);
    }

    
    public void mergeWorkstations(UUID id0, UUID id1) {
        Workstation station0 = this.workstationStorage.get(id0);
        Workstation station1 = this.workstationStorage.get(id1);

        if (station0 == null || station1 == null) {
            ClinkerPacketHandler.sendToServer(new ServerboundWorkstationLoadRequestPacket(id0, this.level));
        } else {
            station0.merge(station1);
        }

        this.workstationStorage.remove(id1);
        Clinker.LOGGER.info("merging workstations " + id0 + " and " + id1);
    }


    public void removeWorkstationBlockFromUUID(BlockPos pos, UUID id) {
        if (!this.workstationStorage.containsKey(id)) { Clinker.LOGGER.warn("No workstation of UUID " + id.toString() + " exists!"); return; }
        this.workstationStorage.get(id).removeBlock(pos);
        if (!this.isClientSide) ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(pos), new ClientboundWorkstationChangeBlockPacket(pos, false, id));
    }

    private void clear() {
        workstationStorage.clear();
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (clientWorkstationManager == null) return;
        clientWorkstationManager.clear();
    }



    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (clientWorkstationManager == null) clientWorkstationManager = new WorkstationManager((Level) event.getLevel());
        clientWorkstationManager.level = (Level) event.getLevel();
    }
}
