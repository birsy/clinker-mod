package birsy.clinker.client.entity.slabcrab;

import foundry.veil.api.client.necromancer.render.Skin;

public class SlabCrabSkin {
    protected static final Skin SKIN;
    static {
        Skin.Builder builder = Skin.builder(64, 64);
        builder.startBone("body");
        builder.addCube(6F, 3F, 16F, -3F, 0F, -8F, 0F, 0F, 0F, 0F, 19F, false);
        builder.addCube(16F, 3F, 16F, -8F, 3F, -8F, 0F, 0F, 0F, 0F, 0F, false);
        builder.addCube(5F, 3F, 3F, 3F, 0F, 5F, 0F, 0F, 0F, 0F, 19F, false);
        builder.addCube(5F, 3F, 3F, -8F, 0F, 5F, 0F, 0F, 0F, 28F, 19F, false);

        builder.startBone("rightLeg0");
        builder.addCube(5F, 4F, 2F, -5F, -3F, -1F, 0F, 0F, 0F, 44F, 19F, false);

        builder.startBone("leftLeg0");
        builder.addCube(5F, 4F, 2F, 0F, -3F, -1F, 0F, 0F, 0F, 44F, 19F, true);

        builder.startBone("rightLeg1");
        builder.addCube(5F, 4F, 2F, -5F, -3F, -1F, 0F, 0F, 0F, 44F, 25F, false);

        builder.startBone("leftLeg1");
        builder.addCube(5F, 4F, 2F, 0F, -3F, -1F, 0F, 0F, 0F, 44F, 25F, true);

        builder.startBone("rightLeg2");
        builder.addCube(5F, 4F, 2F, -5F, -3F, -1F, 0F, 0F, 0F, 44F, 31F, false);

        builder.startBone("leftLeg2");
        builder.addCube(5F, 4F, 2F, 0F, -3F, -1F, 0F, 0F, 0F, 44F, 31F, true);

        builder.startBone("rightLeg3");
        builder.addCube(5F, 4F, 2F, -5F, -3F, -1F, 0F, 0F, 0F, 44F, 37F, false);

        builder.startBone("leftLeg3");
        builder.addCube(5F, 4F, 2F, 0F, -3F, -1F, 0F, 0F, 0F, 44F, 37F, true);

        builder.startBone("rightClaw");
        builder.addCube(5F, 3F, 5F, -4F, -3F, -4F, 0F, 0F, 0F, 0F, 38F, false);

        builder.startBone("leftClaw");
        builder.addCube(5F, 3F, 5F, -1F, -3F, -4F, 0F, 0F, 0F, 20F, 38F, false);

        builder.startBone("rightMandible");
        builder.addCube(3F, 3F, 0F, -3F, -1F, 0F, 0F, 0F, 0F, 0F, 4F, false);

        builder.startBone("leftMandible");
        builder.addCube(3F, 3F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 6F, 4F, false);

        builder.startBone("rightEye");
        builder.addCube(2F, 4F, 0F, -0.5F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, false);

        builder.startBone("leftEye");
        builder.addCube(2F, 4F, 0F, -1.5F, -1F, 0F, 0F, 0F, 0F, 4F, 0F, false);

        builder.startBone("root");

        SKIN = builder.build();
    }
}