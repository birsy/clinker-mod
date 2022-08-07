package birsy.clinker.core.util;

import net.minecraft.util.Mth;

public class SecondOrderDynamics {
    private float xp; // previous input
    private float y, yd; // state variables
    private float w, z, d, k1, k2, k3; // constants
    public boolean enabled = true;

    public SecondOrderDynamics() {
        this.enabled = false;
    }

    public SecondOrderDynamics(float frequency, float damping, float response, float xInit) {
        this.w = (float) (2 * Math.PI * frequency);
        this.z = damping;
        this.d = (float) (this.w * Math.sqrt(Math.abs(damping * damping - 1)));
        this.k1 = (float) (damping / (Math.PI * frequency));
        this.k2 = 1 / (w * w);
        this.k3 = response * damping / w;

        this.xp = xInit;
        this.y = xInit;
        this.yd = 0;
    }

    public void createDynamics(float frequency, float damping, float response, float xInit) {
        this.setDynamics(frequency, damping, response);
        this.xp = xInit;
        this.y = xInit;
        this.yd = 0;
        this.enabled = true;
    }


    public void setDynamics(float frequency, float damping, float response) {
        this.w = (float) (2 * Math.PI * frequency);
        this.z = damping;
        this.d = (float) (this.w * Math.sqrt(Math.abs(damping * damping - 1)));
        this.k1 = (float) (damping / (Math.PI * frequency));
        this.k2 = 1 / (w * w);
        this.k3 = response * damping / w;
    }

    public float update(float deltaT, float x) {
        if (enabled) {
            float xd = (x - xp) / deltaT;
            this.xp = x;
            return this.update(deltaT, x, xd);
        } else {
            y = x;
            return y;
        }
    }
    
    public float update(float deltaT, float x, float xd) {
        if (enabled) {
            float k1_stable, k2_stable;
            if (w * deltaT < z) {
                k1_stable = k1;
                k2_stable = Math.max(Math.max(k2, deltaT * deltaT / 2 + deltaT * k1 / 2), deltaT + k1);
            } else {
                float t1 = (float) Math.exp(-z * w * deltaT);
                float alpha = (float) (2 * t1 * (z <= 1 ? Mth.cos(deltaT * d) : Math.cosh(deltaT * d)));
                float beta = t1 * t1;
                float t2 = deltaT / (1 + beta - alpha);
                k1_stable = (1 - beta) * t2;
                k2_stable = deltaT * t2;
            }
            this.y = y + deltaT * yd;
            this.yd = yd + deltaT * (x + k3 * xd - y - k1_stable * yd) / k2_stable;
        } else {
            y = x;
        }
        return y;
    }

    public float getValue() {
        return y;
    }
}
