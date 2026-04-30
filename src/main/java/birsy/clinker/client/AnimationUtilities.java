package birsy.clinker.client;

import net.minecraft.util.Mth;

public class AnimationUtilities {
    public static float nSin(float time) {
        return Mth.sin(time * Mth.PI);
    }

    public static class SurveyorWheel {
        float radius, pRadius;
        float distance, pDistance;
        float angle, pAngle;

        public SurveyorWheel(float startingRadius) {
            this.radius = startingRadius; this.pRadius = startingRadius;
        }

        public void update(float nextRadius, float nextDistance) {
            this.pDistance = distance;
            this.distance = nextDistance;

            this.pRadius = radius;
            this.radius = nextRadius;

            float distanceTravelled = distance - pDistance;

            float deltaAngle;
            if (Math.abs(radius - pRadius) < 0.001) {
                deltaAngle = distanceTravelled / radius;
            } else {
                deltaAngle = distanceTravelled * (float)(Math.log(radius / pRadius)) / (radius - pRadius);
            }

            this.pAngle = this.angle;
            this.angle += deltaAngle;
        }

        public float radius() { return radius; }
        public float radius(float partialTicks) { return Mth.lerp(partialTicks, pRadius, radius); }
        public float distance() { return distance; }
        public float distance(float partialTicks) { return Mth.lerp(partialTicks, pDistance, distance); }
        public float angle() { return angle; }
        public float angle(float partialTicks) { return Mth.lerp(partialTicks, pAngle, angle); }
    }
}
