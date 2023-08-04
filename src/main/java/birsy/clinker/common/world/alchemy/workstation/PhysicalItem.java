package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.core.util.AxisAngled;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.common.world.physics.rigidbody.ITickableBody;
import birsy.clinker.common.world.physics.rigidbody.VerletRigidBody;
import birsy.clinker.common.world.physics.rigidbody.colliders.MeshCollisionShape;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

@Deprecated
public class PhysicalItem extends VerletRigidBody implements ItemLike, ITickableBody {
    private final AlchemicalWorkstation workstation;
    private final ItemStack itemStack;

    public PhysicalItem(AlchemicalWorkstation workstation, ItemStack itemStack, Vec3 position) {
        super(position, new Quaterniond(), 1.0F,
                MathUtils.matrix(1, 0, 0,
                                 0, 1, 0,
                                 0, 0, 1),
              MeshCollisionShape.Box(new Vec3(-0.5, -0.5, -0.5).scale(0.25), new Vec3(0.5, 0.5, 0.5).scale(0.25)));
        this.workstation = workstation;
        this.itemStack = itemStack;

        this.transform.orientation = new Quaterniond(new AxisAngled(workstation.level.random.nextGaussian(), workstation.level.random.nextGaussian(), workstation.level.random.nextGaussian(), workstation.level.random.nextFloat() * 2.0F * Math.PI));
        this.pTransform.orientation = new Quaterniond(this.transform.orientation);
        this.lastTickTransform.orientation = new Quaterniond(this.transform.orientation);
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
