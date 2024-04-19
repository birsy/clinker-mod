package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.SkeletonFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlaceholderGnomadSkeletonFactory implements SkeletonFactory {
    private final ModelMesh[] meshes = new ModelMesh[7];

    public PlaceholderGnomadSkeletonFactory() {
        int texWidth = 64;
        int texHeight = 64;
        StaticMesh mesh = new StaticMesh(texWidth, texHeight);
        mesh.addCube(14, 30, 14, -7, -5F, -7F, 0F, 0F, 0F, 0F, 0F, false);
        meshes[0] = mesh;
    }
    
    public InterpolatedSkeleton create() {
        PlaceholderGnomadSkeleton skeleton = new PlaceholderGnomadSkeleton();
        InterpolatedBone bodyBone = new InterpolatedBone("body");
        bodyBone.setInitialTransform(0F, 5F, 0F, new Quaternionf());
        skeleton.addBone(bodyBone, meshes[0]);
        skeleton.body = bodyBone;

        skeleton.buildRoots();
        return skeleton;
    }
    
    public static class PlaceholderGnomadSkeleton extends InterpolatedSkeleton {
        protected InterpolatedBone body;
        private Vec3 smoothedAcceleration = Vec3.ZERO;
        private Vec3 smoothedVelocity = Vec3.ZERO;

        @Override
        public void animate(AnimationProperties properties) {
            GnomadEntity entity = (GnomadEntity) properties.getProperty("entity");
            body.reset();
            body.rotation.mul(this.getVelocityTilt(entity));
            body.rotation.mul(new Quaternionf().rotateTo(new Vector3f(0, 0, -1), entity.getViewVector(1.0F).toVector3f()));
            
            if (entity.isSitting()) {
                body.ySize = 0.8f;
            } else {
                body.ySize = 1.0f;
            }
        }

        public Quaternionf getAccelerationTilt(GnomadEntity entity) {
            Vec3 acceleration = entity.acceleration.scale(1);
            this.smoothedAcceleration = acceleration;
            Vector3f axis = new Vector3f(0, 1, 0).cross(smoothedAcceleration.toVector3f()).normalize();
            double precision = 1000;
            Clinker.LOGGER.info(Math.round(acceleration.x() * precision)/precision + ", " + Math.round(acceleration.y() * precision)/precision + ", " + Math.round(acceleration.z() * precision)/precision);
            float angle = (float) smoothedAcceleration.length();
            if (Math.abs(angle) < 0.01) {
                return new Quaternionf();
            }
            return new Quaternionf(new AxisAngle4f(-angle, axis));
        }

        public Quaternionf getVelocityTilt(GnomadEntity entity) {
            Vec3 velocity = entity.getPosition(1).subtract(entity.getPosition(0));
            this.smoothedVelocity = this.smoothedVelocity.lerp(velocity, 0.2F);

            Vector3f axis = new Vector3f(0, 1, 0).cross(smoothedVelocity.toVector3f()).normalize();
            float angle = (float) smoothedVelocity.length() * 2;
            if (Math.abs(angle) < 0.001) {
                return new Quaternionf();
            }
            return new Quaternionf(new AxisAngle4f(angle, axis));
        }
    }
}