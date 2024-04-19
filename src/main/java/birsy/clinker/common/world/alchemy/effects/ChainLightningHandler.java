package birsy.clinker.common.world.alchemy.effects;

import birsy.clinker.client.render.particle.ChainLightningParticle;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChainLightningHandler {
    private static List<LivingEntity> chainLightningAffectedEntities = new ArrayList<>();
    private static final byte SPREAD_DELAY = 12;
    private static final byte COOLDOWN = 30;

    @SubscribeEvent // placeholder... maybe.
    public static void entityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            shock(livingEntity);
        }
    }

    @SubscribeEvent
    public static void entityLoad(EntityJoinLevelEvent event) {
        if (!event.loadedFromDisk()) return;

        if (event.getEntity().hasData(ClinkerDataAttachments.CHAIN_LIGHTNING) && event.getEntity() instanceof LivingEntity livingEntity) {
            chainLightningAffectedEntities.add(livingEntity);
        }
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        chainLightningAffectedEntities.clear();
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (!(event.level instanceof ServerLevel)) return;
        // if the chain lightning's value is negative, that means it is in cooldown.

        Iterator<LivingEntity> iterator = chainLightningAffectedEntities.iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();

            byte chainLightningCharge = entity.getData(ClinkerDataAttachments.CHAIN_LIGHTNING);

            if (!isInCooldown(entity)) {
                // we're waiting until we can shock someone else
                byte nextChainLightningCharge = (byte)(chainLightningCharge - 1);
                if (nextChainLightningCharge == 0) {
                    spreadShock(entity);
                    entity.setData(ClinkerDataAttachments.CHAIN_LIGHTNING, (byte) -COOLDOWN);
                } else {
                    entity.setData(ClinkerDataAttachments.CHAIN_LIGHTNING, nextChainLightningCharge);
                }
            } else {
                // we're in cooldown from being shocked
                byte nextChainLightningCharge = (byte)(chainLightningCharge + 1);
                if (nextChainLightningCharge == 0) {
                    iterator.remove();
                } else {
                    entity.setData(ClinkerDataAttachments.CHAIN_LIGHTNING, nextChainLightningCharge);
                }
            }

        }
    }

    public static boolean isInCooldown(LivingEntity entity) {
        return entity.getData(ClinkerDataAttachments.CHAIN_LIGHTNING) < 0;
    }

    public static void spreadShock(LivingEntity entity) {
        List<LivingEntity> shockedEntities = EntityRetrievalUtil.getEntities(entity, 4, (shockedEntity -> shockedEntity instanceof LivingEntity));
        for (LivingEntity shockedEntity : shockedEntities) {
            if (RandomUtil.oneInNChance(8)) continue; // one in eight chance of not passing the shock along, so it dissipates over time

            shock(shockedEntity);

            if (entity.level() instanceof ServerLevel serverLevel) {
                Vec3 pos1 = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
                Vec3 pos2 = shockedEntity.position().add(0, shockedEntity.getBbHeight() * 0.5, 0);
                Vec3 middlePos = pos1.add(pos2).scale(0.5);

                serverLevel.sendParticles(
                        new ChainLightningParticle.ChainLightningParticleOptions(pos1, pos2),
                        middlePos.x(), middlePos.y(), middlePos.z(), 1,
                        0, 0, 0, 0);
            }
        }
    }

    public static void shock(LivingEntity victim) {
        victim.hurt(victim.damageSources().lightningBolt(), 2.0f);
        if (!isInCooldown(victim)) {
            victim.setData(ClinkerDataAttachments.CHAIN_LIGHTNING, SPREAD_DELAY);
            chainLightningAffectedEntities.add(victim);
        }
    }
}
