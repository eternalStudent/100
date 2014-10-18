package util;

import java.util.Collection;

public class Random {
	
	private static java.util.Random r = new java.util.Random();
	
	public static int nextInt(int i){
		return r.nextInt(i);
	}
	
	public static int nextInt(int x0, int x1){
		return x0+nextInt(x1-x0+1);
	}
	
	public static Point nextPoint(int x0, int x1, int y0, int y1){
		return new Point(nextInt(x0, x1), nextInt(y0, y1));
	}
	
	public static boolean isNext(int i){
		return nextInt(i)==0;
	}
	
	public static boolean isNext(){
		return isNext(2);
	}
	
	public static int normal(int x0, int x1){
		int sum=x0;
		for (int i=0; i<x1-x0; i++)
			sum+=nextInt(2);
		return sum;
	}
	
	public static <E> E nextElement(Collection<E> set){
		int r = nextInt(set.size());
		int i =0;
		for (E e: set){
			if (i==r)
				return e;
			i++;
		}
		return null;
	}

}
