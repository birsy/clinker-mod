package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ClientboundBrainDebugPacket(BrainDebugPayload.BrainDump dump) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundBrainDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/brain"));
    public static final StreamCodec<ByteBuf, ClientboundBrainDebugPacket> STREAM_CODEC = StreamCodec.composite(
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buffer, BrainDebugPayload.BrainDump brain) {
                    brain.write(new FriendlyByteBuf(buffer));
                }
                @Override
                public BrainDebugPayload.BrainDump decode(ByteBuf buffer) {
                    return new BrainDebugPayload.BrainDump(new FriendlyByteBuf(buffer));
                }
            }, ClientboundBrainDebugPacket::dump,
            ClientboundBrainDebugPacket::new
    );

    public ClientboundBrainDebugPacket(Mob entity) {
        this(Util.make(() -> {
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

            return new BrainDebugPayload.BrainDump(
                    entity.getUUID(),
                    entity.getId(),
                    DebugEntityNameGenerator.getEntityName(entity),
                    "N/A",
                    0,
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
        }));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        if (!SharedConstants.IS_RUNNING_IN_IDE) return;
        Minecraft mc = Minecraft.getInstance();
        if (!mc.getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            mc.debugRenderer.brainDebugRenderer.clear();
            return;
        }
        mc.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(this.dump);
    }
}
