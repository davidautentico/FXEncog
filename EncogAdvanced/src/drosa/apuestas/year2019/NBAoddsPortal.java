package drosa.apuestas.year2019;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import drosa.apuestas.ApuestasStats;
import drosa.apuestas.Match;
import drosa.apuestas.year2019.indabet.MatchInda;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class NBAoddsPortal {
	
	private static boolean checkTeam(String teamStr) {

		
		/*if (teamStr.trim().equalsIgnoreCase("Balt")) return false;
		if (teamStr.trim().equalsIgnoreCase("Detr")) return false;
		if (teamStr.trim().equalsIgnoreCase("Whit")) return false;
		if (teamStr.trim().equalsIgnoreCase("Miam")) return false;
		if (teamStr.trim().equalsIgnoreCase("Kans")) return false;
		if (teamStr.trim().equalsIgnoreCase("Pitt")) return false;
		if (teamStr.trim().equalsIgnoreCase("Cinc")) return false;
		if (teamStr.trim().equalsIgnoreCase("Texa")) return false;
		if (teamStr.trim().equalsIgnoreCase("San")) return false;
		return true;*/
		if (teamStr.trim().equalsIgnoreCase("Atla")) return true;
		return false;
		/*if (teamStr.trim().equalsIgnoreCase("Pitt")) return false;
		if (teamStr.trim().equalsIgnoreCase("Minn")) return false;
		if (teamStr.trim().equalsIgnoreCase("Oakl")) return false;
		if (teamStr.trim().equalsIgnoreCase("Tamp")) return false;
		if (teamStr.trim().equalsIgnoreCase("Atla")) return false;*/
		//return true;
		
	}
	
public static void doAnalyze(HashMap<String,ArrayList<Match>> teamsDict){
	
	double wins$ = 0;
	double losses$=0;
	double wins1$ = 0;
	double losses1$=0;
	int totalBets = 0;
	int numLosses = 5;
	Iterator it = teamsDict.entrySet().iterator();
	while (it.hasNext()) {		    	
		Map.Entry pair = (Map.Entry)it.next();
		String teamStr = (String) pair.getKey();
		ArrayList<Match> matches = (ArrayList<Match>) pair.getValue();
		double profit$=0;
		double loss$=0;
		double actualLoss$ = 0;
		int actualStake = 1;
		int currentLosses = 0;
		boolean isValid = checkTeam(teamStr);
		//isValid = true;
		if (!isValid) continue;
		
		System.out.println("**** "+teamStr+" ****");
		String rival="";
		int totalSeqs = 0;
		ArrayList<Double> rachas0 = new ArrayList<Double>();
		double actualCount = 0;
		for (int i=0;i<matches.size()-10;i++){
			Match m = matches.get(i);
			String homeTeam = m.getHomeTeam();
	        String awayTeam = m.getAwayTeam();
	        int homeGoals	= m.getHomeGoals();
	        int awayGoals	= m.getAwayGoals();
	        double homeOdds = m.getHomeOdds();
	        double awayOdds = m.getAwayOdds();
	        
	        boolean isBet = false;
	        double odds = 1.0;
	        int res = 0;
	        if (teamStr.equalsIgnoreCase(homeTeam)){
	        	if (!awayTeam.equalsIgnoreCase(rival)){
	        		rival = awayTeam;
	        		totalSeqs++;
	        	}
	        	
	        	 if (currentLosses>=numLosses){
	 	        	actualStake = (int) (actualLoss$/(homeOdds-1.0) +1.0);
	 	        	if (actualStake>10){
	 	        		//asumo merdidas
	 	        		actualLoss$ = 0;
	 	        		actualStake = 1; 
	 	        		//break;
	 	        	}
	 	        	isBet = true;
	 	        }
	        	
	        	odds = homeOdds;
	        	res = 1;
	        	if (awayGoals>homeGoals){
	        		res = 0;
	        		currentLosses++;
	        		actualCount += 1.0/odds;
	        	}else{
	        		currentLosses = 0;
	        		if (actualCount>0){
	        			rachas0.add(actualCount);
	        			actualCount = 0;
	        		}
	        	}
	        	if (isBet){
		        	totalBets++;
		        	if (res==1){
		        		profit$ += actualStake*(odds-1.0);		        		
		        		wins1$+=actualStake*(odds-1.0);	
		        		actualLoss$ = 0;
		        	}else{
		        		actualLoss$ += actualStake;
		        		losses1$ += actualStake;
	 	        		loss$ += actualStake;
		        	}
		        	//System.out.println(awayTeam+" "+res+" "+PrintUtils.Print2dec(odds, false)
	        		//+" || "+PrintUtils.Print2dec(actualStake, false)+" "+PrintUtils.Print2dec(actualLoss$, false)
	        		//+" || "+PrintUtils.Print2dec(profit$, false)+" "+PrintUtils.Print2dec(loss$, false)
	        		//+" || "+PrintUtils.Print2dec(wins$, false)+" "+PrintUtils.Print2dec(losses1$, false)
	        		//);
		        }else{
		        	//System.out.println(awayTeam+" "+res+" "+PrintUtils.Print2dec(odds, false));
		        }
	        	System.out.println(awayTeam+" "+res+" "+PrintUtils.Print2dec(odds, false));
	        }else if (teamStr.equalsIgnoreCase(awayTeam)){
	        	if (!homeTeam.equalsIgnoreCase(rival)){
	        		rival = homeTeam;
	        		totalSeqs++;
	        	}
	        	if (currentLosses>=numLosses){
	        		actualStake = (int) (actualLoss$/(awayOdds-1.0) +1.0);
	 	        	if (actualStake>10){
	 	        		//asumo merdidas

	 	        		losses$ +=actualLoss$; 
	 	        		actualLoss$ = 0;
	 	        		actualStake = 1;
	 	        	}
	 	        	isBet = true;
		 	    }
	        	res = 1;
	        	if (awayGoals<homeGoals){
	        		res = 0;
	        		currentLosses++;
	        		actualCount += 1.0/odds;
	        	}else{
	        		currentLosses = 0;
	        		if (actualCount>0){
	        			rachas0.add(actualCount);
	        			actualCount = 0;
	        		}
	        		
	        	}
	        	odds = awayOdds;
	        	if (isBet){
		        	totalBets++;
		        	if (res==1){
		        		profit$ += actualStake*(odds-1.0);
		        		
		        		wins$+=actualStake*(odds-1.0)-actualLoss$;
		        		wins1$+=actualStake*(odds-1.0);
		        		actualLoss$ = 0;
		        	}else{
		        		actualLoss$ += actualStake;
		        		losses1$ += actualStake;
		        		loss$ += actualStake;
		        	}
		        	//System.out.println(homeTeam+" "+res+" "+PrintUtils.Print2dec(odds, false)
	        		//+" || "+PrintUtils.Print2dec(actualStake, false)+" "+PrintUtils.Print2dec(actualLoss$, false)
	        		//+" || "+PrintUtils.Print2dec(profit$, false)+" "+PrintUtils.Print2dec(loss$, false)
	        		//+" || "+PrintUtils.Print2dec(wins$, false)+" "+PrintUtils.Print2dec(losses1$, false)
	        		//);
		        }else{
		        	//System.out.println(homeTeam+" "+res+" "+PrintUtils.Print2dec(odds, false));
		        }
	        }
		}//matches
		if (actualLoss$>0){
			losses$ +=actualLoss$; 
		}
		double pf = wins$*1.0/losses$;
		double pf1 = wins1$*1.0/losses1$;
		System.out.println("GLOBAL "+totalSeqs+" "+totalBets+" "+PrintUtils.Print2dec(wins1$, false)
			+" "+PrintUtils.Print2dec(losses1$, false)
			+" "+PrintUtils.Print2dec(pf1, false)
			+" || "
			+" "+teamStr
			+" "+PrintUtils.Print2dec(profit$, false)
			+" "+PrintUtils.Print2dec(loss$, false)
			);
	}
}
	
public static void doAnalyze2(HashMap<String,ArrayList<Match>> teamsDict,int debug){
	
	double wins$ = 0;
	double losses$=0;
	double wins1$ = 0;
	double losses1$=0;
	int totalBets = 0;
	int numLosses = 5;
	int wins = 0;
	int losses = 0;
	Iterator it = teamsDict.entrySet().iterator();
	int totalcases = 0;
	int totaloks=0;
	while (it.hasNext()) {		    	
		Map.Entry pair = (Map.Entry)it.next();
		String teamStr = (String) pair.getKey();
		ArrayList<Match> matches = (ArrayList<Match>) pair.getValue();
		double profit$=0;
		double loss$=0;
		double actualLoss$ = 0;
		double actualStake = 1;
		int currentLosses = 0;
		boolean isValid = checkTeam(teamStr);
		isValid = true;
		if (!isValid) continue;
		
		
		System.out.println("**** "+teamStr+" ****");
		String rival="";
		int totalSeqs = 0;
		ArrayList<Double> rachas0 = new ArrayList<Double>();
		double actualCount = 0;
		ArrayList<Integer> ress = new ArrayList<Integer>();
		ArrayList<Integer> currentSerie = new ArrayList<Integer>();
		ArrayList<Integer> resSeries = new ArrayList<Integer>();
		int points = 0;
    	int points1 = 0;
		for (int i=0;i<matches.size()-1;i++){
			Match m = matches.get(i);
			String homeTeam = m.getHomeTeam();
	        String awayTeam = m.getAwayTeam();
	        int homeGoals	= m.getHomeGoals();
	        int awayGoals	= m.getAwayGoals();
	        double homeOdds = m.getHomeOdds();
	        double awayOdds = m.getAwayOdds();
	        
	        boolean isBet = false;
	        double odds = 1.0;
	        int res = 0;
	        if (teamStr.equalsIgnoreCase(homeTeam)){	        	
	        	if (!awayTeam.equalsIgnoreCase(rival)){
	        		rival = awayTeam;
	        		totalSeqs++;
	        		if (debug==1)
	        		System.out.println("--NUEVA SERIE: "+awayTeam);
	        		points = 0;
	        		points1 = 0;
	        		if (currentSerie.size()>0){
	        			
	        			for (int j=0;j<currentSerie.size();j++) points+=currentSerie.get(j);
	        			resSeries.add(points);
	        			if (resSeries.size()>=2){
	        				points = resSeries.get(resSeries.size()-1);
	        				points1 = resSeries.get(resSeries.size()-2);
	        			}
	        		}
	        		currentSerie.clear();
	        	}
	        	
	        	if (points==0 && points1==0
	        			){
	 	        	actualStake =  (actualLoss$/(homeOdds-1.0) +1.0);
	 	        	if (actualStake>8){
	 	        		//asumo merdidas
	 	        		actualLoss$ = 0;
	 	        		actualStake = 1; 
	 	        		//break;
	 	        	}
	 	        	isBet = true;	 	        		 	        	
	 	        }
	        	
	        	
	        	//isBet = false;
	        	//double avg = MathUtils.average(ress, i-100, i)*100.0;
	        	//if (avg<50.0) isBet = true;
 	        	//else isBet = false;
	        	/*isBet = false;	
	            if (currentSerie.size()>0){
	            	if (currentSerie.size()==1 && currentSerie.get(0)==1) isBet = true;
	            	if (currentSerie.size()==2 && currentSerie.get(0)==1 && currentSerie.get(1)==0) isBet = true;
	            }*/
	        	
	        	
	        	odds = homeOdds;
	        	res = 1;
	        	if (awayGoals>homeGoals){
	        		res = 0;
	        		currentLosses++;
	        		actualCount += 1.0/odds;
	        		ress.add(0);
	        	}else{
	        		currentLosses = 0;
	        		if (actualCount>0){
	        			rachas0.add(actualCount);
	        			actualCount = 0;
	        		}
	        		ress.add(1);
	        	}
	        	currentSerie.add(res);
	        	if (isBet){
		        	totalBets++;
		        	if (res==1){
		        		profit$ += actualStake*(odds-1.0);
		        		
		        		wins1$+=actualStake*(odds-1.0);	
		        		actualLoss$ = 1;
		        		wins++;
		        	}else{
		        		actualLoss$ += actualStake;
		        		losses1$ += actualStake;
	 	        		loss$ += actualStake;
	 	        		losses++;
		        	}
		        	if (debug==1)
		        	System.out.println(awayTeam+" "+res+" "+PrintUtils.Print2dec(odds, false)
	        		+" || "+PrintUtils.Print2dec(actualStake, false)+" "+PrintUtils.Print2dec(actualLoss$, false)
	        		+" || "+PrintUtils.Print2dec(profit$, false)+" "+PrintUtils.Print2dec(loss$, false)
	        		+" || "+PrintUtils.Print2dec(wins1$, false)+" "+PrintUtils.Print2dec(losses1$, false)
	        		+" || "+homeGoals+" "+awayGoals
	        		);
		        }else{
		        	if (debug==1)
		        	System.out.println(awayTeam+" "+res+" "+PrintUtils.Print2dec(odds, false)+" || "+homeGoals+" "+awayGoals);
		        }
	        	//System.out.println(awayTeam+" "+res+" "+PrintUtils.Print2dec(odds, false));
	        }else if (teamStr.equalsIgnoreCase(awayTeam)){
	        	if (!homeTeam.equalsIgnoreCase(rival)){
	        		rival = homeTeam;
	        		totalSeqs++;
	        		if (debug==1)
	        		System.out.println("--NUEVA SERIE: "+homeTeam);
	        		points = 0;
	        		points1 = 0;
	        		if (currentSerie.size()>0){
	        			for (int j=0;j<currentSerie.size();j++) points+=currentSerie.get(j);
	        			resSeries.add(points);
	        			if (resSeries.size()>=2){
	        				points = resSeries.get(resSeries.size()-1);
	        				points1 = resSeries.get(resSeries.size()-2);
	        			}
	        		}
	        		currentSerie.clear();
	        	}
	        	if (points==0 && points1==0
	        			){
	        		actualStake = (actualLoss$/(awayOdds-1.0) +1.0);
	 	        	if (actualStake>8){
	 	        		//asumo merdidas

	 	        		losses$ +=actualLoss$; 
	 	        		actualLoss$ = 0;
	 	        		actualStake = 2;
	 	        	}
	 	        	isBet = true;	 	        		 	        	
		 	    }
	        	//isBet = false;	
	        	/*isBet = false;	
	            if (currentSerie.size()>0){
	            	if (currentSerie.size()==1 && currentSerie.get(0)==1) isBet = true;
	            	if (currentSerie.size()==2 && currentSerie.get(0)==1 && currentSerie.get(1)==0) isBet = true;
	            }*/
	        	
	        	res = 1;
	        	if (awayGoals<homeGoals){
	        		res = 0;
	        		currentLosses++;
	        		actualCount += 1.0/odds;
	        		ress.add(0);
	        	}else{
	        		currentLosses = 0;
	        		if (actualCount>0){
	        			rachas0.add(actualCount);
	        			actualCount = 0;
	        		}
	        		ress.add(1);
	        		
	        	}
	        	currentSerie.add(res);
	        	odds = awayOdds;
	        	if (isBet){
		        	totalBets++;
		        	if (res==1){
		        		profit$ += actualStake*(odds-1.0);
		        		
		        		wins$+=actualStake*(odds-1.0)-actualLoss$;
		        		wins1$+=actualStake*(odds-1.0);
		        		actualLoss$ = 0;
		        		wins++;
		        	}else{
		        		actualLoss$ += actualStake;
		        		losses1$ += actualStake;
		        		loss$ += actualStake;
		        		losses++;
		        	}
		        	if (debug==1)
		        	System.out.println(homeTeam+" "+res+" "+PrintUtils.Print2dec(odds, false)
	        		+" || "+PrintUtils.Print2dec(actualStake, false)+" "+PrintUtils.Print2dec(actualLoss$, false)
	        		+" || "+PrintUtils.Print2dec(profit$, false)+" "+PrintUtils.Print2dec(loss$, false)
	        		+" || "+PrintUtils.Print2dec(wins1$, false)+" "+PrintUtils.Print2dec(losses1$, false)
	        		+" || "+awayGoals+" "+homeGoals);
		        }else{
		        	if (debug==1)
		        	System.out.println(homeTeam+" "+res+" "+PrintUtils.Print2dec(odds, false)+" || "+awayGoals+" "+homeGoals);
		        }
	        	//System.out.println(homeTeam+" "+res+" "+PrintUtils.Print2dec(odds, false));
	        }
	        
	        
	        
		}//matches
		if (actualLoss$>0){
			losses$ +=actualLoss$; 
		}
		for (int i=1;i<ress.size();i++){
			double avg = MathUtils.average(ress, i-19, i)*100.0;
			//System.out.println(PrintUtils.Print2dec(avg, false));
		}
		int cases = 0;
		int oks = 0;
		for (int i=0;i<resSeries.size()-3;i++){
			int r = resSeries.get(i);
			int r1 = resSeries.get(i+1);
			int r2 = resSeries.get(i+2);
			int r3 = resSeries.get(i+3);
			if (r>0
					&& r1>0
					&& r2>3
					){
				cases++;
				totalcases++;
				if (r3>0){
					oks++;
					totaloks++;
				}
			}
			//System.out.println(PrintUtils.Print2dec(avg, false));
		}
		
		
		
		double pf = wins$*1.0/losses$;
		double pf1 = wins1$*1.0/losses1$;
		double avg = MathUtils.average(rachas0);
		System.out.println("GLOBAL "+totalSeqs+" "+totalBets+" "+PrintUtils.Print2dec(wins1$, false)
			+" "+PrintUtils.Print2dec(losses1$, false)
			+" "+PrintUtils.Print2dec(wins*100.0/(wins+losses), false)
			+" "+PrintUtils.Print2dec(pf1, false)
			+" || "
			+" "+teamStr
			+" "+PrintUtils.Print2dec(profit$, false)
			+" "+PrintUtils.Print2dec(loss$, false)
			+" || "+PrintUtils.Print2dec(avg, false)
			+" || "+cases+" "+PrintUtils.Print2dec(oks*100.0/cases, false)
			+" || "+totalcases+" "+PrintUtils.Print2dec(totaloks*100.0/totalcases, false)
			);
	}
}


public static void getTeamMatchs(ArrayList<Match> matches,HashMap<String,ArrayList<Match>> teamsDict) {
				
	for (int i=0;i<matches.size()-1;i++){
		Match m = matches.get(i);
		String homeTeam = m.getHomeTeam();
        String awayTeam = m.getAwayTeam();
        int homeGoals	= m.getHomeGoals();
        int awayGoals	= m.getAwayGoals();
        double homeOdds = m.getHomeOdds();
        double awayOdds = m.getAwayOdds();
        
        if (!teamsDict.containsKey(homeTeam)){
        	teamsDict.put(homeTeam, new ArrayList<Match>());
        }	        
        teamsDict.get(homeTeam).add(m);
        
        if (!teamsDict.containsKey(awayTeam)){
        	teamsDict.put(awayTeam, new ArrayList<Match>());
        }	        
        teamsDict.get(awayTeam).add(m);
	}
}		

public static void studySeries(ArrayList<Match>testMatches) {
	
	HashMap<String,HashMap<String,ArrayList<Integer>>> series = new HashMap<String,HashMap<String,ArrayList<Integer>>>();
	
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
		
	}
	
	//estudiamos las series
	int cases = 0;
	int wins = 0;
	int totalBets = 0;
	int totalWins = 0;
	int totalLosses = 0;
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
			if (res0==1
					&& results.size()==3
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
					
					System.out.println("[WIN] "+serie+" "+rival2+" || "+results.size()+" || "+resStr);						
				}else{
					System.out.println("[LOSS] "+serie+" "+rival2+" || "+results.size()+" || "+resStr);	
				}
			}
		}
		System.out.println(serie+" *** "+total);
	}
	totalBets = totalWins + totalLosses;
	System.out.println(cases+" "+wins*100.0/cases+" || "+totalBets +" "+PrintUtils.Print2dec(totalWins*100.0/totalBets, false));
}	
	
