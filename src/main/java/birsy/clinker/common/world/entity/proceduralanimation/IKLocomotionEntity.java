package birsy.clinker.common.world.entity.proceduralanimation;

public interface IKLocomotionEntity {
    IKLegSystem getLegSystem();
    default IKLeg getLeg(int legIndex) {
        return this.getLegSystem().getLeg(legIndex);
    }
}
