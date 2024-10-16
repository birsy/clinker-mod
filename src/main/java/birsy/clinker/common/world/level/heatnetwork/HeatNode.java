package birsy.clinker.common.world.level.heatnetwork;

import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Arrays;

public class HeatNode {
    public final HeatSocket[] sockets;
    Long2FloatMap contributedHeatBySource = new Long2FloatOpenHashMap(8);

    public HeatNode(int sockets) {
        this.sockets = new HeatSocket[sockets];
        Arrays.fill(this.sockets, new HeatSocket(this));
    }

    public float getTotalHeat() {
        float totalHeat = 0.0F;
        for (Float heat : contributedHeatBySource.values()) totalHeat += heat;
        return totalHeat;
    }

    @Nullable
    protected HeatSourceNode searchForSource(long sourceIndex) {
        if (!this.sourcePropagated(sourceIndex)) return null;

        float thisHeat = this.contributedHeatBySource.get(sourceIndex);
        float thisHeatSign = Mth.sign(thisHeat);
        for (HeatSocket socket : sockets) {
            HeatSocket connectedSocket = socket.connectedSocket();
            if (connectedSocket != null) {
                HeatNode connectedNode = connectedSocket.node;
                if (connectedNode instanceof HeatSourceNode source) {
                    // if this is the source, return it.
                    if (source.sourceIndex == sourceIndex) return source;
                } else if (connectedNode.sourcePropagated(sourceIndex)) {
                    float connectedHeat = connectedNode.contributedHeatBySource.get(sourceIndex);
                    // if heat is increasing (or decreasing if the heat is negative) or the same, this is a good direction to search in.
                    if (connectedHeat*thisHeatSign > thisHeat*thisHeatSign || connectedHeat == thisHeat) {
                        HeatSourceNode potentialSource = connectedNode.searchForSource(sourceIndex);
                        if (potentialSource != null) return potentialSource;
                    }
                }
            }
        }

        return null;
    }

    protected void repropagateRelevantSources() {
        contributedHeatBySource.forEach((sourceIndex, heat) -> {
            HeatSourceNode sourceNode = this.searchForSource(sourceIndex);
            if (sourceNode != null) {
                sourceNode.depropagate();
                sourceNode.propagate();
            }
        });
    }

    protected void addHeatSource(long sourceIndex, float amountContributed) {
        contributedHeatBySource.put(sourceIndex, amountContributed);
    }

    protected boolean sourcePropagated(long sourceIndex) {
        return contributedHeatBySource.containsKey(sourceIndex);
    }

    protected boolean canSourceBePropagatedTo(long sourceIndex) {
        return !sourcePropagated(sourceIndex);
    }

    public void propagate() {
        contributedHeatBySource.forEach((sourceIndex, heat) -> {
            // find how many nodes we're spreading the heat to
            // so a "fork in the road" will evenly divide heat
            // spread.
            int activeConnections = 0;
            for (HeatSocket socket : sockets) {
                HeatSocket connectedSocket = socket.connectedSocket();
                if (connectedSocket != null && connectedSocket.node.canSourceBePropagatedTo(sourceIndex)) {
                    activeConnections++;
                }
            }

            if (activeConnections == 0) return;

            // actually spread the heat!
            float contributingHeat = heat / activeConnections;

            for (HeatSocket socket : sockets) {
                HeatSocket connectedSocket = socket.connectedSocket();
                if (connectedSocket != null && connectedSocket.node.canSourceBePropagatedTo(sourceIndex)) {
                    HeatNode node = connectedSocket.node;
                    float transferredHeat = socket.connection.getTransferredHeat(contributingHeat);
                    // if our pipe isn't transferring any heat than don't do anything
                    if (Mth.abs(transferredHeat) <= 0.01) continue;

                    node.addHeatSource(sourceIndex, transferredHeat);
                    node.propagate();
                }
            }
        });
    }

    public void depropagate(long sourceIndex) {
        contributedHeatBySource.remove(sourceIndex);
        for (HeatSocket socket : sockets) {
            HeatSocket connectedSocket = socket.connectedSocket();
            if (connectedSocket != null && connectedSocket.node.sourcePropagated(sourceIndex)) {
                HeatNode node = connectedSocket.node;
                node.depropagate(sourceIndex);
            }
        }
    }
}
