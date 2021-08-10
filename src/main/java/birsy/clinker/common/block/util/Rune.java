package birsy.clinker.common.block.util;

import net.minecraft.util.StringRepresentable;

public enum Rune implements StringRepresentable {
	NONE("none"), 
	RANDOM("random"),
	A("a"), 
	B("b"), 
	C("c"), 
	D("d"), 
	E("e"), 
	F("f"), 
	G("g"), 
	H("h"), 
	I("i"), 
	J("j"), 
	K("k"), 
	L("l"), 
	M("m"), 
	N("n"), 
	O("o"), 
	P("p"), 
	Q("q"), 
	R("r"), 
	S("s"), 
	T("t"), 
	U("u"), 
	V("v"), 
	W("w"), 
	X("x"), 
	Y("y"), 
	Z("z");
	
	private final String letter;
	
	private Rune(String letterIn) {
		letter = letterIn;
	}
	
	public String toString() {
		return this.getSerializedName();
	}

	public String getSerializedName() {
		return this.letter;
	}
}