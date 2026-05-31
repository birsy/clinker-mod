package birsy.clinker.mixin.common;

import birsy.clinker.client.render.world.cloud.CloudHoleTracker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @Shadow int levels;
    @Shadow List<BeaconBlockEntity.BeaconBeamSection> beamSections;
    @Unique CloudHoleTracker.CloudHoleHandle clinker$cloudHole;

    @Inject(method = "tick", at = @At("HEAD"))
    private static void clinker$tickBeacon(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (level.isClientSide()) {
            BeaconBlockEntityMixin self = (BeaconBlockEntityMixin)(Object)blockEntity;

            boolean shouldHaveBeam = self.levels > 0 && !self.beamSections.isEmpty();
            boolean hasBeam = self.clinker$cloudHole != null;

            CloudHoleTracker holeTracker = CloudHoleTracker.getInstance();
            if (holeTracker == null) {
                if (hasBeam) {
                    self.clinker$cloudHole.free();
                    self.clinker$cloudHole = null;
                }
                return;
            }

            if (shouldHaveBeam && !hasBeam) {
                self.clinker$cloudHole = holeTracker.createHandle(
                        CloudHoleTracker.CloudHoleType.BEACON,
                        pos.getX() + 0.5, pos.getZ() + 0.5,
                        0.0F
                );
                self.clinker$cloudHole.setRadius(25.0F, MathUtils.EasingType.easeOutCubic, 20);
            } else if (!shouldHaveBeam && hasBeam) {
                self.clinker$closeHole();
            }
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    void clinker$removeBeacon(CallbackInfo ci) {
        if (clinker$cloudHole != null) {
            CloudHoleTracker tracker = CloudHoleTracker.getInstance();
            if (tracker != null) clinker$closeHole();
            else clinker$cloudHole.free();
            clinker$cloudHole = null;
        }
    }

    @Unique
    void clinker$closeHole() {
        clinker$cloudHole.setRadius(0, MathUtils.EasingType.easeInCubic, 50);
        clinker$cloudHole.freeOnCompletion();
        clinker$cloudHole = null;
    }
}
