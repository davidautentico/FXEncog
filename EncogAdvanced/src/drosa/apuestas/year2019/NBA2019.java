package drosa.apuestas.year2019;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import drosa.utils.PrintUtils;

public class NBA2019 {

	public static void main(String[] args) throws FileNotFoundException {
		String folderStr ="c:\\nba";
		
		for (int nbars=30;nbars<=30;nbars++){
			for (double thr=0.0;thr<=100.0;thr+=5){
				NBA2019.testHome(folderStr,nbars,thr);
			}
		}
	}
	
	public static void testHome(
			String folderStr,
			int nbars,
			double thr			
			) throws FileNotFoundException{
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>();
		
		int totalmatches = 0;
		int totalwinsH = 0;
		
		int totalmatchesP = 0;
		int totalwinsHP = 0;
		
		File folder = new File(folderStr);
		
		for (final File fileEntry : folder.listFiles()) {
			String fileName = fileEntry.getName();
			Scanner input = new Scanner(fileEntry);

			while(input.hasNext()) {
			    String nextToken = input.next();
			    //or to process line by line
			    String nextLine = input.nextLine();
			    if (nextLine.contains("Notes")) continue;
			    if (nextLine.trim().length()==0) continue;
			   // System.out.println(nextLine);
			    String[] values = nextLine.split(",");
			   
			    try{
				    String teamH = values[4];
				    String teamV = values[2];
				    int teamHP = Integer.valueOf(values[5]);
				    int teamVP = Integer.valueOf(values[3]);
				    
				    int res = 0;
				    if (teamHP>teamVP) res = 1;
				    if (!teams.containsKey(teamH)){
				    	teams.put(teamH,new ArrayList<>());
				    }
				    
				    double perWin = getPercent(teams.get(teamH),nbars);
				    
				    teams.get(teamH).add(res);
				    
				   // System.out.println(teamH+" - "+teamV+" "+teamHP+"-"+teamVP);
				    
				    if (teamHP>teamVP) totalwinsH++;
				    totalmatches++;
				    				    				    
				    if (perWin>=thr && teams.get(teamH).size()>=nbars ){
				    	totalmatchesP++;
						if (res==1) totalwinsHP++;
				    }
			    }catch(Exception e){
			    	System.out.println("[ERROR] "+nextLine+" "+e.getMessage());
			    	break;
			    }		   		   
			}

			input.close();
	    }
		
		 System.out.println(
				 nbars+" "+PrintUtils.Print2dec(thr, false)
				 +" || "+totalmatches+" "+totalwinsH*100.0/totalmatches
				 +" || "+totalmatchesP+" "+totalwinsHP*100.0/totalmatchesP
				 );
		
	}

	private static void decodeFile(File dataFile) throws FileNotFoundException {
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>();
		
		int totalmatches = 0;
		int totalwinsH = 0;
		
		int totalmatchesP = 0;
		int totalwinsHP = 0;
		
		Scanner input = new Scanner(dataFile);

		while(input.hasNext()) {
		    String nextToken = input.next();
		    //or to process line by line
		    String nextLine = input.nextLine();
		    if (nextLine.contains("Notes")) continue;
		    if (nextLine.trim().length()==0) continue;
		   // System.out.println(nextLine);
		    String[] values = nextLine.split(",");
		   
		    try{
			    String teamH = values[4];
			    String teamV = values[2];
			    int teamHP = Integer.valueOf(values[5]);
			    int teamVP = Integer.valueOf(values[3]);
			    
			    int res = 0;
			    if (teamHP>teamVP) res = 1;
			    if (!teams.containsKey(teamH)){
			    	teams.put(teamH,new ArrayList<>());
			    }
			    
			    teams.get(teamH).add(res);
			    
			   // System.out.println(teamH+" - "+teamV+" "+teamHP+"-"+teamVP);
			    
			    if (teamHP>teamVP) totalwinsH++;
			    totalmatches++;
			    
			    double perWin = getPercent(teams.get(teamH),20);
			    
			    if (perWin>=80.0){
			    	totalmatchesP++;
					if (res==1) totalwinsHP++;
			    }
		    }catch(Exception e){
		    	System.out.println("[ERROR] "+nextLine+" "+e.getMessage());
		    	break;
		    }		   		   
		}

		input.close();
		
		 System.out.println(dataFile.getName()
				 +" || "+totalmatches+" "+totalwinsH*100.0/totalmatches
				 +" || "+totalmatchesP+" "+totalwinsHP*100.0/totalmatchesP
				 );
		
	}

	private static double getPercent(ArrayList<Integer> arr, int period) {
		// TODO Auto-generated method stub
		
		int begin = arr.size()-period;
		int end = arr.size()-1;
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

}
