package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Rick {
	
	static public void doTest(			
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			HashMap<Integer,ArrayList<Double>> spreads,
			int y1,int y2,int m1,int m2,
			int openDistance,
			int maxDD,
			int tp,
			int thr,
			StratPerformance sp,
			int debug,
			int printOptions
			){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
				
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		int lastDay = -1;
		int totalDays = 0;
		int totalDaysTrading = 0;
		int lastDayPips = 0;
		int high = -1;
		int low = -1;
		//sp.reset();
		//sp.setInitialBalance(20000);
		//bucle de datos
		boolean dayTraded = false;
		int maxDayLossAcc=0;
		double maxDayLossAcc$$=0;
		double dayBalance = 0;
		int maxDayPositions = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Integer> atrArray = new ArrayList<Integer>(); 
		ArrayList<PositionShort> basketTrades = new ArrayList<PositionShort>();
		ArrayList<Integer> profitTrades = new ArrayList<Integer>();
		int basketTarget = tp;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			boolean canTrade = true;
			
			//System.out.println(DateUtils.datePrint(cal)+" "+positions.size());
			if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0) continue;
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					int range = high-low;
					ranges.add(range);
					
					int atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					//System.out.println("[day range] "+range+" "+atr+" || "+q1.toString());
					atrArray.add(atr);
					totalDays++;
					if (dayTraded) totalDaysTrading++;
					
					int actualDayPips = sp.getWinPips()-sp.getLostPips();
					int diffPips = actualDayPips-lastDayPips;
					/*if (maxDayPositions>0)
					System.out.println(DateUtils.datePrint(cal1)
							+" "+maxDayPositions
							+" "+diffPips
							+" "+maxDayLossAcc
							+" || "+PrintUtils.Print2dec(maxDayLossAcc$$, false)
							+" || "+PrintUtils.Print2dec(maxDayLossAcc$$*100.0/dayBalance, false)
							);*/
					lastDayPips = actualDayPips;
				}
				dayBalance = sp.getActualBalance();
				maxDayLossAcc=0;
				maxDayLossAcc$$ = 0;
				maxDayPositions = 0;
				dayTraded = false;
				lastDay = day;
				high = -1;
				low = -1;
			}
			
			//decidimos 
			int maxMin = maxMins.get(i);
			
			//actualizamos profit
			int profit = 0;
			profitTrades.clear();
			for (int j=0;j<basketTrades.size();j++) {
				PositionShort p = basketTrades.get(j);
				int price = p.getEntry();
				int pips = q.getOpen5()-price;
				if (p.getPositionType()==PositionType.SHORT)
					pips = price-q.getOpen5();
				profit += pips;
				profitTrades.add(pips);
			}				
			//evaluamos salidas
			if (profit<=-maxDD) {
				PositionShort p = basketTrades.get(0);
				int price = p.getEntry();
				int pips = q.getOpen5()-price;
				if (p.getPositionType()==PositionType.SHORT)
					pips = price-q.getOpen5();
				
				if (pips>=0) {
					wins++;
					winPips += pips;
				}else {
					losses++;
					lostPips += -pips;
				}
				basketTrades.remove(0);
				basketTarget += -pips;//aumentamos el target
			}else if (profit>=basketTarget) {
				for (int j=0;j<basketTrades.size();j++) {
					PositionShort p = basketTrades.get(j);
					int price = p.getEntry();
					int pips = q.getOpen5()-price;
					if (p.getPositionType()==PositionType.SHORT)
						pips = price-q.getOpen5();
					profit += pips;
					
					if (pips>=0) {
						wins++;
						winPips += pips;
					}else {
						losses++;
						lostPips += -pips;
					}
				}	
				basketTrades.clear();
			}			
			//evaluamos entradas
			boolean canEnter = false;
			int mode = 0;
			if (basketTrades.size()==0 && h<=9) {				
				if (maxMin>=thr) {
					canEnter = true;
					mode = -1;
				}else if (maxMin<=-thr) {
					canEnter = true;
					mode = 1;
				}
			}else if (basketTrades.size()>0) {
				PositionShort p = basketTrades.get(basketTrades.size()-1);
				int price = p.getEntry();
				if (p.getPositionType()==PositionType.SHORT) {
					if (q.getOpen5()>=price+openDistance) {
						canEnter = true;
						mode = -1;
					}
				}else if (p.getPositionType()==PositionType.LONG) {
					if (q.getOpen5()<=price-openDistance) {
						canEnter = true;
						mode = 1;
					}
				}
			}
			
			if (canEnter) {
				if (mode==1) {
					PositionShort pos = new PositionShort();
					pos.setPositionType(PositionType.LONG);
					pos.setEntry(q.getOpen5());
					basketTrades.add(pos);
				}else if (mode==-1) {
					PositionShort pos = new PositionShort();
					pos.setPositionType(PositionType.SHORT);
					pos.setEntry(q.getOpen5());
					basketTrades.add(pos);
				}
				
				if (basketTrades.size()==1) {
					basketTarget = tp;
				}
			}
							
			//actualizamos high low
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();									
		}
		
		double pf = winPips*1.0/lostPips;
		int trades = wins+losses;
		System.out.println(openDistance+" "+maxDD+" "+tp+" "+thr
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(pf, false)
				);
	}

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		String currency = "eurusd";

		String pathSpread = path0+currency+"_spreads_2014_2019.csv";
		//currency="eurchf";
		//String pathEURUSD = path0+currency+"_5 Mins_Bid_2012.01.01_2019.11.12.csv";
		String pathEURUSD = path0+"EURJPY_5 Mins_Bid_2009.01.01_2019.11.12_(2).csv";
		String pathNews = path0+"News.csv";
		
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
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
		HashMap<Integer,ArrayList<Double>> spreads = new HashMap<Integer,ArrayList<Double>>();
		DAO.readSpreads(pathSpread,2014,2019,spreads);
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			int minSize = 200;
			
			StratPerformance sp = new StratPerformance();
			sp.setInitialBalance(4000);
			for (int openDistance=100;openDistance<=100;openDistance+=100) {
				for (int maxDD=3000;maxDD<=3000;maxDD+=100) {
					for (int tp=100;tp<=5000;tp+=100) {
						for (int thr=0;thr<=0;thr+=100) {
							Rick.doTest("", data, maxMins, spreads, 2009, 2019, 0, 11, openDistance, maxDD, tp,thr, sp, 0, 0);
						}
					}
				}
			}
			
		}

	}

}
