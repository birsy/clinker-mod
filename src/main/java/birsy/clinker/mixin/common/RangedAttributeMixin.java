package birsy.clinker.mixin.common;

import net.neoforged.neoforge.common.PercentageAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PercentageAttribute.class)
public abstract class RangedAttributeMixin {
    @ModifyArgs(
            method = "<init>(Ljava/lang/String;DDDD)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;<init>(Ljava/lang/String;DDD)V")
    )
    private static void clinker$initRangedAttribute(Args args) {
        // this is literally so stupid
        if ("attribute.name.generic.knockback_resistance".equals(args.get(0)))
            args.set(2, -1000000.0);
    }
}
