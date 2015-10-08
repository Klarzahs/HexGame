package schemmer.hexagon.map;

public enum HexTypeInt {
	TYPE_FIELD(0),
	TYPE_HILL(1),
	TYPE_MOUNTAIN(2),
	TYPE_WATER(3),
	TYPE_DEEPWATER(4)
    ;
	
	private final int value;
    private HexTypeInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
