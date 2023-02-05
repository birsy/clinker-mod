package birsy.clinker.common.alchemy.workstation;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.rigidbody.ITickableBody;
import birsy.clinker.core.util.rigidbody.RigidBody;
import birsy.clinker.core.util.rigidbody.colliders.MeshCollisionShape;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public class PhysicalItem extends RigidBody implements ItemLike, ITickableBody {
    private final AlchemicalWorkstation workstation;
    private final ItemStack itemStack;

    public PhysicalItem(AlchemicalWorkstation workstation, ItemStack itemStack, Vec3 position) {
        super(position, new Vec3(0, 0, 0), Vector3f.XP.rotationDegrees(workstation.level.random.nextFloat() * 360), Vec3.ZERO, 1.0F,
                MathUtils.matrix(1, 0, 0,
                                 0, 1, 0,
                                 0, 0, 1),
              MeshCollisionShape.Box(new Vec3(-0.5, -0.5, -0.5).scale(0.25), new Vec3(0.5, 0.5, 0.5).scale(0.25)));
        this.workstation = workstation;
        this.itemStack = itemStack;
    }

    public void tick(float deltaTime) {
        super.tick(deltaTime);
        //Clinker.LOGGER.info(this.angularVelocity);
    }

    @Override
    public Item asItem() {
        return itemStack.getItem();
    }

    public ItemStack asItemStack() {
        return itemStack;
    }
}
