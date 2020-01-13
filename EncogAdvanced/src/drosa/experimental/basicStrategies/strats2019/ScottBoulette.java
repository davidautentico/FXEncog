package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class ScottBoulette {
	
	public static void doTestExtensions(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,int m1,int m2,
			int thr,
			int minExtension,
			int tp,
			int sl,
			int debug
			) {
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
				
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		Calendar calThr = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		int lastDay = -1;
		int totalDays = 0;
		int lastDayPips = 0;
		//sp.reset();
		//sp.setInitialBalance(20000);
		//bucle de datos
		boolean dayTraded = false;
		int maxDayLossAcc=0;
		double maxDayLossAcc$$=0;
		double dayBalance = 0;
		int maxDayPositions = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int dayTrades = 0;
		int dayOpen = -1;
		QuoteShort qm = new QuoteShort();
		int wins = 0;
		int losses = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (day!=lastDay){
				if (lastDay!=-1){
					int range = high-low;
					ranges.add(range);
					
					int atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					//System.out.println("[day range] "+range+" "+atr+" || "+q1.toString());					
					totalDays++;
					lastHigh = high;
					lastLow = low;
				}
				maxDayLossAcc=0;
				maxDayLossAcc$$ = 0;
				maxDayPositions = 0;
				dayTraded = false;
				dayTrades = 0;
				lastDay = day;
				high = -1;
				low = -1;
				dayOpen = q.getOpen5();
			}
			
			TradingUtils.getMaxMinShortOpen(data, qm, calThr, i-thr, i);
			boolean canLow = false;
			boolean canHigh = false;
			if (q.getOpen5()>qm.getLow5()-minExtension) canLow = true;
			if (q.getOpen5()<qm.getHigh5()+minExtension) canHigh = true;
			
			int end = i+588;
			if (end>data.size()-1) end = data.size()-1;
			int mode = 0;
			int tpvalue = 0;
			int slvalue = 0;
			for (int j=i;j<end;j++) {
				QuoteShort qj = data.get(j);
				
				if (mode==0
						) {
					if (canHigh && qj.getOpen5()>=qm.getHigh5()+minExtension) {
						mode = 1;
						tpvalue = qm.getHigh5()+minExtension-tp;
						slvalue = qm.getHigh5()+minExtension+sl;
					}else if (canLow && qj.getOpen5()<=qm.getLow5()-minExtension) {
						mode = -1;
						tpvalue = qm.getLow5()-minExtension+tp;
						slvalue = qm.getLow5()-minExtension-sl;
					}
				}
				if (mode==1) {
					if (qj.getOpen5()>=slvalue) {
						losses++;
						break;
					}else if (qj.getOpen5()<=tpvalue) {
						wins++;
						break;
					}
				}
				
				if (mode==-1) {
					if (qj.getOpen5()>=tpvalue) {
						wins++;
						break;
					}else if (qj.getOpen5()<=slvalue) {
						losses++;
						break;
					}
				}
			}//j			
		}//for
		
		int trades = wins+losses;
		double pf = wins*tp*1.0/(sl*losses);
		
		System.out.println(
				thr+" "+minExtension+" "+tp+" "+sl
				+" || "+trades+" "+PrintUtils.Print2dec(pf, false)+" || "+PrintUtils.Print2dec(1.0/pf, false)
		);
	}

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		//String pathEURUSD = path0+"EURUSD_4 Hours_Bid_2003.12.31_2019.07.23.csv";

		String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.10.17_(1).csv";
		//String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2009.01.01_2019.10.17.csv";
		//String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2004.01.01_2019.09.03.csv";

		//String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2009.01.01_2019.08.04.csv";
		//String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2004.01.01_2019.04.06.csv";
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
		//FFNewsClass.readNews(pathNews,news,0);
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
							
			MeanRevertingSMA2 mm = new MeanRevertingSMA2();
			StratPerformance sp = new StratPerformance(); 
			
			//30min
			//8 0.14 0.60 0.30 13 0 8 
			//8 0.15 0.60 0.10 20 0 8 
			//15
			//18 0.14 1.00 13 0.15
			for (int minExtension=50;minExtension<=50;minExtension++){
				for (int thr=400;thr<=400;thr+=12){
					for (int tp=50;tp<=500;tp+=10){
						for (int sl=1*tp;sl<=1*tp;sl+=1){
							 ScottBoulette.doTestExtensions("", data, 20009, 2019, 0, 11, thr, minExtension, tp, sl, 0);
						}
					}
				}
			}
		}

	}

}
