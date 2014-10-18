package model;
public class Terrain{
	
	public static boolean solid(String name){
		return name.endsWith("wall");
	}
	
	public static boolean halfSolid(String name){
		return solid(name) || name.equals("table") || name.equals("chasm") || name.equals("cage door");
	}
	
	public static boolean transparent(String name){
		return !(name.endsWith("wall") || name.equals("closed door"));
	}

}
