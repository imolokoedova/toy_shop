import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App {
	static class Toy {
		int id;
		String title;
		int lots;
		double weight;
		public Toy(int id, String title, int lots, double weight) {
			this.id = id;
			this.title = title;
			this.lots = lots;
			this.weight = weight;
		}
		String toJson() {
			return String.format("{\"id\":%d, \"title\":\"%s\", \"lots\":%d, \"weight\":%f}", id, title, lots, weight);
		}
	}
	ArrayList<Toy> toys = new ArrayList();
	void toyAppend(String title, int lots, double weight) {
		Toy toy = new Toy(toys.size(), title, lots, weight);
		toys.add(toy);
		System.out.println("added: " + toy.toJson());
	}
	void toySetWeight(int id, double weight) {
		Toy toy = toyById(id);
		toy.weight = weight;
		System.out.println("modified: " + toy.toJson());
	}
	void gambleToy() {
		int length = toys.size();
		double weight_summ = 0;
		for (int i=0; i<length; i++) {
			weight_summ += toys.get(i).weight;
		}
		double rnd = weight_summ * Math.random();
		Toy selected = null;
		double width_summ = 0;
		for (int i=0; i<length; i++) {
			double width = toys.get(i).weight;
			if ((rnd > width_summ) && (rnd < width_summ + width)) {
				selected = toys.get(i);
				break;
			}
			width_summ += width;
		}
		if (selected == null) {
			System.out.println("no toys left");
			return;
		}
		selected.lots -= 1;
		if (selected.lots == 0) {
			toys.remove(selected);
		}
		System.out.println(String.format("writing to file: %s", selected.toJson()));
		toyToFile(selected);
	}
	Toy toyById(int id) {
		for (int i=0; i<toys.size(); i++) {
			if (toys.get(i).id == id) {
				return toys.get(i);
			}
		}
		return null;
	}
	void toyToFile(Toy toy) {
		try {
			FileWriter f = new FileWriter("toys-result.json", true);
			PrintWriter w = new PrintWriter(f);
			w.println(toy.toJson());
			w.close();
			f.close();
		}
		catch (IOException e) {
			System.out.println(String.format("failed to save: %s", e.toString()));
		}
	}
	void saveToys() {
		JSONParser parser = new JSONParser();
		try {
			String str = "[";
			for (int i=0; i<toys.size(); i++) {
				if (i != 0) {
					str += ", ";
				}
				str += toys.get(i).toJson();
				
			}
			str += "]";
			PrintWriter w = new PrintWriter("toys.json");
			w.println(str);
			w.close();
		}
		catch (IOException e) {
			System.out.println(String.format("failed to save: %s", e));
		}		
	}
	void loadToys() {
		JSONParser parser = new JSONParser();
		try {
			FileReader f = new FileReader("toys.json");
			JSONArray js = (JSONArray) parser.parse(f);
			Iterator<JSONObject> iterator = js.iterator();
			while (iterator.hasNext()) {
            	JSONObject obj = iterator.next();
            	int id = ((Long) obj.get("id")).intValue();
            	String title = (String) obj.get("title");
            	int lots = ((Long) obj.get("lots")).intValue();
            	double weight = (Double) obj.get("weight");
            	Toy toy = new Toy(id, title, lots, weight);
            	toys.add(toy);
            }

			f.close();
		}
		catch (FileNotFoundException e) {
			return;
		}
		catch (Exception e) {
			System.out.println(String.format("failed to load: %s", e));
		}		
	}
	public static void main(String[] args) {
		App app = new App();
		app.loadToys();
		switch (args[0]) {
			case "gamble": 
				{
					int count = Integer.parseInt(args[1]);
					for (int i=0; i<count; i++) {
						app.gambleToy();
					}
					app.saveToys();
				}
				break;
			case "new":
				{
					String title = args[1];
					int lots = Integer.parseInt(args[2]);
					double weight = Double.parseDouble(args[3]);
					app.toyAppend(title, lots, weight);
					app.saveToys();
				}
				break;
			case "set":
				{
					int id = Integer.parseInt(args[1]);
					double weight = Double.parseDouble(args[2]);
					app.toySetWeight(id, weight);
					app.saveToys();
				}
				break;
		}
	}
}
