package schemmer.hexagon.processes;

import java.util.Random;

public class PermutationGenerator {
	private static final int SEEDLENGTH = 10;
	
	public static short[] generate(int length, String seed){
		short[] ret = new short[length];
		
		// fill with pos index
		for(short i = 0; i < length; i++){
			ret[i] = i;
		}

		// prolong/short seed if necessary
		while(seed.length() < SEEDLENGTH){
			seed = seed + seed;
		}
		if(seed.length() > SEEDLENGTH){
			seed = seed.substring(0, SEEDLENGTH);
		}
		
		// swap values randomly (depending on seed)
		short temp;
		int target;
		try{
			Random r = new Random(Long.parseLong(seed));
			for (int i = 0; i < length; i++){
				// get swapping index
				target = r.nextInt(length - i)+i;
				temp = ret[target];
				ret[target] = ret[i];
				ret[i] = temp;
			}
		}catch(Exception e){
			System.out.println("Couldn't parse String to Long for Permutation generator. System exit!");
			System.err.println(e.getMessage());
			System.exit(1);
		}

		return ret;
	}
}
