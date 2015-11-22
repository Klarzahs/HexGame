package schemmer.hexagon.buildings;

public class Costs {
	private int[] costs = new int[4];
	
	public Costs(int food, int wood, int stone, int gold){
		costs[0] = food;
		costs[1] = wood;
		costs[2] = stone;
		costs[3] = gold;
	}
	
	public int[] getCosts(){
		return costs;
	}
	
	public int getFoodCosts(){
		return costs[0];
	}
	
	public int getWoodCosts(){
		return costs[1];
	}
	
	public int getStoneCosts(){
		return costs[2];
	}
	
	public int getGoldCosts(){
		return costs[3];
	}
	
	public boolean isHigherThan(Costs co){
		int[] c = co.getArr();
		return (costs[0] >= c[0] && costs[1] >= c[1] && costs[2] >= c[2] && costs[3] >= c[3] );
	}
	
	private int[] getArr(){
		return costs;
	}
	
	public void substract(Costs co){
		int[] c = co.getArr();
		this.costs = ( new Costs(costs[0]- c[0], costs[1] - c[1], costs[2] - c[2], costs[3] - c[3] )).getArr();
	}
	
	public static Costs substract(Costs a, Costs b){
		int[] aa = a.getArr();
		int[] ba = b.getArr();
		return new Costs(aa[0]- ba[0], aa[1] - ba[1], aa[2] - ba[2], aa[3] - ba[3]);
	}
}
