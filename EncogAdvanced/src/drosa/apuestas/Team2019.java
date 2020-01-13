package drosa.apuestas;

import java.util.ArrayList;

public class Team2019 {
	String name = "";
	ArrayList<Integer> matches = new ArrayList<Integer>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Integer> getMatches() {
		return matches;
	}
	public void setMatches(ArrayList<Integer> matches) {
		this.matches = matches;
	}
	
	public void addResult(int res){
		matches.add(res);
	}
	
}
