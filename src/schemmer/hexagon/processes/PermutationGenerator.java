package schemmer.hexagon.processes;

import java.util.Random;

public class PermutationGenerator {
	public static short[] generate(int length){
		short[] ret = new short[length];
		
		for(short i = 0; i < length; i++){
			ret[i] = i;
		}
		
		short temp;
		int target;
		Random r = new Random();
		for (int i = 0; i < length; i++){
			target = r.nextInt(length - i)+i;
			temp = ret[target];
			ret[target] = ret[i];
			ret[i] = temp;
		}
		
		return ret;
	}
}
