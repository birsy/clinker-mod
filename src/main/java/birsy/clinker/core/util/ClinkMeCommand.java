package birsy.clinker.core.util;

import birsy.clinker.core.registry.worldgen.ClinkerWorld;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public class ClinkMeCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((p_304259_) -> Component.translatableEscape("commands.locate.biome.not_found", new Object[]{p_304259_}));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.teleport.invalidPosition"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("clinkme")
                .then(Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(context, Registries.BIOME))
                        .executes((cs) ->
                                locateBiome(cs.getSource(), ResourceOrTagArgument.getResourceOrTag(cs, "biome", Registries.BIOME))))
        );
    }

    private static int locateBiome(CommandSourceStack source, ResourceOrTagArgument.Result<Biome> biome) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(source.getPosition());
        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
        ServerLevel othershore = source.getServer().getLevel(ClinkerWorld.OTHERSHORE);
        Pair<BlockPos, Holder<Biome>> pair = othershore.findClosestBiome3d(biome, blockpos, 6400, 32, 64);
        stopwatch.stop();
        if (pair == null) {
            throw ERROR_BIOME_NOT_FOUND.create(biome.asPrintable());
        } else {
            Vec3 v = pair.getFirst().getBottomCenter();
            return teleportToPos(source, source.getPlayer(), othershore, WorldCoordinates.absolute(v.x, v.y, v.z), WorldCoordinates.current());
        }
    }

    private static int teleportToPos(CommandSourceStack source, ServerPlayer target, ServerLevel level, Coordinates position, @Nullable Coordinates rotation) throws CommandSyntaxException {
        Vec3 vec3 = position.getPosition(source);
        Vec2 vec2 = rotation == null ? null : rotation.getRotation(source);
        Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
        if (position.isXRelative()) {
            set.add(RelativeMovement.X);
        }

        if (position.isYRelative()) {
            set.add(RelativeMovement.Y);
        }

        if (position.isZRelative()) {
            set.add(RelativeMovement.Z);
        }

        if (rotation == null) {
            set.add(RelativeMovement.X_ROT);
            set.add(RelativeMovement.Y_ROT);
        } else {
            if (rotation.isXRelative()) {
                set.add(RelativeMovement.X_ROT);
            }

            if (rotation.isYRelative()) {
                set.add(RelativeMovement.Y_ROT);
            }
        }


        if (rotation == null) {
            performTeleport(source, target, level, vec3.x, vec3.y, vec3.z, set, target.getYRot(), target.getXRot());
        } else {
            performTeleport(source, target, level, vec3.x, vec3.y, vec3.z, set, vec2.y, vec2.x);
        }


        source.sendSuccess(() -> Component.translatable("commands.teleport.success.location.single", target.getDisplayName(), formatDouble(vec3.x), formatDouble(vec3.y), formatDouble(vec3.z)), true);

        return 1;
    }

    private static String formatDouble(double value) {
        return String.format(Locale.ROOT, "%f", value);
    }

    private static void performTeleport(CommandSourceStack source, Entity entity, ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeList, float yaw, float pitch) throws CommandSyntaxException {
        EntityTeleportEvent.TeleportCommand event = EventHooks.onEntityTeleportCommand(entity, x, y, z);
        if (!event.isCanceled()) {
            x = event.getTargetX();
            y = event.getTargetY();
            z = event.getTargetZ();
            BlockPos blockpos = BlockPos.containing(x, y, z);
            if (!Level.isInSpawnableBounds(blockpos)) {
                throw INVALID_POSITION.create();
            } else {
                float f = Mth.wrapDegrees(yaw);
                float f1 = Mth.wrapDegrees(pitch);
                if (entity.teleportTo(level, x, y, z, relativeList, f, f1)) {

                    label23: {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)entity;
                            if (livingentity.isFallFlying()) {
                                break label23;
                            }
                        }

                        entity.setDeltaMovement(entity.getDeltaMovement().multiply((double)1.0F, (double)0.0F, (double)1.0F));
                        entity.setOnGround(true);
                    }

                    if (entity instanceof PathfinderMob) {
                        PathfinderMob pathfindermob = (PathfinderMob)entity;
                        pathfindermob.getNavigation().stop();
                    }
                }

            }
        }
    }
}
