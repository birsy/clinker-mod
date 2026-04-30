package birsy.clinker.client.entity.leggy;

import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.Skeleton;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LeggySkeleton extends Skeleton {
    protected final Bone upperLeg2, lowerLeg2, upperLeg1, lowerLeg1, upperLeg0, lowerLeg0, upperLeg4, lowerLeg4, upperLeg3, lowerLeg3, head, root, legRoot;
    protected final Bone[] upperLegs, lowerLegs;
    protected LeggySkeleton() {
        super();
        this.upperLeg2 = new Bone("upperLeg2");
        this.upperLeg2.setBaseAttributes(new Vector3f(-4.4083893921935475F, -2F, -6.067627457812106F), new Quaternionf().rotationZYX(0F, 0.62831853036F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.upperLeg2);
        
        this.lowerLeg2 = new Bone("lowerLeg2");
        this.lowerLeg2.setBaseAttributes(new Vector3f(0F, -24F, -0.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.lowerLeg2);
        
        this.upperLeg1 = new Bone("upperLeg1");
        this.upperLeg1.setBaseAttributes(new Vector3f(-7.132923872213652F, -2F, 2.317627457812106F), new Quaternionf().rotationZYX(0F, 1.88495559108F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.upperLeg1);
        
        this.lowerLeg1 = new Bone("lowerLeg1");
        this.lowerLeg1.setBaseAttributes(new Vector3f(0F, -24F, -0.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.lowerLeg1);
        
        this.upperLeg0 = new Bone("upperLeg0");
        this.upperLeg0.setBaseAttributes(new Vector3f(0F, -2F, 7.5F), new Quaternionf().rotationZYX(0F, -3.1415926518F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.upperLeg0);
        
        this.lowerLeg0 = new Bone("lowerLeg0");
        this.lowerLeg0.setBaseAttributes(new Vector3f(0F, -24F, -0.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.lowerLeg0);
        
        this.upperLeg4 = new Bone("upperLeg4");
        this.upperLeg4.setBaseAttributes(new Vector3f(7.132923872213652F, -2F, 2.317627457812106F), new Quaternionf().rotationZYX(0F, -1.88495559108F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.upperLeg4);
        
        this.lowerLeg4 = new Bone("lowerLeg4");
        this.lowerLeg4.setBaseAttributes(new Vector3f(0F, -24F, -0.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.lowerLeg4);
        
        this.upperLeg3 = new Bone("upperLeg3");
        this.upperLeg3.setBaseAttributes(new Vector3f(4.4083893921935475F, -2F, -6.067627457812106F), new Quaternionf().rotationZYX(0F, -0.62831853036F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.upperLeg3);
        
        this.lowerLeg3 = new Bone("lowerLeg3");
        this.lowerLeg3.setBaseAttributes(new Vector3f(0F, -24F, -0.5F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.lowerLeg3);
        
        this.head = new Bone("head");
        this.head.setBaseAttributes(new Vector3f(0F, 0F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.head);

        this.legRoot = new Bone("legRoot");
        this.legRoot.setBaseAttributes(new Vector3f(0F, 2F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.legRoot);

        this.root = new Bone("root");
        this.root.setBaseAttributes(new Vector3f(0F, 2F, 0F), new Quaternionf().rotationZYX(0F, 0F, 0F), new Vector3f(0.0F), new Vector3f(1.0F), new Vector4f(1.0F));
        this.addBone(this.root);

        this.upperLeg2.addChild(this.lowerLeg2);
        this.upperLeg1.addChild(this.lowerLeg1);
        this.upperLeg0.addChild(this.lowerLeg0);
        this.upperLeg4.addChild(this.lowerLeg4);
        this.upperLeg3.addChild(this.lowerLeg3);
        this.legRoot.addChild(this.upperLeg3);
        this.legRoot.addChild(this.upperLeg4);
        this.legRoot.addChild(this.upperLeg0);
        this.legRoot.addChild(this.upperLeg1);
        this.legRoot.addChild(this.upperLeg2);
        this.root.addChild(this.legRoot);
        this.root.addChild(this.head);
        this.buildRoots();

        this.upperLegs = new Bone[]{this.upperLeg0, this.upperLeg1, this.upperLeg2, this.upperLeg3, this.upperLeg4};
        this.lowerLegs = new Bone[]{this.lowerLeg0, this.lowerLeg1, this.lowerLeg2, this.lowerLeg3, this.lowerLeg4};
    }
}