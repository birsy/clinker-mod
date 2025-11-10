package birsy.clinker.client.entity.slabcrab;

import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.Skeleton;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SlabCrabSkeleton extends Skeleton {
    protected Bone body, rightLeg0, leftLeg0, rightLeg1, leftLeg1, rightLeg2, leftLeg2, rightLeg3, leftLeg3, rightClaw, leftClaw, rightMandible, leftMandible, rightEye, leftEye, root;
    protected Bone[] rightLegs, leftLegs;
    protected SlabCrabSkeleton() {
        super();
        this.body = new Bone("body");
        this.body.setBaseAttributes(new Vector3f(0F, -1F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.body);

        this.rightLeg0 = new Bone("rightLeg0");
        this.rightLeg0.setBaseAttributes(new Vector3f(8F, 3F, -2F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightLeg0);

        this.leftLeg0 = new Bone("leftLeg0");
        this.leftLeg0.setBaseAttributes(new Vector3f(-8F, 3F, -2F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftLeg0);

        this.rightLeg1 = new Bone("rightLeg1");
        this.rightLeg1.setBaseAttributes(new Vector3f(8F, 3F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightLeg1);

        this.leftLeg1 = new Bone("leftLeg1");
        this.leftLeg1.setBaseAttributes(new Vector3f(-8F, 3F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftLeg1);

        this.rightLeg2 = new Bone("rightLeg2");
        this.rightLeg2.setBaseAttributes(new Vector3f(8F, 3F, 2F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightLeg2);

        this.leftLeg2 = new Bone("leftLeg2");
        this.leftLeg2.setBaseAttributes(new Vector3f(-8F, 3F, 2F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftLeg2);

        this.rightLeg3 = new Bone("rightLeg3");
        this.rightLeg3.setBaseAttributes(new Vector3f(8F, 3F, 4F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightLeg3);

        this.leftLeg3 = new Bone("leftLeg3");
        this.leftLeg3.setBaseAttributes(new Vector3f(-8F, 3F, 4F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftLeg3);

        this.rightClaw = new Bone("rightClaw");
        this.rightClaw.setBaseAttributes(new Vector3f(7F, 3F, -4F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightClaw);

        this.leftClaw = new Bone("leftClaw");
        this.leftClaw.setBaseAttributes(new Vector3f(-7F, 3F, -4F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftClaw);

        this.rightMandible = new Bone("rightMandible");
        this.rightMandible.setBaseAttributes(new Vector3f(3F, 2F, -8F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightMandible);

        this.leftMandible = new Bone("leftMandible");
        this.leftMandible.setBaseAttributes(new Vector3f(-3F, 2F, -8F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftMandible);

        this.rightEye = new Bone("rightEye");
        this.rightEye.setBaseAttributes(new Vector3f(3.5F, 6F, -6.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightEye);

        this.leftEye = new Bone("leftEye");
        this.leftEye.setBaseAttributes(new Vector3f(-3.5F, 6F, -6.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftEye);

        this.root = new Bone("root");
        this.root.setBaseAttributes(new Vector3f(0F, 1F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.root);

        this.rightLegs = new Bone[]{this.rightLeg0, this.rightLeg1, this.rightLeg2, this.rightLeg3};
        this.leftLegs = new Bone[]{this.leftLeg0, this.leftLeg1, this.leftLeg2, this.leftLeg3};

        this.body.addChild(this.rightLeg0);
        this.body.addChild(this.leftLeg0);
        this.body.addChild(this.rightLeg1);
        this.body.addChild(this.leftLeg1);
        this.body.addChild(this.rightLeg2);
        this.body.addChild(this.leftLeg2);
        this.body.addChild(this.rightLeg3);
        this.body.addChild(this.leftLeg3);
        this.body.addChild(this.rightClaw);
        this.body.addChild(this.leftClaw);
        this.body.addChild(this.rightMandible);
        this.body.addChild(this.leftMandible);
        this.body.addChild(this.rightEye);
        this.body.addChild(this.leftEye);
        this.root.addChild(this.body);
        this.buildRoots();
    }
}