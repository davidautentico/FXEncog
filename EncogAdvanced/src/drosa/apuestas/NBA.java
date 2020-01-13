package drosa.apuestas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import drosa.utils.PrintUtils;

public class NBA {

	public static void readBBDD(String folder){
		HashMap<String,Team2019> nba = new HashMap<String,Team2019>();
	}
	
	
	public static void main(String[] args) {
		
		String folder = "C:\\nba\\";
		
		
		/*int y0 = 2016;
		ArrayList<Match> matches2016 = NHL.readFromDisk(folder, y0, y0,1,1);
		HashMap<String,String> teams = NHL.extractTeams(matches2016);
		//NHL.printMap(teams);
		int y2 = 2016;
		int y1 = 2014;
		ArrayList<Match> matches = NHL.readFromDisk(folder, y1, y2,1,1);
		//System.out.println(matches.size());
		String team = "Portland Trail Blazers";
		for (int y=2000;y<=2016;y++){
			String header = y+" ";
			matches = NHL.readFromDisk(folder, y-3, y,1,1);
			//System.out.println(matches.size());
			BetStats betStats = NHL.getAvgStreakByTeam(matches, team);
			NHL.printStreakInfo(header, betStats);
		}*/
		//Equipos2016
		/*int y0 = 2016;
		ArrayList<Match> matches2016 = NHL.readFromDisk(folder, y0, y0,1,1);
		HashMap<String,String> teams = NHL.extractTeams(matches2016);
		
		for (Entry<String, String> entry : teams.entrySet()) {
			String team = entry.getKey();
			
			//if (!team.contains("Predators")) continue;
			
			int y2 = 2016;
			int y1 = 2013;
			ArrayList<Match> matches = NHL.readFromDisk(folder, y1, y2,1,1);
			int res = 1;
			double prob = NHL.getContinueOdds(matches, team, 2, res);
			double oddsWin = 1.0;
			
			if (res==1){
				oddsWin = 100.0/prob;
			}else{
				oddsWin = 100.0/(100.0-prob);
			}
			System.out.println(
					team
					+" "+PrintUtils.Print2dec(prob, false)
					+" || "+PrintUtils.Print2dec(oddsWin, false)
					);
		}*/

	}

}
