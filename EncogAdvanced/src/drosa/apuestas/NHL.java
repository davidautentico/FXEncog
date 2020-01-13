package drosa.apuestas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class NHL {
	
	
	//sport = 0: nhl,1: nba
	public static ArrayList<Match> readFromDisk(String folder,int y1,int y2,int type,int sport){
		
		ArrayList<Match> matches1 = new ArrayList<Match>();
		
		for (int y=y1;y<=y2;y++){
			String fileName = folder+"nhl"+y+".txt";
			if (sport==1){
				//leagues_NBA_1970_games_games.csv
				fileName = folder+"leagues_NBA_"+y+"_games_games.csv";
			}
			File f = new File(fileName);
			if (!f.exists()){
				if (sport==1){
					fileName = folder+"nba"+y+".txt";
					//System.out.println("fileName: "+fileName);
					f = new File(fileName);
					if (!f.exists()){
						continue;
					}else{
						type=4;
					}
				}else{
					//System.out.println("[ERROR] NO EXISTE: "+fileName);
					continue;
				}
			}
			//System.out.println("a cargar: "+fileName);	
			ArrayList<Match> matches = Match.readFromDisk(fileName,type);
			for (int i=0;i<matches.size();i++) matches1.add(matches.get(i));							
		}
		
		return matches1;
	}
	
	static HashMap<String, String> extractTeams(ArrayList<Match> matches) {
		// TODO Auto-generated method stub
		//System.out.println("[extractTeams]: entrado");
		HashMap<String, String> teams = new HashMap<String, String>();
		
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			String homeTeam = m.getHomeTeam().trim();
			String awayTeam = m.getAwayTeam().trim();
			
			if (homeTeam.equalsIgnoreCase("Washington Capital")){
				//System.out.println(m.toString());
			}
			if (awayTeam.equalsIgnoreCase("Washington Capital")){
				//System.out.println(m.toString());
			}
			if (!teams.containsKey(homeTeam)){
				//System.out.println("anadiendo home: "+homeTeam);
				teams.put(homeTeam,homeTeam);
			}
			if (!teams.containsKey(awayTeam)){
				//System.out.println("anadiendo away: "+awayTeam);
				teams.put(awayTeam,awayTeam);
			}
			
		}
		
		return teams;
	}
	
	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public static double getWinPer(ArrayList<Match> matches,String team){
		
		double winPer = 100.0;
		
		int total = 0;
		int wins = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			int win = 0;
			if (m.homeTeam.equalsIgnoreCase(team)){
				total++;
				if (m.homeGoals>m.getAwayGoals()){//ha ganado
					win=1;
					wins++;
				}else if (m.homeGoals<m.getAwayGoals()){//ha perdido
					win=-1;
				}
			}else if (m.awayTeam.equalsIgnoreCase(team)){
				total++;
				if (m.homeGoals>m.getAwayGoals()){//ha ganado
					win=-1;
				}else if (m.homeGoals<m.getAwayGoals()){//ha perdido
					win=1;
					wins++;
				}
			}		
		}
		
		if (total==0) return 100.0;
		
		return wins*100.0/total;
		
	}
	
	public static ArrayList<Integer> getRachas(ArrayList<Match> matches,String team){
	
		ArrayList<Integer> rachas = new ArrayList<Integer>();
		
		for (int i=0;i<=9;i++){//-5 -4 -3 -2 -1 1 2 3 4 5
			rachas.add(0);
		}
		
		int actualStreak = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			int win = 0;
			if (m.homeTeam.equalsIgnoreCase(team)){
				if (m.homeGoals>m.getAwayGoals()){//ha ganado
					win=1;
				}else if (m.homeGoals<m.getAwayGoals()){//ha perdido
					win=-1;
				}
			}else if (m.awayTeam.equalsIgnoreCase(team)){
				if (m.homeGoals>m.getAwayGoals()){//ha ganado
					win=-1;
				}else if (m.homeGoals<m.getAwayGoals()){//ha perdido
					win=1;
				}
			}
			
			if (win==1){
				if (actualStreak>=0){
					actualStreak++;
					if (actualStreak>=5) actualStreak=5;
				}
				else{
					//metemos la actual que era negativa	
					
					for (int r=4;r>=actualStreak+5;r--){
						rachas.set(r,rachas.get(r)+1);//para indexarlo es +5
					}
					actualStreak = 1;
				}
			}else if (win==-1){
				if (actualStreak<=0){
					actualStreak--;
					if (actualStreak<=-5) actualStreak=-5;
				}
				else{
					//metemos la actual	que era positiva
					//metemos la actual que era negativa	
					for (int r=5;r<=actualStreak+4;r++){
						rachas.set(r,rachas.get(r)+1);//para indexarlo es +5
					}
					//rachas.set(actualStreak+4,rachas.get(actualStreak+4)+1);//para indexarlo es +4
					actualStreak = -1;
				}
			}
		}
		
		return rachas;
	}
	
	public static BetStats getStreakOdds(ArrayList<Match> matches,int rachaLen1,int rachaLen2,int res1,int res2,int expected){
		BetStats betStats = new BetStats();
		
		HashMap<String,ArrayList<Integer>> streaks = new HashMap<String,ArrayList<Integer>>();
		
		int cases = 0;
		int wins = 0;
		int maxWins = 0;
		int actualWins = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			
			
			
			
			//COMPUTAMOS RESULTADO ACTUAL
			int h = 0;
			int a = 0;
			if (m.getHomeGoals()>m.getAwayGoals()){
				h=1;
				a = -1;
			}else{
				h=-1;
				a = 1;
			}
			
			//COMPROBAMOS SI EL STREAK ANTERIOR COINCIDE CON LO BUSCADO
			if (streaks.containsKey(homeTeam)){
				ArrayList<Integer> streak1 = streaks.get(homeTeam);
				if (streaks.containsKey(awayTeam)){
					ArrayList<Integer> streak2 = streaks.get(awayTeam);
					if (streak1.size()>=rachaLen1 && streak2.size()>=rachaLen2){
						boolean isValid = false;
						for (int s1 = streak1.size()-1;s1>=streak1.size()-1-(rachaLen1-1);s1--){
							int r1 = streak1.get(s1);
							if (r1==res1){
								isValid = true;								
							}else{
								isValid = false;
								break;
							}
						}
						if (isValid){
							isValid = false;
							for (int s2 = streak2.size()-1;s2>=streak2.size()-1-(rachaLen2-1);s2--){
								int r2 = streak2.get(s2);
								if (r2==res2){
									isValid = true;								
								}else{
									isValid = false;
									break;
								}
							}
							
							//Si es valido se computa
							if (isValid){
								cases++;
								if (h==expected){
									wins++;
									actualWins++;
									if (actualWins>=maxWins) maxWins = actualWins;
									
									actualLosses = 0;
								}else{
									actualLosses++;
									if (actualLosses >=maxLosses) maxLosses = actualLosses;
									
									actualWins = 0;
								}
							}
						}//isValid
					}
				}
			}
			
			//AGREGAMOS EL RESULTADO ACTUAL
			if (!streaks.containsKey(homeTeam)){
				streaks.put(homeTeam, new ArrayList<Integer>());
			}
			streaks.get(homeTeam).add(h);
			
			if (!streaks.containsKey(awayTeam)){
				streaks.put(awayTeam, new ArrayList<Integer>());
			}
			streaks.get(awayTeam).add(a);
			
			
		}
		
						
		betStats.setCases(cases);
		betStats.setWins(wins);
		betStats.setMaxLosses(maxLosses);
		betStats.setMaxWins(maxWins);
		
		return betStats;
	}
	
	public static BetStats getStreakOddsByTeam(ArrayList<Match> matches,String team,int rachaLen1,int rachaLen2,int res1,int res2,int expected){
		BetStats betStats = new BetStats();
		
		HashMap<String,ArrayList<Integer>> streaks = new HashMap<String,ArrayList<Integer>>();
		
		int cases = 0;
		int wins = 0;
		int maxWins = 0;
		int actualWins = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			
			
			
			
			//COMPUTAMOS RESULTADO ACTUAL
			int h = 0;
			int a = 0;
			if (m.getHomeGoals()>m.getAwayGoals()){
				h=1;
				a = -1;
			}else{
				h=-1;
				a = 1;
			}
			
			//COMPROBAMOS SI EL STREAK ANTERIOR COINCIDE CON LO BUSCADO
			if (streaks.containsKey(homeTeam) && homeTeam.equalsIgnoreCase(team)){
				ArrayList<Integer> streak1 = streaks.get(homeTeam);
				if (streaks.containsKey(awayTeam)){
					ArrayList<Integer> streak2 = streaks.get(awayTeam);
					if (streak1.size()>=rachaLen1 && streak2.size()>=rachaLen2){
						boolean isValid = false;
						for (int s1 = streak1.size()-1;s1>=streak1.size()-1-(rachaLen1-1);s1--){
							int r1 = streak1.get(s1);
							if (r1==res1 || res1==0){
								isValid = true;								
							}else{
								isValid = false;
								break;
							}
						}
						if (isValid){
							isValid = false;
							for (int s2 = streak2.size()-1;s2>=streak2.size()-1-(rachaLen2-1);s2--){
								int r2 = streak2.get(s2);
								if (r2==res2 || res2==0){
									isValid = true;								
								}else{
									isValid = false;
									break;
								}
							}
							
							//Si es valido se computa
							if (isValid){
								cases++;
								if (h==expected){
									wins++;
									actualWins++;
									if (actualWins>=maxWins) maxWins = actualWins;
									
									actualLosses = 0;
								}else{
									actualLosses++;
									if (actualLosses >=maxLosses) maxLosses = actualLosses;
									
									actualWins = 0;
								}
							}
						}//isValid
					}
				}
			}
			
			//AGREGAMOS EL RESULTADO ACTUAL
			if (!streaks.containsKey(homeTeam)){
				streaks.put(homeTeam, new ArrayList<Integer>());
			}
			streaks.get(homeTeam).add(h);
			
			if (!streaks.containsKey(awayTeam)){
				streaks.put(awayTeam, new ArrayList<Integer>());
			}
			streaks.get(awayTeam).add(a);
			
			
		}
		
						
		betStats.setCases(cases);
		betStats.setWins(wins);
		betStats.setMaxLosses(maxLosses);
		betStats.setMaxWins(maxWins);
		
		return betStats;
	}
	
	
	public static BetStats getAvgStreakByTeam(ArrayList<Match> matches,String team,BetStats betStats){
		
		//System.out.println("[getAvgStreakByTeam] entrado,,");
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		
		int last=0;
		int count = 0;
		int worse = 0;
		int best = 0;
		int losses = 0;
		int wins = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			
			if (!homeTeam.equalsIgnoreCase(team) && !awayTeam.equalsIgnoreCase(team)) continue;		
			
			if (team.contains("Winnipeg Jets")){
				//System.out.println("mirando partido..."+i+" || "+m.toString());
			}
			
			//COMPUTAMOS RESULTADO ACTUAL
			int h = 0;
			int a = 0;
			if (m.getHomeGoals()>m.getAwayGoals()){
				h=1;
				a = -1;
			}else{
				h=-1;
				a = 1;
			}
			
			int res = 0;
			if (homeTeam.equalsIgnoreCase(team)) res = h;
			if (awayTeam.equalsIgnoreCase(team)) res = a;
			
						
			//AGREGAMOS EL RESULTADO ACTUAL
			if (res==1) wins++;
			else losses++;
			
			if (res==last || last==0){
				last = res;
				count++;
				if (res==1 && count>best) best = count;
				if (res==-1 && count>=worse) worse = count;
			}else if (res!=last && last!=0){
				//actualizamos streak
				if (team.contains("Washington Capital")){
					//System.out.println("añadiendo: "+last*count);
				}
				streaks.add(last*count);
				//System.out.println(last*count);
				//nuevo streak
				last = res;
				count = 1;
			}									
		}
		//metemos la ultima
		streaks.add(last*count);
		if (team.contains("Washington Capital")){
			//System.out.println(last*count);
		}
		
		betStats.getStreaks().clear();				
		betStats.setStreaks(streaks);
		betStats.setBestStreak(best);
		betStats.setWorseStreak(worse);
		betStats.setCases(wins+losses);
		betStats.setWins(wins);
		betStats.setLosses(losses);
		
		
		return betStats;
	}
	
	public static BetStats getContinueOdds(ArrayList<Match> matches,String team,int rachaLen,int res){
		
		BetStats betStats = new BetStats();
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		//recopilacion todos los prtidos de team	
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			int win = 0;
			if (m.homeTeam.equalsIgnoreCase(team)){
				if (m.homeGoals>m.getAwayGoals()){//ha ganado
					win=1;
				}else if (m.homeGoals<m.getAwayGoals()){//ha perdido
					win=-1;
				}
			}else if (m.awayTeam.equalsIgnoreCase(team)){
				if (m.homeGoals>m.getAwayGoals()){//ha ganado
					win=-1;
				}else if (m.homeGoals<m.getAwayGoals()){//ha perdido
					win=1;
				}
			}
			
			if (win!=0){
				//System.out.println(win);
				results.add(win);
			}
		}
		
		//comprobamos las continuaciones
		int cases = 0;
		int continuations = 0;
		//buscamos el patrón
		for (int i=rachaLen;i<results.size();i++){
			boolean isValid = false;
			//System.out.println("case "+i+" || "+results.get(i));	
			for (int j=i-rachaLen;j<i;j++){
				int win = results.get(j);
				if (win==res) isValid = true;
				else{
					isValid = false;
					break;
				}
			}
			
			//evaluamos i
			if (isValid){
				cases++;
				if (results.get(i)==res){
					continuations++;
					//System.out.println("FOUND "+cases+" "+continuations);					
				}
			}
		}
		
		
		betStats.setCases(cases);
		betStats.setWins(continuations);
		//System.out.println(cases+" "+continuations);
		return betStats;
	}
	
	public static void printStreakInfo(String header,BetStats betStats){
		
		//BetStats betStats = NHL.getAvgStreakByTeam(matches, team);
		ArrayList<Integer> streak = betStats.getStreaks();
		ArrayList<Integer> streakPos = new ArrayList<Integer> ();
		ArrayList<Integer> streakNeg = new ArrayList<Integer> ();
		
		for (int i=0;i<streak.size();i++){
			if (streak.get(i)>=1){
				streakPos.add(streak.get(i));
			}else if (streak.get(i)<=-1){
				streakNeg.add(-streak.get(i));
			}
		}
		
		double avgPos = MathUtils.average(streakPos);
		double avgNeg = MathUtils.average(streakNeg);
		
		double desvP = Math.sqrt(MathUtils.variance(streakPos));
		double desvN = Math.sqrt(MathUtils.variance(streakNeg));
		
		String header2 = header
				+" || "
				+" "+PrintUtils.Print2dec(avgPos,false)+" "+PrintUtils.Print2dec(desvP,false)
				+" || "+PrintUtils.Print2dec(avgNeg,false)+" "+PrintUtils.Print2dec(desvN,false)
				+" || "+betStats.getBestStreak()+" "+betStats.getWorseStreak()
				;
		System.out.println(header2);
		//MathUtils.summary_completeInt(header2, streakNeg);
		
	}
	
