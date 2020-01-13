package drosa.apuestas.year2019.indabet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import drosa.apuestas.year2019.NBAoddsPortal;
import drosa.apuestas.year2019.indabet.MatchInda;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class NHLPinnacle {
	
	private static int testSimulation(
			ArrayList<MatchInda> matches, 
			int y1, int y2,
			int period,
			double thr,
			int printMode
			) {
		// TODO Auto-generated method stub
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Double>> teamsOdds = new HashMap<String,ArrayList<Double>>();
		HashMap<String,Double> teamsBank = new HashMap<String,Double>();
		
		String seasonStr = y1+"/"+y2;
		
		
		int wins = 0;
		int losses = 0;
		double winsO = 0;
		double lossesO = 0;
		double stake = 0;
		int totalThr = 0;
		for (int i=0;i<matches.size();i++){
			MatchInda m = matches.get(i);
			
			//if (!m.getSeason().equalsIgnoreCase(seasonStr)) continue;
			//System.out.println(m.getYear());
			if (m.getYear()<y1 || m.getYear()>y2) continue;
			
			
									
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			if (homeTeam.equalsIgnoreCase("GOLD")) {
				//System.out.println(homeTeam+" "+awayTeam+" || "+homeGoals+" "+awayGoals+" "+resh+" "+resa);
			}
			
			//vemos posibilidades actuales
			ArrayList<Integer> ha = null;
			ArrayList<Integer> aa = null;
			
			ArrayList<Double> ho = null;
			ArrayList<Double> ao = null;
			
			if (teams.containsKey(homeTeam)){
				ha = teams.get(homeTeam);
				ho = teamsOdds.get(homeTeam);
			}
			if (teams.containsKey(awayTeam)){
				aa = teams.get(awayTeam);
				ao = teamsOdds.get(awayTeam);
			}
			
			//if (!homeTeam.equalsIgnoreCase("WSH") && !awayTeam.equalsIgnoreCase("WSH")) continue;
			
			if (ha!= null && aa!=null){
				//double perH = MathUtils.average(ha,ha.size()-period, ha.size()-1);
				//double perA = MathUtils.average(aa,aa.size()-period, aa.size()-1);
				int rachaH0 = getRacha(ha,0);
				int rachaA0 = getRacha(aa,0);
				double homeBank = teamsBank.get(homeTeam);
				double awayBank = teamsBank.get(homeTeam);				
			}
			
			//agregamos resultado actual
			if (!teams.containsKey(homeTeam)){
				teams.put(homeTeam, new ArrayList<Integer>());
				teamsBank.put(homeTeam,0.0);
				teamsOdds.put(homeTeam, new ArrayList<Double>());
			}
			ha = teams.get(homeTeam);
			ha.add(resh);
			teamsOdds.get(homeTeam).add(homeOdds);
			
			if (!teams.containsKey(awayTeam)){
				teams.put(awayTeam, new ArrayList<Integer>());
				teamsBank.put(awayTeam,0.0);
				teamsOdds.put(awayTeam, new ArrayList<Double>());
			}
			aa = teams.get(awayTeam);
			aa.add(resa);
			teamsOdds.get(awayTeam).add(awayOdds);
			
		}
		
		
		int cases = 0;
		int cases6=0;
		double accOdds = 0;
		int totalStakes3_10 = 0;
		int totalStakes2_10 = 0;
		int winBets = 0;
		int lossBets= 0;
		double globalWins = 0;
		double globalLosses = 0;
		int globalStreaks0=0;
		int globalStreaks6=0;
		Iterator hmIterator = teams.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
            Map.Entry ele = (Map.Entry)hmIterator.next(); 
            String team = (String) ele.getKey(); 

            ArrayList<Integer> results = (ArrayList<Integer>) ele.getValue();
            ArrayList<Double> odds = teamsOdds.get(team);
            int maxLoss = 0;  
            int currentStreak = 0;
            double currentf = 3.0;
            wins = 0;
            losses = 0;
            stake=0;
            double winso = 0;
            double losso = 0;
            double actualSerieLoss = 0;
            String  streakStr="";
            ArrayList<Integer> streakArr = new ArrayList<Integer>(); 
            for (int i=0;i<results.size()-0;i++){
            	int res = results.get(i);
            	double ro = odds.get(i);
            	//stake = getStake(currentModel,currentStreak,ro,actualSerieLoss);
            	stake = getStakef(currentf,currentStreak,ro,actualSerieLoss);
            	//System.out.println(stake);
            	            	            	                        	            	            	                                           
            	if (res==1) {
            		if (currentStreak>0) {
        				//System.out.println(team+" "+currentStreak);
            			globalStreaks0++;
            			if (currentStreak>5) globalStreaks6++;
            			
            			streakStr += currentStreak+" ";
            			streakArr.add(currentStreak);
            			
            			if (streakArr.size()>=period+1){
                    		double avg = MathUtils.average(streakArr, streakArr.size()-period, streakArr.size()-2);
                    		double dt = Math.sqrt(MathUtils.variance(streakArr, streakArr.size()-period, streakArr.size()-2));
                    		double f = avg + 2*dt;
                    		double sr = streakArr.get(streakArr.size()-1);
                    		if (f>=thr) {
                    			cases++;
                    			if (sr>=6) cases6++;
                    		}
                    		currentf = f;
                    		//System.out.println("Reset f: "+team+" "+f);
                    	}
        			}
            		//actualizamos el modelo, si es necesario, cuando ganamos, para la proxima            	
                	//if (maxLoss>=currentModel && currentModel>0) currentModel = maxLoss;
            		
            		currentStreak=0;
            		wins++;
            		
            		if (stake>0) {
            			winso += stake*(ro-1.0);
            			winBets++;
            			globalWins += stake*(ro-1.0);
            			
            			//accOdds += ro;
            			if (team.equalsIgnoreCase("aMTL"))
            				System.out.println("[WIN] "+team+" "+PrintUtils.Print2dec(stake*(ro-1.0),false)+" "+PrintUtils.Print2dec(actualSerieLoss,false)
            				+" || "+PrintUtils.Print2dec(stake*(ro-1.0)-actualSerieLoss,false)
            				+" || "+PrintUtils.Print2dec(winso,false)+" "+PrintUtils.Print2dec(losso,false)
            				+" || "+streakStr
            				);
            		}
            		actualSerieLoss = 0;
            	}else if (res==0) {
            		currentStreak++;
            		losses++;
            		if (currentStreak>maxLoss) {
            			maxLoss = currentStreak;
            			
            		}
            		
            		if (stake>0) {
            			losso += stake*1.0;
            			lossBets++;
            			globalLosses += stake*1.0;
            			
            			actualSerieLoss += stake*1.0;
            			
            			if (team.equalsIgnoreCase("aMTL"))
            				System.out.println("[LOSS] "+team+" "+PrintUtils.Print2dec(stake*(ro-1.0),false)+" "+PrintUtils.Print2dec(actualSerieLoss,false)
            				+" || "+PrintUtils.Print2dec(stake*(ro-1.0)-actualSerieLoss,false)
            				+" || "+PrintUtils.Print2dec(winso,false)+" "+PrintUtils.Print2dec(losso,false)
            				+" || "+currentStreak
            				//+" || "+currentModel
            				+" || "+PrintUtils.Print2dec(ro,false)
            				+" || "+PrintUtils.Print2dec(stake,false)
            				);
            		}
            	}
            	
            	if (stake==0) actualSerieLoss = 0;
            	
            	
            }
            int total = wins+losses;
            double totalo = winso-losso;
            double avgStreak = MathUtils.average(streakArr, 0, streakArr.size()-1);
            double dt = Math.sqrt(MathUtils.variance(streakArr, 0, streakArr.size()-1));
            if (printMode ==2)
         System.out.println(team+" || "+maxLoss
            		+" "+wins+" "+ losses
            		+" "+PrintUtils.Print2dec(wins*100.0/total,false)
            		+" "+PrintUtils.Print2dec(winso/losso,false)
            		+" "+PrintUtils.Print2dec(totalo,false)
            		+" || "+" "+PrintUtils.Print2dec(avgStreak,false)+" "+PrintUtils.Print2dec(dt,false)
            		+" || "+streakStr
            );
            //actualizamos modelo de equipo
           // int newModel = maxLoss+0;
           // if (maxLoss>=5) newModel = 0;
           // teamModels.put(team,newModel);
        } 	
		
		if (printMode ==0)
			System.out.println(y1+" "+y2+" "+period+" || "+winBets+" "+lossBets
					+" "+PrintUtils.Print2dec(globalWins*1.0/globalLosses,false)
					+" "+PrintUtils.Print2dec(globalWins-globalLosses,false)
					+" "+globalWins+" "+globalLosses
					+" || "+globalStreaks0+" "+globalStreaks6
					+" || "+PrintUtils.Print2dec(accOdds*1.0/winBets,false)
	        );
		
		if (printMode ==1)
		System.out.println(period
				+" "+PrintUtils.Print2dec(thr,false)
				+" ||  "+cases+" "+PrintUtils.Print2dec(cases6*100.0/cases,false)
        );
		
		return 0;
	}
	
	private static double getStakef(double currentf, int currentStreak, double ro, double actualSerieLoss) {
		double stake = 0;
		double ff = 6.0;
		if (currentf>=0 && currentf<=ff) {
			if (currentStreak<=1) return 0;
		}else if ( currentf<=ff+0.5) {
			if (currentStreak<=2) return 0;
		}else if ( currentf<=ff+1.0) {
			if (currentStreak<=3) return 0;
		}else if (currentf<=ff+1.5) {
			if (currentStreak<=4) return 0;
		}else if (currentf>ff+1.5) {
			if (currentStreak<=5) return 0;
		}
				
		double target = 1.0;
		
		//target = stake*(ro-1.0)-actualSerieLoss;
		
		stake = (target+actualSerieLoss)*1.0/(ro-1.0);
		
		//System.out.println("actualLoss: "+actualSerieLoss);
		
		if (stake>10) {
			stake = 10;
		}
		
		return stake;
	}

	private static double getStake(int currentModel, int currentStreak, double ro, double actualSerieLoss) {
		double stake = 0;
		
		if (currentModel==0) return 0;
		


		if (currentModel<=5) {
			if (currentStreak<=0) return 0;
			if (currentStreak>5) return 10;
		}
		if (currentModel==6) {
			if (currentStreak<=2) return 0;
			if (currentStreak>6) return 10;
		}
		if (currentModel==7) {
			if (currentStreak<=3) return 0;
			if (currentStreak>7) return 1;
		}
		if (currentModel==8) {
			if (currentStreak<=4) return 0;
			if (currentStreak>8) return 1;
		}
		
		//if (currentStreak>8) return 0;
		
		double target = 1.0;
		
		//target = stake*(ro-1.0)-actualSerieLoss;
		
		stake = (target+actualSerieLoss)*1.0/(ro-1.0);
		
		if (stake>10) {
			if (currentStreak>currentModel) stake = 0;
			else stake = 10;
		}
		
		return stake;
	}

	private static int testStreaks(ArrayList<MatchInda> matches, 
			int y1, int y2,int period, double thr
			) {
		// TODO Auto-generated method stub
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Double>> teamsOdds = new HashMap<String,ArrayList<Double>>();
		HashMap<String,Double> teamsBank = new HashMap<String,Double>();
		
		String seasonStr = y1+"/"+y2;
		
		int wins = 0;
		int losses = 0;
		double winsO = 0;
		double lossesO = 0;
		double stake = 0;
		int totalThr = 0;
		for (int i=0;i<matches.size();i++){
			MatchInda m = matches.get(i);
			
			//if (!m.getSeason().equalsIgnoreCase(seasonStr)) continue;
			
			if (m.getYear()<y1 || m.getYear()>y2) continue;
									
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			//vemos posibilidades actuales
			ArrayList<Integer> ha = null;
			ArrayList<Integer> aa = null;
			
			ArrayList<Double> ho = null;
			ArrayList<Double> ao = null;
			
			if (teams.containsKey(homeTeam)){
				ha = teams.get(homeTeam);
				ho = teamsOdds.get(homeTeam);
			}
			if (teams.containsKey(awayTeam)){
				aa = teams.get(awayTeam);
				ao = teamsOdds.get(awayTeam);
			}
			
			//if (!homeTeam.equalsIgnoreCase("WSH") && !awayTeam.equalsIgnoreCase("WSH")) continue;
			
			if (ha!= null && aa!=null){
				//double perH = MathUtils.average(ha,ha.size()-period, ha.size()-1);
				//double perA = MathUtils.average(aa,aa.size()-period, aa.size()-1);
				int rachaH0 = getRacha(ha,0);
				int rachaA0 = getRacha(aa,0);
				double homeBank = teamsBank.get(homeTeam);
				double awayBank = teamsBank.get(homeTeam);				
			}
			
			//agregamos resultado actual
			if (!teams.containsKey(homeTeam)){
				teams.put(homeTeam, new ArrayList<Integer>());
				teamsBank.put(homeTeam,0.0);
				teamsOdds.put(homeTeam, new ArrayList<Double>());
			}
			ha = teams.get(homeTeam);
			ha.add(resh);
			teamsOdds.get(homeTeam).add(homeOdds);
			
			if (!teams.containsKey(awayTeam)){
				teams.put(awayTeam, new ArrayList<Integer>());
				teamsBank.put(awayTeam,0.0);
				teamsOdds.put(awayTeam, new ArrayList<Double>());
			}
			aa = teams.get(awayTeam);
			aa.add(resa);
			teamsOdds.get(awayTeam).add(awayOdds);
			
		}
		
		
		int cases = 0;
		int cases6 = 0;
		double accOdds = 0;
		int totalStakes3_10 = 0;
		int totalStakes2_10 = 0;
		int winBets = 0;
		int lossBets= 0;
		Iterator hmIterator = teams.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
            Map.Entry ele = (Map.Entry)hmIterator.next(); 
            String team = (String) ele.getKey(); 

            ArrayList<Integer> results = (ArrayList<Integer>) ele.getValue();
            ArrayList<Double> odds = teamsOdds.get(team);
            int maxLoss = 0;  
            int currentStreak = 0;
            wins = 0;
            losses = 0;
            stake=0;
            double winso = 0;
            double losso = 0;
            for (int i=0;i<results.size()-0;i++){
            	int res = results.get(i);
            	double ro = odds.get(i);
            	stake = 0;
            	if (currentStreak>=3) {
            		if (currentStreak==1) stake = 1;
            		if (currentStreak==2) stake = 1;
            		if (currentStreak==3) stake = 1;
            		if (currentStreak==4) stake = 2;
            		if (currentStreak==5) stake = 4;
            		if (currentStreak==6) stake = 8;
            		if (currentStreak==7) stake = 10;
            		if (currentStreak==8) stake = 4;
            		if (currentStreak==9) stake = 8;
            		if (currentStreak==10) stake = 10;
            	}
            	
            	if (res==1) {
            		currentStreak=0;
            		wins++;
            		
            		if (stake>0) {
            			winso += stake*(ro-1.0);
            			winBets++;
            		}
            	}else if (res==0) {
            		currentStreak++;
            		losses++;
            		if (currentStreak>maxLoss) {
            			maxLoss = currentStreak;
            		}
            		
            		if (stake>0) {
            			losso += stake*1.0;
            			lossBets++;
            		}
            	}
            }
            int total = wins+losses;
            double totalo = winso-losso;
           /* System.out.println(team+" || "+maxLoss
            		+" "+wins+" "+ losses
            		+" "+PrintUtils.Print2dec(wins*100.0/total,false)
            		+" "+PrintUtils.Print2dec(totalo,false)
            );*/
        } 	
		
		System.out.println(period
				+" "+PrintUtils.Print2dec(thr,false)
				+" ||  "+PrintUtils.Print2dec(cases6*100.0/cases,false)
        );
		
		return 0;
	}
	
	
	private static int testStreaksYears(ArrayList<MatchInda> matches, 
			int y1, int y2
			) {
		// TODO Auto-generated method stub
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Integer>> teamYears = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Double>> teamsOdds = new HashMap<String,ArrayList<Double>>();
		HashMap<String,Double> teamsBank = new HashMap<String,Double>();
		
		String seasonStr = y1+"/"+y2;
		
		int wins = 0;
		int losses = 0;
		double winsO = 0;
		double lossesO = 0;
		double stake = 0;
		int totalThr = 0;
		for (int i=0;i<matches.size();i++){
			MatchInda m = matches.get(i);
			
			//if (!m.getSeason().equalsIgnoreCase(seasonStr)) continue;
			
			if (m.getYear()<y1 || m.getYear()>y2) continue;
			
						
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			//vemos posibilidades actuales
			ArrayList<Integer> ha = null;
			ArrayList<Integer> aa = null;
			ArrayList<Integer> yy = null;
			
			ArrayList<Double> ho = null;
			ArrayList<Double> ao = null;
			
			if (teams.containsKey(homeTeam)){
				ha = teams.get(homeTeam);
				ho = teamsOdds.get(homeTeam);
			}
			if (teams.containsKey(awayTeam)){
				aa = teams.get(awayTeam);
				ao = teamsOdds.get(awayTeam);
			}
			
			//if (!homeTeam.equalsIgnoreCase("WSH") && !awayTeam.equalsIgnoreCase("WSH")) continue;
			
			if (ha!= null && aa!=null){
				//double perH = MathUtils.average(ha,ha.size()-period, ha.size()-1);
				//double perA = MathUtils.average(aa,aa.size()-period, aa.size()-1);
				int rachaH0 = getRacha(ha,0);
				int rachaA0 = getRacha(aa,0);
				double homeBank = teamsBank.get(homeTeam);
				double awayBank = teamsBank.get(homeTeam);				
			}
			
			//agregamos resultado actual
			if (!teams.containsKey(homeTeam)){
				teams.put(homeTeam, new ArrayList<Integer>());
				teamYears.put(homeTeam, new ArrayList<Integer>());
				teamsBank.put(homeTeam,0.0);
				teamsOdds.put(homeTeam, new ArrayList<Double>());
			}
			ha = teams.get(homeTeam);
			ha.add(resh);
			teamsOdds.get(homeTeam).add(homeOdds);
			teamYears.get(homeTeam).add(m.getYear());
			
			if (!teams.containsKey(awayTeam)){
				teams.put(awayTeam, new ArrayList<Integer>());
				teamYears.put(homeTeam, new ArrayList<Integer>());
				teamsBank.put(awayTeam,0.0);
				teamsOdds.put(awayTeam, new ArrayList<Double>());
			}
			aa = teams.get(awayTeam);
			aa.add(resa);
			teamsOdds.get(awayTeam).add(awayOdds);
			teamYears.get(homeTeam).add(m.getYear());			
		}
		
		
		int cases = 0;
		double accOdds = 0;
		int totalStakes3_10 = 0;
		int totalStakes2_10 = 0;
		Iterator hmIterator = teams.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
            Map.Entry ele = (Map.Entry)hmIterator.next(); 
            String team = (String) ele.getKey(); 

            ArrayList<Integer> results = (ArrayList<Integer>) ele.getValue();
            ArrayList<Double> odds = teamsOdds.get(team);
            ArrayList<Integer> years = teamYears.get(team);
            int maxLoss = 0;  
            int currentStreak = 0;
            wins = 0;
            losses = 0;
            stake=0;
            double winso = 0;
            double losso = 0;
            for (int i=0;i<results.size()-0;i++){
            	int res = results.get(i);
            	int year = years.get(i);
            	double ro = odds.get(i);
            	stake = 0;
            	if (currentStreak>=3) {
            		if (currentStreak==1) stake = 1;
            		if (currentStreak==2) stake = 1;
            		if (currentStreak==3) stake = 1;
            		if (currentStreak==4) stake = 2;
            		if (currentStreak==5) stake = 4;
            		if (currentStreak==6) stake = 8;
            		if (currentStreak==7) stake = 10;
            		if (currentStreak==8) stake = 4;
            		if (currentStreak==9) stake = 8;
            		if (currentStreak==10) stake = 10;
            	}
            	
            	if (res==1) {
            		currentStreak=0;
            		wins++;
            		
            		if (stake>0) winso += stake*(ro-1.0);
            	}else if (res==0) {
            		currentStreak++;
            		losses++;
            		if (currentStreak>maxLoss) {
            			maxLoss = currentStreak;
            		}
            		
            		if (stake>0) losso += stake*1.0;
            	}
            }
            int total = wins+losses;
            double totalo = winso-losso;
            /*System.out.println(team+" || "+maxLoss
            		+" "+wins+" "+ losses
            		+" "+PrintUtils.Print2dec(wins*100.0/total,false)
            		+" "+PrintUtils.Print2dec(totalo,false)
            );*/
        } 		
		
		return 0;
	}
	private static int testTeams(ArrayList<MatchInda> matches, 
			int y1, int y2,
			int period,
			double thr1,
			double thr2
			) {
		// TODO Auto-generated method stub
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Double>> teamsOdds = new HashMap<String,ArrayList<Double>>();
		HashMap<String,Double> teamsBank = new HashMap<String,Double>();
		
		String seasonStr = y1+"/"+y2;
		
		int wins = 0;
		int losses = 0;
		double winsO = 0;
		double lossesO = 0;
		double stake = 0;
		int totalThr = 0;
		for (int i=0;i<matches.size();i++){
			MatchInda m = matches.get(i);
			
			//if (!m.getSeason().equalsIgnoreCase(seasonStr)) continue;
			
			if (m.getYear()<y1 || m.getYear()>y2) continue;
			
			
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			//vemos posibilidades actuales
			ArrayList<Integer> ha = null;
			ArrayList<Integer> aa = null;
			
			ArrayList<Double> ho = null;
			ArrayList<Double> ao = null;
			
			if (teams.containsKey(homeTeam)){
				ha = teams.get(homeTeam);
				ho = teamsOdds.get(homeTeam);
			}
			if (teams.containsKey(awayTeam)){
				aa = teams.get(awayTeam);
				ao = teamsOdds.get(awayTeam);
			}
			
			//if (!homeTeam.equalsIgnoreCase("WSH") && !awayTeam.equalsIgnoreCase("WSH")) continue;
			
			if (ha!= null && aa!=null){
				double perH = MathUtils.average(ha,ha.size()-period, ha.size()-1);
				double perA = MathUtils.average(aa,aa.size()-period, aa.size()-1);
				int rachaH0 = getRacha(ha,0);
				int rachaA0 = getRacha(aa,0);
				double homeBank = teamsBank.get(homeTeam);
				double awayBank = teamsBank.get(homeTeam);				
			}
			
			//agregamos resultado actual
			if (!teams.containsKey(homeTeam)){
				teams.put(homeTeam, new ArrayList<Integer>());
				teamsBank.put(homeTeam,0.0);
				teamsOdds.put(homeTeam, new ArrayList<Double>());
			}
			ha = teams.get(homeTeam);
			ha.add(resh);
			teamsOdds.get(homeTeam).add(homeOdds);
			
			if (!teams.containsKey(awayTeam)){
				teams.put(awayTeam, new ArrayList<Integer>());
				teamsBank.put(awayTeam,0.0);
				teamsOdds.put(awayTeam, new ArrayList<Double>());
			}
			aa = teams.get(awayTeam);
			aa.add(resa);
			teamsOdds.get(awayTeam).add(awayOdds);
			
		}
		
		
		int cases = 0;
		double accOdds = 0;
		int totalStakes3_10 = 0;
		int totalStakes2_10 = 0;
		Iterator hmIterator = teams.entrySet().iterator(); 
		while (hmIterator.hasNext()) { 
            Map.Entry ele = (Map.Entry)hmIterator.next(); 
            String team = (String) ele.getKey(); 

            ArrayList<Integer> results = (ArrayList<Integer>) ele.getValue();
            ArrayList<Double> odds = teamsOdds.get(team);
            
            
            for (int i=period+1;i<results.size()-4;i++){
            	double percent = MathUtils.average(results, i-period, i-1);
            	int res_2 = results.get(i-2);
            	int res_1 = results.get(i-1);
            	int res0 = results.get(i);
            	int res1 = results.get(i+1);
            	int res2 = results.get(i+2);
            	int res3 = results.get(i+3);
            	int res4 = results.get(i+4);
            	if (res_1==0 
            			&& percent>=thr1
            			//&& res_2==0
            			//&& odds.get(i)<1.9
            			&& odds.get(i)>2.4
            			){
            		cases++;
            		boolean isHome = true;
            		if (isHome) {
		            	if (res0==0 && res1==0 && res2==0 && res3==0            			
		            			){
		            		losses++;
		            		accOdds+=odds.get(i);
		            		accOdds+=odds.get(i+1);
		            		accOdds+=odds.get(i+2);
		            		accOdds+=odds.get(i+3);
		            		//System.out.println(team+" "+percent);
		            		double accAcumulada = 0;
		            		double totalAccAcumulada = 0;
		            		
		            		double stake0 = 1.0;
		            		accAcumulada += stake0;
		            		totalAccAcumulada += stake0;
		            		
		            		double stake1 = (accAcumulada+1)*1.0/(odds.get(i+1)-1);
		            		accAcumulada += stake1;
		            		totalAccAcumulada += stake1;
		            		
		            		double stake2 = (accAcumulada+1)*1.0/(odds.get(i+2)-1);
		            		if (stake2>10) {
		            			stake2 = 1;
		            			accAcumulada = 1;
		            			totalStakes2_10++;
		            			//System.out.println(odds.get(i)+" "+odds.get(i+1)+" "+odds.get(i+2)+" || "+totalAccAcumulada);
		            		}
		            		accAcumulada += stake2;
		            		totalAccAcumulada += stake2;
		            		
		            		double stake3 = (accAcumulada+1)*1.0/(odds.get(i+3)-1);
		            		if (stake3>10) {
		            			stake3 = 1;
		            			accAcumulada = 1;
		            			totalStakes3_10++;
		            		}
		            		accAcumulada += stake3;
		            		totalAccAcumulada += stake3;
		            		
		            		
		            		lossesO += totalAccAcumulada;
		            		//System.out.println(totalAccAcumulada);
		            	}else {
		            			
		            		double profit = 0;
		            		if (res0==1) {
		            			profit = calculateProfit(odds,i,0);
		            			//System.out.println("Profit0: "+profit);
		            		}
		            		else if (res1==1) {
		            			profit = calculateProfit(odds,i,1);
		            			//System.out.println("Profit1: "+profit);
		            		}
		            		else if (res2==1) {
		            			profit = calculateProfit(odds,i,2);
		            			//System.out.println("Profit2: "+profit);
		            		}
		            		else if (res3==1) {
		            			profit = calculateProfit(odds,i,3);
		            			//System.out.println("Profit3: "+profit);
		            		}
		            		
		            		if (profit>=0) winsO+= profit;
		            		else lossesO += -profit;
		            	}
            		}else {
            		}
            	}
            }
        } 
		
		int totalBets = wins+losses;
		double winPer = wins*100.0/totalBets;
		double pf = winsO*1.0/lossesO;
		
		System.out.println(y1+" "+y2+" "+period
				+" "+PrintUtils.Print2dec(thr1, false)
				+" "+PrintUtils.Print2dec(thr2, false)
				+" || "
				+" "+cases
				+" "+losses
				+" "+PrintUtils.Print2dec(winsO, false)
				+" "+PrintUtils.Print2dec(lossesO, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(losses*100.0/cases, false)
				+" || "+PrintUtils.Print2dec(accOdds*1.0/(4.0*losses), false)
				+" || "+totalStakes2_10+" "+totalStakes3_10
				);
		
		
		return 0;
	}
	
	private static double calculateProfit(ArrayList<Double> odds,int index, int pos) {
		
		double o0 = odds.get(index);
		double o1 = odds.get(index+1);
		double o2 = odds.get(index+2);
		double o3 = odds.get(index+3);
		
		if (pos==0) {
			double stake = 1.0/(o0-1.0); 
			return stake*(o0-1.0);
		}else if (pos==1) {
			double stake = 1.0/(o0-1.0); 
			double accLoss = stake;
			double stake1 = (accLoss+1.0)/(o1-1.0); 
			return stake1*(o1-1.0)-accLoss;
		}else if (pos==2) {
			
			double profit = 0;
			double stake = 1.0/(o0-1.0); 
			double accLoss = stake;
			double stake1 = (accLoss+1.0)/(o1-1.0); 
			accLoss = stake+stake1;
			profit = accLoss;
			double stake2 = (accLoss+1.0)/(o2-1.0); 
			if (stake2>10) {
				stake2 = 1;
			}
			
			return stake2*(o2-1.0)-profit;
		}else if (pos==3) {
			double profit = 0;
			double stake = 1.0/(o0-1.0); 
			double accLoss = stake;
			double stake1 = (accLoss+1.0)/(o1-1.0); 
			accLoss = stake+stake1;
			
			double stake2 = (accLoss+1.0)/(o2-1.0); 
			if (stake2>10) {
				profit += accLoss;
				stake2 = 1;				
				accLoss = 1;
			}else {
				accLoss += stake2;
				//System.out.println("accLoss: "+accLoss);
			}
			profit += accLoss;
			
			double stake3 = (accLoss+1.0)/(o3-1.0); 
			//System.out.println("stake3: "+ stake3+" || "+profit);
			if (stake3>10) {
				stake3 = 1;
			}
			
			return stake3*(o3-1.0)-profit;
		}
		
		return 0;
	}

	private static int getRacha(ArrayList<Integer> arr, int value) {
		
		int racha = 0;
		
		for (int i = arr.size()-1;i>=0;i--){
			int v = arr.get(i);
			
			if (v==value) racha++;
			else break;
		}
		
		return racha;
	}
	
	public static void testSimulationDesv(ArrayList<MatchInda> matches,int n,int racha,double minDesv) {
		//todo
		int wins = 0;
		int losses = 0;
		double accOdds=0;
		double accWinOdds = 0;
		HashMap<String,ArrayList<Integer>> teams= new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamsLossRachas= new HashMap<String,ArrayList<Integer>>(); 
		for (int i=0;i<matches.size();i++) {
			MatchInda m = matches.get(i);
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			ArrayList<Integer> homeMatches = null;
			ArrayList<Integer> awayMatches = null;
			ArrayList<Integer> homeLossRachas = null;
			ArrayList<Integer> awayLossRachas = null;
			
			if (!teams.containsKey(homeTeam)) {
				teams.put(homeTeam,new ArrayList<Integer>());
				teamsLossRachas.put(homeTeam,new ArrayList<Integer>());
				teamsLossRachas.get(homeTeam).add(0);
			}
			if (!teams.containsKey(awayTeam)) {
				teams.put(awayTeam,new ArrayList<Integer>());
				teamsLossRachas.put(awayTeam,new ArrayList<Integer>());
				teamsLossRachas.get(awayTeam).add(0);
			}
			
			homeMatches = teams.get(homeTeam);
			awayMatches = teams.get(awayTeam);
			homeLossRachas = teamsLossRachas.get(homeTeam);
			awayLossRachas = teamsLossRachas.get(awayTeam);
			//decision de trading
			if (homeLossRachas.size()>=n) {
				int rachaH = getRacha(homeMatches,0);
				double avg = MathUtils.average(homeLossRachas,homeLossRachas.size()-n,homeLossRachas.size()-1) ;
				double dt = Math.sqrt(MathUtils.variance(homeLossRachas,homeLossRachas.size()-n,homeLossRachas.size()-1)) ;
				
				if (rachaH>=racha
						&& dt<=minDesv
						) {
					accOdds+=homeOdds;
					if (resh==1) {
						wins++;
						accWinOdds+=homeOdds;
					}
					else losses++;
				}
				
				/*System.out.println(homeTeam
						+" || "
						+" "+PrintUtils.Print2dec(avg, false)
						+" "+PrintUtils.Print2dec(dt, false)
						);*/
			}
			
			//actualizamos resultados
			homeMatches.add(resh);
			awayMatches.add(resa);
			int actualRachaH = homeLossRachas.get(homeLossRachas.size()-1);
			int actualRachaA = awayLossRachas.get(awayLossRachas.size()-1);
			
			if (resh==1 && actualRachaH>0) {
				homeLossRachas.add(0);
			}else {
				homeLossRachas.set(homeLossRachas.size()-1, actualRachaH+1);
			}
			
			if (resh==1 && actualRachaA>0){
				awayLossRachas.add(0);
			}else {
				awayLossRachas.set(awayLossRachas.size()-1, actualRachaA+1);
			}
		}
		
		int trades = wins+losses;
		System.out.println(n+" "+racha+" "+PrintUtils.Print2dec(minDesv, false)
				+" || "
				+" "+trades+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" || "+PrintUtils.Print2dec(accOdds*1.0/trades, false)
				+" || "+PrintUtils.Print2dec(accWinOdds*1.0/wins, false)
				);
	}
	
	public static void testSimulationRachas50(ArrayList<MatchInda> matches) {
		//todo
		int wins = 0;
		int losses = 0;
		double accOdds=0;
		double accWinOdds = 0;
		double accLossOdds = 0;
		String teamCase="tbl";
		int cases=0;
		HashMap<String,ArrayList<Integer>> teams= new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamsLossRachas= new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamsWinsRachas= new HashMap<String,ArrayList<Integer>>();
		for (int i=0;i<matches.size();i++) {
			MatchInda m = matches.get(i);
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			ArrayList<Integer> homeMatches = null;
			ArrayList<Integer> awayMatches = null;
			ArrayList<Integer> homeLossRachas = null;
			ArrayList<Integer> awayLossRachas = null;
			ArrayList<Integer> homeWinsRachas = null;
			ArrayList<Integer> awayWinsRachas = null;
			
			if (!teams.containsKey(homeTeam)) {
				teams.put(homeTeam,new ArrayList<Integer>());
				teamsLossRachas.put(homeTeam,new ArrayList<Integer>());
				teamsLossRachas.get(homeTeam).add(0);
				teamsWinsRachas.put(homeTeam,new ArrayList<Integer>());
				teamsWinsRachas.get(homeTeam).add(0);
			}
			if (!teams.containsKey(awayTeam)) {
				teams.put(awayTeam,new ArrayList<Integer>());
				teamsLossRachas.put(awayTeam,new ArrayList<Integer>());
				teamsLossRachas.get(awayTeam).add(0);
				teamsWinsRachas.put(awayTeam,new ArrayList<Integer>());
				teamsWinsRachas.get(awayTeam).add(0);
			}
			
			homeMatches = teams.get(homeTeam);
			awayMatches = teams.get(awayTeam);
			homeLossRachas = teamsLossRachas.get(homeTeam);
			awayLossRachas = teamsLossRachas.get(awayTeam);
			homeWinsRachas = teamsWinsRachas.get(homeTeam);
			awayWinsRachas = teamsWinsRachas.get(awayTeam);
			
			/*int rachaLossH = getRacha(homeMatches,0);
			int rachaLossA = getRacha(awayMatches,0);
			int rachaWinH = getRacha(homeMatches,1);
			int rachaWinA = getRacha(awayMatches,1);*/
			
			//necesitamos calcular medias y desviaciones para las rachas de ganancias y perdidas
			//tanto para home como para loss
			double avgHL = -1;
			double dtHL = -1;
			double avgHW = -1;
			double dtHW = -1;
			double avgAL = -1;
			double dtAL = -1;
			double avgAW = -1;
			double dtAW = -1;
			
			//trading
			
			if (homeTeam.equalsIgnoreCase(teamCase)){
				int rachaLossH = getRacha(homeMatches,0);

				//System.out.println(homeTeam+" "+awayTeam+" racha="+rachaLossH+" || "+cases);
				if (rachaLossH==1){
					cases++;
					if (resh==1){
						accWinOdds+=(homeOdds-1.0);
						wins++;
					}else{
						accLossOdds += 1;
						losses++;
					}
					/*if (resa==1){
						accWinOdds+=(awayOdds-1.0);
						wins++;
					}else{
						accLossOdds += 1;
						losses++;
					}*/
				}
			}else if (awayTeam.equalsIgnoreCase(teamCase)){
				int rachaLossA = getRacha(awayMatches,0);
				//System.out.println(awayTeam+" "+homeTeam+" racha="+rachaLossA+" || "+cases);
				if (rachaLossA==1){
					cases++;
					if (resa==1){
						accWinOdds+=(awayOdds-1.0);
						wins++;
					}else{
						accLossOdds += 1;
						losses++;
					}
					/*if (resh==1){
						accWinOdds+=(homeOdds-1.0);
						wins++;
					}else{
						accLossOdds += 1;
						losses++;
					}*/
				}
			}
			
			//todo
			
			//actualizamos resultados
			homeMatches.add(resh);
			awayMatches.add(resa);
			int actualRachaH = homeLossRachas.get(homeLossRachas.size()-1);
			int actualRachaA = awayLossRachas.get(awayLossRachas.size()-1);
			
			if (resh==1 && actualRachaH>0) {
				homeLossRachas.add(0);
			}else {
				if (resh==0)
					homeLossRachas.set(homeLossRachas.size()-1, actualRachaH+1);
			}
			
			if (resa==1 && actualRachaA>0){
				awayLossRachas.add(0);
			}else {
				if (resa==0)
					awayLossRachas.set(awayLossRachas.size()-1, actualRachaA+1);
			}
		}
		
		//pintamos teamsLossRachas por equipo
		 Iterator it = teamsLossRachas.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        String team = (String) pair.getKey();
		        ArrayList<Integer> rachas = (ArrayList<Integer>)pair.getValue();
		        String rachaStr ="";
		        ArrayList<Integer> rachasAcc=new ArrayList<Integer>();
		        for (int i=0;i<=5;i++) rachasAcc.add(0);
		        for (int i=0;i<rachas.size();i++){
		        	int rachaActual = rachas.get(i);
		        	if (rachaActual>=5) rachaActual=5;
		        	int acc = rachasAcc.get(rachaActual);
		        	rachasAcc.set(rachaActual, acc+1);		        	
		        }
		        int totalr=0;
		        for (int i=0;i<=5;i++){
		        	int acc = rachasAcc.get(i);
		        	rachaStr+=acc+" ";
		        	totalr+=acc;
		        }
		        double per1 = rachasAcc.get(1)*100.0/totalr;
		        System.out.println(team
		        		+" || "+totalr
		        		+" || "+PrintUtils.Print2dec(per1, false)
		        		+" || "+rachaStr
		        		);
		    }
		    int totaltrades=wins+losses;
		    double pf = accWinOdds*1.0/accLossOdds;
		    double winPer = wins*100.0/totaltrades;
		    System.out.println(teamCase
		    		+" >>> "+PrintUtils.Print2dec(winPer, false)
		    		+" >>> "+PrintUtils.Print2dec(pf, false)
		    		);
		/*int trades = wins+losses;
		System.out.println(n+" "+racha+" "+PrintUtils.Print2dec(minDesv, false)
				+" || "
				+" "+trades+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" || "+PrintUtils.Print2dec(accOdds*1.0/trades, false)
				+" || "+PrintUtils.Print2dec(accWinOdds*1.0/wins, false)
				);*/
	}
	
	public static void testSimulationDesv2(ArrayList<MatchInda> matches,int n,int racha,double minDesv) {
		//todo
		int wins = 0;
		int losses = 0;
		double accOdds=0;
		double accWinOdds = 0;
		HashMap<String,ArrayList<Integer>> teams= new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamsLossRachas= new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamsWinsRachas= new HashMap<String,ArrayList<Integer>>();
		for (int i=0;i<matches.size();i++) {
			MatchInda m = matches.get(i);
			
			String homeTeam = m.getHomeTeam();
			String awayTeam = m.getAwayTeam();
			int homeGoals = m.getHomeGoals();
			int awayGoals = m.getAwayGoals();
			double homeOdds = m.getHomeOdds1();
			double awayOdds = m.getAwayOdds1();
			
			int resh = homeGoals>awayGoals?1:0;
			int resa = awayGoals>homeGoals?1:0;
			
			ArrayList<Integer> homeMatches = null;
			ArrayList<Integer> awayMatches = null;
			ArrayList<Integer> homeLossRachas = null;
			ArrayList<Integer> awayLossRachas = null;
			ArrayList<Integer> homeWinsRachas = null;
			ArrayList<Integer> awayWinsRachas = null;
			
			if (!teams.containsKey(homeTeam)) {
				teams.put(homeTeam,new ArrayList<Integer>());
				teamsLossRachas.put(homeTeam,new ArrayList<Integer>());
				teamsLossRachas.get(homeTeam).add(0);
				teamsWinsRachas.put(homeTeam,new ArrayList<Integer>());
				teamsWinsRachas.get(homeTeam).add(0);
			}
			if (!teams.containsKey(awayTeam)) {
				teams.put(awayTeam,new ArrayList<Integer>());
				teamsLossRachas.put(awayTeam,new ArrayList<Integer>());
				teamsLossRachas.get(awayTeam).add(0);
				teamsWinsRachas.put(awayTeam,new ArrayList<Integer>());
				teamsWinsRachas.get(awayTeam).add(0);
			}
			
			homeMatches = teams.get(homeTeam);
			awayMatches = teams.get(awayTeam);
			homeLossRachas = teamsLossRachas.get(homeTeam);
			awayLossRachas = teamsLossRachas.get(awayTeam);
			homeWinsRachas = teamsWinsRachas.get(homeTeam);
			awayWinsRachas = teamsWinsRachas.get(awayTeam);
			
			int rachaLossH = getRacha(homeMatches,0);
			int rachaLossA = getRacha(awayMatches,0);
			int rachaWinH = getRacha(homeMatches,1);
			int rachaWinA = getRacha(awayMatches,1);
			
			//necesitamos calcular medias y desviaciones para las rachas de ganancias y perdidas
			//tanto para home como para loss
			double avgHL = -1;
			double dtHL = -1;
			double avgHW = -1;
			double dtHW = -1;
			double avgAL = -1;
			double dtAL = -1;
			double avgAW = -1;
			double dtAW = -1;
			
			//todo
			
			//para home
			if (rachaLossH>0) {
				if (homeLossRachas.size()>=n) {
					double avg = MathUtils.average(homeLossRachas,homeLossRachas.size()-n,homeLossRachas.size()-1) ;
					double dt = Math.sqrt(MathUtils.variance(homeLossRachas,homeLossRachas.size()-n,homeLossRachas.size()-1)) ;
					
					long thr = Math.round(avg + dt);
				}
			}else if (rachaWinH>0) {
				
			}
			//decision de trading
			if (homeLossRachas.size()>=n) {
				int rachaH = getRacha(homeMatches,0);
				double avg = MathUtils.average(homeLossRachas,homeLossRachas.size()-n,homeLossRachas.size()-1) ;
				double dt = Math.sqrt(MathUtils.variance(homeLossRachas,homeLossRachas.size()-n,homeLossRachas.size()-1)) ;
				
				if (rachaH>=racha
						&& dt<=minDesv
						) {
					accOdds+=homeOdds;
					if (resh==1) {
						wins++;
						accWinOdds+=homeOdds;
					}
					else losses++;
				}
			}
			
			//actualizamos resultados
			homeMatches.add(resh);
			awayMatches.add(resa);
			int actualRachaH = homeLossRachas.get(homeLossRachas.size()-1);
			int actualRachaA = awayLossRachas.get(awayLossRachas.size()-1);
			
			if (resh==1 && actualRachaH>0) {
				homeLossRachas.add(0);
			}else {
				homeLossRachas.set(homeLossRachas.size()-1, actualRachaH+1);
			}
			
			if (resh==1 && actualRachaA>0){
				awayLossRachas.add(0);
			}else {
				awayLossRachas.set(awayLossRachas.size()-1, actualRachaA+1);
			}
		}
		
		int trades = wins+losses;
		System.out.println(n+" "+racha+" "+PrintUtils.Print2dec(minDesv, false)
				+" || "
				+" "+trades+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" || "+PrintUtils.Print2dec(accOdds*1.0/trades, false)
				+" || "+PrintUtils.Print2dec(accWinOdds*1.0/wins, false)
				);
	}

	public static void getMatches(String fileName, ArrayList<MatchInda> matches) throws FileNotFoundException{
		
		File file = new File(fileName);
		Scanner input = new Scanner(file);
		int i = 0;
		matches.clear();
		while(input.hasNext()) {
		    String nextLine = input.nextLine();
		    if (!nextLine.contains("Regular")) continue;
		    String[] values = nextLine.split(";");
		    if (values.length!=29) continue;
		    
		    //System.out.println(values.length+" || " +nextLine);
		    
		    int year = Integer.valueOf(values[0]);
			int day = Integer.valueOf(values[1]);
			String monthStr = values[2];
			String season = values[3];
			String matchType=values[4];
			
			String homeTeam = values[7];
			String awayTeam = values[8];
			int homeGoals = Integer.valueOf(values[9]);
			int awayGoals = Integer.valueOf(values[10]);
			double homeOdds1 = Double.valueOf(values[25].replace(',', '.'));
			double awayOdds1 = Double.valueOf(values[26].replace(',', '.'));
			double homeOdds2 = Double.valueOf(values[27].replace(',', '.'));
			double awayOdds2 = Double.valueOf(values[28].replace(',', '.'));
		    
			MatchInda m = new MatchInda();
			
			m.setParameters(year,day,monthStr,season,matchType,homeTeam,awayTeam,homeGoals,awayGoals,
					homeOdds1,awayOdds1,homeOdds2,awayOdds2);
		    
		    
		    //decodificamos la linea
			matches.add(0,m);
		}
	}

	public static void main(String[] args) throws IOException {
		String fileName = "c:\\nhl\\indabet\\IH NHL_Pinnacle_ML_06 Oct 2019.csv";

		ArrayList<MatchInda> matches = new ArrayList<MatchInda>();
		
		NHLPinnacle.getMatches(fileName, matches);
		
		int y1 = 2013;
		int y2 = 2019;
		double thr1 = 0.7;
		double thr2 = 0.5;
		
		//NHL
		for (y1 = 2018;y1<=2018;y1+=1){
			y2 = y1+0;
			for (int period=50;period<=50;period++) {
				for (double minDesv=1.0;minDesv<=1.0;minDesv+=0.10) {
					//NHLPinnacle.testSimulation(matches,y1,y2,period,thr,0);
					//NHLPinnacle.testSimulationDesv(matches, period, 5, minDesv);
					NHLPinnacle.testSimulationRachas50(matches);
				}
			}
		}
		
		//NBA
		/*ArrayList<MatchInda> matchesNBA = new ArrayList<MatchInda>();
		NBAoddsPortal.getMatches("c:\\nba", 2013, 2019, matchesNBA);
		System.out.println("partidos NBA: "+matchesNBA.size());
		for (y1 = 2017;y1<=2017;y1+=1){
			y2 = y1+2;
			for (int period=10;period<=10;period++) {
				for (double thr=2.0;thr<=2.0;thr+=0.1) {
					NHLPinnacle.testSimulation(matchesNBA,y1,y2,period,thr,0);
				}
			}
		}*/
		/*ArrayList<MatchInda> matchesMLB = new ArrayList<MatchInda>();
		NBAoddsPortal.getMatches("c:\\mlb", 2016, 2019, matchesMLB );
		System.out.println("partidos mlb: "+matchesMLB.size());
		for (y1 = 2016;y1<=2016;y1+=1){
			y2 = y1+3;
			for (int period=10;period<=10;period++) {
				for (double thr=2.0;thr<=2.0;thr+=0.1) {
					NHLPinnacle.testSimulation(matchesMLB,y1,y2,period,thr,0);
				}
			}
		}*/
	}

	

}
