package birsy.clinker.common.world.alchemy.effects;

import birsy.clinker.client.render.particle.ChainLightningParticle;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataAttachments;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChainLightningHandler {
    private static List<LivingEntity> chainLightningAffectedEntities = new ArrayList<>();
    private static List<LivingEntity> entitiesToAdd = new ArrayList<>();
    private static Int2ObjectOpenHashMap<TravellingBolt> travellingBolts = new Int2ObjectOpenHashMap<>();
    private static int currentTravellingLightningID = 0;

    public static int BOLT_TRAVEL_TIME = 15;
    public static int SPREAD_DELAY = 25;
    public static int COOLDOWN_TIME = (int)(SPREAD_DELAY * 2.5) + (int)(BOLT_TRAVEL_TIME * 2.5);

    @SubscribeEvent // placeholder... maybe.
    public static void entityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (canShock(livingEntity)) {
                shock(event.getLightning(), livingEntity);
            }
        }
    }

    @SubscribeEvent
    public static void entityLoad(EntityJoinLevelEvent event) {
        if (!event.loadedFromDisk()) return;

        if ((event.getEntity().hasData(ClinkerDataAttachments.CHAIN_LIGHTNING_SPREAD_DELAY) || event.getEntity().hasData(ClinkerDataAttachments.CHAIN_LIGHTNING_RECEIVE_COOLDOWN))  && event.getEntity() instanceof LivingEntity livingEntity) {
            entitiesToAdd.add(livingEntity);
        }
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        chainLightningAffectedEntities.clear();
        entitiesToAdd.clear();
        travellingBolts.clear();
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END ) {
            return;
        }


        Iterator<LivingEntity> iterator = chainLightningAffectedEntities.iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();
            int receiveCooldown = entity.getData(ClinkerDataAttachments.CHAIN_LIGHTNING_RECEIVE_COOLDOWN);
            int spreadDelay = entity.getData(ClinkerDataAttachments.CHAIN_LIGHTNING_SPREAD_DELAY);

            entity.setCustomName(Component.literal("R: " + receiveCooldown + " | S: " + spreadDelay));
            entity.setCustomNameVisible(true);
            // if both the cooldown and delay are zero, we can safely stop caring about this.
            if ((receiveCooldown <= 0 && spreadDelay <= 0) || entity.isRemoved()) {
                iterator.remove();
                continue;
            }

            if (receiveCooldown > 0) { entity.setData(ClinkerDataAttachments.CHAIN_LIGHTNING_RECEIVE_COOLDOWN, receiveCooldown - 1); }
            if (spreadDelay > 0) {
                int nextValue = spreadDelay - 1;
                entity.setData(ClinkerDataAttachments.CHAIN_LIGHTNING_SPREAD_DELAY, nextValue);
                if (nextValue == 0) { spreadShock(entity); } // if our spread delay just turned to zero, spread the shock to nearby mobs.
            }
        }

        chainLightningAffectedEntities.addAll(entitiesToAdd);
        entitiesToAdd.clear();

        for (TravellingBolt bolt : travellingBolts.values()) {
            bolt.tick();
        }
    }

    public static boolean canShock(LivingEntity entity) {
        return entity.getData(ClinkerDataAttachments.CHAIN_LIGHTNING_RECEIVE_COOLDOWN) <= 0 && entity.getData(ClinkerDataAttachments.CHAIN_LIGHTNING_SPREAD_DELAY) <= 0;
    }

    public static void spreadShock(LivingEntity entity) {
        List<LivingEntity> shockedEntities = EntityRetrievalUtil.getEntities(entity, 4, (shockedEntity -> shockedEntity instanceof LivingEntity le && canShock(le)));
        for (LivingEntity shockedEntity : shockedEntities) {
            //if (RandomUtil.oneInNChance(8)) continue; // one in eight chance of not passing the shock along, so it dissipates over time
            if (entity instanceof Player) {
                continue;
            }
            shock(entity, shockedEntity);
        }
    }

    public static void shock(@Nullable Entity shocker, LivingEntity victim) {
        TravellingBolt bolt = new TravellingBolt(shocker, victim);
        bolt.begin();
    }

    private static class TravellingBolt {
        @Nullable
        Entity shocker;
        final LivingEntity victim;
        int travelTime = 0;
        final int id;

        private TravellingBolt(Entity shocker, LivingEntity victim) {
            this.id = currentTravellingLightningID++;
            travellingBolts.put(this.id, this);
            this.shocker = shocker;
            this.victim = victim;
        }

        private TravellingBolt(LivingEntity victim) {
            this.id = currentTravellingLightningID++;
            travellingBolts.put(this.id, this);
            this.victim = victim;
        }

        protected void tick() {
            travelTime++;
            if (this.travelTime >= BOLT_TRAVEL_TIME) {
                this.finish();
            }
        }

        protected void begin() {
            victim.setData(ClinkerDataAttachments.CHAIN_LIGHTNING_RECEIVE_COOLDOWN, COOLDOWN_TIME);
            entitiesToAdd.add(this.victim);
            this.spawnBoltParticle();
        }

        protected void finish() {
            this.victim.hurt(victim.damageSources().lightningBolt(), 0.2f);
            this.victim.setData(ClinkerDataAttachments.CHAIN_LIGHTNING_SPREAD_DELAY, SPREAD_DELAY);
            travellingBolts.remove(this.id);
        }

        private void spawnBoltParticle() {
            if (shocker != null) {
                if (shocker.level() instanceof ServerLevel serverLevel) {
                    Vec3 pos1 = shocker.position().add(0, shocker.getBbHeight() * 0.5, 0);
                    Vec3 pos2 = victim.position().add(0, victim.getBbHeight() * 0.5, 0);
                    Vec3 middlePos = pos1.add(pos2).scale(0.5);

                    serverLevel.sendParticles(
                            new ChainLightningParticle.ChainLightningParticleOptions(pos1, pos2),
                            middlePos.x(), middlePos.y(), middlePos.z(), 1,
                            0, 0, 0, 0);
                }
            }
        }
    }
}
