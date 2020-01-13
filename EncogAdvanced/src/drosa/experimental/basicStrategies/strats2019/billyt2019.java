package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.basicStrategies.strats2018.TickStudy2019;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class billyt2019 {
	
	public static void dailyBreak(
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int tp,int sl,
			int nBack,int minDist
			){
		
		Calendar cal = Calendar.getInstance();
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int lastDay = -1;
		int month  = -1;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int lastHigh2 = -1;
		int lastLow2 = -1;
		int totalDays=0;
		int mode = 0;
		int wins=0;
		int losses= 0;
		int count = 0;
		int maxLosingStreak = 0;
		int actualLosses = 0;
		
		ArrayList<Integer> seq = new ArrayList<Integer>();
		for (int i=1;i<data.size()-1;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			month = cal.get(Calendar.MONTH);
			 			 
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
						
			if (day!=lastDay){				
				if (high!=-1){
					lastHigh2 = lastHigh;
					lastLow2 = lastLow;
					lastHigh = high;
					lastLow = low;
				}				
				high = -1;
				low = -1;
				lastDay = day;
				mode = 0;
				totalDays++;
			}
						
			if (mode==0){
				if (q.getOpen5()<=lastHigh && q.getHigh5()>=lastHigh 
						&& lastHigh!=-1
						&& lastHigh2!=-1
						//&& (lastHigh<=lastHigh2 && lastLow>=lastLow2)//IB
						&& q.getOpen5()-data.get(i-nBack).getClose5()>=minDist
						&& q.getOpen5()-data.get(i-nBack-12).getClose5()>=minDist
						&& q.getOpen5()-data.get(i-nBack-24).getClose5()>=minDist
						&& q.getOpen5()-data.get(i-nBack-36).getClose5()>=minDist
						//&& (lastHigh-lastHigh2)>=800
						){
					for (int j=i;j<data.size();j++){
						QuoteShort qj = data.get(j);
						if (qj.getHigh5()>=lastHigh+tp){
							wins++;
							count++;
							mode=3;
							actualLosses=0;
							seq.add(1);
							break;
						}else if (qj.getLow5()<=lastHigh-sl){
							losses++;
							count++;
							mode=3;
							actualLosses++;
							if (actualLosses>=maxLosingStreak) maxLosingStreak = actualLosses;
							seq.add(-1);
							break;
						}
					}
				}else if (q.getOpen5()>=lastLow && q.getLow5()<=lastLow 
						&& lastLow!=-1
						&& lastHigh2!=-1
						//&& (lastHigh<=lastHigh2 && lastLow>=lastLow2)//IB
						//&& (lastLow2-lastLow)>=800
						&& -q.getOpen5()+data.get(i-nBack).getClose5()>=minDist
						&& -q.getOpen5()+data.get(i-nBack-12).getClose5()>=minDist
						&& -q.getOpen5()+data.get(i-nBack-24).getClose5()>=minDist
						&& -q.getOpen5()+data.get(i-nBack-36).getClose5()>=minDist
						){
					for (int j=i;j<data.size();j++){
						QuoteShort qj = data.get(j);
						if (qj.getLow5()<=lastLow-tp){
							wins++;
							count++;
							mode=3;
							actualLosses=0;
							seq.add(1);
							break;
						}else if (qj.getHigh5()>=lastLow+sl){
							losses++;
							count++;
							mode=3;
							actualLosses++;
							if (actualLosses>=maxLosingStreak) maxLosingStreak = actualLosses;
							seq.add(-1);
							break;
						}
					}
				}
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		double size = 0;
		int racha=0;
		double winSizes=0;
		double lostSizes = 0;
		double mult = sl*1.0/tp;
		int trades = 0;
		int maxSize = 10;
		for (int i=0;i<seq.size();i++){
			int res = seq.get(i);
			if (res==1){
				winSizes+= tp;
			}else if (res==-1){
				lostSizes += sl;
			}
		}
		
		double pf = winSizes*1.0/lostSizes;
		
		double winPer = wins*100.0/count;
		System.out.println(tp
				+" "+sl+" "+nBack+" "+minDist
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+maxLosingStreak
				+" || "+PrintUtils.Print2dec(pf, false)
				+" "+trades+" "+winSizes+" "+lostSizes
				);
	}
	
	
	public static void dailyBreak2(
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int tp,int sl
			){
		
		Calendar cal = Calendar.getInstance();
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int lastDay = -1;
		int month  = -1;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int lastHigh2 = -1;
		int lastLow2 = -1;
		int totalDays=0;
		int mode = 0;
		int wins=0;
		int losses= 0;
		int count = 0;
		int maxLosingStreak = 0;
		int actualLosses = 0;
		int breakNum = 0;
		HashMap<Integer,ArrayList<Integer>> breakHash = new HashMap<Integer,ArrayList<Integer>>();
		
		ArrayList<Integer> seq = new ArrayList<Integer>();
		boolean isNewDay = false;
		int highTest = -1;
		int lowTest = -1;
		for (int i=1;i<data.size()-1;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			month = cal.get(Calendar.MONTH);
			 			 
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
						
			if (day!=lastDay){				
				if (high!=-1){
					lastHigh2 = lastHigh;
					lastLow2 = lastLow;
					lastHigh = high;
					lastLow = low;
				}				
				high = -1;
				low = -1;
				lastDay = day;				
				totalDays++;
				isNewDay = true;
			}
			
			if (mode==0 
					//&& h<10
					) {
				highTest = lastHigh;
				lowTest = lastLow;
				
				if (q.getOpen5()<=highTest 
						&& q.getHigh5()>=highTest 
						&& highTest !=-1
					) {
					mode = 1;
					if (isNewDay) breakNum = 0;
					breakNum++;					
					if (!breakHash.containsKey(breakNum)){
						breakHash.put(breakNum, new ArrayList<Integer>());
					}
					isNewDay = false;
				}else if (q.getOpen5()>=lowTest
						&& q.getLow5()<=lowTest 
						&& lowTest !=-1
					) {
					mode = -1;
					if (isNewDay) breakNum = 0;
					breakNum++;
					if (!breakHash.containsKey(breakNum)){
						breakHash.put(breakNum, new ArrayList<Integer>());
					}
					isNewDay = false;
				} 
			}
			
			if (mode==1) {
				if (q.getHigh5()>=highTest+tp) {
					breakHash.get(breakNum).add(1);
					mode = 0;
				}else if (q.getLow5()<=highTest-sl) {
					breakHash.get(breakNum).add(0);
					mode = 0;
				}
			}else if (mode==-1) {
				if (q.getLow5()<=lowTest-tp) {
					breakHash.get(breakNum).add(1);
					mode = 0;
				}else if (q.getHigh5()>=lowTest+sl) {
					breakHash.get(breakNum).add(0);
					mode = 0;
				}
			}
				
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		String resStr="";
		
		for (int i=1;i<=10;i++) {
			if (!breakHash.containsKey(i)) break;
			ArrayList<Integer> res = breakHash.get(i);			
			double resPer = MathUtils.average(res, 0,res.size()-1)*100.0;
			resStr += res.size()+" "+PrintUtils.Print2dec(resPer, false)+" "+PrintUtils.Print2dec(tp*resPer/((100.0-resPer)*sl), false)+" || ";
		}
		
		ArrayList<Integer> res = breakHash.get(1);	
		int cases0=0;
		int cases00=0;
		int uwin = 0;
		int ulosses = 0;
		double stake = 1;
		for (int i=5;i<res.size();i++) {
			int res_4 =res.get(i-4);
			int res_3 =res.get(i-3);
			int res_2 =res.get(i-2);
			int res_1 =res.get(i-1);
			int res0 =res.get(i);
			System.out.println(res.get(i));
			if (res_1==0 
					&& res_2==0 
					&& res_3==0
					//&& res_4==0
					) {
				cases0++;
				if (res0==0) {
					cases00++;
				}
			}			
		}
		System.out.println(res.size()+" "+cases0+" "+PrintUtils.Print2dec(cases00*100.0/cases0,false)
			+" || "+PrintUtils.Print2dec(100.0-cases00*100.0/cases0,false));		
		System.out.println(resStr);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String path0 ="C:\\fxdata\\";
		//String pathEURUSD1801 = path0+"eurusd_5 Mins_Bid_2009.01.01_2018.12.27.csv";
		String pathEURUSD1801 = path0+"usdcad_5 Mins_Bid_2009.01.01_2019.10.07.csv";
		//String pathEURUSD1801 = path0+"EURUSD_5 Mins_Bid_2009.01.01_2018.12.26.csv";
		ArrayList<String> paths = new ArrayList<String>();
		/*paths.add(pathEURUSD170);
		paths.add(pathEURUSD1700);
		paths.add(pathEURUSD171);
		paths.add(pathEURUSD172);
		paths.add(pathEURUSD173);
		paths.add(pathEURUSD174);
		paths.add(pathEURUSD175);
		paths.add(pathEURUSD176);
		paths.add(pathEURUSD177);
		paths.add(pathEURUSD178);
		paths.add(pathEURUSD179);
		paths.add(pathEURUSD1710);
		paths.add(pathEURUSD0);
		paths.add(pathEURUSD00);
		paths.add(pathEURUSD1);
		paths.add(pathEURUSD2);
		paths.add(pathEURUSD3);
		paths.add(pathEURUSD4);
		paths.add(pathEURUSD5);
		paths.add(pathEURUSD6);
		paths.add(pathEURUSD7);
		paths.add(pathEURUSD8);
		paths.add(pathEURUSD9);*/
		paths.add(pathEURUSD1801);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		//limit = 0;
		String provider ="";
		try {
			Sizeof.runGC ();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		ArrayList<QuoteShort> data = null;
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			Sizeof.runGC ();
			String path = paths.get(i);	
			
			dataI 		= new ArrayList<QuoteShort>();
			
			//TickStudy2019.testSpreads();
			
			//Tick.readFromDiskToQuoteShort(dataI, path, 6);
			//TestLines.calculateCalendarAdjustedSinside(dataI);	
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			data = TradingUtils.cleanWeekendDataS(dataI);
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("Leidos ticks: "+data.size());
		
			for (int tp=100;tp<=100;tp+=10){
				for (int sl=(int) (6.0*tp);sl<=6.0*tp;sl+=1*tp){
					//for (int sl=200;sl<=200;sl+=10){
						//for (int tp=(int) (3.0*sl);tp<=3.0*sl;tp+=1*sl){
							for (int nBack=0;nBack<=0;nBack+=12){
								for (int minDist=0;minDist<=0;minDist+=100){
									billyt2019.dailyBreak2(data, 2009, 2019, 01, 11, tp, sl);
								}
							}							
						}
					}
		}
	}

}
