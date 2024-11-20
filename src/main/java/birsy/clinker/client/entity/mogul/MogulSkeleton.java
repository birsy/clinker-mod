package birsy.clinker.client.entity.mogul;

import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import org.joml.Quaternionf;

public class MogulSkeleton extends Skeleton<GnomadMogulEntity> {
    protected Bone MogulRoot, MogulBody, MogulFrontRobe, MogulBackRobe, MogulLeftRobe, MogulRightRobe, MogulRightArm, MogulRightLeg, MogulLeftLeg, MogulLeftArm, MogulNeck, MogulHead, MogulFace, MogulNose, MogulHelmetBase, MogulHelmetUpper, MogulHelmetOrnament, MogulBackHelmetFlap, MogulLeftHelmetFlap, MogulRightHelmetFlap;
    public Bone MogulRightArmGrasp, MogulLeftArmGrasp;

    protected MogulSkeleton(GnomadMogulEntity parent) {
        super();
        Bone MogulRootBone = new Bone("MogulRoot");
        MogulRootBone.setInitialTransform(0F, 0F, -1.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulRootBone);
        this.MogulRoot = MogulRootBone;

        Bone MogulBodyBone = new Bone("MogulBody");
        MogulBodyBone.setInitialTransform(0F, 24F, 0F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulBodyBone);
        this.MogulBody = MogulBodyBone;

        Bone MogulFrontRobeBone = new Bone("MogulFrontRobe");
        MogulFrontRobeBone.setInitialTransform(0F, 0F, -19.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulFrontRobeBone);
        this.MogulFrontRobe = MogulFrontRobeBone;

        Bone MogulBackRobeBone = new Bone("MogulBackRobe");
        MogulBackRobeBone.setInitialTransform(0F, 0F, 22.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulBackRobeBone);
        this.MogulBackRobe = MogulBackRobeBone;

        Bone MogulLeftRobeBone = new Bone("MogulLeftRobe");
        MogulLeftRobeBone.setInitialTransform(-18F, 0F, 1.5F, new Quaternionf().rotationZYX(-0.17453292509999999F, 1.5707963259F, 0F));
        this.addBone(MogulLeftRobeBone);
        this.MogulLeftRobe = MogulLeftRobeBone;

        Bone MogulRightRobeBone = new Bone("MogulRightRobe");
        MogulRightRobeBone.setInitialTransform(18F, 0F, 1.5F, new Quaternionf().rotationZYX(0.17453292509999999F, -1.5707963259F, 0F));
        this.addBone(MogulRightRobeBone);
        this.MogulRightRobe = MogulRightRobeBone;

        Bone MogulRightArmBone = new Bone("MogulRightArm");
        MogulRightArmBone.setInitialTransform(19F, 16F, -8F, new Quaternionf().rotationZYX(0.15514701159461386F, 0F, 0.34906585019999997F));
        this.addBone(MogulRightArmBone);
        this.MogulRightArm = MogulRightArmBone;

        Bone MogulRightLegBone = new Bone("MogulRightLeg");
        MogulRightLegBone.setInitialTransform(10.5F, 0F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulRightLegBone);
        this.MogulRightLeg = MogulRightLegBone;

        Bone MogulLeftLegBone = new Bone("MogulLeftLeg");
        MogulLeftLegBone.setInitialTransform(-10.5F, 0F, 1.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulLeftLegBone);
        this.MogulLeftLeg = MogulLeftLegBone;

        Bone MogulLeftArmBone = new Bone("MogulLeftArm");
        MogulLeftArmBone.setInitialTransform(-18F, 16F, -8F, new Quaternionf().rotationZYX(-0.11875624137966828F, 0.05519076687280652F, 0.43305050170380455F));
        this.addBone(MogulLeftArmBone);
        this.MogulLeftArm = MogulLeftArmBone;

        Bone MogulNeckBone = new Bone("MogulNeck");
        MogulNeckBone.setInitialTransform(0F, 18.25F, -19F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulNeckBone);
        this.MogulNeck = MogulNeckBone;

        Bone MogulHeadBone = new Bone("MogulHead");
        MogulHeadBone.setInitialTransform(0F, 4.25F, -9.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulHeadBone);
        this.MogulHead = MogulHeadBone;

        Bone MogulFaceBone = new Bone("MogulFace");
        MogulFaceBone.setInitialTransform(0F, 2.6666666666666643F, -10.5F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulFaceBone);
        this.MogulFace = MogulFaceBone;

        Bone MogulNoseBone = new Bone("MogulNose");
        MogulNoseBone.setInitialTransform(0F, 5.333333333333336F, -3.5F, new Quaternionf().rotationZYX(0F, 0F, 0.5235987752999999F));
        this.addBone(MogulNoseBone);
        this.MogulNose = MogulNoseBone;

        Bone MogulHelmetBaseBone = new Bone("MogulHelmetBase");
        MogulHelmetBaseBone.setInitialTransform(0F, 10.25F, -3F, new Quaternionf().rotationZYX(0F, 0F, 0F));
        this.addBone(MogulHelmetBaseBone);
        this.MogulHelmetBase = MogulHelmetBaseBone;

        Bone MogulHelmetUpperBone = new Bone("MogulHelmetUpper");
        MogulHelmetUpperBone.setInitialTransform(0F, 6F, -6F, new Quaternionf().rotationZYX(0F, 0F, 0.08726646254999999F));
        this.addBone(MogulHelmetUpperBone);
        this.MogulHelmetUpper = MogulHelmetUpperBone;

        Bone MogulHelmetOrnamentBone = new Bone("MogulHelmetOrnament");
        MogulHelmetOrnamentBone.setInitialTransform(0F, 10.75F, 6.000000000000007F, new Quaternionf().rotationZYX(0.08726646255000002F, -4.3368086874712995e-19F, 0.08726646254999999F));
        this.addBone(MogulHelmetOrnamentBone);
        this.MogulHelmetOrnament = MogulHelmetOrnamentBone;

        Bone MogulBackHelmetFlapBone = new Bone("MogulBackHelmetFlap");
        MogulBackHelmetFlapBone.setInitialTransform(0F, -1.25F, 8F, new Quaternionf().rotationZYX(0F, 0F, -1.1344640131500001F));
        this.addBone(MogulBackHelmetFlapBone);
        this.MogulBackHelmetFlap = MogulBackHelmetFlapBone;

        Bone MogulLeftHelmetFlapBone = new Bone("MogulLeftHelmetFlap");
        MogulLeftHelmetFlapBone.setInitialTransform(-10F, -1.25F, 0.5F, new Quaternionf().rotationZYX(-0.6981317003999999F, 1.5707963259F, 0F));
        this.addBone(MogulLeftHelmetFlapBone);
        this.MogulLeftHelmetFlap = MogulLeftHelmetFlapBone;

        Bone MogulRightHelmetFlapBone = new Bone("MogulRightHelmetFlap");
        MogulRightHelmetFlapBone.setInitialTransform(10F, -1.25F, 0.5F, new Quaternionf().rotationZYX(0.6981317003999999F, -1.5707963259F, 0F));
        this.addBone(MogulRightHelmetFlapBone);
        this.MogulRightHelmetFlap = MogulRightHelmetFlapBone;

        Bone MogulRightArmGraspBone = new Bone("MogulRightArmGrasp");
        MogulRightArmGraspBone.setInitialTransform(-0.5F, -30.5F, -1.5F, new Quaternionf().rotationZYX(0F, 0F, -1.5707963258999997F));
        this.addBone(MogulRightArmGraspBone);
        this.MogulRightArmGrasp = MogulRightArmGraspBone;
        Bone MogulLeftArmGraspBone = new Bone("MogulLeftArmGrasp");
        MogulLeftArmGraspBone.setInitialTransform(-0.5F, -30.5F, -1.5F, new Quaternionf().rotationZYX(0F, 0F, -1.5707963258999997F));
        this.addBone(MogulLeftArmGraspBone);
        this.MogulLeftArmGrasp = MogulLeftArmGraspBone;

        MogulRootBone.addChild(MogulBodyBone);
        MogulBodyBone.addChild(MogulFrontRobeBone);
        MogulBodyBone.addChild(MogulBackRobeBone);
        MogulBodyBone.addChild(MogulLeftRobeBone);
        MogulBodyBone.addChild(MogulRightRobeBone);
        MogulBodyBone.addChild(MogulRightArmBone);
        MogulBodyBone.addChild(MogulRightLegBone);
        MogulBodyBone.addChild(MogulLeftLegBone);
        MogulBodyBone.addChild(MogulLeftArmBone);
        MogulBodyBone.addChild(MogulNeckBone);
        MogulNeckBone.addChild(MogulHeadBone);
        MogulHeadBone.addChild(MogulFaceBone);
        MogulHeadBone.addChild(MogulHelmetBaseBone);
        MogulFaceBone.addChild(MogulNoseBone);
        MogulHelmetBaseBone.addChild(MogulHelmetUpperBone);
        MogulHelmetBaseBone.addChild(MogulBackHelmetFlapBone);
        MogulHelmetBaseBone.addChild(MogulLeftHelmetFlapBone);
        MogulHelmetBaseBone.addChild(MogulRightHelmetFlapBone);
        MogulHelmetUpperBone.addChild(MogulHelmetOrnamentBone);

        MogulRightArmBone.addChild(MogulRightArmGraspBone);
        MogulLeftArmBone.addChild(MogulLeftArmGraspBone);

        this.buildRoots();
    }
}
