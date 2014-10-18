package model;
import java.util.ArrayList;
import java.util.List;

public class Log {
	
	private List<String> list = new ArrayList<>();
	public int cap = 3;
	
	public Log(int cap){
		this.cap = cap;
	}
	
	public void write(String text){
		list.add(text);
	}
	
	public int size(){
		return list.size();
	}
	
	public String get(int i){
		if (i>=0 && i<size())
			return list.get(i);
		return "";
	}
	
	public String[] getLast(int n){
		String[] st = new String[n];
		for (int i=0; i<n; i++)
			st[i] = get(size()-n+i);
		return st;
	}
	
	public String[] getLast(){
		return getLast(cap);
	}

}
