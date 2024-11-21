package birsy.clinker.common.world.entity.urn;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundBrainDebugPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.GnomadSquadSensor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.GenericAttackTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;

import java.util.List;

public class UrnEntity extends Mob implements SmartBrainOwner<UrnEntity> {
    public UrnEntity(EntityType<? extends UrnEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain(this);
        ClinkerPacketHandler.sendToClientsTrackingEntity(this, new ClientboundBrainDebugPacket(this));
    }
    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new SmoothGroundNavigation(this, pLevel);
    }
    @Override
    public List<ExtendedSensor<UrnEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<UrnEntity>().setRadius(28.0F),
                new NearbyPlayersSensor<UrnEntity>().setRadius(28.0F),
                new GenericAttackTargetSensor<UrnEntity>().setPredicate((other, me) -> other instanceof Player),
                new HurtBySensor<>(),
                new InWaterSensor<>()
        );
    }
}
