package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.*;
import birsy.clinker.client.model.base.mesh.StaticMesh;
import birsy.clinker.core.util.Quaternionf;

public class FaeUrnModelFactory implements ModelFactory {
    private final StaticMesh[] meshes = new StaticMesh[10];

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
        InterpolatedSkeleton model = new InterpolatedSkeleton() {
            @Override
            protected void animate(AnimationProperties properties) {}
        };
        InterpolatedBone FaeUrnModelPart = new InterpolatedBone("FaeUrn");
        FaeUrnModelPart.setInitialTransform(0F, 11F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnModelPart, meshes[0]);

        InterpolatedBone FaeUrnLidModelPart = new InterpolatedBone("FaeUrnLid");
        FaeUrnLidModelPart.setInitialTransform(0F, 20.5F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnLidModelPart, meshes[1]);

        InterpolatedBone FaeUrnFrontRightUpperLegModelPart = new InterpolatedBone("FaeUrnFrontRightUpperLeg");
        FaeUrnFrontRightUpperLegModelPart.setInitialTransform(2F, 0F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnFrontRightUpperLegModelPart, meshes[2]);

        InterpolatedBone FaeUrnFrontRightLowerLegModelPart = new InterpolatedBone("FaeUrnFrontRightLowerLeg");
        FaeUrnFrontRightLowerLegModelPart.setInitialTransform(2F, -9F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnFrontRightLowerLegModelPart, meshes[3]);

        InterpolatedBone FaeUrnBackRightUpperLegModelPart = new InterpolatedBone("FaeUrnBackRightUpperLeg");
        FaeUrnBackRightUpperLegModelPart.setInitialTransform(2F, 0F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnBackRightUpperLegModelPart, meshes[4]);

        InterpolatedBone FaeUrnBackRightLowerLegModelPart = new InterpolatedBone("FaeUrnBackRightLowerLeg");
        FaeUrnBackRightLowerLegModelPart.setInitialTransform(2F, -9F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnBackRightLowerLegModelPart, meshes[5]);

        InterpolatedBone FaeUrnFrontLeftUpperLegModelPart = new InterpolatedBone("FaeUrnFrontLeftUpperLeg");
        FaeUrnFrontLeftUpperLegModelPart.setInitialTransform(-3F, 0F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnFrontLeftUpperLegModelPart, meshes[6]);

        InterpolatedBone FaeUrnFrontLeftLowerLegModelPart = new InterpolatedBone("FaeUrnFrontLeftLowerLeg");
        FaeUrnFrontLeftLowerLegModelPart.setInitialTransform(-3F, -9F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnFrontLeftLowerLegModelPart, meshes[7]);

        InterpolatedBone FaeUrnBackLeftUpperLegModelPart = new InterpolatedBone("FaeUrnBackLeftUpperLeg");
        FaeUrnBackLeftUpperLegModelPart.setInitialTransform(-3F, 0F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnBackLeftUpperLegModelPart, meshes[8]);

        InterpolatedBone FaeUrnBackLeftLowerLegModelPart = new InterpolatedBone("FaeUrnBackLeftLowerLeg");
        FaeUrnBackLeftLowerLegModelPart.setInitialTransform(-3F, -9F, 2F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        model.addModelPart(FaeUrnBackLeftLowerLegModelPart, meshes[9]);

        return model;
    }
}