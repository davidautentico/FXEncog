package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class TestDaveTrade2019 {
	
	private static void doTestAuto(
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins, 
			int y1, int y2) {
		//
		
	}

	public static void main(String[] args) {
		//Buscamos la mejor implementación usando múltiples periodos de test
		
		String path0 ="C:\\fxdata\\";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.04.01_2019.07.09.csv";
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
			Calendar cal = Calendar.getInstance();
			System.out.println("path: "+path+" "+data.size());
			double aMaxFactorGlobal = -9999;
			
			//TestMeanReversion3.doTestMR2(data,maxMins, 2009, 2019, 6, 1, 0);
			
			TestDaveTrade2019.doTestAuto(data,maxMins, 2009, 2019);
			
		}						
	}



}
