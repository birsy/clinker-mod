package birsy.clinker.mixin.common;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique private static final ResourceLocation WATER_CONDUCTIVITY_DEBUFF_ID = Clinker.resource("water_conductivity");

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Holder<Attribute> attribute);

    @Inject(method = "tick", at = @At("RETURN"))
    private void clinker$addWaterConductivityDebuff(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        AttributeInstance attributeInstance = this.getAttribute(ClinkerAttributes.CONDUCTIVITY);
        if (self.isInWater() && !attributeInstance.hasModifier(WATER_CONDUCTIVITY_DEBUFF_ID)) {
            attributeInstance.addTransientModifier(
                    new AttributeModifier(
                            WATER_CONDUCTIVITY_DEBUFF_ID,
                            -3,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        } else if (attributeInstance.hasModifier(WATER_CONDUCTIVITY_DEBUFF_ID)) {
            attributeInstance.removeModifier(WATER_CONDUCTIVITY_DEBUFF_ID);
        }
    }
}