private static void doEvaluateMatchesSeq(ArrayList<Match> matches,String atest,int period,double thr,int debug) {
		
		
		HashMap<String,ArrayList<Integer>> teamsR = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,Double> teams$ = new HashMap<String,Double>();
		HashMap<String,ApuestasStats> stats = new HashMap<String,ApuestasStats>();
		
		double profit$ = 0;
		double loss$=0;
		int totalBets = 0;
		double maxBalance = 10000;
		double balance = 10000;
		double stakeBase = 20.0;
		double risk = 0.5;
		double maxDD = 0.0;
		
		String testTeam =atest;
		String lastTeam ="";
		int wins = 0;
		int losses = 0;
		double winsS = 0;
		double lossesS = 0;
		int totalSeq = 0;
		double wins$ = 0;
		double losses$=0;
		boolean endSeq = false;
		for (int i=0;i<matches.size()-1;i++){
			Match m = matches.get(i);
			String homeTeam = m.getHomeTeam();
	        String awayTeam = m.getAwayTeam();
	        int homeGoals	= m.getHomeGoals();
	        int awayGoals	= m.getAwayGoals();
	        double homeOdds = m.getHomeOdds();
	        double awayOdds = m.getAwayOdds();
	        boolean print = false;
	        if (homeTeam.equalsIgnoreCase(testTeam)){
	        	if (!awayTeam.equalsIgnoreCase(lastTeam)){	 
	        		int trades = wins+losses;
	        		if (wins+losses>0 && wins==0){
	        			//System.out.println(testTeam+" "+lastTeam+" || "+wins+" / "+trades+" || "+PrintUtils.Print2dec(winsS-lossesS, false));
	        			//System.out.println("0");
	        		}else{
	        			//System.out.println("1");
	        		}
	        		if (wins==0){
	        			losses$ +=lossesS;
	        		}
	        		wins = 0;
	        		losses = 0;
	        		winsS = 0;
	        		lossesS = 0;
	        		lastTeam = awayTeam;
	        		totalSeq++;

	        		endSeq = false;
	        		
	        		//System.out.println("[NEW SEQ] "+testTeam+" "+lastTeam);
	        	}
	        	double stake = lossesS/(homeOdds-1.0)+1.0;
	        	if (homeGoals>awayGoals){
		        		winsS += stake*(homeOdds-1.0);	        		
				    	wins++;
				    	//se cortaria aqui la secuencia
				    	if (!endSeq){
					    	wins$ += (winsS-lossesS);
					    	endSeq = true;
				    	}

			    	//System.out.println("[WIN H ]  "+PrintUtils.Print2dec(winsS, false)+"  "+PrintUtils.Print2dec(lossesS, false)+" || "+lastTeam);
			    }else{
			    	lossesS+=stake;
			    	losses++;
			    }
	        }else if (awayTeam.equalsIgnoreCase(testTeam)){
	        	if (!homeTeam.equalsIgnoreCase(lastTeam)){
	        		int trades = wins+losses;
	        		if (wins+losses>0 && wins==0){
	        			//System.out.println(testTeam+" "+lastTeam+" || "+wins+" / "+trades+" || "+PrintUtils.Print2dec(winsS-lossesS, false));
	        			//System.out.println("0");
	        		}else{
	        			//System.out.println("1");
	        		}
	        		if (wins==0){
	        			losses$ +=lossesS;
	        		}
	        		wins = 0;
	        		losses = 0;
	        		winsS = 0;
	        		lossesS = 0;
	        		lastTeam = homeTeam;
	        		totalSeq++;
	        		endSeq = false;
	        		//System.out.println("[NEW SEQ] "+testTeam+" "+lastTeam);
	        	}
	        	double stake = lossesS/(awayOdds-1.0)+1.0;
	        	if (homeGoals<awayGoals){
	        		winsS += stake*(awayOdds-1.0);	
			    	wins++;
			    	
			    	if (!endSeq){
				    	wins$ += (winsS-lossesS);
				    	endSeq = true;
			    	}

			    	//System.out.println("[WIN A]  "+PrintUtils.Print2dec(winsS, false)+"  "+PrintUtils.Print2dec(lossesS, false)+" || "+lastTeam);
			    }else{
			    	lossesS+=stake;
			    	losses++;
			    }
	        }
	        	        
	        //anotamos resultado
	        int res = 0;
			int resA = 1;
		    if (homeGoals>awayGoals){
		    	res = 1;
		    	resA = 0;
		    }
		    
		    //miramos la racha de uno y otro
		    ArrayList<Integer> homer = null;
		    ArrayList<Integer> awayr = null;
		    
		    if (teamsR.containsKey(homeTeam)
		    		&& teamsR.containsKey(awayTeam)
		    		){
		    	homer = teamsR.get(homeTeam);
		    	awayr = teamsR.get(awayTeam);
		    	
		    	ApuestasStats asHome = stats.get(homeTeam);
		    	
	    	
		    }else{
		    	 if (!teamsR.containsKey(homeTeam)){
		    		 teamsR.put(homeTeam, new ArrayList<Integer>());
		    		 teams$.put(homeTeam,0.0);
		    		 stats.put(homeTeam, new ApuestasStats(homeTeam));
		    	 }
		    	 if (!teamsR.containsKey(awayTeam)){
		    		 teamsR.put(awayTeam, new ArrayList<Integer>());
		    		 teams$.put(awayTeam,0.0);
		    		 stats.put(awayTeam, new ApuestasStats(awayTeam));
		    	 }
		    }
		    
		    //añadimos resultado
		    teamsR.get(homeTeam).add(res);
	    	teamsR.get(awayTeam).add(resA);
	    	
	    	if (balance>=maxBalance){
	    		maxBalance = balance;
	    	}else{
	    		double dd = (maxBalance-balance)*100.0/balance;
	    		if (dd>=maxDD) maxDD = dd;
	    	}
		}
		
		System.out.println(atest+" || "+totalSeq+" "+PrintUtils.Print2dec(wins$*1.0/losses$, false));
		
		//exponemos resultados de cada equipo
		/*double pf = 0;
		Iterator it = stats.entrySet().iterator();
		while (it.hasNext()) {		    	
			  Map.Entry pair = (Map.Entry)it.next();
			  ApuestasStats as = (ApuestasStats) pair.getValue();
			  if (as.getBets()>0 
					 // && as.getPercent()<20.0
					  && as.getAvgOdds()<2.5
					  ){
				  pf += as.getProfit$();
				//System.out.println(as.toString());
			  }
		}
		
		/System.out.println(thr+" "+PrintUtils.Print2Int(period, 3)
			+" || PF= "+PrintUtils.Print2dec(profit$*1.0/(loss$*1), false)
			+" || BAL= "+PrintUtils.Print2dec(balance, false)
			+" || DD= "+PrintUtils.Print2dec(maxDD, false)
			+" || "+totalBets
			+" ||| "+PrintUtils.Print2dec(pf, false)
		);*/
	}	
	
