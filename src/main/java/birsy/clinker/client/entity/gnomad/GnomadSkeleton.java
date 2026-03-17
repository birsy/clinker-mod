package birsy.clinker.client.entity.gnomad;

import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.Skeleton;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GnomadSkeleton extends Skeleton {
    protected Bone face, nose, head, torso, bag, hat, neck, headJoint, rightArm, leftArm, leftLeg, rightLeg, skirt, root;
    protected GnomadSkeleton() {
        super();
        this.face = new Bone("face");
        this.face.setBaseAttributes(new Vector3f(0F, 1.5F, -5.25F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.face);

        this.nose = new Bone("nose");
        this.nose.setBaseAttributes(new Vector3f(0F, 2F, -1F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.nose);

        this.head = new Bone("head");
        this.head.setBaseAttributes(new Vector3f(0F, 0F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.head);

        this.torso = new Bone("torso");
        this.torso.setBaseAttributes(new Vector3f(0F, 11F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.torso);

        this.bag = new Bone("bag");
        this.bag.setBaseAttributes(new Vector3f(0F, 13F, 4F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.bag);

        this.hat = new Bone("hat");
        this.hat.setBaseAttributes(new Vector3f(0F, 4.25F, -2.75F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.hat);

        this.neck = new Bone("neck");
        this.neck.setBaseAttributes(new Vector3f(0F, 12.5F, -2.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.neck);

        this.headJoint = new Bone("headJoint");
        this.headJoint.setBaseAttributes(new Vector3f(0F, 0.5F, -4.25F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.headJoint);

        this.rightArm = new Bone("rightArm");
        this.rightArm.setBaseAttributes(new Vector3f(6F, 9F, -1F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightArm);

        this.leftArm = new Bone("leftArm");
        this.leftArm.setBaseAttributes(new Vector3f(-6F, 9F, -1F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftArm);

        this.leftLeg = new Bone("leftLeg");
        this.leftLeg.setBaseAttributes(new Vector3f(-3F, 9F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftLeg);

        this.rightLeg = new Bone("rightLeg");
        this.rightLeg.setBaseAttributes(new Vector3f(3F, 9F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightLeg);

        this.skirt = new Bone("skirt");
        this.skirt.setBaseAttributes(new Vector3f(0F, -0.25F, 4.25F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.skirt);

        this.root = new Bone("root");
        this.root.setBaseAttributes(new Vector3f(0F, 0F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.root);

        this.face.addChild(this.nose);
        this.head.addChild(this.face);
        this.head.addChild(this.hat);
        this.torso.addChild(this.skirt);
        this.torso.addChild(this.leftArm);
        this.torso.addChild(this.rightArm);
        this.torso.addChild(this.bag);
        this.torso.addChild(this.neck);
        this.neck.addChild(this.headJoint);
        this.headJoint.addChild(this.head);
        this.root.addChild(this.torso);
        this.root.addChild(this.rightLeg);
        this.root.addChild(this.leftLeg);
        this.buildRoots();
    }
}
