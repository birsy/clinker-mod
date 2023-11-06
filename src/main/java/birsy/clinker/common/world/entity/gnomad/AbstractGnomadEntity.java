package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.common.world.entity.IVelocityTilt;
import birsy.clinker.common.world.entity.ai.ISitter;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGnomadEntity extends Monster implements IVelocityTilt, ISitter {
    private float pRoll = 0.0F;
    private float roll = 0.0F;
    private float pPitch = 0.0F;
    private float pitch = 0.0F;

    private float pSitTicks = 0.0F;
    private float sitTicks = 0.0F;
    private boolean sitting = false;

    public boolean headShaking = false;
    public float pHeadShakingIntensity = 0.0F;
    public float headShakingIntensity = 0.0F;
    public int headShakingLength = 10;

    public GnomadSquad<AbstractGnomadEntity> gnomadSquad;
    public int ticksOutsideSquadRange = 0;
    public boolean isSquadLeader = false;

    public AbstractGnomadEntity(EntityType<? extends AbstractGnomadEntity> entity, Level level) {
        super(entity, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateHeadShaking();
        this.updateSitting();
        this.updateEntityRotation(this);
    }

    public void updateHeadShaking() {
        this.pHeadShakingIntensity = this.headShakingIntensity;

        if (this.random.nextInt(100) == 0 && headShakingIntensity <= 0.0F) {
            this.headShaking = true;
            this.headShakingLength = this.random.nextInt(50) + 10;
        }

        float intensityDelta = 1.0F / this.headShakingLength * (this.headShaking ? 1.0F : -1.0F);
        this.headShakingIntensity = Mth.clamp(this.headShakingIntensity + intensityDelta, 0.0F, 1.0F);

        if (headShakingIntensity >= 1.0F) {
            this.headShaking = false;
        }
    }

    public float getHeadShakingIntensity(float partialTicks) {
        return Mth.lerp(partialTicks, this.pHeadShakingIntensity, this.headShakingIntensity);
    }

    public boolean isSitting() {
        return sitting || this.isPassenger() && (this.getVehicle() != null && this.getVehicle().shouldRiderSit());
    }
    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }
    public float getSitTicks(float partialTicks) {
        float sitAmount = Mth.lerp(partialTicks, pSitTicks, sitTicks);
        float bounceIntensity = 1.3F;
        return ((MathUtils.ease(sitAmount, MathUtils.EasingType.easeInBack) - sitAmount) * bounceIntensity) + sitAmount;
    }
    public void setSitTicks(float sitTicks) {
        this.pSitTicks = this.sitTicks;
        this.sitTicks = sitTicks;
    }

    public float getRoll(float partialTicks) {
        return Mth.rotLerp(partialTicks, this.pRoll, this.roll);
    }
    public float getPitch(float partialTicks) {
        return Mth.rotLerp(partialTicks, this.pPitch, this.pitch);
    }
    public void setPitch(float pitch) {
        this.pPitch = this.pitch;
        this.pitch = pitch;
    }
    public void setRoll(float roll) {
        this.pRoll = this.roll;
        this.roll = roll;
    }

    public class GnomadSquad<E extends AbstractGnomadEntity> {
        public Level level;
        public List<E> squadMembers;
        public Vec3 squadCenter;
        public E squadLeader;
        public long squadFactionID;
        private int ticksExisted = 0;

        public GnomadSquad(long squadFactionID, E squadLeader) {
            this(squadFactionID, squadLeader.level());
            squadLeader.isSquadLeader = true;
            this.squadMembers.add(squadLeader);
            this.squadLeader = squadLeader;
        }

        public GnomadSquad(long squadFactionID, Level level) {
            this.level = level;
            this.squadMembers = new ArrayList<>();
            this.squadFactionID = squadFactionID;
        }

        public void addSquadMember(E squadMember) {
            squadMembers.add(squadMember);
        }

        public void tick() {
            this.ticksExisted++;
            if (!squadMembers.isEmpty()) {
                if ((this.ticksExisted + squadFactionID) % 5 == 0) {
                    calculateSquadCenter();

                    float squadRange = 10.0F;
                    for (AbstractGnomadEntity entity : this.level.getEntities(EntityTypeTest.forClass(AbstractGnomadEntity.class), new AABB(squadCenter.add(-squadRange, -squadRange, -squadRange), squadCenter.add(squadRange, squadRange, squadRange)), EntitySelector.NO_SPECTATORS)) {
                        if (!this.squadMembers.contains(entity)) {
                            addSquadMember((E) entity);
                        }
                    }
                }

                for (int i = 0; i < squadMembers.size(); i++) {
                    E squadMember = squadMembers.get(i);

                    if (squadMember.position().distanceTo(squadCenter) > 24.0F) {
                        squadMember.ticksOutsideSquadRange++;
                        if (squadMember.ticksOutsideSquadRange > (10 * 20)) {
                            squadMembers.remove(i);
                        }
                    } else {
                        squadMember.ticksOutsideSquadRange = 0;
                    }
                }
            }
        }

        public void calculateSquadCenter() {
            Vec3 baseSquadAverage = null;
            boolean squadHasLeader = squadLeader != null;

            for (E squadMember : squadMembers) {
                if (squadMember != squadLeader) {
                    if (baseSquadAverage == null) {
                        baseSquadAverage = squadMember.position();
                    } else {
                        baseSquadAverage = baseSquadAverage.add(squadMember.position());
                    }
                }
            }
            float divisor = 1.0F / (float)(squadHasLeader ? squadMembers.size() - 1 : squadMembers.size());
            baseSquadAverage.multiply(divisor, divisor, divisor);

            if (squadHasLeader) {
                float leaderBias = 0.5F;
                squadCenter = squadLeader.position().multiply(leaderBias, leaderBias, leaderBias).add(baseSquadAverage.multiply(1.0F - leaderBias, 1.0F - leaderBias, 1.0F - leaderBias));
            } else {
                squadCenter = baseSquadAverage;
            }
        }
    }
}
