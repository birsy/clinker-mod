package birsy.clinker.common.world.alchemy.workstation.camera;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.core.util.Quaterniond;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WorkstationCamera {
    final Workstation workstation;

    public Vec3 position;
    public float height;
    public Vec3 direction;
    public Quaterniond rotation;

    public CameraPath.CameraPathNode node;
    public float lineProgress;

    public WorkstationCamera(Workstation workstation) {
        this.workstation = workstation;
        this.position = Vec3.ZERO;
    }

    public void update() {
        if (this.node == null) return;

        CameraPath.CameraPathNode leftNeighbor = node.getLeftNeighbor();
        boolean hasLeftNode = leftNeighbor != null;
        CameraPath.CameraPathNode rightNeighbor = node.getRightNeighbor();
        boolean hasRightNode = rightNeighbor != null;

        if (lineProgress > 1 && hasRightNode) {
            this.node = rightNeighbor;
            this.lineProgress = this.lineProgress - 1;
        } else if (this.lineProgress > 0 && !hasRightNode) {
            this.lineProgress = 0;
        } else if (lineProgress < 0 && hasLeftNode) {
            this.node = leftNeighbor;
            this.lineProgress = this.lineProgress + 1;
        } else if (this.lineProgress < 0 && !hasLeftNode) {
            this.lineProgress = 0;
        }

        float currentMinHeight = Mth.lerp(lineProgress, node.offset, this.getNextNodeForTranslation().offset);
        float currentMaxHeight = currentMinHeight + Mth.lerp(lineProgress, node.height, this.getNextNodeForTranslation().height);
        this.height = Mth.clamp(this.height, currentMinHeight, currentMaxHeight);
        this.position = node.position.lerp(this.getNextNodeForTranslation().position, lineProgress).add(0, this.height, 0);
        this.direction = node.direction.lerp(this.getNextNodeForTranslation().direction, lineProgress).normalize();
    }

    private CameraPath.CameraPathNode getNextNodeForTranslation() {
        return node.neighbors[1] == null ?  this.node : node.neighbors[1];
    }
}
