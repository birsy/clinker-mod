package birsy.clinker.client.entity.mogul;

import foundry.veil.api.client.necromancer.render.Skin;

public class MogulSkin {
    protected static final Skin MOGUL_SKIN;
    static {
        Skin.Builder builder = Skin.builder(MogulSkeleton.STATIC_INSTANCE, 256, 256);
        builder.startBone("MogulRoot");

        builder.startBone("MogulBody");
        builder.addCube(36F, 6F, 36F, -18F, 20F, -19.5F, 0F, 0F, 0F, 112F, 0F, false);
        builder.addCube(36F, 20F, 42F, -18F, 0F, -19.5F, 0F, 0F, 0F, 100F, 42F, false);

        builder.startBone("MogulFrontRobe");
        builder.addCube(36F, 25F, 2F, -18F, -24F, 0F, -0.1F, 0F, 0F, 104F, 131F, false);

        builder.startBone("MogulBackRobe");
        builder.addCube(36F, 25F, 2F, -18F, -24F, -2F, -0.1F, 0F, 0F, 180F, 131F, false);

        builder.startBone("MogulLeftRobe");
        builder.addCube(42F, 25F, 2F, -21F, -24F, 0F, -0.1F, 0F, 0F, 168F, 104F, false);

        builder.startBone("MogulRightRobe");
        builder.addCube(42F, 25F, 2F, -21F, -24F, 0F, -0.1F, 0F, 0F, 80F, 104F, false);

        builder.startBone("MogulRightArm");
        builder.addCube(5F, 13F, 6F, -3F, -13.5F, -3F, 0F, 0F, 0F, 0F, 113F, false);
        builder.addCube(4F, 19F, 4F, -2.5F, -32.5F, -1.5F, 0F, 0F, 0F, 0F, 132F, false);
        builder.addCube(4F, 10.5F, 4F, -2.5F, -24.5F, -1.5F, 0.4F, 0.4F, 0.4F, 16F, 133F, false);

        builder.startBone("MogulRightLeg");
        builder.addCube(5F, 24F, 6F, -2.5F, -24F, -3F, 0F, 0F, 0F, 32F, 113F, false);
        builder.addCube(5F, 9.5F, 6F, -2.5F, -24F, -3F, 0.25F, 0.25F, 0.25F, 54F, 128F, false);

        builder.startBone("MogulLeftLeg");
        builder.addCube(5F, 24F, 6F, -2.5F, -24F, -3F, 0F, 0F, 0F, 32F, 113F, true);
        builder.addCube(5F, 9.5F, 6F, -2.5F, -24F, -3F, 0.25F, 0.25F, 0.25F, 54F, 128F, true);

        builder.startBone("MogulLeftArm");
        builder.addCube(5F, 13F, 6F, -2.5F, -13.5F, -3F, 0F, 0F, 0F, 0F, 113F, true);
        builder.addCube(4F, 10.5F, 4F, -2F, -24.5F, -1.5F, 0.4F, 0.4F, 0.4F, 16F, 133F, true);
        builder.addCube(4F, 19F, 4F, -2F, -32.5F, -1.5F, 0F, 0F, 0F, 0F, 132F, true);

        builder.startBone("MogulNeck");
        builder.addCube(12F, 9F, 14F, -6F, -3.25F, -10F, 0F, 0F, 0F, 64F, 0F, false);

        builder.startBone("MogulHead");
        builder.addCube(18F, 16F, 14F, -9F, -5F, -10.5F, 0F, 0F, 0F, 0F, 0F, false);

        builder.startBone("MogulFace");
        builder.addCube(18F, 4F, 4F, -9F, -11.666666666666664F, -3.5F, 0F, 0F, 0F, 2F, 48F, false);
        builder.addCube(22F, 16F, 4F, -11F, -7.666666666666664F, -3.5F, 0F, 0F, 0F, 0F, 32F, false);
        builder.addCube(18F, 2F, 4F, -9F, 8.333333333333336F, -3.5F, 0F, 0F, 0F, 2F, 30F, false);

        builder.startBone("MogulNose");
        builder.addCube(6F, 6F, 6F, -3F, -6F, 0F, 0F, 0F, 0F, 52F, 30F, false);

        builder.startBone("MogulHelmetBase");
        builder.addCube(20F, 8F, 18F, -10F, -2.25F, -10F, 0F, 0F, 0F, 0F, 63F, false);
        builder.addCube(14F, 3F, 2F, -7F, 5.75F, -9F, 0F, 0F, 0F, 0F, 56F, false);

        builder.startBone("MogulHelmetUpper");
        builder.addCube(16F, 6F, 12F, -8F, -0.25F, 0F, 0F, 0F, 0F, 58F, 63F, false);
        builder.addCube(10F, 5F, 12F, -5F, 5.75F, 0F, 0F, 0F, 0F, 58F, 46F, false);

        builder.startBone("MogulHelmetOrnament");
        builder.addCube(5F, 9F, 0F, -2.5F, 0F, -7.105427357601002e-15F, 0F, 0F, 0F, 0F, 63F, false);

        builder.startBone("MogulBackHelmetFlap");
        builder.addCube(20F, 10F, 2F, -10F, -10F, -1F, 0F, 0F, 0F, 0F, 89F, false);

        builder.startBone("MogulLeftHelmetFlap");
        builder.addCube(15F, 10F, 2F, -7.5F, -10F, -1F, 0F, 0F, 0F, 34F, 101F, false);

        builder.startBone("MogulRightHelmetFlap");
        builder.addCube(15F, 10F, 2F, -7.5F, -10F, -1F, 0F, 0F, 0F, 0F, 101F, false);

        MOGUL_SKIN = builder.build();
    }
}
