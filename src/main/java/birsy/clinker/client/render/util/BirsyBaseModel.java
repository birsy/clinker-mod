package birsy.clinker.client.render.util;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BirsyBaseModel<T extends Entity> extends EntityModel<T>
{
    public void setRotateAngle(BirsyModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
        
        modelRenderer.defaultRotateAngleX = x;
        modelRenderer.defaultRotateAngleY = y;
        modelRenderer.defaultRotateAngleZ = z;
    }
}
