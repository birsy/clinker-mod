package birsy.clinker.mixin.common;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.Properties.class)
public interface BlockBehavior$PropertiesAccessor {
    @Accessor("offsetFunction")
    void setOffsetFunction(BlockBehaviour.OffsetFunction function);
}
