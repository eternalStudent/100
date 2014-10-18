package model.mobs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Resources;

public class ListOfTraits{
		
		public List<Trait> traits = new ArrayList<>();
		
		public ListOfTraits(){
			try {
				readFile(Resources.toString(getClass().getResource("/raw/traits.txt"), StandardCharsets.UTF_8));
			} 
			catch (ParseException | IOException e) {}
		}
		
		private void add(String name, String desc){
			traits.add(new Trait(name,  desc));
		}
		
		public Trait get(int i){
			return traits.get(i);
		}
		
		private String read(String text, String field, String initialValue){
			Pattern p = Pattern.compile("\\["+field+"\\]: \"[^\"]*\"");
			Matcher m = p.matcher(text);			
			if (m.find())try{
				Object[] values = new MessageFormat("["+field+"]: \"{0}\"").parse(m.group());
				return values[0].toString();
			}
			catch(ParseException e){
				e.printStackTrace();
			}
			return initialValue;
		}

		
		private void readFile(String text) throws FileNotFoundException, ParseException{
			Pattern pattern = Pattern.compile("\\{([^//}])*\\}");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()){
				String elem = matcher.group();
				String name = read(elem, "name", "");
				String desc = read(elem, "desc", "");
				add(name, desc);
			}

		}
		
		public int size(){
			return traits.size();
		}
	}
