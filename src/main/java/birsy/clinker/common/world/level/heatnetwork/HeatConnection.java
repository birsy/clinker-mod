package birsy.clinker.common.world.level.heatnetwork;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;

public class HeatConnection {
    final HeatSocket a, b;
    final float efficiency, loss;

    public HeatConnection(HeatSocket a, HeatSocket b, float efficiency, float loss) {
        this.a = a;
        this.b = b;
        this.efficiency = efficiency;
        this.loss = loss;

        if (a.node != b.node) {
            a.node.repropagateRelevantSources();
            b.node.repropagateRelevantSources();
        } else {
            Clinker.LOGGER.error("Please don't try to attach a heat node to itself. That doesn't make sense.");
        }
    }

    public float getTransferredHeat(float heatIn) {
        return MathUtils.approach(heatIn * efficiency, 0, loss);
    }

    public static float calculateEfficiencyGiven(float initialValue, float loss, float range) {
        return (float) Math.pow(loss / (initialValue + loss), 1.0 / range);
    }
}
