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
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class AlphaOmega extends AlgoBasic  {
	
	int h1=0;
	int h2=9;
	int h3=10;
	int h4=23;	
	double f1 = 0.50;
	double f2 = 0.25;
	int minRange = 150;
	int dayOrderThr = 0;
	
	int lastDayTrade = -1;
	int dayOrder = 0;
	int lastDay = -1;
	
	
	public void setParameters(int h1,int h2,int h3,int h4,double f1,double f2,
			int minRange,
			int dayOrderThr
			){
		this.h1=h1;
		this.h2=h2;
		this.h3=h3;
		this.h4=h4;	
		this.f1 = f1;
		this.f2 = f2;
		this.minRange = minRange;
		this.dayOrderThr = dayOrderThr;
	}

	@Override
	public void doManagePositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions) {
	
	}

	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			StratPerformance sp) {
		
		QuoteShort q = data.get(i);
		int j = 0;
		QuoteShort.getCalendar(cali, q);
		int h = cali.get(Calendar.HOUR_OF_DAY);
		int min = cali.get(Calendar.MINUTE);
		int actualRange = this.high-this.low;
		int target50 = (int) (actualRange*f1);
		int target25 = (int) (actualRange*f2);
		while (j<positions.size()){
			PositionShort p = positions.get(j);
			boolean isClosed = false;
			int pips = 0;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					pips = q.getOpen5()-p.getEntry();
					int priceThr = low + target50;
					if (h<=h3){
						priceThr = low + target50;
					}else{
						priceThr = low + target25;
					}					
					if (q.getOpen5()>priceThr){
						pips = q.getOpen5()-p.getEntry();
						isClosed = true;
					}else{
						if (h==23 && min==55){
							pips = q.getOpen5()-p.getEntry();
							isClosed = true;
						}
					}
					
					
					
				}else if (p.getPositionType()==PositionType.SHORT){
					pips = p.getEntry()-q.getOpen5();
					int priceThr = high - target50;
					if (h>=h1 && h<=h2){
						priceThr = high - target50;
					}else{
						priceThr = high - target25;
					}					
					if (q.getOpen5()<priceThr){
						pips = p.getEntry()-q.getOpen5();
						isClosed = true;
					}else{
						if (h==23 && min==55){
							pips = p.getEntry()-q.getOpen5();
							isClosed = true;
						}
					}
				}
			}
			
			if (isClosed){
				sp.addTrade(p.getMicroLots(),pips);
				positions.remove(j);
			}else{
				j++;
			}
		}
		
	}

	@Override
	public int doEvaluateEntries(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			boolean canTrade, StratPerformance sp) {
		// TODO Auto-generated method stub
		int trades = 0;
		
		if (high==-1 || low==-1) return 0;
		
		int actualRange = this.high-this.low;
		if (actualRange<minRange) return 0;
		
		int target50 = (int) (actualRange*f1);
		int target25 = (int) (actualRange*f2);
		
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cali, q);
		int h = cali.get(Calendar.HOUR_OF_DAY);
		int day = cali.get(Calendar.DAY_OF_YEAR);
		
		if (day!=lastDay){
			dayOrder = 0;
			lastDay = day;
		}
		//no abrimos tras h2
		if (h<h1 || h>h2) return 0;
		
		//test long
		
		//if (lastDayTrade == day) return 0;
		
		if (q.getOpen5()>=high){
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			pos.setPositionType(PositionType.SHORT);
			pos.setTp(q.getOpen5()-7000);
			pos.setSl(q.getOpen5()+5000);
			
			if (dayOrder==dayOrderThr){
				positions.add(pos);
				trades++;
			}			
			dayOrder++;
			lastDayTrade = day;
		}else if (q.getOpen5()<=low){
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			pos.setPositionType(PositionType.LONG);
			pos.setTp(q.getOpen5()+7000);
			pos.setSl(q.getOpen5()-5000);		
						
			if (dayOrder==dayOrderThr){
				positions.add(pos);
				trades++;
			}						
			dayOrder++;
			lastDayTrade = day;
		}
		
				
		return trades;
	}
	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		//String pathEURUSD = path0+"EURUSD_4 Hours_Bid_2003.12.31_2019.07.23.csv";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.07.23.csv";
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.07.25.csv";
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
								
				AlphaOmega mm = new AlphaOmega();
				int h1 = 0;
				int h2 = h1+8;
				int h3 = 18;
				int h4 = 23;
				StratPerformance sp = new StratPerformance();
				for (h1=0;h1<=23;h1++){
					h2 = h1+0;
					for (double f1=0.50;f1<=0.50;f1+=0.10){
						for (double f2=0.25;f2<=0.25;f2+=0.05){
							for (int minRange = 100;minRange<=100;minRange+=10){
								for (int dot=0;dot<=0;dot++){
									mm.setParameters(h1, h2, h3, h4, f1, f2, minRange,dot);
									String header=PrintUtils.Print2dec(f1, false)+" "+PrintUtils.Print2dec(f2, false)+" "+minRange;
									mm.doTest(header,data, 2009, 2019, 0, 11, sp, 0, true);		
								}
							}
						}
					}
				}
				
			}

	}

}
