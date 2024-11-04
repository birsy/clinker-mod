package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class GroundBodyRotationControl extends BodyRotationControl {
    public GroundBodyRotationControl(GroundLocomoteEntity pMob) {
        super(pMob);
    }
}