private static void doEvaluateMatches(ArrayList<Match> matches,int period,double thr,int debug) {
		
		
		HashMap<String,ArrayList<Integer>> teamsR = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,Double> teams$ = new HashMap<String,Double>();
		HashMap<String,ApuestasStats> stats = new HashMap<String,ApuestasStats>();
		
		double profit$ = 0;
		double loss$=0;
		int totalBets = 0;
		double maxBalance = 10000;
		double balance = 10000;
		double stakeBase = 20.0;
		double risk = 0.5;
		double maxDD = 0.0;
		for (int i=0;i<matches.size()-1;i++){
			Match m = matches.get(i);
			String homeTeam = m.getHomeTeam();
	        String awayTeam = m.getAwayTeam();
	        int homeGoals	= m.getHomeGoals();
	        int awayGoals	= m.getAwayGoals();
	        double homeOdds = m.getHomeOdds();
	        double awayOdds = m.getAwayOdds();
	        
	        //anotamos resultado
	        int res = 0;
			int resA = 1;
		    if (homeGoals>awayGoals){
		    	res = 1;
		    	resA = 0;
		    }
		    
		    //miramos la racha de uno y otro
		    ArrayList<Integer> homer = null;
		    ArrayList<Integer> awayr = null;
		    
		    if (teamsR.containsKey(homeTeam)
		    		&& teamsR.containsKey(awayTeam)
		    		){
		    	homer = teamsR.get(homeTeam);
		    	awayr = teamsR.get(awayTeam);
		    	
		    	ApuestasStats asHome = stats.get(homeTeam);
		    	ApuestasStats asAway= stats.get(awayTeam);
		    	
		    	if (homer.size()>=period && awayr.size()>=period){
		    		double stakeH = 0;
		    		double stakeA = 0;
		    		boolean cond1 = homer.get(homer.size()-1)==0
		    				&& homer.get(homer.size()-2)==0
		    				&& homer.get(homer.size()-3)==0
		    				&& homer.get(homer.size()-4)==0;
		    		boolean cond2 =//awayr.get(awayr.size()-1)==1 && awayr.get(awayr.size()-2)==1 && awayr.get(awayr.size()-3)==1 && awayr.get(awayr.size()-4)==1
		    				homer.get(homer.size()-1)==0
		    				&& homer.get(homer.size()-2)==0
		    				//&& homer.get(homer.size()-3)==0
		    				//&& homer.get(homer.size()-4)==0
		    				;
		    		if (true &&
		    				cond1
		    				){
		    			double home$ = teams$.get(homeTeam);
		    			stakeH = (home$/(homeOdds-1.0))+1;
		    			if (stakeH==0) stakeH = 1;
		    			if (stakeH>10) stakeH = 1;
		    			 double per = getPercent(homer,homer.size()-1-period,homer.size()-1);
		    			if (per<thr) stakeH = 0;
		    		}
		    		if (awayr.get(awayr.size()-1)==0
		    				&& awayr.get(awayr.size()-2)==0
		    				&& awayr.get(awayr.size()-3)==0
		    				&& awayr.get(awayr.size()-4)==0
		    				&& awayr.get(awayr.size()-5)==0
		    				){
		    			double away$ = teams$.get(awayTeam);
		    			stakeA = (int) (away$/(awayOdds-1.0));
		    			if (stakeA==0) stakeA = 1;
		    			if (stakeA>10) stakeA = 1;
		    			 double per = getPercent(awayr,awayr.size()-1-period,awayr.size()-1);
		    			if (per<thr) stakeA = 0;
		    			//if (awayOdds<1.2) stakeH = 0;
		    		}
		    		
		    	    if (stakeH>0 || stakeA>0){		    		
			    		if (true
			    				//&& homeOdds<awayOdds
			    				&& stakeH>=stakeA
			    				){
			    			
			    			totalBets++;
			    			if (res==1){
			    				
			    				if (debug==1)
			    				System.out.println(i+" || "+homeTeam+" "+awayTeam+" || home@ "+stakeH+" "+PrintUtils.Print2dec(teams$.get(homeTeam), false)
			    				+" || "+PrintUtils.Print2dec(homeOdds, false)
		    					+" || "+PrintUtils.Print2dec(stakeH*(homeOdds-1.0), false)
		    					);
			    				teams$.put(homeTeam,0.0);
			    				profit$ += stakeH*(homeOdds-1.0)*stakeBase;
			    				
			    				balance += stakeH*(homeOdds-1.0)*stakeBase;
			    				
			    				asHome.addResult(stakeH*(homeOdds-1.0),homeOdds);
			    				
			    				if (debug==1)	
			    				System.out.println(PrintUtils.Print2dec(profit$, false)+" || "+PrintUtils.Print2dec(loss$, false));
			    			}else{
			    				if (debug==1)
			    				System.out.println(i+" || "+homeTeam+" "+awayTeam+" || home@ "+stakeH+" "+PrintUtils.Print2dec(teams$.get(homeTeam), false)
			    				+" || "+PrintUtils.Print2dec(homeOdds, false)
		    					+" || "+PrintUtils.Print2dec(- stakeH*(1.0), false)
		    					);
			    				double home$ = teams$.get(homeTeam);
			    				teams$.put(homeTeam,home$+stakeH*1.0);
			    				loss$ += stakeBase*stakeH;
			    				
			    				balance -= stakeBase*stakeH;
			    				
			    				asHome.addResult(-stakeH*(1.0),homeOdds);
			    				if (debug==1)
			    				System.out.println(PrintUtils.Print2dec(profit$, false)+" || "+PrintUtils.Print2dec(loss$, false));
			    			}
			    		}else{
			    			if (debug==1)
			    			System.out.println(i+" || "+homeTeam+" "+awayTeam+" || away@ "+stakeA+" "+PrintUtils.Print2dec(teams$.get(awayTeam), false)+" || "+PrintUtils.Print2dec(awayOdds, false));
			    			totalBets++;
			    			if (resA==1){
			    				teams$.put(awayTeam,0.0);
			    				profit$ += stakeA*(awayOdds-1.0)*stakeBase;
			    				
			    				balance += stakeA*(awayOdds-1.0)*stakeBase;
			    				
			    				if (debug==1)
			    				System.out.println(PrintUtils.Print2dec(profit$, false)+" || "+PrintUtils.Print2dec(loss$, false));
			    				
			    				asAway.addResult(stakeA*(awayOdds-1.0),awayOdds);
			    			}else{
			    				if (debug==1)
			    				System.out.println(i+" || "+homeTeam+" "+awayTeam+" || away@ "+stakeH+" "+PrintUtils.Print2dec(teams$.get(awayTeam), false)
			    				+" || "+PrintUtils.Print2dec(awayOdds, false)
		    					+" || "+PrintUtils.Print2dec(- stakeA*(1.0), false)
		    					);
			    				double away$ = teams$.get(awayTeam);
			    				teams$.put(awayTeam,away$+stakeA*1.0);
			    				loss$ += stakeBase*stakeA;
			    				
			    				balance -= stakeBase*stakeA;
			    				
			    				if (debug==1)
			    				System.out.println(PrintUtils.Print2dec(profit$, false)+" || "+PrintUtils.Print2dec(loss$, false));
			    				
			    				asAway.addResult(-stakeA*(1.0),awayOdds);
			    			}
			    		}
		    	    }
		    	}		    	
		    }else{
		    	 if (!teamsR.containsKey(homeTeam)){
		    		 teamsR.put(homeTeam, new ArrayList<Integer>());
		    		 teams$.put(homeTeam,0.0);
		    		 stats.put(homeTeam, new ApuestasStats(homeTeam));
		    	 }
		    	 if (!teamsR.containsKey(awayTeam)){
		    		 teamsR.put(awayTeam, new ArrayList<Integer>());
		    		 teams$.put(awayTeam,0.0);
		    		 stats.put(awayTeam, new ApuestasStats(awayTeam));
		    	 }
		    }
		    
		    //añadimos resultado
		    teamsR.get(homeTeam).add(res);
	    	teamsR.get(awayTeam).add(resA);
	    	
	    	if (balance>=maxBalance){
	    		maxBalance = balance;
	    	}else{
	    		double dd = (maxBalance-balance)*100.0/balance;
	    		if (dd>=maxDD) maxDD = dd;
	    	}
		}
		
		//exponemos resultados de cada equipo
		double pf = 0;
		Iterator it = stats.entrySet().iterator();
		while (it.hasNext()) {		    	
			  Map.Entry pair = (Map.Entry)it.next();
			  ApuestasStats as = (ApuestasStats) pair.getValue();
			  if (as.getBets()>0 
					 // && as.getPercent()<20.0
					  && as.getAvgOdds()<2.5
					  ){
				  pf += as.getProfit$();
				//System.out.println(as.toString());
			  }
		}
		
		System.out.println(thr+" "+PrintUtils.Print2Int(period, 3)
			+" || PF= "+PrintUtils.Print2dec(profit$*1.0/(loss$*1), false)
			+" || BAL= "+PrintUtils.Print2dec(balance, false)
			+" || DD= "+PrintUtils.Print2dec(maxDD, false)
			+" || "+totalBets
			+" ||| "+PrintUtils.Print2dec(pf, false)
		);
	}

	
	
	

	private static int getLossesStreak(ArrayList<Integer> arr) {

		int currentStreak = 0;
		for (int i=arr.size()-1;i>=0;i--){
			int res = arr.get(i);
			if (res<=0) currentStreak++;
			else break;
		}
		return currentStreak;
	}

	private static double getPercent(ArrayList<Integer> arr,int begin,int end) {
		// TODO Auto-generated method stub
		
		if (begin<=0) begin = 0;
		int total = 0;
		int wins = 0;
		for (int i=begin;i<=end;i++){
			int res = arr.get(i);
			if (res==1) wins++;
			total++;
		}
		if (total==0) return 0;
		return wins*100.0/total;
	}
	
	private static double getPercentG(ArrayList<Integer> arr,int begin,int end) {
		// TODO Auto-generated method stub
		
		if (begin<=0) begin = 0;
		int total = 0;
		int acc = 0;
		for (int i=begin;i<=end;i++){
			int res = arr.get(i);
			acc +=res;
			total++;
		}
		if (total==0) return 0;
		return acc*1.0/total;
	}
	
