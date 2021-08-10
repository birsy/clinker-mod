package birsy.clinker.common.block.util;

import net.minecraft.util.StringRepresentable;

public enum PillarCap implements StringRepresentable {
	NONE("none"),
	UP("up"),
	DOWN("down"),
	BOTH("both");
	
	private final String letter;
	
	private PillarCap(String letterIn) {
		letter = letterIn;
	}
	
	public String toString() {
		return this.getSerializedName();
	}

	public String getSerializedName() {
		return this.letter;
	}
}