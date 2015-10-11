package schemmer.hexagon.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;
import schemmer.hexagon.units.Unit;

public class AStar {
	public static ArrayList<Hexagon> calculate(MapHandler mh, Hexagon start, Hexagon goal, int maxMovement){
		PriorityQueue frontier = new PriorityQueue(start,0);
		ArrayList<Hexagon> visited = new ArrayList<Hexagon>();
		AbstractMap<Hexagon, Hexagon> cameFrom = new HashMap<Hexagon, Hexagon>();
		AbstractMap<Hexagon, Integer> costSoFar = new HashMap<Hexagon, Integer>();
		cameFrom.put(start, null);
		costSoFar.put(start, 0);
		
		System.out.println("Start: "+start.getCoords().printCube());
		System.out.println("goal: "+goal.getCoords().printCube());
		
		
		Hexagon current;
		while(!frontier.isEmpty()){
			System.out.println("Now Checking generally");
			current = frontier.getFirst();
			frontier.deleteFirst();
			
			if (current == goal) {
				System.out.println("Finished!");
				ArrayList<Hexagon> ret = new ArrayList<Hexagon>();
				Hexagon x = goal;
				while(x != null){
					System.out.println("Now Checking: "+x.getCoords().printCube());
					ret.add(x);
					x = cameFrom.get(x);
				}
				System.out.println("Finished completely!");
				return ret;
			}
			
			for (int i = 0; i < Cube.directions.length; i++){
				Cube c = Cube.addCubes(current.getCoords(), Cube.directions[i]);
				Hexagon next = mh.getInArray(c);
				if(next != null){
					if(!frontier.contains(next)){
						int mCosts = next.getMovementCosts();
						if(mCosts != -1 && costSoFar.get(current) + mCosts <= maxMovement){			//
							int newCost = costSoFar.get(current) + mCosts;
							if(costSoFar.get(next) == null){
								costSoFar.put(next, newCost);
								cameFrom.put(next, current);
								int priority = newCost + Cube.distance(current.getCoords(), c)*mCosts;
								frontier.add(next, priority);
							} 
						}
					} else{
						int mCosts = next.getMovementCosts();
						if(mCosts != -1 && costSoFar.get(current) + mCosts < costSoFar.get(next)){
							cameFrom.put(next, current);
							costSoFar.put(next, costSoFar.get(current) + mCosts);
						}
					}
				}	// == null
			}   // for
			
			if(frontier.isEmpty()){
				System.out.println("Returning empty");
				return new ArrayList<Hexagon>();
			}
		}
		System.out.println("Returning null");
		return null;
	}

	
	
	
	public static boolean isDoable(Unit u, MapHandler mh, Hexagon start, Hexagon goal, boolean debug){
		ArrayList<Hexagon> movements = AStar.calculate(mh, start, goal, u.getMovementSpeed());
		int totalCost = 0;
		if(debug)System.out.println("Start: "+start.getCoords().printCube());
		if(debug)System.out.println("Goal: "+goal.getCoords().printCube()+", "+movements.size());

		while(movements.size() > 0){
			if(debug)System.out.println("Mov: "+movements.get(0).getCoords().printCube());
			if(movements.get(0).getMovementCosts() == -1) System.out.println("CRITICAL ERROR!");
			totalCost += movements.get(0).getMovementCosts();
			movements.remove(0);
		}
		if(totalCost <= u.getMovementSpeed())
			return true;
		return false;
	}
	
	public static void test(){
		Hexagon h = new Hexagon(new Cube(0,0,0));
		PriorityQueue pq = new PriorityQueue(h, 1);
		Log.d("AStar", pq.toString());
		pq.add(h, 3);
		Log.d("AStar", pq.toString());
		pq.add(h, 2);
		Log.d("AStar", pq.toString());
		pq.add(h, 5);
		Log.d("AStar", pq.toString());
		pq.add(h, 5);
		Log.d("AStar", pq.toString());
		pq.deleteFirst();
		Log.d("AStar", pq.toString());
		pq.deleteFirst();
		Log.d("AStar", pq.toString());
		pq.add(h, 6);
		Log.d("AStar", pq.toString());
		pq.delete(6);
		Log.d("AStar", pq.toString());
		pq.delete(3);
		Log.d("AStar", pq.toString());
	}
}