public static void getMatches(String folderStr,int y1,int y2,ArrayList<MatchInda> matches) throws IOException {
	for (int year=y2;year>=y1;year--){
		File folder = new File(folderStr);
		BufferedReader reader;
		int totalm = 0;
		double accH = 0;
		int homeWins = 0;
		double winsBet = 0;
		double lostBet = 0;
		String lastLine = "";
		for (final File fileEntry : folder.listFiles()) {
			String fileName = fileEntry.getName();
			//System.out.println(fileEntry.getAbsolutePath());
			
			if (!fileName.contains(year+"o.csv")) continue;	
			
			int i=0;
			 int lastMatchLine = -1;
		        int homeGoals = -1;
		        int awayGoals = -1;
			String homeTeam = "";
	        String awayTeam = "";
	        double homeOdds = 0.0;
	        double drawOdds = 0.0;
	        double awayOdds = 0.0;
	        int bets=0;
			reader = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
			String line = reader.readLine();

			while (line!=null){
				 if (line.contains(String.valueOf(year)) || line.contains(String.valueOf(year+1))){
				    	String[] values = line.split(" ");
					 	//System.out.println("Fecha: "+line+" || "+values[0]+" "+values[1]+" "+values[2]);					    	
				   }
				 
				 if (line.contains(":") 
						 //&& !lastLine.contains("Offs")
						 ){	        				
     				int index = line.lastIndexOf(":");
     				if (index>=5){
     					//System.out.println(line);
     					homeTeam = line.substring(5, index-3).split("-")[0].trim().substring(0, 4);
     					
    					awayTeam = line.substring(5, index-3).split("-")[1].trim().substring(0, 4);
    					
    					homeGoals = Integer.valueOf(line.substring(index-3, index).trim());
    					awayGoals = Integer.valueOf(line.substring(index+1, index+4).trim());
    					lastMatchLine = i;    	
     				} 
				 }
 				if (lastMatchLine>=0){
    				if (i==lastMatchLine+1
    						&& !line.trim().equalsIgnoreCase("-")
    						){
    					homeOdds = Double.valueOf(line.trim());	    					
    				}
    				//if (i==lastMatchLine+2) drawOdds = Double.valueOf(line.trim());
    				if (i==lastMatchLine+2
    						&& !line.trim().equalsIgnoreCase("-")
    						){
    					awayOdds = Double.valueOf(line.trim());
    					
    					MatchInda m = new MatchInda();
    					m.setHomeTeam(homeTeam);
    					m.setAwayTeam(awayTeam);
    					m.setAwayGoals(awayGoals);
    					m.setHomeGoals(homeGoals);
    					m.setHomeOdds1(homeOdds);
    					m.setAwayOdds1(awayOdds);
    					m.setYear(Integer.valueOf(year));
    					
    					matches.add(0,m);    	
    					
    					
    					
    					//System.out.println(m.getHomeTeam());
    				}
 				}
				
				 i++;
				 lastLine = line;
				line = reader.readLine();
			}

			reader.close();
		}
	}
}

	
public static void test(String folderStr,int year, int nbars, double thr) throws IOException {
		
		HashMap<String,ArrayList<Integer>> teamsH = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Double>> teamsHo = new HashMap<String,ArrayList<Double>>();
		HashMap<String,ArrayList<Integer>> teamsA = new HashMap<String,ArrayList<Integer>>();
		HashMap<String,ArrayList<Double>> teamsAo = new HashMap<String,ArrayList<Double>>();
		
		ArrayList<Match> matches = new ArrayList<Match>();
		
		for (year=2013;year>=2013;year--){		
			File folder = new File(folderStr);
			BufferedReader reader;
			int totalm = 0;
			double accH = 0;
			int homeWins = 0;
			double winsBet = 0;
			double lostBet = 0;
			String lastLine = "";
			for (final File fileEntry : folder.listFiles()) {
				String fileName = fileEntry.getName();
				//System.out.println(fileEntry.getAbsolutePath());
				
				if (!fileName.contains(year+"o.csv")) continue;	
				
				int i=0;
				 int lastMatchLine = -1;
			        int homeGoals = -1;
			        int awayGoals = -1;
				String homeTeam = "";
		        String awayTeam = "";
		        double homeOdds = 0.0;
		        double drawOdds = 0.0;
		        double awayOdds = 0.0;
		        int bets=0;
				reader = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
				String line = reader.readLine();
	
				while (line!=null){
					 if (line.contains(String.valueOf(year)) || line.contains(String.valueOf(year+1))){
					    	String[] values = line.split(" ");
						 	//System.out.println("Fecha: "+line+" || "+values[0]+" "+values[1]+" "+values[2]);
					    	
					    }
					 
					 if (line.contains(":") 
							 //&& !lastLine.contains("Offs")
							 ){	        				
	     				int index = line.lastIndexOf(":");
	     				if (index>=5){
	     					//System.out.println(line);
	     					homeTeam = line.substring(5, index-3).split("-")[0].trim().substring(0, 4);
	     					
	    					awayTeam = line.substring(5, index-3).split("-")[1].trim().substring(0, 4);
	    					
	    					homeGoals = Integer.valueOf(line.substring(index-2, index).trim());
	    					awayGoals = Integer.valueOf(line.substring(index+1, index+3).trim());
	    					lastMatchLine = i;    	
	     				} 
					 }
	 				if (lastMatchLine>=0){
	    				if (i==lastMatchLine+1
	    						&& !line.trim().equalsIgnoreCase("-")
	    						){
	    					homeOdds = Double.valueOf(line.trim());	    					
	    				}
	    				//if (i==lastMatchLine+2) drawOdds = Double.valueOf(line.trim());
	    				if (i==lastMatchLine+2
	    						&& !line.trim().equalsIgnoreCase("-")
	    						){
	    					awayOdds = Double.valueOf(line.trim());
	    					
	    					Match m = new Match();
	    					m.setHomeTeam(homeTeam);
	    					m.setAwayTeam(awayTeam);
	    					m.setAwayGoals(awayGoals);
	    					m.setHomeGoals(homeGoals);
	    					m.setHomeOdds(homeOdds);
	    					m.setAwayOdds(awayOdds);
	    					
	    					matches.add(0,m);
	    					
	    					/*match = new Match();
	    					match.setHomeGoals(homeGoals);
	    					match.setAwayGoals(awayGoals);
	    					match.setHomeOdds(homeOdds);
	    					match.setDrawOdds(drawOdds);
	    					match.setAwayOdds(awayOdds);
	    					match.setHomeTeam(homeTeam.trim());
	    					match.setAwayTeam(awayTeam.trim());
	    					matches.add(match); */     
	    					
	    					//System.out.println(line+" || "+homeTeam+" | "+awayTeam+" | "+homeGoals+" | "+awayGoals+" | "+homeOdds+" | "+awayOdds);
	    					
	    					int res = 0;
	    					int resA = 1;
	    				    if (homeGoals>awayGoals){
	    				    	res = 1;
	    				    	resA = 0;
	    				    }
	    				    
	    					if (!teamsH.containsKey(homeTeam)){
	    						teamsH.put(homeTeam, new ArrayList<Integer>());
	    						teamsHo.put(homeTeam, new ArrayList<Double>());
	    					}
	    					//lo metemos al principio por que el archivo empieza de mas nuevo a mas viejo
	    					teamsH.get(homeTeam).add(0,res);
	    					teamsHo.get(homeTeam).add(0,homeOdds);
	    					
	    					if (!teamsA.containsKey(awayTeam)){
	    						teamsA.put(awayTeam, new ArrayList<Integer>());
	    					}
	    					//lo metemos al principio por que el archivo empieza de mas nuevo a mas viejo
	    					teamsA.get(awayTeam).add(0,resA);
	    					
	    					boolean isBet = false;
	    					/*if (homeOdds>=15.0){
	    						isBet = true;
	    						bets++;
	    					}
	    					
	    					if (res==1){
	    						homeWins++;
	    						if (isBet){
	    							winsBet += homeOdds-1.0;    
	    						}
	    					}else{
	    						if (isBet)
	    							lostBet += 1;
	    					}*/
	    					totalm++;
	    					accH+=homeOdds;
	    				}
	 				}
					
					 i++;
					 lastLine = line;
					line = reader.readLine();
				}
	
				reader.close();
			}
		}
		
		HashMap<String,ArrayList<Match>> teamsDict = new HashMap<String,ArrayList<Match>>(); 
		NBAoddsPortal.studySeries(matches);
		
		//NBAoddsPortal.getTeamMatchs(matches, teamsDict);
		//NBAoddsPortal.doAnalyze2(teamsDict,0);
	}

	public static void main(String[] args) throws IOException {
		String folderStr ="c:\\nba";
		
		for (int y=2018;y<=2018;y++)
		for (int nbars=100;nbars<=100;nbars+=1){
			for (double thr=10.0;thr<=10.0;thr+=10.0){
				NBAoddsPortal.test(folderStr,y,nbars,thr);
			}
		}

	}
	
	
}
