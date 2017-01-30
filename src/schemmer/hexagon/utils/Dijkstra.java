package schemmer.hexagon.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import schemmer.hexagon.handler.MapHandler;
import schemmer.hexagon.map.Hexagon;

public class Dijkstra {
	public static ArrayList<Hexagon> getMovementRange(Hexagon[][] map, MapHandler mh, Hexagon start){
		ArrayList<Hexagon> result = Dijkstra.calculateResult(map, mh, start);
		return result;
	}
	
	
	public static int getMovementCost(Hexagon[][] map, MapHandler mh, Hexagon start, Hexagon goal){
		ArrayList<Hexagon> result = Dijkstra.calculateResult(map, mh, start);
		
	    for(int i = 0; i < result.size(); i++){
	    	if(result.get(i).equals(goal)){
	    		//System.out.println(goal.getCosts());
	    		return goal.getCosts();
	    	}
	    }
	    
		return -1;
	}
	
	public static ArrayList<Hexagon> getMovementPath(Hexagon[][] map, MapHandler mh, Hexagon start, Hexagon goal){
		ArrayList<Hexagon> result = new ArrayList<Hexagon>();
		
		AbstractMap<Hexagon, Hexagon> previous = new  HashMap<Hexagon, Hexagon>();
		AbstractMap<Hexagon, Integer> costs = new  HashMap<Hexagon, Integer>();
		PriorityQueue<Hexagon> q = new PriorityQueue<Hexagon>(100, hexComp);

		Hexagon current, next;
		
		//initialise
		int maxMovement = 0;
		if(start.getUnit() != null)
			maxMovement = start.getUnit().getMovementSpeed();
        
		for (int dx = -maxMovement; dx <= maxMovement; dx++){
		    for (int dy = Math.max(-maxMovement, -dx-maxMovement); dy <= Math.min(maxMovement, -dx + maxMovement); dy++){
		    	int dz = - dx - dy;
		    	Cube c = Cube.addCubes(start.getCoords(), new Cube(dx, dy, dz));
		    	current = mh.getInArray(c);
		    	
		    	if(current != null){
			    	previous.put(current, null);
			    	costs.put(current, 999);
		    		current.priority = 99;
		    		q.add(current);
		    	}
		    }
		}
		
		costs.put(start, 0);
		start.priority = 0;
		q.add(start);
		//System.out.println("Start: "+start.printCoords()+"("+costs.get(start)+")"+" | q length: " + q.size());
		while(!q.isEmpty()){
			current = q.peek();
			q.remove();
			
			//System.out.println("Now checking: "+current.printCoords()+" with costs: "+costs.get(current));
			
			for (int i = 0; i < Cube.directions.length; i++){
				//iterate over each neighbor
				next = mh.getInArray(Cube.addCubes(current.getCoords(), Cube.directions[i]));
				if(next != null && q.contains(next)){
					distanceUpdate(current, next, previous, costs);	
					//update priority
					q.remove(next);
					q.add(next);
				}
			}
		}
		
		//if the costs are lower than maxMovement and the goal is in the array calculate the path
		if(maxMovement != 0){
			if(costs.get(goal) <= maxMovement){
				current = goal;
				while(previous.get(current) != null){
					result.add(current);
					current = previous.get(current);
				}
				result.add(start);
			}
			return result;
		}
		return null;
	}
	
	public static ArrayList<Hexagon> calculateResult(Hexagon[][] map, MapHandler mh, Hexagon start){
		ArrayList<Hexagon> result = new ArrayList<Hexagon>();
		
		AbstractMap<Hexagon, Hexagon> previous = new  HashMap<Hexagon, Hexagon>();
		AbstractMap<Hexagon, Integer> costs = new  HashMap<Hexagon, Integer>();
		PriorityQueue<Hexagon> q = new PriorityQueue<Hexagon>(100, hexComp);

		Hexagon current, next;
		
		//initialise
		int maxMovement = start.getUnit().getMovementSpeed();
		
		for (int dx = -maxMovement; dx <= maxMovement; dx++){
		    for (int dy = Math.max(-maxMovement, -dx-maxMovement); dy <= Math.min(maxMovement, -dx + maxMovement); dy++){
		    	int dz = - dx - dy;
		    	Cube c = Cube.addCubes(start.getCoords(), new Cube(dx, dy, dz));
		    	current = mh.getInArray(c);
		    	
		    	if(current != null){
			    	previous.put(current, null);
			    	costs.put(current, 999);
		    		current.priority = 99;
		    		q.add(current);
		    	}
		    }
		}
		
		costs.put(start, 0);
		start.priority = 0;
		q.add(start);
		//System.out.println("Start: "+start.printCoords()+"("+costs.get(start)+")"+" | q length: " + q.size());
		while(!q.isEmpty()){
			current = q.peek();
			q.remove();
			
			//System.out.println("Now checking: "+current.printCoords()+" with costs: "+costs.get(current));
			
			for (int i = 0; i < Cube.directions.length; i++){
				//iterate over each neighbor
				next = mh.getInArray(Cube.addCubes(current.getCoords(), Cube.directions[i]));
				if(next != null && q.contains(next)){
					distanceUpdate(current, next, previous, costs);	
					//update priority
					q.remove(next);
					q.add(next);
				}
			}
		}
		
		//if the costs are lower than maxMovement add it to the result
		Iterator<Entry<Hexagon, Integer>> it = costs.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Hexagon, Integer> pair = it.next();
	        
	        if(pair.getValue() <= maxMovement){
	        	result.add(pair.getKey());
	        	pair.getKey().setCosts(pair.getValue());
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    return result;
	}
	
	//calculate and compare the new distance from current node (u) to next node (v)
	private static void distanceUpdate(Hexagon u, Hexagon v, AbstractMap<Hexagon, Hexagon> previous, AbstractMap<Hexagon, Integer> costs){
		try{
			if(v.getMovementCosts() != -1){ 			// is mountain or water
				int alt = costs.get(u) + Dijkstra.getWeightedCosts(u, v);
				if (alt < costs.get(v)){
					costs.put(v, alt);
					previous.put(v, u);
					v.priority = alt;
					//System.out.println("Updated costs from "+u.printCoords()+" to "+v.printCoords()+": "+alt+" by: "+costs.get(u)+" + "+v.getMovementCosts());
				}
			}
		}
		catch (java.lang.NullPointerException e){
			System.out.println("Nullpointer: "+costs.get(u));
			System.out.println("u coords:" +u.printCoords());
			System.out.println("v movement:"+v.getMovementCosts());
		}
	}
	
	private static Comparator<Hexagon> hexComp = new Comparator<Hexagon>(){
        
        @Override
        public int compare(Hexagon x, Hexagon y) {
            return x.priority - y.priority;
        }
    };
    
    // calculates the weighted average movement costs from Hex x to Hex y
    // otherwise it is possible to go from x(2) to y(1), but not from y(1) to x(2)
    //TODO: bug with hills: sometimes you get a free movepoint
    private static int getWeightedCosts(Hexagon x, Hexagon y){
    	return Dijkstra.castToInt(0.5f*x.getMovementCosts() + 0.5f*y.getMovementCosts());
    }
    
    private static int castToInt(float f){
    	if (f % 1f < 0.5f)
    		return (int) f;
    	return (int)(f + 1);
    }
	
}
