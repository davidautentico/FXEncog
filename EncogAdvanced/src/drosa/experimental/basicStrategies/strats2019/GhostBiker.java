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
import drosa.utils.TradingUtils;

public class GhostBiker extends AlgoBasic {
	
	Calendar cal = Calendar.getInstance();
	int h1 = 18;
	int h2 = 18;
	int fib0 = -1;
	int fib30 = 0;
	
	
	@Override
	public void doEvaluateEntries(ArrayList<QuoteShort> data, int idx, ArrayList<PositionShort> positions,
			boolean canTrade, StratPerformance sp) {
		
		QuoteShort q = data.get(idx);
		QuoteShort.getCalendar(cal, q);
		
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if (canTrade && h>=h1 && h<=h2){
			
			//
		}		
	}
	
	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> data, int idx, ArrayList<PositionShort> positions,
			StratPerformance sp) {
		// TODO Auto-generated method stub		
	}
	
	
	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.07.23.csv";
						
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
			Calendar cal = Calendar.getInstance();
			System.out.println("path: "+path+" "+data.size());
			double aMaxFactorGlobal = -9999;
			
			
		}
	}



	

}
