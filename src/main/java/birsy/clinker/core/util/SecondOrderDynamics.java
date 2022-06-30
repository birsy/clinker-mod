package birsy.clinker.core.util;

import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class SecondOrderDynamics {
    private double xp; // previous input
    private double y, yd; // state variables
    private double w, z, d, k1, k2, k3; // constants

    public SecondOrderDynamics(double frequency, double damping, double response, double xInit) {
        this.w = 2 * Math.PI * frequency;
        this.z = damping;
        this.d = this.w * Math.sqrt(Math.abs(damping * damping - 1));
        this.k1 = damping / (Math.PI * frequency);
        this.k2 = 1 / (w * w);
        this.k3 = response * damping / w;

        this.xp = xInit;
        this.y = xInit;
        this.yd = 0;
    }

    public double update(double deltaT, double x) {
        double xd = (x - xp) / deltaT;
        this.xp = x;
        return this.update(deltaT, x, xd);
    }
    
    public double update(double deltaT, double x, double xd) {
        double k1_stable, k2_stable;
        if (w * deltaT < z) {
            k1_stable = k1;
            k2_stable = Math.max(Math.max(k2, deltaT * deltaT / 2 + deltaT * k1 / 2), deltaT + k1);
        } else {
            double t1 = Math.exp(-z * w * deltaT);
            double alpha = 2 * t1 * (z <= 1 ? Mth.cos((float) (deltaT * d)) : Math.cosh(deltaT * d));
            double beta = t1 * t1;
            double t2 = deltaT / (1 + beta - alpha);
            k1_stable = (1 - beta) * t2;
            k2_stable = deltaT * t2;
        }
        this.y = y + deltaT * yd;
        this.yd = yd + deltaT * (x + k3 * xd - y - k1_stable * yd) / k2_stable;
        return y;
    }
}
