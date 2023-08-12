package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.ModelFactory;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.core.util.Quaternionf;

public class FaeUrnModelFactory implements ModelFactory {
    private final ModelMesh[] meshes = new ModelMesh[10];

    public FaeUrnModelFactory() {
        StaticMesh mesh0 = new StaticMesh();
        mesh0.addCube(22F, 16F, 22F, -11F, 2F, -11F, 0F, 0F, 0F, 0F, 0F);
        mesh0.addCube(14F, 22F, 2F, -8F, 0F, -8F, 0F, 0F, 0F, 0F, 38F);
        mesh0.addCube(14F, 22F, 2F, -8F, 0F, -8F, 0F, 0F, 0F, 0F, 38F);
        mesh0.addCube(14F, 22F, 2F, -8F, 0F, -8F, 0F, 0F, 0F, 0F, 38F);
        mesh0.addCube(12F, 2F, 12F, -6F, 0F, -6F, 0F, 0F, 0F, 32F, 38F);
        mesh0.addCube(14F, 22F, 2F, -8F, 0F, -8F, 0F, 0F, 0F, 0F, 38F);
        meshes[0] = mesh0;

        StaticMesh mesh1 = new StaticMesh();
        mesh1.addCube(12F, 2F, 12F, -6F, -1F, -6F, 0F, 0F, 0F, 80F, 38F);
        mesh1.addCube(5F, 7F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 80F, 38F);
        mesh1.addCube(5F, 7F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 80F, 38F);
        meshes[1] = mesh1;

        StaticMesh mesh2 = new StaticMesh();
        mesh2.addCube(2F, 9F, 2F, -1F, -9F, -1F, 0F, 0F, 0F, 80F, 0F);
        meshes[2] = mesh2;

        StaticMesh mesh3 = new StaticMesh();
        mesh3.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh3.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh3.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh3.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh3.addCube(5F, 18F, 5F, -2.5F, -34F, -2.5F, 0F, 0F, 0F, 88F, 0F);
        mesh3.addCube(3F, 26F, 3F, -1.5F, -40F, -1.5F, 0F, 0F, 0F, 108F, 0F);
        meshes[3] = mesh3;

        StaticMesh mesh4 = new StaticMesh();
        mesh4.addCube(2F, 9F, 2F, -1F, -9F, -1F, 0F, 0F, 0F, 80F, 0F);
        meshes[4] = mesh4;

        StaticMesh mesh5 = new StaticMesh();
        mesh5.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh5.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh5.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh5.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh5.addCube(5F, 18F, 5F, -2.5F, -34F, -2.5F, 0F, 0F, 0F, 88F, 0F);
        mesh5.addCube(3F, 26F, 3F, -1.5F, -40F, -1.5F, 0F, 0F, 0F, 108F, 0F);
        meshes[5] = mesh5;

        StaticMesh mesh6 = new StaticMesh();
        mesh6.addCube(2F, 9F, 2F, -1F, -9F, -1F, 0F, 0F, 0F, 80F, 0F);
        meshes[6] = mesh6;

        StaticMesh mesh7 = new StaticMesh();
        mesh7.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh7.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh7.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh7.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh7.addCube(5F, 18F, 5F, -2.5F, -34F, -2.5F, 0F, 0F, 0F, 88F, 0F);
        mesh7.addCube(3F, 26F, 3F, -1.5F, -40F, -1.5F, 0F, 0F, 0F, 108F, 0F);
        meshes[7] = mesh7;

        StaticMesh mesh8 = new StaticMesh();
        mesh8.addCube(2F, 9F, 2F, -1F, -9F, -1F, 0F, 0F, 0F, 80F, 0F);
        meshes[8] = mesh8;

        StaticMesh mesh9 = new StaticMesh();
        mesh9.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh9.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh9.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh9.addCube(5F, 2F, 0F, -2.5F, -2F, 0F, 0F, 0F, 0F, 88F, 23F);
        mesh9.addCube(5F, 18F, 5F, -2.5F, -34F, -2.5F, 0F, 0F, 0F, 88F, 0F);
        mesh9.addCube(3F, 26F, 3F, -1.5F, -40F, -1.5F, 0F, 0F, 0F, 108F, 0F);
        meshes[9] = mesh9;

    }

