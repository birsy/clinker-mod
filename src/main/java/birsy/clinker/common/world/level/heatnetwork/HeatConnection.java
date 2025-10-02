package birsy.clinker.common.world.level.heatnetwork;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;

import java.util.Collection;

public class HeatConnection {
    public final HeatSocket a, b;
    final float efficiency, loss;

    public HeatConnection(HeatSocket a, HeatSocket b, float efficiency, float loss) {
        this.a = a;
        this.b = b;
        this.efficiency = efficiency;
        this.loss = loss;

        if (a.node != b.node) {
            Collection<HeatSourceNode> aSources = a.node.collectRelevantHeatSources();
            Collection<HeatSourceNode> bSources = b.node.collectRelevantHeatSources();

            a.connection = this;
            b.connection = this;

            a.node.repropagateRelevantSources(aSources);
            b.node.repropagateRelevantSources(bSources);
        } else {
            Clinker.LOGGER.error("Please don't try to attach a heat node to itself. That doesn't make sense.");
        }
    }

    public void sever() {
        this.sever(true, true);
    }

    public void sever(boolean repropagateA, boolean repropagateB) {
        Collection<HeatSourceNode> aSources = null, bSources = null;
        if (repropagateA) aSources = a.node.collectRelevantHeatSources();
        if (repropagateB) bSources = b.node.collectRelevantHeatSources();

        a.connection = null;
        b.connection = null;

        if (repropagateA) a.node.repropagateRelevantSources(aSources);
        if (repropagateB) b.node.repropagateRelevantSources(bSources);
    }
    
    public float getTransferredHeat(float heatIn) {
        return MathUtils.approach(heatIn * efficiency, 0, loss);
    }

    public static float calculateEfficiencyGiven(float initialValue, float loss, float range) {
        return (float) Math.pow(loss / (initialValue + loss), 1.0 / range);
    }
}
