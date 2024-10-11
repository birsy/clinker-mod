package birsy.clinker.common.world.level.heatnetwork;

import java.util.function.Function;

public class HeatSourceNode extends HeatNode {
    protected final long sourceIndex;
    protected final Function<HeatSourceNode, Float> outputHeatSupplier;

    public HeatSourceNode(long sourceIndex, int sockets, Function<HeatSourceNode, Float> outputHeatSupplier) {
        super(sockets);
        this.sourceIndex = sourceIndex;
        this.outputHeatSupplier = outputHeatSupplier;
    }

    public HeatSourceNode(long sourceIndex, int sockets, float temperature) {
        this(sourceIndex, sockets, (node) -> temperature);
    }

    public float getOutputHeat() {
        return outputHeatSupplier.apply(this);
    }

    public void depropagate() {
        for (HeatSocket socket : sockets) {
            HeatSocket connectedSocket = socket.connectedSocket();
            if (connectedSocket != null && connectedSocket.node.sourcePropagated(this.sourceIndex)) {
                HeatNode node = connectedSocket.node;
                node.depropagate(this.sourceIndex);
            }
        }
    }

    @Override
    public float getTotalHeat() {
        return this.getOutputHeat();
    }

    @Override
    public void depropagate(long sourceIndex) {
        if (sourceIndex != this.sourceIndex) return;
        super.depropagate(sourceIndex);
    }

    @Override
    protected boolean canSourceBePropagatedTo(long sourceIndex) {
        return false;
    }

    @Override
    public void propagate() {
        int activeConnections = 0;
        for (HeatSocket socket : sockets) {
            HeatSocket connectedSocket = socket.connectedSocket();
            if (connectedSocket != null && connectedSocket.node.canSourceBePropagatedTo(this.sourceIndex)) {
                activeConnections++;
            }
        }

        if (activeConnections == 0) return;

        // actually spread the heat!
        float contributingHeat = this.getOutputHeat() / activeConnections;
        for (HeatSocket socket : sockets) {
            HeatSocket connectedSocket = socket.connectedSocket();
            if (connectedSocket != null && connectedSocket.node.canSourceBePropagatedTo(this.sourceIndex)) {
                HeatNode node = connectedSocket.node;
                float transferredHeat = socket.connection.getTransferredHeat(contributingHeat);
                node.addHeatSource(sourceIndex, transferredHeat);
                node.propagate();
            }
        }
    }
}
