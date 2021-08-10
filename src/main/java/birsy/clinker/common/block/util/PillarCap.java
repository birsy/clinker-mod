package birsy.clinker.common.block.util;

import net.minecraft.util.IStringSerializable;

public enum PillarCap implements IStringSerializable {
	NONE("none"),
	UP("up"),
	DOWN("down"),
	BOTH("both");
	
	private final String letter;
	
	private PillarCap(String letterIn) {
		letter = letterIn;
	}
	
	public String toString() {
		return this.getString();
	}

	public String getString() {
		return this.letter;
	}
}