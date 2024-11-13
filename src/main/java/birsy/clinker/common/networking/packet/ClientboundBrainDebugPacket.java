package birsy.clinker.common.networking.packet;

import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientboundBrainDebugPacket extends ClientboundPacket {
    final BrainDebugPayload.BrainDump dump;

    public ClientboundBrainDebugPacket(Mob entity) {
        String inventory = "";
        if (entity instanceof InventoryCarrier inv) inventory = inv.getInventory().toString();
        List<String> activities = new ArrayList<>();
        for (Activity activeActivity : entity.getBrain().getActiveActivities()) activities.add(activeActivity.getName());
        List<String> behaviors = new ArrayList<>();
        for (BehaviorControl<?> behavior : entity.getBrain().getRunningBehaviors()) activities.add(behavior.debugString());
        List<String> memories = new ArrayList<>();
        for (MemoryModuleType<?> memoryModuleType : entity.getBrain().getMemories().keySet()) memories.add(memoryModuleType.toString());

        Set<BlockPos> POIs = new HashSet<>();
        if (BrainUtils.hasMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get())) {
            POIs.add(BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos());
        }

        this.dump = new BrainDebugPayload.BrainDump(
                entity.getUUID(),
                entity.getId(),
                DebugEntityNameGenerator.getEntityName(entity),
                "N/A",
                entity.getExperienceReward(),
                entity.getHealth(),
                entity.getMaxHealth(),
                entity.position(), inventory, entity.getNavigation().getPath(),
                false,
                entity.isAggressive() ? 1 : 0,
                activities,
                behaviors,
                memories,
                List.of(),
                POIs, Set.of());
    }

    public ClientboundBrainDebugPacket(FriendlyByteBuf buffer) {
        this.dump = new BrainDebugPayload.BrainDump(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.dump.write(buffer);
    }

    @Override
    public void run(PlayPayloadContext context) {
        if (!SharedConstants.IS_RUNNING_IN_IDE) return;
        Minecraft mc = Minecraft.getInstance();
        if (!mc.getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            mc.debugRenderer.brainDebugRenderer.clear();
            return;
        }
        mc.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(this.dump);
    }
}
