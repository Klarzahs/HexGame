package schemmer.hexagon.units;

public enum UnitState {
	STATE_FOOD(0),
	STATE_WOOD(1),
	STATE_STONE(2),
	STATE_GOLD(3),
	STATE_BUILDING(4),
	STATE_NONE(5)
    ;
	
	private final int value;
    private UnitState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
