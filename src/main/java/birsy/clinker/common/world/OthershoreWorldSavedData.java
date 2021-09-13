package birsy.clinker.common.world;

import birsy.clinker.core.Clinker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class OthershoreWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = Clinker.MOD_ID + "_dimensionInfo";

    public OthershoreWorldSavedData() {
        super(DATA_NAME);
    }

    @Override
    public void read(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("Weather", 0);
        return null;
    }

    public enum Weather {

    }
}
