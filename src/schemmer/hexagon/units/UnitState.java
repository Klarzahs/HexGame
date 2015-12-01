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
    
    public static UnitState getStateOfValue(int i){
    	switch(i){
	    	case 0: return STATE_FOOD;
	    	case 1: return STATE_WOOD;
	    	case 2: return STATE_STONE;
	    	case 3: return STATE_GOLD;
	    	case 4: return STATE_BUILDING;
	    	case 5: return STATE_NONE;
	    	default: return STATE_NONE;
    	}
    }

}
