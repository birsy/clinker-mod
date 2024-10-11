package birsy.clinker.common.world.level.heatgraph;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Heat node attached to a tile entity.
 * <p>
 * Queues updates until next tick,
 * allowing changes to the network
 * to propagate over time!
 */
public class TileHeatNode extends HeatNode {
    final BlockEntity entity;
    final private Queue<Runnable> queuedUpdates;

    public TileHeatNode(int sockets, BlockEntity entity) {
        super(sockets);
        this.entity = entity;
        this.queuedUpdates = new ArrayDeque<>();
    }

    public void update() {
        for (int i = 0; i < queuedUpdates.size(); i++) {
            queuedUpdates.remove().run();
        }
    }

    @Override
    public void propagate() {
        queuedUpdates.add(super::propagate);
    }

    @Override
    public void depropagate(long sourceIndex) {
        queuedUpdates.add(() -> super.depropagate(sourceIndex));
    }
}
