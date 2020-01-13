package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestTrends2019 {

	public static void main(String[] args) {
String path0 ="C:\\fxdata\\";
		

		String currency = "gbpusd";

		String pathSpread = path0+currency+"_spreads_2014_2019.csv";
		//currency="eurchf";
		//String pathEURUSD = path0+currency+"_5 Mins_Bid_2012.01.01_2019.11.12.csv";
		String pathEURUSD = path0+currency+"_5 Mins_Bid_2012.01.01_2019.12.02.csv";
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
		Calendar cal = Calendar.getInstance();
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int minSize=100;minSize<=1000;minSize+=50) {
				ArrayList<TrendClass> trends = TradingUtils.calculateTrends(data, minSize);			
				int testSize = 8000*minSize;
				for (int j=0;j<1;j+=1) {
					int acc = 0;
					total = 0;
					int nones = 0;
					int cases = 0;
					int losses = 0;
					int accLoss = 0;
					int wins = 0;
					int accWins =0;
					int accSizeC = 0;
					int casesC = 0;
					int accBigWins = 0;
					int accDiff=0;
					int totalDiff = 0;
					int accSizeTotalW = 0;
					int accSizeTotalL = 0;
					for (int t=0;t<trends.size();t++) {
						TrendClass tr = trends.get(t);
						int index1 = tr.getIndex1();
						int index3 = tr.getIndex3();
						int indexC = tr.getIndexC();		
						
						QuoteShort.getCalendar(cal, data.get(index3));
						int h3 = cal.get(Calendar.HOUR_OF_DAY);
						if (h3>8) {
							int sizeReal = data.get(indexC).getClose5()-data.get(index3).getClose5();
							int sizeTotal =data.get(indexC).getClose5()-data.get(index1).getClose5();
							if (tr.getMode()==-1) {
								sizeReal = data.get(index3).getClose5()-data.get(indexC).getClose5();
								sizeTotal = Math.abs(data.get(index1).getClose5()-data.get(indexC).getClose5());
							}
								
							int size = trends.get(t).getSize();
							int sizeC = trends.get(t).getSizeClose();
							//trends.get(t).getSizeClose();
							total++;
							
							if (sizeReal>=0) {
								wins++;
								accWins+=sizeReal;
								accSizeTotalW+=sizeTotal;
								//System.out.println("[WIN] "+tr.getMode()+" "+sizeReal+" "+sizeTotal);
							}else{
								losses++;
								accLoss+=-sizeReal;
								accSizeTotalL+=sizeTotal;
							}
							casesC++;
							accSizeC+=sizeC;
						}
					}
					int trades = wins+losses;
					double pf = (accWins+accBigWins)*1.0/accLoss;
					double avg = (accWins-accLoss)*0.1/trades;
					System.out.println(
							minSize+" || "+trends.size()
							//+" "+PrintUtils.Print2dec(cases*100.0/total,false)+" "+PrintUtils.Print2dec(testSize-minSize,false)
							//+" || "+PrintUtils.Print2dec(acc*1.0/nones,false)
							//+" || "+PrintUtils.Print2dec(accSizeC*1.0/casesC,false)
							//+" || "+PrintUtils.Print2dec(acc*1.0/nones,false)
							+" || "+PrintUtils.Print2dec(wins*100.0/total,false)+" "+PrintUtils.Print2dec(accWins*1.0/wins,false)
							+" "+PrintUtils.Print2dec(accSizeTotalW*1.0/wins,false)
							+" || "+PrintUtils.Print2dec(losses*100.0/total,false)+" "+PrintUtils.Print2dec(accLoss*1.0/losses,false)
							+" "+PrintUtils.Print2dec(accSizeTotalL*1.0/losses,false)
							+" || "+PrintUtils.Print2dec(pf,false)
							+" || "+PrintUtils.Print2dec(avg,false)
							//+" || "+PrintUtils.Print2dec(accDiff*1.0/totalDiff,false)
							//+" || "+PrintUtils.Print2dec(accSizeC*1.0/casesC,false)
							);
				}
			}//minSize				
		}

	}

}
