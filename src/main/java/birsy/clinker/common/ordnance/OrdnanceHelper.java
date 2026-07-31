package birsy.clinker.common.ordnance;

import birsy.clinker.client.particle.OrdnanceTrailParticle;
import birsy.clinker.common.networking.packet.ClientboundOrdnanceExplosionPacket;
import birsy.clinker.common.entity.projectile.FlechetteEntity;
import birsy.clinker.common.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.entity.system.chainlightning.ChainLightningSystem;
import birsy.clinker.common.ordnance.modifiers.ExplosiveModifier;
import birsy.clinker.common.ordnance.modifiers.FlechettesModifier;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.UUID;

public class OrdnanceHelper {
    private static final Vector3d[] PARTICLE_POINTS = MathUtils.generateSpherePoints(500);

    private static final Vector3d vec = new Vector3d();
    public static void detonate(OrdnanceModifierSet modifierSet, double x, double y, double z, Level level, @Nullable OrdnanceEntity bomb, @Nullable Entity throwerOrHolder) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                    new ChunkPos(BlockPos.containing(x, y, z)),
                    new ClientboundOrdnanceExplosionPacket(modifierSet, x, y, z, bomb == null ? -1 : bomb.getId(), throwerOrHolder == null ? -1 : throwerOrHolder.getId())
            );
        }

        boolean isClient = level.isClientSide();

        ExplosiveModifier explosiveModifier = modifierSet.getModifier(ClinkerOrdnanceModifierTypes.EXPLOSIVE.get());
        if (explosiveModifier != null) {
            float radius = explosiveModifier.getExplosionRadius();
            if (isClient) {
                // todo: explosion particle sphere
                for (Vector3d particlePoint : PARTICLE_POINTS) {
                    particlePoint.normalize(vec);
                    vec.mul(radius + level.random.nextGaussian() * 0.1F);
                    // actual particle here...
                    level.addParticle(
                            new OrdnanceTrailParticle.Options(modifierSet.gradient(), 2.0F + (float) (level.random.nextGaussian() * 0.3F)),
                            x + vec.x, y + vec.y, z + vec.z,
                            level.random.nextGaussian() * 0.01F,
                            Math.abs(level.random.nextGaussian()) * 0.01 * 2,
                            level.random.nextGaussian() * 0.01
                    );
                }

                level.addParticle(ParticleTypes.FLASH, true, x, y, z, 0, 0, 0);
                level.addParticle(ClinkerParticles.EXPLOSION_LIGHT.get(), true, x, y, z, 0, 0, 0);

                level.playLocalSound(x, y, z, ClinkerSounds.ORDNANCE_EXPLODE.get(), SoundSource.BLOCKS, 3F, Mth.lerp(level.random.nextFloat(), 0.4F, 0.6F), false);
                level.playLocalSound(x, y, z, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 4F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
                //level.playLocalSound(x, y, z, SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, SoundSource.BLOCKS, 0.1F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
            }
            Collection<Entity> entities = EntityRetrievalUtil.getEntities(level, new Vec3(x, y, z), radius * 2);
            for (Entity entity : entities) {
                Vector3d directionToBomb = vec.set(x, y, z).sub(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ());
                double distanceToBomb = directionToBomb.length();
                if (distanceToBomb > radius) continue;
                double power = Mth.clampedMap(distanceToBomb, radius * 0.5, radius, explosiveModifier.power(), explosiveModifier.power() * 0.5);
                // todo: compute per-entity "exposure"
                if (distanceToBomb == 0) distanceToBomb = 1.0;
                Vector3d knockbackVector = directionToBomb.mul((1.0 / distanceToBomb) * power * -0.6);
                hurt(entity,
                     DamageTypes.EXPLOSION, (float) power * 10,
                     modifierSet,
                     x, y, z, level,
                     knockbackVector.x, knockbackVector.y, knockbackVector.z,
                     bomb, throwerOrHolder, 0);
            }
        }

        FlechettesModifier flechettesModifier = modifierSet.getModifier(ClinkerOrdnanceModifierTypes.FLECHETTES.get());
        if (flechettesModifier != null) {
            if (!isClient) {
                // spawn the flechettes
                int flechetteCount = 128;
                for (int i = 0; i < flechetteCount; i++) {
                    double sX = level.random.nextGaussian(),
                            sY = level.random.nextGaussian(),
                            sZ = level.random.nextGaussian();
                    double length = Mth.length(sX, sY, sZ);
                    if (length == 0) continue;
                    sX /= length; sY = Math.abs(sY / length); sZ /= length;

                    FlechetteEntity entity = new FlechetteEntity(ClinkerEntities.FLECHETTE.get(), level);
                    entity.setOwner(throwerOrHolder);
                    entity.shoot(sX, sY, sZ,2.0F + (float)level.random.nextGaussian() * 0.1F, 0.0F);

                    double spawnRadius = 0.125;
                    entity.setPos(x + sX * spawnRadius, y + sY * spawnRadius, z + sZ * spawnRadius);
                    level.addFreshEntity(entity);
                }
            } else {
                level.addParticle(ParticleTypes.FLASH, true, x, y, z, 0, 0, 0);
                level.playLocalSound(x, y, z, ClinkerSounds.ORDNANCE_EXPLODE.get(), SoundSource.BLOCKS, 3F, Mth.lerp(level.random.nextFloat(), 0.4F, 0.6F), false);
            }
        }


        if (bomb != null && !isClient) bomb.discard();
    }

    public static void hurt(Entity entity, ResourceKey<DamageType> damageType, float damage, OrdnanceModifierSet modifierSet,
                            double x, double y, double z, Level level,
                            double extraKnockbackX, double extraKnockbackY, double extraKnockbackZ,
                            @Nullable Entity directSource, @Nullable Entity throwerOrHolder,
                            int recursionDepth) {
        DamageSource damageSource = new DamageSource(
                entity.damageSources().damageTypes.getHolderOrThrow(damageType),
                directSource, throwerOrHolder,
                new Vec3(x, y, z)
        );
        entity.hurt(damageSource, damage);

        // knockback stuffs
        if (extraKnockbackX != 0 || extraKnockbackY != 0 || extraKnockbackZ != 0) {
            if (entity instanceof LivingEntity blockingEntity && blockingEntity.isDamageSourceBlocked(damageSource)) {
                level.playSound(
                        null,
                        blockingEntity.getX(), blockingEntity.getY(), blockingEntity.getZ(),
                        SoundEvents.WIND_CHARGE_BURST,
                        blockingEntity.getSoundSource(),
                        0.5F, 1.0F
                );
                entity.addDeltaMovement(new Vec3(extraKnockbackX * 2.5, extraKnockbackY * 2.5, extraKnockbackZ * 2.5));
            } else {
                entity.addDeltaMovement(new Vec3(extraKnockbackX, extraKnockbackY, extraKnockbackZ));
            }
        }

        // apply chain lightning, which transfers other modifier effects
        if (modifierSet.hasModifier(ClinkerOrdnanceModifierTypes.ELECTRIFIED.get()) && recursionDepth == 0 && level instanceof ServerLevel serverLevel) {
            UUID sourceId = UUID.randomUUID();
            if (directSource != null) sourceId = directSource.getUUID();
            if (throwerOrHolder != null) sourceId = throwerOrHolder.getUUID();
            ChainLightningSystem.get(serverLevel)
                    .emit(sourceId,
                          (chainLightning, chainedEntity) -> hurt(
                                entity, DamageTypes.LIGHTNING_BOLT, damage, modifierSet,
                                x, y, z, level, 0, 0, 0,
                                directSource, throwerOrHolder, 1
                          ),
                          entity
                    );
        }
    }
}
