package texts;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.io.Resources;

public class Text {
	
	private List<String> list = new ArrayList<>();
	private int currentLine = -1;
	
	public Text(String path) throws IOException{
		String text = Resources.toString(getClass().getResource(path+".txt"),  StandardCharsets.UTF_8);
		Scanner sc = new Scanner(text);
		while (sc.hasNextLine()){
			list.add(sc.nextLine());
		}
		sc.close();
	}
	
	public String getLine(int i){
		if (i<0 || i>=list.size())
			return "";
		return list.get(i);
	}
	
	public String getNextLine(){
		currentLine++;
		return getLine(currentLine);
	}
	
}
