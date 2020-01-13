package drosa.experimental.basicStrategies.strats2019;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import drosa.classes.Tick;
import drosa.experimental.PositionShort;
import drosa.experimental.ticks.TickUtils;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;

public class FutureESticks {
	
	private static void doStudy(String header,HashMap<Integer, ArrayList<Integer>> trends, int isHigh,int tp) {
		// TODO Auto-generated method stub
		
		int cases = 0;
		int acc = 0;
		for (Integer key : trends.keySet()) {
			
			ArrayList<Integer> arr = trends.get(key);
			if (arr.size()>1) {
				cases++;
				for (int i=0;i<=0;i++) {
					int size = arr.get(i);
					if (isHigh==1) {
						if (size<0) {					
							acc+=0;
							break;
						}else {
							cases++;
							acc+=size;
						}
					}else {
						if (size>0) {					
							acc+=0;
							break;
						}else {
							cases++;
							acc+=-size;
						}
					}
				}
			}
		}
		double avg = acc*1.0/cases; 
		System.out.println(
				header
				+" || "+cases+" "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(tp*1.0/avg, false)
				);
	}

	
	private static void doTest2(
			ArrayList<Tick> ticks, 
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			StratPerformance sp
		) {
		
		int lastDay = -1;
		int high = -1;
		int low = -1;
		int lastH = -1;
		int lastL = -1;
		int mode = 0;
		int index1 = -1;
		int index2 = -1;
		int isBid = 0;
		int currentDay=-1;
		int trade = 0;
		int entry = 0;
		int wins = 0;
		int losses = 0;
		int tpvalue = 0;
		int slvalue = 0;
		int winPips = 0;
		int lostPips = 0;
		int dayTrade = 0;
		ArrayList<Integer> dTrends = new ArrayList<Integer>();
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		for (int i=0;i<ticks.size();i++) {
			Tick t = ticks.get(i);
			int h = t.getHour();
			int day = t.getDay();
			currentDay = t.getYear()*365+t.getMonth()*30+day;
			if (day!=lastDay) {				
				if (lastDay>=1) {					
					lastH = high;
					lastL = low;
				}
				index1 = -1;
				index2 = -1;
				mode = 0;
				dTrends.clear();
				high = -1;
				low = -1;
				dayTrade = 0;
				//entry = 0;
				//trade = 0;
				lastDay = day;
			}
		
			int diffH = (t.getAsk()-lastH)/25;
			int diffL = (lastL-t.getBid())/25;
			
			if (h>=h1 && h<=h2) {
				if (diffH>=thr) {
					PositionShort p = new PositionShort();
					p.setEntry(t.getBid());
					p.setTp(p.getEntry()-tp*25);
					p.setSl(p.getEntry()+sl*25);
					p.setPositionType(PositionType.SHORT);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOrder(dayTrade);
					positions.add(p);
					dayTrade++;
				}else if (diffL>=thr) {
					PositionShort p = new PositionShort();
					p.setEntry(t.getAsk());
					p.setTp(p.getEntry()+tp*25);
					p.setSl(p.getEntry()-sl*25);
					p.setPositionType(PositionType.LONG);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOrder(dayTrade);
					positions.add(p);
					dayTrade++;
				}
			}
			
			
			int j=0;
			while(j<positions.size()) {
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN) {
					int pips = 0;
					boolean isClosed = false;
					if (p.getPositionType()==PositionType.SHORT) {
						if (t.getAsk()>=p.getSl()) {
							pips = -sl;
							isClosed = true;
						}else if (t.getAsk()<=p.getTp()){
							pips = tp;
							isClosed = true;
						}
					}else if (p.getPositionType()==PositionType.LONG) {
						if (t.getBid()<=p.getSl()) {
							pips = -sl;
							isClosed = true;
						}else if (t.getBid()>=p.getTp()){
							pips = tp;
							isClosed = true;
						}
					}
					
					if (isClosed) {
						if (p.getOrder()==0) {
							if (pips>=0) {
								wins++;
								winPips += pips;
								sp.addTrade(1,pips, 0, 0, 0);
							}else {
								losses++;
								lostPips += -pips;
								sp.addTrade(1,pips, 0, 0, 0);
							}
						}
						positions.remove(j);
					}else {
						j++;
					}
				}
			}
			
			if (high==-1 || t.getBid()>=high) high = t.getBid();
			if (low==-1 || t.getAsk()<=low) low = t.getAsk();		
		}
		
