package birsy.clinker.client.entity.gnomad.runt;

import birsy.clinker.client.entity.gnomad.SuppliesDelivererSkeleton;
import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.render.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GnomadRuntSkeleton extends Skeleton implements SuppliesDelivererSkeleton {
    protected Bone rightArm, leftLeg, rightLeg, leftArm, torso, head, face, hat, root;
    protected GnomadRuntSkeleton() {
        super();
        this.rightArm = new Bone("rightArm");
        this.rightArm.setBaseAttributes(new Vector3f(3.5F, 6.5F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightArm);

        this.leftLeg = new Bone("leftLeg");
        this.leftLeg.setBaseAttributes(new Vector3f(-3F, 6.25F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftLeg);

        this.rightLeg = new Bone("rightLeg");
        this.rightLeg.setBaseAttributes(new Vector3f(3F, 6.25F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.rightLeg);

        this.leftArm = new Bone("leftArm");
        this.leftArm.setBaseAttributes(new Vector3f(-3.5F, 6.5F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.leftArm);

        this.torso = new Bone("torso");
        this.torso.setBaseAttributes(new Vector3f(0F, 5.5F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.torso);

        this.head = new Bone("head");
        this.head.setBaseAttributes(new Vector3f(0F, 8F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.head);

        this.face = new Bone("face");
        this.face.setBaseAttributes(new Vector3f(0F, 3.5F, -3F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.face);

        this.hat = new Bone("hat");
        this.hat.setBaseAttributes(new Vector3f(0F, 6F, -0.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.hat);

        this.root = new Bone("root");
        this.root.setBaseAttributes(new Vector3f(0F, 0F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.root);

        this.torso.addChild(this.rightArm);
        this.torso.addChild(this.leftArm);
        this.torso.addChild(this.head);
        this.head.addChild(this.hat);
        this.head.addChild(this.face);
        this.root.addChild(this.leftLeg);
        this.root.addChild(this.rightLeg);
        this.root.addChild(this.torso);
        this.buildRoots();
    }

    @Override
    public Bone suppliesParentBone() {
        return this.torso;
    }
    @Override
    public void suppliesOffset(MatrixStack matrixStack) {
        matrixStack.translate(0, 0.8, -0.5);
    }
}
