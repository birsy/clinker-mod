package birsy.clinker.common.world.alchemy;

public enum MatterState {
    SOLID("solid"), POWDER("powder"), LIQUID("liquid"), GAS("gas");
    public final String name;

    MatterState(String name) {
        this.name = name;
    }

    public static MatterState fromName(String name) {
        for (MatterState state : MatterState.values()) {
            if (state.name() == name) {
                return state;
            }
        }

        return null;
    }
}
