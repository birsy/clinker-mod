package birsy.clinker.mixin.common;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.PathfindingDebugPayload;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugPackets.class)
public abstract class DebugPacketsMixin {
    @Shadow
    private static void sendPacketToAllPlayers(ServerLevel p_133692_, CustomPacketPayload p_296119_) {}

    @Inject(method = "sendPathFindingPacket(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/level/pathfinder/Path;F)V", at = @At("TAIL"))
    private static void clinker$sendPathFindingPacket(Level level, Mob mob, Path path, float maxNodeDistance, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel) sendPacketToAllPlayers(serverLevel, new PathfindingDebugPayload(mob.getId(), path, maxNodeDistance));
    }
}
