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
			id = id;
			title = title;
			lots = lots;
			weight = weight;
		}
		String toJson() {
			return "";
		}
	}
	ArrayList<Toy> toys = new ArrayList();
	void toyAppend(Toy toy) {
		toys.add(toy);
	}
	void toySetWeight(int id, double weight) {
		Toy toy = toyById(id);
		toy.weight = weight;
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
		System.out.println(String.format("writing to file: %s", selected.toString()));
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
			System.out.println(String.format("failed to save: %s", e));
		}
	}
	void saveToys() {
		JSONParser parser = new JSONParser();
		try {
			FileReader f = new FileReader("toys.json");
			JSONArray js = (JSONArray) parser.parse(f);
			Iterator<JSONObject> iterator = js.iterator();
			while (iterator.hasNext()) {
            	System.out.println(iterator.next());
            }

			f.close();
		}
		catch (Exception e) {
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
            	System.out.println(iterator.next());
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
				int count = Integer.parseInt(args[1]);
				for (int i=0; i<count; i++) {
					app.gambleToy();
				}
				break;
			case "new":
				app.saveToys();
				break;
			case "show":
				break;
		}
		return ;
	}
}
