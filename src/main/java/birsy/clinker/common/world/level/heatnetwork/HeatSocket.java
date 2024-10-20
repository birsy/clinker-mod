package birsy.clinker.common.world.level.heatnetwork;

import javax.annotation.Nullable;

public class HeatSocket {
    public final HeatNode node;
    HeatConnection connection;

    public HeatSocket(HeatNode node) {
        this.node = node;
    }

    public boolean connected() {
        return this.connection != null;
    }

    public HeatConnection connection() {
        return this.connection;
    }

    @Nullable
    public HeatSocket connectedSocket() {
        if (!this.connected()) return null;
        return connection.a == this ? connection.b : connection.a;
    }
}
