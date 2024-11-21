package birsy.clinker.common.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class ClientboundPathfindingDebugPacket extends ClientboundPacket {
    private final Path path;
    private final float maxDistanceToWaypoint;
    private final int id;

    public ClientboundPathfindingDebugPacket(Mob entity, Path path) {
        this.path = path;
        this.maxDistanceToWaypoint = entity.getNavigation().getMaxDistanceToWaypoint();
        this.id = entity.getId();
    }

    public ClientboundPathfindingDebugPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readInt();
        this.maxDistanceToWaypoint = buffer.readFloat();
        BlockPos target = buffer.readBlockPos();
        boolean reached  = buffer.readBoolean();
        int index = buffer.readInt();

        int nodeCount = buffer.readInt();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            nodes.add(Node.createFromStream(buffer));
        }
        this.path = new Path(nodes, target, reached);
        this.path.setNextNodeIndex(index);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.id);
        buffer.writeFloat(this.maxDistanceToWaypoint);
        buffer.writeBlockPos(path.getTarget());
        buffer.writeBoolean(path.canReach());
        buffer.writeInt(this.path.getNextNodeIndex());

        buffer.writeInt(this.path.getNodeCount());
        for (Node node : this.path.nodes) {
            node.writeToStream(buffer);
        }
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        mc.debugRenderer.pathfindingRenderer.addPath(id, path, maxDistanceToWaypoint);
    }
}
