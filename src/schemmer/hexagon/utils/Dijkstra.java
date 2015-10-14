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
		ArrayList<Hexagon> result = new ArrayList<Hexagon>();
		
		AbstractMap<Hexagon, Hexagon> previous = new  HashMap<Hexagon, Hexagon>();
		AbstractMap<Hexagon, Integer> costs = new  HashMap<Hexagon, Integer>();
		Comparator<Hexagon> comp = new HexagonComparator();
		PriorityQueue<Hexagon> q = new PriorityQueue<Hexagon>(100, comp);

		Hexagon current, next;
		
		//initialise
		int maxMovement = start.getUnit().getMovementSpeed();
		
		
		for (int qi = 0; qi < map.length; qi++){
		    for (int ri = 0; ri < map[qi].length; ri++){
		    	current = map[qi][ri];
		    	
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
		int index = 0;
		while(!q.isEmpty()){
			current = q.peek();
			q.remove();
			
			//System.out.println("Now checking: "+current.printCoords()+" with costs: "+costs.get(current));
			
			for (int i = 0; i < Cube.directions.length; i++){
				//iterate over each neighbor
				next = mh.getInArray(Cube.addCubes(current.getCoords(), Cube.directions[i]));
				if(next != null && q.contains(next)){
					distanceUpdate(current, next, previous, costs);	
				}
			}
		}
		
		//if the costs are lower than maxMovement add it to the result
		Iterator<Entry<Hexagon, Integer>> it = costs.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Hexagon, Integer> pair = (Map.Entry<Hexagon, Integer>)it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        if((int)pair.getValue() <= maxMovement){
	        	result.add(pair.getKey());
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		return result;
	}
	
	
	public static int getMovementCost(Hexagon[][] map, MapHandler mh, Hexagon start, Hexagon goal){
		ArrayList<Hexagon> result = new ArrayList<Hexagon>();
		
		AbstractMap<Hexagon, Hexagon> previous = new  HashMap<Hexagon, Hexagon>();
		AbstractMap<Hexagon, Integer> costs = new  HashMap<Hexagon, Integer>();
		Comparator<Hexagon> comp = new HexagonComparator();
		PriorityQueue<Hexagon> q = new PriorityQueue<Hexagon>(100, comp);

		Hexagon current, next;
		
		//initialise
		int maxMovement = start.getUnit().getMovementSpeed();
		
		
		for (int qi = 0; qi < map.length; qi++){
		    for (int ri = 0; ri < map[qi].length; ri++){
		    	current = map[qi][ri];
		    	
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
				}
			}
		}
		
		//if the costs are lower than maxMovement add it to the result
		Iterator<Entry<Hexagon, Integer>> it = costs.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Hexagon, Integer> pair = (Map.Entry<Hexagon, Integer>)it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        if((int)pair.getValue() <= maxMovement){
	        	result.add(pair.getKey());
	        	pair.getKey().setCosts(pair.getValue());
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
	    for(int i = 0; i < result.size(); i++){
	    	if(result.get(i).equals(goal)){
	    		System.out.println(goal.getCosts());
	    		return goal.getCosts();
	    	}
	    }
	    
		return -1;
	}
	
	
	
	//calculate and compare the new distance from current node (u) to next node (v)
	public static void distanceUpdate(Hexagon u, Hexagon v, AbstractMap<Hexagon, Hexagon> previous, AbstractMap<Hexagon, Integer> costs){
		try{
			if(v.getMovementCosts() != -1){ 			// is mountain or water
				int alt = costs.get(u) + v.getMovementCosts();
				if (alt < costs.get(v)){
					costs.put(v, alt);
					previous.put(v, u);
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
	
	private static int getPrio(PriorityQueue<Hexagon> q, Hexagon h){
		Iterator<Hexagon> it = q.iterator();
	    while (it.hasNext()) {
	       Hexagon hex = it.next();
	        if(hex.equals(h)){
	        	return hex.priority;
	        }
	    }
	    return -1;
	}
	
	
	public static class HexagonComparator implements Comparator<Hexagon>{
		@Override
	    public int compare(Hexagon x, Hexagon y)
	    {
	        return x.priority - y.priority;
	    }
	}
}
