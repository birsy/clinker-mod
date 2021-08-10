package birsy.clinker.common.tileentity;

import birsy.clinker.core.registry.ClinkerTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulWellTileEntity extends TileEntity implements ITickableTileEntity {
    private int ageInTicks;
    private int prevAgeInTicks;

    private int chargeUpTime = 0;
    private int prevChargeUpTime;

    public SoulWellTileEntity() {
        super(ClinkerTileEntities.SOUL_WELL.get());
    }

    @Override
    public void tick() {
        this.prevAgeInTicks = this.ageInTicks;
        this.ageInTicks++;

        this.prevChargeUpTime = this.chargeUpTime;
        if (chargeUpTime > 60) {
            this.chargeUpTime++;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getChargeUpTime (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevChargeUpTime, this.chargeUpTime);
    }

    @OnlyIn(Dist.CLIENT)
    public float getAgeInTicks (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevAgeInTicks, this.ageInTicks);
    }
}
