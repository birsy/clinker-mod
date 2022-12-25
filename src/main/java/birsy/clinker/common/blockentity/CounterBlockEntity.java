package birsy.clinker.common.blockentity;

import birsy.clinker.common.alchemy.workstation.AlchemicalWorkstation;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class CounterBlockEntity extends BlockEntity {
    public int timeSinceLastItem = 300;
    public AlchemicalWorkstation workstation;

    public CounterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.COUNTER.get(), pPos, pBlockState);
        this.workstation = new AlchemicalWorkstation(this.level, this.getBlockPos());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CounterBlockEntity entity) {
        entity.workstation.level = level;
        entity.timeSinceLastItem++;
        if (entity.timeSinceLastItem > 30 && entity.workstation.getItems().size() < 64) {

            Collection<Item> items = ForgeRegistries.ITEMS.getValues();
            Item item = (Item) items.toArray()[level.random.nextInt(items.size())];

            entity.workstation.addItem(new ItemStack(item, 1), new Vec3(pos.getX() + 0.5 + level.random.nextGaussian() * 0.5, pos.getY() + 4 + level.random.nextFloat(), pos.getZ() + 0.5 + level.random.nextGaussian() * 0.5));
            entity.timeSinceLastItem = 0;
        }
        entity.workstation.tick(1.0F / 20.0F);
    }
}