public static void setStreakInfo(String header,BetStats betStats){
		
		//BetStats betStats = NHL.getAvgStreakByTeam(matches, team);
		ArrayList<Integer> streak = betStats.getStreaks();
		ArrayList<Integer> streakPos = new ArrayList<Integer> ();
		ArrayList<Integer> streakNeg = new ArrayList<Integer> ();
		
		for (int i=0;i<streak.size();i++){
			if (streak.get(i)>=1){
				streakPos.add(streak.get(i));
			}else if (streak.get(i)<=-1){
				streakNeg.add(-streak.get(i));
			}
		}
		
		double avgPos = MathUtils.average(streakPos);
		double avgNeg = MathUtils.average(streakNeg);
		
		double desvP = Math.sqrt(MathUtils.variance(streakPos));
		double desvN = Math.sqrt(MathUtils.variance(streakNeg));
		
		betStats.setAvgNeg(avgNeg);
		betStats.setAvgPos(avgPos);
		betStats.setDesvN(desvN);
		betStats.setDesvP(desvP);
		
		/*String header2 = header
				+" || "
				+" "+PrintUtils.Print2dec(avgPos,false)+" "+PrintUtils.Print2dec(desvP,false)
				+" || "+PrintUtils.Print2dec(avgNeg,false)+" "+PrintUtils.Print2dec(desvN,false)
				+" || "+betStats.getBestStreak()+" "+betStats.getWorseStreak()
				;*/
		//System.out.println(header2);
		//MathUtils.summary_completeInt(header2, streakNeg);
		
	}

   public static TestStats testSystemByTeam(ArrayList<Match> matches,ArrayList<Match> testMatches,String testTeam,ArrayList<Integer> cuotasArrays,double factor,int debug){
		
		HashMap<String,BetStats> teamsStats = new HashMap<String,BetStats>();
		
		HashMap<String,String> teams = NHL.extractTeams(testMatches);
		for (Entry<String, String> entry : teams.entrySet()) {
			String team = entry.getKey();
			BetStats betStats = new BetStats();
			NHL.getAvgStreakByTeam(matches, team,betStats);
			betStats.recalculateStreak();
			teamsStats.put(team, betStats);
		}
		
		double totalProfit = 0;
		double avgCuotaH = 0.7;
		double avgCuotaA = 1.3;
		int totalBets = 0;
		int totalAmount = 0;
		for (int i=0;i<testMatches.size();i++){
			Match m = testMatches.get(i);
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			
			BetStats hStats = teamsStats.get(homeTeam);
			BetStats aStats = teamsStats.get(awayTeam);
			
			BetStats testStats = null; 
			int result = 0;
			int actualStreak = 0;
			int rivalStreak = 0;
			int home = 0;
			double avgCuota=0.0;
			if (homeTeam.contains(testTeam)){
				home=1;
				avgCuota=avgCuotaH;
				testStats = hStats;
				result = (m.getHomeGoals()>m.awayGoals?1:-1);
				actualStreak = hStats.getStreaks().get(hStats.getStreaks().size()-1);
				rivalStreak = aStats.getStreaks().get(aStats.getStreaks().size()-1);
				/*System.out.println("[home] "
						+" "+(m.getHomeGoals()>m.awayGoals?1:-1)
						+" || "+hStats.getStreaks().get(hStats.getStreaks().size()-1)
						+" "+homeTeam +" - "+awayTeam+" ->"
						+" "+hStats.getOddsStr() + " ||| " +aStats.getOddsStr()					
						);*/
			}
			
			if (awayTeam.contains(testTeam)){
				home=-1;
				avgCuota=avgCuotaA;
				testStats = aStats;
				result = (m.getHomeGoals()<m.awayGoals?1:-1);
				actualStreak = aStats.getStreaks().get(aStats.getStreaks().size()-1);
				rivalStreak = hStats.getStreaks().get(hStats.getStreaks().size()-1);
				/*System.out.println("[AWAY] "
						+" "+(m.getHomeGoals()<m.awayGoals?1:-1)
						+" || "+aStats.getStreaks().get(aStats.getStreaks().size()-1)
						+" "+awayTeam +" - "+homeTeam+" ->"
						+" "+aStats.getOddsStr() + " ||| " +hStats.getOddsStr()
						);*/
			}
			
			
			//aplicamos sistema
			if (result!=0){
				int cuota = 0;
				if (actualStreak<0 
						//&& home==-1
						&& (rivalStreak>0 || home==1)
						){//solo entramos en los perdidos
					if (testStats.getAvgNeg()>-factor 
							//&& testStats.getAvgNeg()<-factor+1.0
							){						
						cuota = cuotasArrays.get(-actualStreak);
					}					
				}	
				
				if (cuota>0){
					totalAmount+=cuota;
					totalBets++;
					if (result==-1)
						totalProfit += cuota*result;
					else
						totalProfit += cuota*result*avgCuota;
					if (debug==1)
						System.out.println("[BET] "+result+" || "+totalBets+" "+cuota+" "+totalProfit+" ||| "+actualStreak+" "+rivalStreak);
				}
			}
			
			
			//añadimos el test y recalculamos estadisticas
			matches.add(m);
			
			if (teamsStats.containsKey(homeTeam)						
					){
				BetStats betStats = teamsStats.get(homeTeam);
				NHL.getAvgStreakByTeam(matches, homeTeam,betStats);
				betStats.recalculateStreak();
				if (homeTeam.contains(testTeam)){
					//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}
			
			if (teamsStats.containsKey(awayTeam)						
					){
				BetStats betStats = teamsStats.get(awayTeam);
				NHL.getAvgStreakByTeam(matches, awayTeam,betStats);
				betStats.recalculateStreak();
				if (awayTeam.contains(testTeam)){
					//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}					
		}//for
		
		/*BetStats betStats = teamsStats.get(testTeam);
		NHL.getAvgStreakByTeam(matches, "Arizona Coyotes",betStats);
		betStats.recalculateStreak();
		
		for (int i=4;i>=1;i--){
			System.out.println(betStats.getStreaks().get(betStats.getStreaks().size()-i));
		}*/
		
		//System.out.println(testTeam+" "+totalBets+" "+totalAmount+" "+totalProfit);
		
		TestStats stats = new TestStats();
		
		stats.setTotalAmount(totalAmount);
		stats.setTotalBets(totalBets);
		stats.setTotalProfit(totalProfit);
		
		return stats;
	}

	public static void testSystem(ArrayList<Match> matches,ArrayList<Match> testMatches){
		
		HashMap<String,BetStats> teamsStats = new HashMap<String,BetStats>();
		
		HashMap<String,String> teams = NHL.extractTeams(testMatches);
		for (Entry<String, String> entry : teams.entrySet()) {
			String team = entry.getKey();
			BetStats betStats = new BetStats();
			NHL.getAvgStreakByTeam(matches, team,betStats);
			betStats.recalculateStreak();
			teamsStats.put(team, betStats);
		}
		
		for (int i=0;i<testMatches.size();i++){
			Match m = testMatches.get(i);
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			
			BetStats hStats = teamsStats.get(homeTeam);
			BetStats aStats = teamsStats.get(awayTeam);
			
			if (homeTeam.contains("Arizona")){
				System.out.println("[home] "
						+" "+(m.getHomeGoals()>m.awayGoals?1:-1)
						+" || "+hStats.getStreaks().get(hStats.getStreaks().size()-1)
						+" "+homeTeam +" - "+awayTeam+" ->"
						+" "+hStats.getOddsStr() + " ||| " +aStats.getOddsStr()					
						);
			}
			
			if (awayTeam.contains("Arizona")){
				System.out.println("[AWAY] "
						+" "+(m.getHomeGoals()<m.awayGoals?1:-1)
						+" || "+aStats.getStreaks().get(aStats.getStreaks().size()-1)
						+" "+awayTeam +" - "+homeTeam+" ->"
						+" "+aStats.getOddsStr() + " ||| " +hStats.getOddsStr()
						);
			}
			
			//añadimos el test y recalculamos estadisticas
			matches.add(m);
			
			if (teamsStats.containsKey(homeTeam)						
					){
				BetStats betStats = teamsStats.get(homeTeam);
				NHL.getAvgStreakByTeam(matches, homeTeam,betStats);
				betStats.recalculateStreak();
				if (homeTeam.contains("Arizona")){
					//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}
			
			if (teamsStats.containsKey(awayTeam)						
					){
				BetStats betStats = teamsStats.get(awayTeam);
				NHL.getAvgStreakByTeam(matches, awayTeam,betStats);
				betStats.recalculateStreak();
				if (awayTeam.contains("Arizona")){
					//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}					
		}//for
		
		BetStats betStats = teamsStats.get("Arizona Coyotes");
		NHL.getAvgStreakByTeam(matches, "Arizona Coyotes",betStats);
		betStats.recalculateStreak();
		
		for (int i=4;i>=1;i--){
			System.out.println(betStats.getStreaks().get(betStats.getStreaks().size()-i));
		}
	}
	
	
	 public static TestStats testSystemByMatch(ArrayList<Match> matches,
			 ArrayList<Match> testMatches,
			 ArrayList<Integer> cuotasArrays,
			 double factor,
			 int diff,
			 int debug){
		 
		 	matches = new ArrayList<Match>();
			
		 	ArrayList<Bet> bets = new ArrayList<Bet>();
			HashMap<String,BetStats> teamsStats = new HashMap<String,BetStats>();
			HashMap<String,Integer> canTradeTeam = new HashMap<String,Integer>();
			HashMap<String,String> teams = NHL.extractTeams(testMatches);
			for (Entry<String, String> entry : teams.entrySet()) {
				String team = entry.getKey();
				BetStats betStats = new BetStats();
				NHL.getAvgStreakByTeam(matches, team,betStats);
				betStats.recalculateStreak();
				teamsStats.put(team, betStats);
				betStats.streaks.clear();
				betStats.streaks.add(0);
				canTradeTeam.put(team,1);
			}
			
			double totalProfit = 0;
			double avgCuotaH = 0.75;
			double avgCuotaA = 1.25;
			double totalAvgCuotas = 0;
			int totalBets = 0;
			int totalAmount = 0;
			int totalWins = 0;
			HashMap<String,Double> teamMoney = new HashMap<String,Double>();
			
			//System.out.println(testMatches.size());
			for (int i=0;i<testMatches.size();i++){
				Match m = testMatches.get(i);
				String homeTeam = m.getHomeTeam().trim();
				String awayTeam = m.getAwayTeam().trim();
				
				BetStats hStats = teamsStats.get(homeTeam);
				BetStats aStats = teamsStats.get(awayTeam);
				
				BetStats testStats = null; 
				int result = 0;
				int homeStreak = hStats.getStreaks().get(hStats.getStreaks().size()-1);
				int awayStreak = aStats.getStreaks().get(aStats.getStreaks().size()-1);
				int home = 0;
				int cuota = 0;
				int betStreak = 0;
				double avgCuota=0.0;
				double avgNeg = 0;
				String teamBet ="";
				boolean canTradeHome = canTradeTeam.get(homeTeam)==1;
				boolean canTradeAway = canTradeTeam.get(awayTeam)==1;
				result = (m.getHomeGoals()>m.awayGoals?1:-1);
				double factorH = -homeStreak/hStats.getAvgNeg();
				double factorA = -awayStreak/aStats.getAvgNeg();
				int diffStreak = homeStreak-awayStreak;
				double hWinPer = hStats.getWins()*100.0/hStats.getCases();
				double aWinPer = aStats.getWins()*100.0/hStats.getCases();
				
				String t = "Dallas Stars";
				int testStreak = -3;
				
				if (homeTeam.contains(t) 
						|| awayTeam.contains(t)){
					System.out.println("[MATCH] "
							+" "+m.getDay()+"-"+m.getMonth()+"-"+m.getYear()
							+" "+homeTeam+" "+awayTeam+" "+m.getHomeGoals()+" "+m.awayGoals
							+" || "+PrintUtils.Print2dec(m.homeOdds, false)+" "+PrintUtils.Print2dec(m.awayOdds, false)
							+" || "+homeStreak+" "+awayStreak//+" || "+streakStr 
							+" || "+cuota
						    +" "+PrintUtils.Print2dec(avgCuota+1, false)
						    //+" "+PrintUtils.Print2dec(res, false)	
						    +"  ||| "+PrintUtils.Print2dec((hStats.getWins()*100.0/hStats.getCases()), false)
						    +" "+PrintUtils.Print2dec((aStats.getWins()*100.0/hStats.getCases()), false)
							);
				}else{
					canTradeHome = false;
					canTradeAway = false;
				}
				
				
				if (debug==5 && (homeStreak<0 || awayStreak<0)){
					String line = PrintUtils.Print2dec(hStats.getAvgNeg(),false)
							+","+PrintUtils.Print2dec(hStats.getAvgPos(),false)
							+","+PrintUtils.Print2dec(aStats.getAvgNeg(),false)
							+","+PrintUtils.Print2dec(aStats.getAvgPos(),false)
							+","+homeStreak
							+","+awayStreak
							+","+result
							;
					System.out.println(line);
				}
				
				
				if (canTradeHome){
					if (true
							&& homeStreak==testStreak
							&& homeTeam.contains(t)
							//&& awayStreak==3
							//&& hWinPer<factor
							//&& aWinPer<factor
							//&& aWinPer>40.0
							//&& awayStreak<=-5	
							//&& awayStreak==-2
							//&& diffStreak<diff 
							//&& hStats.getWorseStreak()==8
							//&& hStats.getWorseStreak()<aStats.getWorseStreak()
							//&& awayStreak>0
							//&& -homeStreak>hStats.getWorseStreak()/2	
							//&& -awayStreak<=aStats.getWorseStreak()	
							//&& m.getHomeOdds()>=1.7
							//&& m.getAwayOdds()>=2.0
							//&& hStats.getAvgNeg()<=2.0
							//&& aStats.getAvgPos()<=factor
							//&& -homeStreak>=hStats.getAvgNeg()
							//&& hStats.getAvgNeg()<=1.8
							//&& aStats.getAvgPos()<=1.9
							//&& homeStreak<=awayStreak
							//&& factorH>=factorA
							//&& factorH>=1.8
							//&& homeStreak==-diff
						){
						
						cuota = 1;
						avgCuota = m.getHomeOdds()-1.0;
						//avgCuota = 1.7;
						//if (factorH>=2.7) cuota=1;
						//else  cuota=1;
					}
				}
				
				if (cuota>0){
					result = (m.getHomeGoals()>m.awayGoals?1:-1);
					betStreak = homeStreak;
					avgNeg = hStats.getAvgNeg();
					teamBet = homeTeam;
				}
				
				if (cuota==0
						&& canTradeAway
						){	
					if (true				
						&& awayStreak==testStreak
						&& awayTeam.contains(t)
						//&& homeStreak==-1
						//&& homeStreak==2				
						//&& m.getHomeOdds()>=1.5 
						//&& m.getAwayOdds()<1.7
						//&& -awayStreak>aStats.getAvgNeg()-0
						//&& aStats.getAvgNeg()>=1.8
						//&& aStats.getAvgNeg()<=1.7
						//&& aStats.getAvgPos()>=1.8
						//&& homeStreak<=awayStreak
						//&& factorA>=factor
					){
						cuota = 1;
						//avgCuota = 2.2;
						avgCuota = m.getAwayOdds()-1.0;
						//if (factorA>=2.7) cuota=10;
						//else  cuota=1;
						//if (awayStreak==-3) cuota=6;
						/*if (awayStreak==-4) cuota=6;
						if (awayStreak==-5) cuota=8;
						if (awayStreak==-6) cuota=10;*/
					}
					if (cuota>0){
						result = (m.getAwayGoals()>m.homeGoals?1:-1);
						betStreak = awayStreak;
						avgNeg = aStats.getAvgNeg();
						teamBet = awayTeam;
					}
					
				}
				
				
				
				
				if (cuota>0){
					Bet bet = new Bet();
					bet.setM(m);					
					bet.setOdds(avgCuota);
					
					totalAmount+=cuota;
					totalBets++;
					String resStr ="WIN";
					double res = 0;
					if (result==-1){
						totalProfit += cuota*result;
						res = cuota*result;
						resStr = "FAIL";
						if (betStreak<=-4){
							//canTradeTeam.put(teamBet,-1);
						}
					}else{
						totalProfit += cuota*result*avgCuota;
						res = cuota*result*avgCuota;
						totalWins++;
					}
					totalAvgCuotas += (1.0+avgCuota);
					
					if (!teamMoney.containsKey(teamBet)){
						teamMoney.put(teamBet, 0.0);
					}
					
					teamMoney.put(teamBet, teamMoney.get(teamBet)+res);
					
					if (debug==4){
						System.out.println("[BET] "
							    +" "+homeTeam+","+awayTeam+","+cuota
							    +","+PrintUtils.Print2dec(avgCuota+1, false)
							    +","+PrintUtils.Print2dec(res, false)							 
								);
					}
					
					/*if (debug==2 || debug==3
							&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning")) 
							){
						
						System.out.println("[MATCH] "
								+" "+m.getDay()+"-"+m.getMonth()+"-"+m.getYear()
								+" "+homeTeam+" "+awayTeam+" "+m.getHomeGoals()+" "+m.awayGoals
								+" || "+PrintUtils.Print2dec(m.homeOdds, false)+" "+PrintUtils.Print2dec(m.awayOdds, false)
								+" || "+homeStreak+" "+awayStreak//+" || "+streakStr 
								+" || "+cuota
							    +" "+PrintUtils.Print2dec(avgCuota+1, false)
							    +" "+PrintUtils.Print2dec(res, false)	
							    +"  ||| "+PrintUtils.Print2dec((hStats.getWins()*100.0/hStats.getCases()), false)
							    +" "+PrintUtils.Print2dec((aStats.getWins()*100.0/hStats.getCases()), false)
								);
					}*/
					
					if (debug==1
							&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning"))
							//&& betStreak<=-4
							//&& result==-1
							){
						System.out.println("[BET] "
							    +" "+m.homeGoals+" "+m.awayGoals+" "+homeTeam+" "+awayTeam+" "+result
							    //+" || "+totalBets+" "+cuota+" "+totalProfit
								+" ||| "+homeStreak+" "+awayStreak
								+" ||| "+resStr+"("+betStreak+")"
								+" "+PrintUtils.Print2dec(avgCuota+1, false)
								//+" "+betStreak
								+" "+PrintUtils.Print2dec(hStats.getAvgNeg(), false)
								+" "+PrintUtils.Print2dec(aStats.getAvgNeg(), false)
								//+" || "+teamBet+" "+PrintUtils.Print2dec(teamMoney.get(teamBet), false)
								);
					}
					
					
					
					
				}
				
				
				
				//añadimos el test y recalculamos estadisticas
				matches.add(m);
				//System.out.println("[TEAMS1] "+teamsStats.size());
				if (teamsStats.containsKey(homeTeam.trim())						
						){
					BetStats betStats = teamsStats.get(homeTeam);
					NHL.getAvgStreakByTeam(matches, homeTeam,betStats);
					betStats.recalculateStreak();
					//System.out.println("[TEAMS2] "+teamsStats.size());
					if (homeTeam.contains(homeTeam)
							&& homeTeam.contains("Washington Capital")		
							){
						//System.out.println("[TEAMS3] "+teamsStats.size());//+" || "+matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
					}
				}
				
				if (teamsStats.containsKey(awayTeam)						
						){
					BetStats betStats = teamsStats.get(awayTeam.trim());
					NHL.getAvgStreakByTeam(matches, awayTeam,betStats);
					betStats.recalculateStreak();
					if (awayTeam.contains(awayTeam)
						&& awayTeam.equalsIgnoreCase("Washington Capital")	
							){
						//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
					}
				}					
			}//for
			
			
			//System.out.println(testTeam+" "+totalBets+" "+totalAmount+" "+totalProfit);
			
			TestStats stats = new TestStats();
			
			stats.setTotalAmount(totalAmount);
			stats.setTotalBets(totalBets);
			stats.setTotalProfit(totalProfit);
			stats.setTotalWins(totalWins);
			stats.setTotalAvgCuotas(totalAvgCuotas);
			
			return stats;
		}
	 
	 public static TestStats testSystemByMatch2(ArrayList<Match> matches,
			 ArrayList<Match> testMatches,
			 ArrayList<Integer> cuotasArrays,
			 double factor,
			 int diff,
			 String t,
			 int testStreak,
			 int debug){
		 
		 	matches = new ArrayList<Match>();
			
		 	ArrayList<Bet> bets = new ArrayList<Bet>();
			HashMap<String,BetStats> teamsStats = new HashMap<String,BetStats>();
			HashMap<String,Integer> canTradeTeam = new HashMap<String,Integer>();
			HashMap<String,String> teams = NHL.extractTeams(testMatches);
			for (Entry<String, String> entry : teams.entrySet()) {
				String team = entry.getKey();
				BetStats betStats = new BetStats();
				NHL.getAvgStreakByTeam(matches, team,betStats);
				betStats.recalculateStreak();
				teamsStats.put(team, betStats);
				betStats.streaks.clear();
				betStats.streaks.add(0);
				canTradeTeam.put(team,1);
			}
			
			double totalProfit = 0;
			double avgCuotaH = 0.75;
			double avgCuotaA = 1.25;
			double totalAvgCuotas = 0;
			int totalBets = 0;
			int totalAmount = 0;
			int totalWins = 0;
			HashMap<String,Double> teamMoney = new HashMap<String,Double>();
			
			//System.out.println(testMatches.size());
			for (int i=0;i<testMatches.size();i++){
				Match m = testMatches.get(i);
				String homeTeam = m.getHomeTeam().trim();
				String awayTeam = m.getAwayTeam().trim();
				
				BetStats hStats = teamsStats.get(homeTeam);
				BetStats aStats = teamsStats.get(awayTeam);
				
				BetStats testStats = null; 
				int result = 0;
				int homeStreak = hStats.getStreaks().get(hStats.getStreaks().size()-1);
				int awayStreak = aStats.getStreaks().get(aStats.getStreaks().size()-1);
				int home = 0;
				int cuota = 0;
				int betStreak = 0;
				double avgCuota=0.0;
				double avgNeg = 0;
				String teamBet ="";
				boolean canTradeHome = canTradeTeam.get(homeTeam)==1;
				boolean canTradeAway = canTradeTeam.get(awayTeam)==1;
				result = (m.getHomeGoals()>m.awayGoals?1:-1);
				double factorH = -homeStreak/hStats.getAvgNeg();
				double factorA = -awayStreak/aStats.getAvgNeg();
				int diffStreak = homeStreak-awayStreak;
				double hWinPer = hStats.getWins()*100.0/hStats.getCases();
				double aWinPer = aStats.getWins()*100.0/hStats.getCases();
				
				//String t = "Dallas Stars";
				//int testStreak = -3;
				
				if (debug==6 &&
						(homeTeam.contains(t) 
						|| awayTeam.contains(t))){
					System.out.println("[MATCH] "
							+" "+m.getDay()+"-"+m.getMonth()+"-"+m.getYear()
							+" "+homeTeam+" "+awayTeam+" "+m.getHomeGoals()+" "+m.awayGoals
							+" || "+PrintUtils.Print2dec(m.homeOdds, false)+" "+PrintUtils.Print2dec(m.awayOdds, false)
							+" || "+homeStreak+" "+awayStreak//+" || "+streakStr 
							+" || "+cuota
						    +" "+PrintUtils.Print2dec(avgCuota+1, false)
						    //+" "+PrintUtils.Print2dec(res, false)	
						    +"  ||| "+PrintUtils.Print2dec((hStats.getWins()*100.0/hStats.getCases()), false)
						    +" "+PrintUtils.Print2dec((aStats.getWins()*100.0/hStats.getCases()), false)
							);
				}else{
					//canTradeHome = false;
					//canTradeAway = false;
				}
				
				
				if (debug==5 && (homeStreak<0 || awayStreak<0)){
					String line = PrintUtils.Print2dec(hStats.getAvgNeg(),false)
							+","+PrintUtils.Print2dec(hStats.getAvgPos(),false)
							+","+PrintUtils.Print2dec(aStats.getAvgNeg(),false)
							+","+PrintUtils.Print2dec(aStats.getAvgPos(),false)
							+","+homeStreak
							+","+awayStreak
							+","+result
							;
					System.out.println(line);
				}
				
				
				if (canTradeHome){
					if (true
							&& homeStreak==testStreak
							&& homeTeam.contains(t)
							//&& awayStreak==3
							//&& hWinPer<factor
							//&& aWinPer<factor
							//&& aWinPer>40.0
							//&& awayStreak<=-5	
							//&& awayStreak==-2
							//&& diffStreak<diff 
							//&& hStats.getWorseStreak()==8
							//&& hStats.getWorseStreak()<aStats.getWorseStreak()
							//&& awayStreak>0
							//&& -homeStreak>hStats.getWorseStreak()/2	
							//&& -awayStreak<=aStats.getWorseStreak()	
							//&& m.getHomeOdds()>=1.7
							//&& m.getAwayOdds()>=2.0
							//&& hStats.getAvgNeg()<=2.0
							//&& aStats.getAvgPos()<=factor
							//&& -homeStreak>=hStats.getAvgNeg()
							//&& hStats.getAvgNeg()<=1.8
							//&& aStats.getAvgPos()<=1.9
							//&& homeStreak<=awayStreak
							//&& factorH>=factorA
							//&& factorH>=1.8
							//&& homeStreak==-diff
						){
						
						cuota = 1;
						avgCuota = m.getHomeOdds()-1.0;
						//avgCuota = 1.7;
						//if (factorH>=2.7) cuota=1;
						//else  cuota=1;
					}
				}
				
				if (cuota>0){
					result = (m.getHomeGoals()>m.awayGoals?1:-1);
					betStreak = homeStreak;
					avgNeg = hStats.getAvgNeg();
					teamBet = homeTeam;
				}
				
				if (cuota==0
						&& canTradeAway
						){	
					if (true				
						&& awayStreak==testStreak
						&& awayTeam.contains(t)
						//&& homeStreak==-1
						//&& homeStreak==2				
						//&& m.getHomeOdds()>=1.5 
						//&& m.getAwayOdds()<1.7
						//&& -awayStreak>aStats.getAvgNeg()-0
						//&& aStats.getAvgNeg()>=1.8
						//&& aStats.getAvgNeg()<=1.7
						//&& aStats.getAvgPos()>=1.8
						//&& homeStreak<=awayStreak
						//&& factorA>=factor
					){
						cuota = 1;
						//avgCuota = 2.2;
						avgCuota = m.getAwayOdds()-1.0;
						//if (factorA>=2.7) cuota=10;
						//else  cuota=1;
						//if (awayStreak==-3) cuota=6;
						/*if (awayStreak==-4) cuota=6;
						if (awayStreak==-5) cuota=8;
						if (awayStreak==-6) cuota=10;*/
					}
					if (cuota>0){
						result = (m.getAwayGoals()>m.homeGoals?1:-1);
						betStreak = awayStreak;
						avgNeg = aStats.getAvgNeg();
						teamBet = awayTeam;
					}
					
				}
				
				
				
				
				if (cuota>0){
					Bet bet = new Bet();
					bet.setM(m);					
					bet.setOdds(avgCuota);
					
					totalAmount+=cuota;
					totalBets++;
					String resStr ="WIN";
					double res = 0;
					if (result==-1){
						totalProfit += cuota*result;
						res = cuota*result;
						resStr = "FAIL";
						if (betStreak<=-4){
							//canTradeTeam.put(teamBet,-1);
						}
					}else{
						totalProfit += cuota*result*avgCuota;
						res = cuota*result*avgCuota;
						totalWins++;
					}
					totalAvgCuotas += (1.0+avgCuota);
					
					if (!teamMoney.containsKey(teamBet)){
						teamMoney.put(teamBet, 0.0);
					}
					
					teamMoney.put(teamBet, teamMoney.get(teamBet)+res);
					
					if (debug==4){
						System.out.println("[BET] "
							    +" "+homeTeam+","+awayTeam+","+cuota
							    +","+PrintUtils.Print2dec(avgCuota+1, false)
							    +","+PrintUtils.Print2dec(res, false)							 
								);
					}
					
					/*if (debug==2 || debug==3
							&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning")) 
							){
						
						System.out.println("[MATCH] "
								+" "+m.getDay()+"-"+m.getMonth()+"-"+m.getYear()
								+" "+homeTeam+" "+awayTeam+" "+m.getHomeGoals()+" "+m.awayGoals
								+" || "+PrintUtils.Print2dec(m.homeOdds, false)+" "+PrintUtils.Print2dec(m.awayOdds, false)
								+" || "+homeStreak+" "+awayStreak//+" || "+streakStr 
								+" || "+cuota
							    +" "+PrintUtils.Print2dec(avgCuota+1, false)
							    +" "+PrintUtils.Print2dec(res, false)	
							    +"  ||| "+PrintUtils.Print2dec((hStats.getWins()*100.0/hStats.getCases()), false)
							    +" "+PrintUtils.Print2dec((aStats.getWins()*100.0/hStats.getCases()), false)
								);
					}*/
					
					if (debug==1
							&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning"))
							//&& betStreak<=-4
							//&& result==-1
							){
						System.out.println("[BET] "
							    +" "+m.homeGoals+" "+m.awayGoals+" "+homeTeam+" "+awayTeam+" "+result
							    //+" || "+totalBets+" "+cuota+" "+totalProfit
								+" ||| "+homeStreak+" "+awayStreak
								+" ||| "+resStr+"("+betStreak+")"
								+" "+PrintUtils.Print2dec(avgCuota+1, false)
								//+" "+betStreak
								+" "+PrintUtils.Print2dec(hStats.getAvgNeg(), false)
								+" "+PrintUtils.Print2dec(aStats.getAvgNeg(), false)
								//+" || "+teamBet+" "+PrintUtils.Print2dec(teamMoney.get(teamBet), false)
								);
					}
					
					
					
					
				}
				
				
				
				//añadimos el test y recalculamos estadisticas
				matches.add(m);
				//System.out.println("[TEAMS1] "+teamsStats.size());
				if (teamsStats.containsKey(homeTeam.trim())						
						){
					BetStats betStats = teamsStats.get(homeTeam);
					NHL.getAvgStreakByTeam(matches, homeTeam,betStats);
					betStats.recalculateStreak();
					//System.out.println("[TEAMS2] "+teamsStats.size());
					if (homeTeam.contains(homeTeam)
							&& homeTeam.contains("Washington Capital")		
							){
						//System.out.println("[TEAMS3] "+teamsStats.size());//+" || "+matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
					}
				}
				
				if (teamsStats.containsKey(awayTeam)						
						){
					BetStats betStats = teamsStats.get(awayTeam.trim());
					NHL.getAvgStreakByTeam(matches, awayTeam,betStats);
					betStats.recalculateStreak();
					if (awayTeam.contains(awayTeam)
						&& awayTeam.equalsIgnoreCase("Washington Capital")	
							){
						//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
					}
				}					
			}//for
			
			
			//System.out.println(testTeam+" "+totalBets+" "+totalAmount+" "+totalProfit);
			
			TestStats stats = new TestStats();
			
			stats.setTotalAmount(totalAmount);
			stats.setTotalBets(totalBets);
			stats.setTotalProfit(totalProfit);
			stats.setTotalWins(totalWins);
			stats.setTotalAvgCuotas(totalAvgCuotas);
			
			return stats;
		}
	 
	public static void test1(String folder){
		
		ArrayList<Integer> cuotasArrays = new ArrayList<Integer>(); 
		
		for (int i=0;i<=50;i++){
			cuotasArrays.add(0);
		}
		for (int jump=-1;jump<=-1;jump++){
		
		for (double factor = 1.10;factor<=1.10;factor+=0.10){
			for (int diff=1;diff<=1;diff++){
			int globalBets = 0;
			int globalAmount = 0;
			int globalProfit = 0;
			int globalWins = 0;
			double globalAvgCuotas = 0.0;
			
				for (int y=2016;y<=2016;y++){
					int y2 = y-1;
					int y1 = y-1;			
					//ArrayList<Match> testMatches = NHL.readFromDisk(folder, y, y,3,0);
					//ArrayList<Match> matches = NHL.readFromDisk(folder, y1, y2,3,0);
					//ArrayList<Match> matches		= SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 0);
					ArrayList<Match> testMatches 	= SportsInsight.readAndReorderNHL(folder, y, y, 1, 0);				
					HashMap<String,String> teams = NHL.extractTeams(testMatches);
					//for (double factor = 2.0;factor<=2.0;factor+=0.1){
						//for (int diff=1;diff<=10;diff++){
							cuotasArrays.set(1, 1);
							cuotasArrays.set(2, 2);
							cuotasArrays.set(3, 3);
							cuotasArrays.set(4, 8);
							cuotasArrays.set(5, 10);
							
							
							int totalBets = 0;
						    int totalAmount = 0;
						    int totalWins = 0;
						    double totalProfit = 0;		
						    double totalAvgCuotas = 0;
							//String team = "Toronto Maple Leafs";
						    //System.out.println(matches.size()+" "+testMatches.size());
						    //ArrayList<Match> matchesO = NHL.readFromDisk(folder, y1, y2,3,0);
						    ArrayList<Match> matchesO = SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 1);
							TestStats stats = NHL.testSystemByMatch(matchesO, testMatches,cuotasArrays,factor,diff,3);
							
							totalBets += stats.getTotalBets();
							totalAmount += stats.getTotalAmount();
							totalProfit += stats.getTotalProfit();
							totalWins += stats.getTotalWins();
							totalAvgCuotas += stats.getTotalAvgCuotas();
							
							globalBets += totalBets;
							globalAmount += totalAmount;
							globalProfit += totalProfit;
							globalWins += stats.getTotalWins();
							globalAvgCuotas += stats.getTotalAvgCuotas();
							/*System.out.println(
									y1+" "+y2+" "+y
									+" "+PrintUtils.Print2dec(factor, false)
									+" || "
									+" "+totalBets
									+" "+totalAmount
									+" "+PrintUtils.Print2dec(totalProfit, false)
									+" "+PrintUtils.Print2dec(totalWins*100.0/totalBets, false)
									+" "+PrintUtils.Print2dec(totalAmount*1.0/totalBets, false)
									+" "+PrintUtils.Print2dec(totalAvgCuotas*1.0/totalBets, false)
									+" "+PrintUtils.Print2dec(totalProfit*100.0/totalAmount, false)
									);*/
						//}
					//}
				
				}
				System.out.println(
						globalBets
						+" "+globalAmount
						+" "+PrintUtils.Print2dec(globalWins*100.0/globalBets, false)
						+" "+PrintUtils.Print2dec(globalAvgCuotas*1.0/globalBets, false)
						+" "+PrintUtils.Print2dec(globalProfit*100.0/globalAmount, false)
						);
			}//diff
		}//factor
	}
		
	}
	
	
	public static void test2(String folder,int n){
		
		System.out.println("NEGATIVA "+n);
		BetStats betStats = new BetStats();
		int years = 16;
		for (int streakTest=-1;streakTest>=-8;streakTest--){
			int avg = 0;
			double avgNeg = 0.0;
			
			for (int y=2016-years+1;y<=2016;y++){
				
				int total = 0;
				
				//ArrayList<Match> testMatches 	= SportsInsight.readAndReorderNHL(folder, y, y, 1, 0);		
				//ArrayList<Match> testMatches1 	= SportsInsight.readAndReorderNHL(folder, y-1, y-3, 2, 0);
				
				//ArrayList<Match> testMatches 	= SportsInsight.readAndReorderNHL(folder, y, y, 1, 0);		
				//ArrayList<Match> testMatches1 	= SportsInsight.readAndReorderNHL(folder, y-n, y-n, 2, 0);	
				
				ArrayList<Match> testMatches = NHL.readFromDisk(folder, y, y, 3, 0);	
				ArrayList<Match> testMatches1 = NHL.readFromDisk(folder, y-n, y-n, 3, 0);	
				
				HashMap<String,String> teams = NHL.extractTeams(testMatches);		
				int y1 = 2015;
				int y2 = 2015;
				double factor = 1.0;
				double totalNeg = 0.0;
				for (Entry<String, String> entry : teams.entrySet()) {
					String team = entry.getKey();
					
					//if (!team.equalsIgnoreCase("Winnipeg Jets")) continue;
					
					//System.out.println(testMatches1.size());
					NHL.getAvgStreakByTeam(testMatches1, team,betStats);
					if (betStats.getWins()+betStats.getLosses()>0){
						betStats.recalculateStreak();
						TestStats stats0 = null;
					    //ArrayList<Match> matchesO = SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 1);
						//ArrayList<Match> matchesO = SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 1);
						//ArrayList<Match> matchesO = NHL.readFromDisk(folder, y1, y2,3,0);
						
					    for (int streak=-1;streak>=-10;streak--){
					    	TestStats stats = NHL.testSystemByMatch2(null, testMatches,null,factor,0,team,streak,0);
					    	if (stats.getTotalBets()==0){
					    		//stats = stats0;
					    		if ((streak+1)<=streakTest){						    		    		
					    			totalNeg += betStats.getAvgNeg();
					    			total++;
					    			//System.out.println(y+" "+team+" "+total+" "+totalNeg+" || "+betStats.getAvgNeg());
					    		}
					    		
						    	break;
					    	}else{
					    		stats0 = stats;
					    	}		    	
					    }	
					}
				} //team
				//System.out.println(streakTest+" "+y+" "+total);
				avg+=total;
				avgNeg += totalNeg;
				//System.out.println(avgNeg+" "+avg+" "+PrintUtils.Print2dec(avgNeg*1.0/avg,false));
			}
			System.out.println(streakTest+" "+avg*1.0/years+" "+PrintUtils.Print3dec(avgNeg*1.0/avg,false));
			//System.out.println(streakTest+" "+y+" "+total);
		}
	}
	
	
	 public static TestStats testSystemByMatch3(
			 ArrayList<Match> testMatches,
			 HashMap<String,ArrayList<Double>> teamNegativeOdds,
			 HashMap<String,ArrayList<Double>> teamPositiveOdds,
			 double factor,
			 int debug){
		 
		 ArrayList<Match>matches = new ArrayList<Match>();
			
		 	ArrayList<Bet> bets = new ArrayList<Bet>();
			HashMap<String,BetStats> teamsStats = new HashMap<String,BetStats>();
			HashMap<String,Integer> canTradeTeam = new HashMap<String,Integer>();
			HashMap<String,String> teams = NHL.extractTeams(testMatches);
			for (Entry<String, String> entry : teams.entrySet()) {
				String team = entry.getKey();
				BetStats betStats = new BetStats();
				NHL.getAvgStreakByTeam(matches, team,betStats);
				betStats.recalculateStreak();
				teamsStats.put(team, betStats);
				betStats.streaks.clear();
				betStats.streaks.add(0);
				canTradeTeam.put(team,1);
			}
			
			double totalWinsU = 0.0;
			double totalLossesU = 0.0;
			double totalProfit = 0;
			double avgCuotaH = 0.75;
			double avgCuotaA = 1.25;
			double totalAvgCuotas = 0;
			double totalAvgNeg = 0;
			int totalBets = 0;
			int totalAmount = 0;
			int totalWins = 0;
			HashMap<String,Double> teamMoney = new HashMap<String,Double>();
			
			//System.out.println(testMatches.size());
		for (int i=0;i<testMatches.size();i++){
			Match m = testMatches.get(i);
			String homeTeam = m.getHomeTeam().trim();
			String awayTeam = m.getAwayTeam().trim();				
			BetStats hStats = teamsStats.get(homeTeam);
			BetStats aStats = teamsStats.get(awayTeam);				
			BetStats testStats = null; 
			int result = 0;
			int homeStreak = hStats.getStreaks().get(hStats.getStreaks().size()-1);
			int awayStreak = aStats.getStreaks().get(aStats.getStreaks().size()-1);
			int home = 0;
			int cuota = 0;
			int betStreak = 0;
			double avgCuota=0.0;
			double avgNeg = 0;
			String teamBet ="";
			boolean canTradeHome = canTradeTeam.get(homeTeam)==1;
			boolean canTradeAway = canTradeTeam.get(awayTeam)==1;
			result = (m.getHomeGoals()>m.awayGoals?1:-1);
			double hPosOdds1 = teamPositiveOdds.get(homeTeam).get(1);
			double hNegOdds1 = teamNegativeOdds.get(homeTeam).get(1);
			double aPosOdds1 = teamPositiveOdds.get(awayTeam).get(1);
			double aNegOdds1 = teamNegativeOdds.get(awayTeam).get(1);
		
			
			//System.out.println(homeStreak+" "+awayStreak);
			
			if (canTradeHome){
				if (true
					//&& homeStreak==-3
					//&& awayStreak==1
					//&& aNegOdds1>=factor
					//&& m.getHomeOdds()>1.7
					//&& hNegOdds1<=2.00
					&& aPosOdds1<=factor
				){						
					//cuota = 1;
					//avgCuota = m.getHomeOdds()-1.0;					
				}
			}
				
			if (cuota>0){
				result = (m.getHomeGoals()>m.awayGoals?1:-1);
				betStreak = homeStreak;
				avgNeg = hNegOdds1;//hStats.getAvgNeg();
				teamBet = homeTeam;
			}
				
			if (cuota==0 && canTradeAway){	
				if (true				
						//&& homeStreak==-1
						//&& awayStreak<0	
						//&& hPosOdds1<=2.0
						&& aPosOdds1>=factor
						//&& aPosOdds1<=1.45
						//&& m.getAwayOdds()>=1.7 && m.getAwayOdds() <=3.0
				){
					cuota = 1;					
					avgCuota = m.getAwayOdds()-1.0;						
				}
				if (cuota>0){
					result = (m.getAwayGoals()>m.homeGoals?1:-1);
					betStreak = awayStreak;
					avgNeg = aNegOdds1;//aStats.getAvgNeg();
					teamBet = awayTeam;
				}					
			}
				
				
								
			if (cuota>0){
				Bet bet = new Bet();
				bet.setM(m);					
				bet.setOdds(avgCuota);
				
				totalAmount+=cuota;
				totalBets++;
				String resStr ="WIN";
				double res = 0;
				if (result==-1){
					totalProfit += cuota*result;
					totalLossesU += -cuota*result; 
					res = cuota*result;
					resStr = "FAIL";
					if (betStreak<=-4){
						//canTradeTeam.put(teamBet,-1);
					}
				}else{
					totalWinsU +=  cuota*result*avgCuota;
					totalProfit += cuota*result*avgCuota;
					res = cuota*result*avgCuota;
					totalWins++;
					if (avgNeg>=0) totalAvgNeg += avgNeg;
				}
				totalAvgCuotas += (1.0+avgCuota);
				//totalAvgNeg += avgNeg;
				
				if (!teamMoney.containsKey(teamBet)){
					teamMoney.put(teamBet, 0.0);
				}
				
				teamMoney.put(teamBet, teamMoney.get(teamBet)+res);
				
				if (debug==4){
					System.out.println("[BET] "
						    +" "+homeTeam+","+awayTeam+","+cuota
						    +","+PrintUtils.Print2dec(avgCuota+1, false)
						    +","+PrintUtils.Print2dec(res, false)							 
							);
				}
				
				/*if (debug==2 || debug==3
						&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning")) 
						){
					
					System.out.println("[MATCH] "
							+" "+m.getDay()+"-"+m.getMonth()+"-"+m.getYear()
							+" "+homeTeam+" "+awayTeam+" "+m.getHomeGoals()+" "+m.awayGoals
							+" || "+PrintUtils.Print2dec(m.homeOdds, false)+" "+PrintUtils.Print2dec(m.awayOdds, false)
							+" || "+homeStreak+" "+awayStreak//+" || "+streakStr 
							+" || "+cuota
						    +" "+PrintUtils.Print2dec(avgCuota+1, false)
						    +" "+PrintUtils.Print2dec(res, false)	
						    +"  ||| "+PrintUtils.Print2dec((hStats.getWins()*100.0/hStats.getCases()), false)
						    +" "+PrintUtils.Print2dec((aStats.getWins()*100.0/hStats.getCases()), false)
							);
				}*/
				
				if (debug==1
						//&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning"))
						//&& betStreak<=-4
						//&& result==-1
						){
					System.out.println("[BET] "
						    +" "+m.homeGoals+" "+m.awayGoals+" "+homeTeam+" "+awayTeam+" "+result
						    //+" || "+totalBets+" "+cuota+" "+totalProfit
							+" ||| "+homeStreak+" "+awayStreak
							+" ||| "+resStr+"("+betStreak+")"
							+" "+PrintUtils.Print2dec(avgCuota+1, false)
							//+" "+betStreak
							+" "+PrintUtils.Print2dec(hStats.getAvgNeg(), false)
							+" "+PrintUtils.Print2dec(aStats.getAvgNeg(), false)
							//+" || "+teamBet+" "+PrintUtils.Print2dec(teamMoney.get(teamBet), false)
							);
				}																
			}//if cuota>0
				
				
				
			//añadimos el test y recalculamos estadisticas
			matches.add(m);
			//System.out.println("[TEAMS1] "+teamsStats.size());
			if (teamsStats.containsKey(homeTeam.trim())						
					){
				BetStats betStats = teamsStats.get(homeTeam);
				NHL.getAvgStreakByTeam(matches, homeTeam,betStats);
				betStats.recalculateStreak();
				//System.out.println("[TEAMS2] "+teamsStats.size());
				if (homeTeam.contains(homeTeam)
						&& homeTeam.contains("Washington Capital")		
						){
					//System.out.println("[TEAMS3] "+teamsStats.size());//+" || "+matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}
			
			if (teamsStats.containsKey(awayTeam)						
					){
				BetStats betStats = teamsStats.get(awayTeam.trim());
				NHL.getAvgStreakByTeam(matches, awayTeam,betStats);
				betStats.recalculateStreak();
				if (awayTeam.contains(awayTeam)
					&& awayTeam.equalsIgnoreCase("Washington Capital")	
						){
					//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}					
		}//for
			
			
		/*System.out.println(totalBets
				+" "+totalAmount
				//+" "+totalProfit
				+" "+PrintUtils.Print2dec(totalWins*100.0/totalBets, false)
				+" "+PrintUtils.Print2dec(totalProfit*100.0/totalAmount, false)
				);*/
		
		TestStats stats = new TestStats();
		
		stats.setTotalAmount(totalAmount);
		stats.setTotalBets(totalBets);
		stats.setTotalProfit(totalProfit);
		stats.setTotalWins(totalWins);
		stats.setTotalWinsU(totalWinsU);
		stats.setTotalLossesU(totalWinsU);
		stats.setTotalAvgCuotas(totalAvgCuotas);
		stats.setTotalAvgNeg(totalAvgNeg);
		
		return stats;
	}
	 
	 
	 /*****Se añaden pesos a los partidos, siendo mas pesados los mas recientes
	  * 
	  * @param testMatches
	  * @param teamNegativeOdds
	  * @param teamPositiveOdds
	  * @param factor
	  * @param debug
	  * @return
	  */
	 public static TestStats testSystemByMatch4(
			 ArrayList<Match> historyMatches,
			 ArrayList<Match> testMatches,
			 HashMap<String,ArrayList<Double>> teamNegativeOdds,
			 HashMap<String,ArrayList<Double>> teamPositiveOdds,
			 double factor,
			 int debug){
		 
		 ArrayList<Match>matches = new ArrayList<Match>();
			
		 	ArrayList<Bet> bets = new ArrayList<Bet>();
			HashMap<String,BetStats> teamsStats = new HashMap<String,BetStats>();
			HashMap<String,Integer> canTradeTeam = new HashMap<String,Integer>();
			HashMap<String,String> teams = NHL.extractTeams(testMatches);
			for (Entry<String, String> entry : teams.entrySet()) {
				String team = entry.getKey();
				BetStats betStats = new BetStats();
				NHL.getAvgStreakByTeam(matches, team,betStats);
				betStats.recalculateStreak();
				teamsStats.put(team, betStats);
				betStats.streaks.clear();
				betStats.streaks.add(0);
				canTradeTeam.put(team,1);
			}
			
			double totalWinsU = 0.0;
			double totalLossesU = 0.0;
			double totalProfit = 0;
			double avgCuotaH = 0.75;
			double avgCuotaA = 1.25;
			double totalAvgCuotas = 0;
			double totalAvgNeg = 0;
			int totalBets = 0;
			int totalAmount = 0;
			int totalWins = 0;
			HashMap<String,Double> teamMoney = new HashMap<String,Double>();
			
			//System.out.println(testMatches.size());
		for (int i=0;i<testMatches.size();i++){
			Match m = testMatches.get(i);
			String homeTeam = m.getHomeTeam().trim();
			String awayTeam = m.getAwayTeam().trim();				
			BetStats hStats = teamsStats.get(homeTeam);
			BetStats aStats = teamsStats.get(awayTeam);				
			BetStats testStats = null; 
			int result = 0;
			int homeStreak = hStats.getStreaks().get(hStats.getStreaks().size()-1);
			int awayStreak = aStats.getStreaks().get(aStats.getStreaks().size()-1);
			int home = 0;
			int cuota = 0;
			int betStreak = 0;
			double avgCuota=0.0;
			double avgNeg = 0;
			String teamBet ="";
			boolean canTradeHome = canTradeTeam.get(homeTeam)==1;
			boolean canTradeAway = canTradeTeam.get(awayTeam)==1;
			result = (m.getHomeGoals()>m.awayGoals?1:-1);
			double hPosOdds1 = teamPositiveOdds.get(homeTeam).get(1);
			double hNegOdds1 = teamNegativeOdds.get(homeTeam).get(1);
			double aPosOdds1 = teamPositiveOdds.get(awayTeam).get(1);
			double aNegOdds1 = teamNegativeOdds.get(awayTeam).get(1);
		
			
			//System.out.println(homeStreak+" "+awayStreak);
			
			if (canTradeHome){
				if (true
					//&& homeStreak==-3
					//&& awayStreak==1
					//&& aNegOdds1>=factor
					//&& m.getHomeOdds()>1.7
					//&& hNegOdds1<=2.00
					&& aPosOdds1<=factor
				){						
					//cuota = 1;
					//avgCuota = m.getHomeOdds()-1.0;					
				}
			}
				
			if (cuota>0){
				result = (m.getHomeGoals()>m.awayGoals?1:-1);
				betStreak = homeStreak;
				avgNeg = hNegOdds1;//hStats.getAvgNeg();
				teamBet = homeTeam;
			}
				
			if (cuota==0 && canTradeAway){	
				if (true				
						//&& homeStreak==-1
						//&& awayStreak<0	
						//&& hPosOdds1<=2.0
						&& aPosOdds1>=factor
						//&& aPosOdds1<=1.45
						//&& m.getAwayOdds()>=1.7 && m.getAwayOdds() <=3.0
				){
					cuota = 1;					
					avgCuota = m.getAwayOdds()-1.0;						
				}
				if (cuota>0){
					result = (m.getAwayGoals()>m.homeGoals?1:-1);
					betStreak = awayStreak;
					avgNeg = aNegOdds1;//aStats.getAvgNeg();
					teamBet = awayTeam;
				}					
			}
				
				
								
			if (cuota>0){
				Bet bet = new Bet();
				bet.setM(m);					
				bet.setOdds(avgCuota);
				
				totalAmount+=cuota;
				totalBets++;
				String resStr ="WIN";
				double res = 0;
				if (result==-1){
					totalProfit += cuota*result;
					totalLossesU += -cuota*result; 
					res = cuota*result;
					resStr = "FAIL";
					if (betStreak<=-4){
						//canTradeTeam.put(teamBet,-1);
					}
				}else{
					totalWinsU +=  cuota*result*avgCuota;
					totalProfit += cuota*result*avgCuota;
					res = cuota*result*avgCuota;
					totalWins++;
					if (avgNeg>=0) totalAvgNeg += avgNeg;
				}
				totalAvgCuotas += (1.0+avgCuota);
				//totalAvgNeg += avgNeg;
				
				if (!teamMoney.containsKey(teamBet)){
					teamMoney.put(teamBet, 0.0);
				}
				
				teamMoney.put(teamBet, teamMoney.get(teamBet)+res);
				
				if (debug==4){
					System.out.println("[BET] "
						    +" "+homeTeam+","+awayTeam+","+cuota
						    +","+PrintUtils.Print2dec(avgCuota+1, false)
						    +","+PrintUtils.Print2dec(res, false)							 
							);
				}
				
				/*if (debug==2 || debug==3
						&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning")) 
						){
					
					System.out.println("[MATCH] "
							+" "+m.getDay()+"-"+m.getMonth()+"-"+m.getYear()
							+" "+homeTeam+" "+awayTeam+" "+m.getHomeGoals()+" "+m.awayGoals
							+" || "+PrintUtils.Print2dec(m.homeOdds, false)+" "+PrintUtils.Print2dec(m.awayOdds, false)
							+" || "+homeStreak+" "+awayStreak//+" || "+streakStr 
							+" || "+cuota
						    +" "+PrintUtils.Print2dec(avgCuota+1, false)
						    +" "+PrintUtils.Print2dec(res, false)	
						    +"  ||| "+PrintUtils.Print2dec((hStats.getWins()*100.0/hStats.getCases()), false)
						    +" "+PrintUtils.Print2dec((aStats.getWins()*100.0/hStats.getCases()), false)
							);
				}*/
				
				if (debug==1
						//&& (homeTeam.contains("Tampa Bay Lightning") || awayTeam.contains("Tampa Bay Lightning"))
						//&& betStreak<=-4
						//&& result==-1
						){
					System.out.println("[BET] "
						    +" "+m.homeGoals+" "+m.awayGoals+" "+homeTeam+" "+awayTeam+" "+result
						    //+" || "+totalBets+" "+cuota+" "+totalProfit
							+" ||| "+homeStreak+" "+awayStreak
							+" ||| "+resStr+"("+betStreak+")"
							+" "+PrintUtils.Print2dec(avgCuota+1, false)
							//+" "+betStreak
							+" "+PrintUtils.Print2dec(hStats.getAvgNeg(), false)
							+" "+PrintUtils.Print2dec(aStats.getAvgNeg(), false)
							//+" || "+teamBet+" "+PrintUtils.Print2dec(teamMoney.get(teamBet), false)
							);
				}																
			}//if cuota>0
				
				
				
			//añadimos el test y recalculamos estadisticas
			matches.add(m);
			//System.out.println("[TEAMS1] "+teamsStats.size());
			if (teamsStats.containsKey(homeTeam.trim())						
					){
				BetStats betStats = teamsStats.get(homeTeam);
				NHL.getAvgStreakByTeam(matches, homeTeam,betStats);
				betStats.recalculateStreak();
				//System.out.println("[TEAMS2] "+teamsStats.size());
				if (homeTeam.contains(homeTeam)
						&& homeTeam.contains("Washington Capital")		
						){
					//System.out.println("[TEAMS3] "+teamsStats.size());//+" || "+matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}
			
			if (teamsStats.containsKey(awayTeam)						
					){
				BetStats betStats = teamsStats.get(awayTeam.trim());
				NHL.getAvgStreakByTeam(matches, awayTeam,betStats);
				betStats.recalculateStreak();
				if (awayTeam.contains(awayTeam)
					&& awayTeam.equalsIgnoreCase("Washington Capital")	
						){
					//System.out.println(matches.size()+" || "+betStats.getStreaks().size()+" "+betStats.getAvgNeg()+" "+betStats.avgPos);
				}
			}					
		}//for
			
			
		/*System.out.println(totalBets
				+" "+totalAmount
				//+" "+totalProfit
				+" "+PrintUtils.Print2dec(totalWins*100.0/totalBets, false)
				+" "+PrintUtils.Print2dec(totalProfit*100.0/totalAmount, false)
				);*/
		
		TestStats stats = new TestStats();
		
		stats.setTotalAmount(totalAmount);
		stats.setTotalBets(totalBets);
		stats.setTotalProfit(totalProfit);
		stats.setTotalWins(totalWins);
		stats.setTotalWinsU(totalWinsU);
		stats.setTotalLossesU(totalWinsU);
		stats.setTotalAvgCuotas(totalAvgCuotas);
		stats.setTotalAvgNeg(totalAvgNeg);
		
		return stats;
	} 
	
	public static void test2Pos(String folder,int n){
		
		System.out.println("POSITIVA "+n);
		BetStats betStats = new BetStats();
		int years = 16;
		for (int streakTest=2;streakTest<=8;streakTest++){
			int avg = 0;
			double avgNeg = 0.0;
			
			for (int y=2016-years+1;y<=2016;y++){
				
				int total = 0;
				
				//ArrayList<Match> testMatches 	= SportsInsight.readAndReorderNHL(folder, y, y, 1, 0);		
				//ArrayList<Match> testMatches1 	= SportsInsight.readAndReorderNHL(folder, y-1, y-3, 2, 0);
				
				//ArrayList<Match> testMatches 	= SportsInsight.readAndReorderNHL(folder, y, y, 1, 0);		
				//ArrayList<Match> testMatches1 	= SportsInsight.readAndReorderNHL(folder, y-n, y-n, 2, 0);	
				
				ArrayList<Match> testMatches = NHL.readFromDisk(folder, y, y, 3, 0);	
				ArrayList<Match> testMatches1 = NHL.readFromDisk(folder, y-n, y-n, 3, 0);	
				
				HashMap<String,String> teams = NHL.extractTeams(testMatches);		
				int y1 = 2015;
				int y2 = 2015;
				double factor = 1.0;
				double totalNeg = 0.0;
				for (Entry<String, String> entry : teams.entrySet()) {
					String team = entry.getKey();
					
					//if (!team.equalsIgnoreCase("Winnipeg Jets")) continue;
					
					//System.out.println(testMatches1.size());
					NHL.getAvgStreakByTeam(testMatches1, team,betStats);
					if (betStats.getWins()+betStats.getLosses()>0){
						betStats.recalculateStreak();
						TestStats stats0 = null;
					    //ArrayList<Match> matchesO = SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 1);
						//ArrayList<Match> matchesO = SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 1);
						//ArrayList<Match> matchesO = NHL.readFromDisk(folder, y1, y2,3,0);
						
					    for (int streak=2;streak<=10;streak++){
					    	TestStats stats = NHL.testSystemByMatch2(null, testMatches,null,factor,0,team,streak,0);
					    	if (stats.getTotalBets()==0){
					    		//stats = stats0;
					    		if ((streak-1)>=streakTest){						    		    		
					    			totalNeg += betStats.getAvgPos();
					    			total++;
					    			//System.out.println(y+" "+team+" "+total+" "+totalNeg+" || "+betStats.getAvgNeg());
					    		}
					    		
						    	break;
					    	}else{
					    		stats0 = stats;
					    	}		    	
					    }	
					}
				} //team
				//System.out.println(streakTest+" "+y+" "+total);
				avg+=total;
				avgNeg += totalNeg;
				//System.out.println(avgNeg+" "+avg+" "+PrintUtils.Print2dec(avgNeg*1.0/avg,false));
			}
			System.out.println(streakTest+" "+avg*1.0/years+" "+PrintUtils.Print3dec(avgNeg*1.0/avg,false));
			//System.out.println(streakTest+" "+y+" "+total);
		}
	}
	
	public static void test3(int y1,int y2,double factor1,double factor2,boolean isSportInsight){
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		/*for (int n=1;n<=2;n++){
			test2(folder2,n);
		}
		
		for (int n=1;n<=2;n++){
			test2Pos(folder2,n);
		}*/
		
		
		for (double factor = factor1;factor<=factor2;factor +=0.05){
			int totalBets = 0;
			int totalAmount = 0;
			int totalWins = 0;
			double totalProfit = 0;
			double totalWinsU = 0.0;
			double totalLossesU = 0.0;
			double totalAvgNeg = 0.0;
			for (int y=y1;y<=y2;y++){
				HashMap<String,ArrayList<Double>> teamNegativeOdds = new HashMap<String,ArrayList<Double>>();
				HashMap<String,ArrayList<Double>> teamPositiveOdds = new HashMap<String,ArrayList<Double>>();
				ArrayList<Match> testMatches = null;
				ArrayList<Match> testMatches1 = null;
				if (!isSportInsight){
					testMatches = NHL.readFromDisk(folder, y, y, 3, 0);	
					testMatches1 = NHL.readFromDisk(folder, y-1, y-1, 3, 0);
				}else{
					testMatches = SportsInsight.readAndReorderNHL(folder2, y, y, 1, 0);
					testMatches1 = SportsInsight.readAndReorderNHL(folder2, y-1, y-1, 1, 0);
				}
				BetStats betStats = new BetStats();
				//extraemos equipos
				HashMap<String,String> teams = NHL.extractTeams(testMatches);
				//metemos probabilidades
				for (Entry<String, String> entry : teams.entrySet()) {
					String team = entry.getKey();
					//recalculamos streaks
					NHL.getAvgStreakByTeam(testMatches1, team,betStats);	
					betStats.recalculateStreak();
					
					teamNegativeOdds.put(team,new ArrayList<Double>());
					teamPositiveOdds.put(team,new ArrayList<Double>());
					
					ArrayList<Double> negArray = teamNegativeOdds.get(team);
					ArrayList<Double> posArray = teamPositiveOdds.get(team);
					
					negArray.add(0.0);negArray.add(betStats.getAvgNeg());
					posArray.add(0.0);posArray.add(betStats.getAvgPos());
					//System.out.println(team+" || "+PrintUtils.Print2dec(betStats.getAvgNeg(), false)+" || "+PrintUtils.Print2dec(betStats.getAvgPos(), false));
				}			
				TestStats stats = NHL.testSystemByMatch3(testMatches, teamNegativeOdds, teamPositiveOdds,factor, 0);
				totalBets+= stats.getTotalBets();
				totalAmount+=stats.getTotalAmount();
				totalWins+=stats.getTotalWins();
				totalProfit+=stats.getTotalProfit();
				totalWinsU += stats.getTotalWinsU();
				totalLossesU += stats.getTotalWinsU();
				totalAvgNeg += stats.getTotalAvgNeg();
			}
		
			double totalU = totalWinsU+totalLossesU;
			System.out.println(" "
					+" "+y1+" "+y2
					+" "+PrintUtils.Print2dec(factor, false)+" || "
					+" "+totalBets
					+" "+totalAmount
					//+" "+totalProfit
					+" "+PrintUtils.Print2dec(totalWins*100.0/totalBets, false)
					//+" "+PrintUtils.Print2dec(totalWinsU*100.0/totalU, false)
					+" "+PrintUtils.Print2dec(totalProfit*100.0/totalAmount, false)
					+" || "+PrintUtils.Print2dec(totalAvgNeg/totalWins, false)
			);
		}
	}
	
	
	public static void test5(int y1,int y2,
			boolean isSportInsight){
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> testMatches =   null;
		
		if (!isSportInsight){
			testMatches = NHL.readFromDisk(folder, y1, y2, 3, 0);
		}else{
			testMatches = SportsInsight.readAndReorderNHL(folder2, y1, y2, 1, 0);
		}
		
		HashMap<String,BetStats> teamBetStats = new HashMap<String,BetStats>();		
		StreakUtils.calculateStreaksM( testMatches, teamBetStats,false);
		
		for (Entry<String,BetStats> entry : teamBetStats.entrySet()) {
			String team = entry.getKey();
			
			//if (!team.equalsIgnoreCase("Nashville Predators")) continue;
			
			BetStats stats = teamBetStats.get(team);
			String header2 = 
					String.format("%22s",team)
					+" || "+PrintUtils.Print2dec(stats.avgNeg,false)
					+" "+PrintUtils.Print2dec(stats.avgPos,false)
					+" || "+PrintUtils.Print2dec(stats.avgGlobal,false)
					;
			System.out.println(header2);
		}
		
	}
	public static void test4(int y1,int y2,
			int yTest1,int yTest2,
			double factor1,double factor2,
			boolean isSportInsight){
		
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> testMatches = null;
		if (!isSportInsight){
			testMatches = NHL.readFromDisk(folder, yTest1, yTest2, 3, 0);
		}else{
			testMatches = SportsInsight.readAndReorderNHL(folder2, yTest1, yTest2, 1, 0);
		}
					
		//System.out.println("total matches: "+testMatches.size());
		
		for (double afactor = factor1;afactor<=factor2;afactor+=0.04){
			for (double jump = 0.50;jump<=0.5;jump+=0.10){
				double afactor1 = afactor-jump;
				//afactor1 = 1.55;
				//if (afactor1<=1.00) continue;
				ArrayList<Match> historicalMatches = null;
				if (!isSportInsight){
					historicalMatches = NHL.readFromDisk(folder, yTest1-1, yTest1-1, 3, 0);
				}else{
					historicalMatches = SportsInsight.readAndReorderNHL(folder2, yTest1-1, yTest1-1, 1, 0);	
				}
					
				HashMap<String,BetStats> teamBetStats = new HashMap<String,BetStats>();		
				StreakUtils.calculateStreaksM(historicalMatches, teamBetStats,false);		
				historicalMatches.clear();
				HashMap<String,Integer> teamResults = new HashMap<String,Integer>();
				
				double totalProfit = 0;
				int totalAmount = 0;
				int totalBets = 0;
				int totalWins = 0;
				double avgCuota = 0;
				double avgCuotaWin = 0;
				for (int i=0;i<testMatches.size();i++){			
					Match m = testMatches.get(i);
					String hTeam = m.getHomeTeam();
					String aTeam = m.getAwayTeam();
					BetStats hStats = teamBetStats.get(hTeam);
					BetStats aStats = teamBetStats.get(aTeam);
					//int hStreak = hStats.getLastStreak();
					//int aStreak = aStats.getLastStreak();
					int stake = 0;
					double cuota = 0.0;
					int teamBet = 0;
					int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
					double hOdds = m.getHomeOdds();
					double aOdds = m.getAwayOdds();
					
					int hStreak = 0;
					if (!teamResults.containsKey(hTeam)){
						teamResults.put(hTeam,0);
					}
					int aStreak = 0;
					if (!teamResults.containsKey(aTeam)){
						teamResults.put(aTeam,0);
					}
					
					hStreak = teamResults.get(hTeam);
					aStreak = teamResults.get(aTeam);
					
					System.out.println("[MATCH] "+m.toString()+" || "+hStreak+" "+aStreak);
					
					stake = 0;
					cuota = 0.0;
					//SISTEMA
					//hStreak==-1,awayOdds 1.55-1.86 61.96% 7.32% BET = -1(away)   
					//hStreak==-2,awayOdds 1.60-2.30 54.38% 7.78% BET = -1(away)
					//hStreak==-3,homeOdds           57.83% 6.84% BET =  1(home)
					//hStreak<-3,homeOdds 1.55-2.50  52.84% 1.45% BET =  1(home)  
					//aStreak<=-1 hStreak==3 44.77% 5.50% BET = -1(away)
					//aStreak==-1 hStreak==3 188 47.34% 10.51 BET = -1 (away)
					//aStreak==-1 hStreak==2 AwayOdds>=1.80 BET = 1 (home)
					//aStreak==-1 hStreak==1 BET = 1 (home)
					//aStreak==-2 hStreak==1 430 47.91% 6.19 BET = -1 (away)
					//aStreak<-3 hStreak==1 AwayOdds>=2.20 150 68.0% 3.14 BET = 1 (home)
					if (true
							//&& hTeam.contains("Edmonton Oilers")
							//&& aStreak>0
							//&& hStats.getMaxLosses()>=afactor
							&& (hStreak<=-2 || aStreak<=-2)
						){
					  //System.out.println(m.toString()+" || "+hStats.getMaxLosses()+" || "+hStreak+" "+aStreak);
						stake = 1;
						//cuota = 2.0;
						
						cuota = hOdds;								
						teamBet = 1;					
						//cuota = aOdds;
						//teamBet = -1;
					}
					
					if (stake==0){
						if (true
							//&& hStreak>hStats.getAvgPos()
							//&& aStreak<0
							//&& hStats.getAvgPos()<=afactor	
							//&& hTeam.equalsIgnoreCase(hTeam)
								){
							//if (hTeam.equalsIgnoreCase("Columbus Blue Jackets"))
								//System.out.println(hTeam+" "+aTeam+" "+hStreak+" "+hStats.getAvgPos()+" || "+res);
							//stake = 1;
							//cuota = aOdds;
							//teamBet = -1;
						}
					}
					
					//System.out.println(hStats.getAvgPos());
					//si es mayor que 0 es que hay bet
					if (stake>0){
						if (teamBet==res){
							totalWins++;
							totalProfit += stake*cuota-stake;
							avgCuotaWin += cuota;
							//System.out.println("win: "+totalWins+" "+cuota+" "+totalProfit);
						}else{
							totalProfit += -stake;
						}
						totalAmount += stake;
						avgCuota += cuota;
						totalBets++;
					}
										
					//actualizamos streak
					if (res==1){
						if (hStreak>=0){
							teamResults.put(hTeam, teamResults.get(hTeam)+1);
						}else{
							teamResults.put(hTeam, 1);
						}
						if (aStreak<=0){
							teamResults.put(aTeam, teamResults.get(aTeam)-1);
						}else{
							teamResults.put(aTeam, -1);
						}
					}else if (res==-1){
						if (hStreak<=0){
							teamResults.put(hTeam, teamResults.get(hTeam)-1);
						}else{
							teamResults.put(hTeam, -1);
						}
						if (aStreak>=0){
							teamResults.put(aTeam, teamResults.get(aTeam)+1);
						}else{
							teamResults.put(aTeam, 1);
						}
					}
					
					
					//añadimos partido			
					historicalMatches.add(m);
					boolean hdebug = false;
					boolean adebug = false;
					StreakUtils.calculateStreaksTeam(historicalMatches, teamBetStats,hTeam,hdebug);
					StreakUtils.calculateStreaksTeam(historicalMatches, teamBetStats,aTeam,adebug);
				}
				
				double winPer = totalWins*100.0/totalBets;
				double correctOdds = 100.0/winPer;
				System.out.println(" "
						+" "+y1+" "+y2+" "+yTest1+" "+yTest2
						+" "
						+" "+PrintUtils.Print2dec(afactor1, false)
						+" "+PrintUtils.Print2dec(afactor, false)
						+" || "
						//+" "+PrintUtils.Print2dec(factor, false)+" || "
						+" "+totalBets
						+" "+totalAmount
						//+" "+totalProfit
						+" "+PrintUtils.Print2dec(totalWins*100.0/totalBets, false)
						//+" "+PrintUtils.Print2dec(totalWinsU*100.0/totalU, false)
						+" "+PrintUtils.Print2dec(avgCuota/totalBets, false)
						+" "+PrintUtils.Print2dec(avgCuotaWin/totalWins, false)
						+" "+PrintUtils.Print2dec(totalProfit*100.0/totalAmount, false)
						+" || >"+PrintUtils.Print2dec(correctOdds, false)
						);
				
			}//jump
		}//factor
	}
	
	
	public static void testSeries(
			int yTest1,int yTest2,
			boolean isSportInsight,int period,double thr){
		
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> testMatches = null;
		if (!isSportInsight){
			testMatches = NHL.readFromDisk(folder, yTest1, yTest2, 3, 0);
		}else{
			testMatches = SportsInsight.readAndReorderNHL(folder2, yTest1, yTest2, 1, 0);
		}
		
		HashMap<String,HashMap<String,ArrayList<Integer>>> series = new HashMap<String,HashMap<String,ArrayList<Integer>>>();
		
		HashMap<String,ArrayList<Integer>> teamStats = new HashMap<String,ArrayList<Integer>>();
		int wins = 0;
		int losses = 0;

		for (int i=0;i<testMatches.size();i++){			
			Match m = testMatches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			int stake = 0;
			double cuota = 0.0;
			int teamBet = 0;
			int res = m.getHomeGoals()>m.getAwayGoals()?1:0;
			int resA = m.getHomeGoals()<m.getAwayGoals()?1:0;
			
			String serieHA = hTeam+" - "+aTeam;
			String serieAH = aTeam+" - "+hTeam;
			
			if (!series.containsKey(hTeam)){
				series.put(hTeam,new HashMap<String,ArrayList<Integer>>());
			}
			if (!series.containsKey(aTeam)){
				series.put(aTeam,new HashMap<String,ArrayList<Integer>>());
			}
						
			HashMap<String,ArrayList<Integer>> homeMatches = series.get(hTeam);
			if (!homeMatches.containsKey(aTeam)){
				homeMatches.put(aTeam, new ArrayList<Integer>());
			}
			ArrayList<Integer> hm = homeMatches.get(aTeam);
			hm.add(res);
			
			HashMap<String,ArrayList<Integer>> awayMatches = series.get(aTeam);
			if (!awayMatches.containsKey(hTeam)){
				awayMatches.put(hTeam, new ArrayList<Integer>());
			}
			ArrayList<Integer> am = awayMatches.get(hTeam);
			am.add(resA);
			
			if (!teamStats.containsKey(hTeam)){
				teamStats.put(hTeam,new ArrayList<Integer>());
			}
			
			if (!teamStats.containsKey(aTeam)){
				teamStats.put(aTeam,new ArrayList<Integer>());
			}
			
			//VEMOS P
			ArrayList<Integer> arrh = teamStats.get(hTeam);
			double avgH = MathUtils.average(arrh, arrh.size()-period, arrh.size()-1);
			//away
			ArrayList<Integer> arra = teamStats.get(aTeam);
			double avgA = MathUtils.average(arra, arra.size()-period, arra.size()-1);
			
			/*if (avgH>=thr && avgA<0.40){
				if (res==1) wins++;
				else losses++;
			}*/
			
			if (avgH<=0.25 && avgA>=thr){
				if (resA==1) wins++;
				else losses++;
			}
						
			
			arrh.add(res);
			arra.add(resA);							
		}
		
		//estudiamos las series
		int cases = 0;
		int winsS = 0;
		int totalBets = 0;
		int totalWins = 0;
		int totalLosses = 0;
		int teamWins = 0;
		int teamLosses = 0;
		Iterator it = series.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String serie = (String) pair.getKey();
			HashMap<String,ArrayList<Integer>> rivals = (HashMap<String,ArrayList<Integer>>) pair.getValue();

			Iterator it2 = rivals.entrySet().iterator();
			int total = 0;
			while (it2.hasNext()) {
				Map.Entry pair2 = (Map.Entry)it2.next();
				String rival2 = (String) pair2.getKey();
				ArrayList<Integer> results = (ArrayList<Integer>) pair2.getValue();
												
				total +=results.size();
				int res0 = results.get(0);
				if (res0==0
						&& results.size()==4
						){
					cases++;
					int resm = 0;
					String resStr = results.get(0)+" ";
					for (int i=1;i<results.size();i++){
						resm += results.get(i); 
						resStr+=" "+results.get(i);
					}
					
					if (results.size()==3 && resm>=1
							|| (results.size()==4 && resm>=2)
							|| (results.size()==5 && resm>=2)
							){
						wins++;
						
						//System.out.println("[WIN] "+serie+" "+rival2+" || "+results.size()+" || "+resStr);						
					}else{
						//System.out.println("[LOSS] "+serie+" "+rival2+" || "+results.size()+" || "+resStr);	
					}
				}
			}
			//System.out.println(serie+" *** "+total);
		}
		totalBets = wins+losses;
		System.out.println(period+" "+PrintUtils.Print2dec(thr, false)
				+" || "+testMatches.size()+" "+totalBets +" "+PrintUtils.Print2dec(wins*100.0/totalBets, false)
				);
	}
					

	public static void main(String[] args) {
		
		
		//NHL.test5(2016, 2016, false);
		//NHL.test3(2006, 2016,1.4,3.0, true);
		/*for (int y=2016;y<=2016;y++){
			for (int y1=2010;y1<=2010;y1+=6)
				NHL.test4(y-11,y-11,y1,y1+9,0.30,0.30,false);
			//NHL.test5(y, y, true);
		}*/
		for (int period=25;period<=25;period++){
			for (double thr=0.1;thr<=0.9;thr+=0.05){
				NHL.testSeries(2010,2015,false,period,thr);
			}
		}
		
		System.out.println("PROGRAMA FINALIZADO");
	}

	

}