		//daysTracked
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		/*System.out.println(
				tp+" "+entryThr
				+" || "+totalDays
				+" || "+trades+" "+PrintUtils.Print2dec(winPer, false)
				);*/
	}
	
	
	private static void doTest(
			ArrayList<Tick> ticks, 
			int tp,
			int entryThr,
			HashMap<Integer,ArrayList<Integer>> trends,
			StratPerformance sp
		) {
		
		int lastDay = -1;
		int high = -1;
		int low = -1;
		int lastH = -1;
		int lastL = -1;
		int mode = 0;
		int index1 = -1;
		int index2 = -1;
		int isBid = 0;
		int currentDay=-1;
		int trade = 0;
		int entry = 0;
		int wins = 0;
		int losses = 0;
		int tpvalue = 0;
		int slvalue = 0;
		ArrayList<Integer> dTrends = new ArrayList<Integer>();
		for (int i=0;i<ticks.size();i++) {
			Tick t = ticks.get(i);
			int h = t.getHour();
			int day = t.getDay();
			currentDay = t.getYear()*365+t.getMonth()*30+day;
			if (day!=lastDay) {
				
				if (lastDay>=1) {
					
					trends.put(currentDay,new ArrayList<Integer>());
					for (int j=0;j<dTrends.size();j++) trends.get(currentDay).add(dTrends.get(j));
					
					lastH = high;
					lastL = low;
				}
				index1 = -1;
				index2 = -1;
				mode = 0;
				dTrends.clear();
				high = -1;
				low = -1;
				//entry = 0;
				//trade = 0;
				lastDay = day;
			}
			
			if (entry>0) {
				if (trade==-1) {
					int profit = (entry-t.getAsk())/25;
					if (t.getAsk()<=tpvalue) {
						wins++;
						entry=0;
						trade = 0;
						sp.addTrade(1, tp,5, 5, 0);
					}else if (t.getAsk()>=slvalue) {
						losses++;
						entry=0;
						trade = 0;
						sp.addTrade(1,-3*tp,5, 5, 0);
					}
				}
			}
			
			if (lastH>=0 && lastL>=0 && entry==0) {
				if (mode==0) {
					if (index1==-1) {
						if (t.getAsk()>=lastH) {
							index1 = i;
						}else if (t.getAsk()<=lastL) {
							//index1 = i;
						}
					}else{
						int diffH = (t.getAsk()-ticks.get(index1).getAsk())/25;
						int diffL = (ticks.get(index1).getAsk()-t.getAsk())/25;
						
						if (diffH>=tp) {
							mode = 1;
							index2 = i;
						}else if (diffL>=tp) {
							mode = -1;
							index2 = i;
						}
					}
				}
							
				if (mode==1) {
					if (t.getAsk()>ticks.get(index2).getAsk()){
						index2 = i;
						
						int currentTrend = (t.getAsk()-ticks.get(index1).getAsk())/25;
						if (currentTrend>=entryThr) {
							if (entry==0
									//&& h<=9
									) {
								trade = -1;
								entry = t.getBid();
								tpvalue = entry-tp*25;
								slvalue = entry+3*tp*25;
							}
						}
					}else {
						int diffL = (ticks.get(index2).getAsk()-t.getAsk())/25;
						if (diffL>=tp) {
							int sizet = (ticks.get(index2).getAsk()-ticks.get(index1).getAsk())/25;
							dTrends.add(sizet);
							
							index1 = index2;
							index2 = i;
							mode=-1;
						}
					}					
				}else if (mode==-1) {
					if (t.getAsk()<ticks.get(index2).getAsk()){
						index2 = i;
					}else {
						int diffH = (t.getAsk()-ticks.get(index2).getAsk())/25;
						if (diffH>=tp) {
							int sizet = (ticks.get(index1).getAsk()-ticks.get(index2).getAsk())/25;
							dTrends.add(-sizet);
							
							index1 = index2;
							index2 = i;
							mode=1;
						}
					}		
				}
			}
			
			
			if (high==-1 || t.getBid()>=high) high = t.getBid();
			if (low==-1 || t.getAsk()<=low) low = t.getAsk();		
		}
		
		//daysTracked
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		int totalDays = trends.size();
		/*System.out.println(
				tp+" "+entryThr
				+" || "+totalDays
				+" || "+trades+" "+PrintUtils.Print2dec(winPer, false)
				);*/
	}

	public static void main(String[] args) throws FileNotFoundException {
		String fileName = "C:\\fxdata\\futuros\\ES20191028\\ES.txt";
		
		
		
		/*for (int y1=2016;y1<=2019;y1++) {
			int y2 = y1;
			for (int m1=0;m1<=11;m1++) {
				int m2 = m1;

				ArrayList<Tick> ticks = TickUtils.readFastTicksKibot(fileName, y1, y2,m1,m2,true);
				System.out.println("total ticks: "+ticks.size());
				
				try(  PrintWriter out = new PrintWriter(fileName+"_"+y1+"_"+y2+" "+m1+"_ticks.txt")  ){
					for (int d=0;d<ticks.size();d++){
						out.println(ticks.get(d).toString2());
					}
					out.close();
				}
			}
		}*/
		
		ArrayList<Tick> ticks = null;
		
		for (int h1=0;h1<=0;h1++) {
			int h2 = h1+23;
			for (int tp=5;tp<=10;tp++) {
				for (int sl=3*tp;sl<=15*tp;sl+=1*tp) {
					for (int entryThr=0;entryThr<=30;entryThr+=5) {
						StratPerformance sp = new StratPerformance();
						for (int y1=2019;y1<=2019;y1++) {
							int y2 = y1;
							for (int m1=0;m1<=11;m1++) {
								int m2 = m1;
								String fileName2 = fileName+"_"+y1+"_"+y2+" "+m1+"_ticks.txt";				
								ticks = TickUtils.readTicksDaveMinutes(fileName2,y1,y2,2);	
								FutureESticks.doTest2(ticks,h1,h2,entryThr,tp,sl,sp);
								
							}
						}
						String header = tp+" "+sl+" "+entryThr;
						System.out.println(header+" || "+sp.toString());
					}
				}
			}
		}
	}



}