    public InterpolatedSkeleton create() {
        FaeUrnModel model = new FaeUrnModel();
        InterpolatedBone FaeUrnModelPart = new InterpolatedBone("FaeUrn");
        FaeUrnModelPart.setInitialTransform(0F, 11F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnModelPart, meshes[0]);
        model.FaeUrn = FaeUrnModelPart;

        InterpolatedBone FaeUrnLidModelPart = new InterpolatedBone("FaeUrnLid");
        FaeUrnLidModelPart.setInitialTransform(0F, 20.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnLidModelPart, meshes[1]);
        model.FaeUrnLid = FaeUrnLidModelPart;

        InterpolatedBone FaeUrnFrontRightUpperLegModelPart = new InterpolatedBone("FaeUrnFrontRightUpperLeg");
        FaeUrnFrontRightUpperLegModelPart.setInitialTransform(2F, 0F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnFrontRightUpperLegModelPart, meshes[2]);
        model.FaeUrnFrontRightUpperLeg = FaeUrnFrontRightUpperLegModelPart;

        InterpolatedBone FaeUrnFrontRightLowerLegModelPart = new InterpolatedBone("FaeUrnFrontRightLowerLeg");
        FaeUrnFrontRightLowerLegModelPart.setInitialTransform(2F, -9F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnFrontRightLowerLegModelPart, meshes[3]);
        model.FaeUrnFrontRightLowerLeg = FaeUrnFrontRightLowerLegModelPart;

        InterpolatedBone FaeUrnBackRightUpperLegModelPart = new InterpolatedBone("FaeUrnBackRightUpperLeg");
        FaeUrnBackRightUpperLegModelPart.setInitialTransform(2F, 0F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnBackRightUpperLegModelPart, meshes[4]);
        model.FaeUrnBackRightUpperLeg = FaeUrnBackRightUpperLegModelPart;

        InterpolatedBone FaeUrnBackRightLowerLegModelPart = new InterpolatedBone("FaeUrnBackRightLowerLeg");
        FaeUrnBackRightLowerLegModelPart.setInitialTransform(2F, -9F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnBackRightLowerLegModelPart, meshes[5]);
        model.FaeUrnBackRightLowerLeg = FaeUrnBackRightLowerLegModelPart;

        InterpolatedBone FaeUrnFrontLeftUpperLegModelPart = new InterpolatedBone("FaeUrnFrontLeftUpperLeg");
        FaeUrnFrontLeftUpperLegModelPart.setInitialTransform(-3F, 0F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnFrontLeftUpperLegModelPart, meshes[6]);
        model.FaeUrnFrontLeftUpperLeg = FaeUrnFrontLeftUpperLegModelPart;

        InterpolatedBone FaeUrnFrontLeftLowerLegModelPart = new InterpolatedBone("FaeUrnFrontLeftLowerLeg");
        FaeUrnFrontLeftLowerLegModelPart.setInitialTransform(-3F, -9F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnFrontLeftLowerLegModelPart, meshes[7]);
        model.FaeUrnFrontLeftLowerLeg = FaeUrnFrontLeftLowerLegModelPart;

        InterpolatedBone FaeUrnBackLeftUpperLegModelPart = new InterpolatedBone("FaeUrnBackLeftUpperLeg");
        FaeUrnBackLeftUpperLegModelPart.setInitialTransform(-3F, 0F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnBackLeftUpperLegModelPart, meshes[8]);
        model.FaeUrnBackLeftUpperLeg = FaeUrnBackLeftUpperLegModelPart;

        InterpolatedBone FaeUrnBackLeftLowerLegModelPart = new InterpolatedBone("FaeUrnBackLeftLowerLeg");
        FaeUrnBackLeftLowerLegModelPart.setInitialTransform(-3F, -9F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addBone(FaeUrnBackLeftLowerLegModelPart, meshes[9]);
        model.FaeUrnBackLeftLowerLeg = FaeUrnBackLeftLowerLegModelPart;

        return model;
    }

    public static class FaeUrnModel extends InterpolatedSkeleton {
        protected InterpolatedBone FaeUrn;
        protected InterpolatedBone FaeUrnLid;
        protected InterpolatedBone FaeUrnFrontRightUpperLeg;
        protected InterpolatedBone FaeUrnFrontRightLowerLeg;
        protected InterpolatedBone FaeUrnBackRightUpperLeg;
        protected InterpolatedBone FaeUrnBackRightLowerLeg;
        protected InterpolatedBone FaeUrnFrontLeftUpperLeg;
        protected InterpolatedBone FaeUrnFrontLeftLowerLeg;
        protected InterpolatedBone FaeUrnBackLeftUpperLeg;
        protected InterpolatedBone FaeUrnBackLeftLowerLeg;

        @Override
        public void animate(AnimationProperties properties) {

        }
    }
}