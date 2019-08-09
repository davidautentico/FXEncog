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
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Minuteman60 extends AlgoBasic {

	int lt = 1000;
	int st = 48;
	double thrlt = 1.0;
	double tpR = 5.0;
	int lastDayTrade = -1;
	boolean forward = true;
	boolean reverse = false;
	int h1=0;
	int h2=23;
	Calendar cal = Calendar.getInstance();
	ArrayList<Integer> values = new ArrayList<Integer>();
	
	int totalDays = 0;
	int totalDaysTrading = 0;
	
	
	public void setParameters(
			int lt,int st,
			double thrlt,
			double tpR,
			boolean forward,
			boolean reverse,
			int h1,int h2){
		
		this.lt = lt;
		this.st = st;
		this.thrlt = thrlt;
		this.tpR = tpR;
		this.forward = forward;
		this.reverse = reverse;
		this.h1 = h1;
		this.h2 = h2;
	}
	
	
	public int getLt() {
		return lt;
	}


	public void setLt(int lt) {
		this.lt = lt;
	}


	public int getSt() {
		return st;
	}


	public void setSt(int st) {
		this.st = st;
	}


	public double getThrlt() {
		return thrlt;
	}


	public void setThrlt(double thrlt) {
		this.thrlt = thrlt;
	}


	public double getTpR() {
		return tpR;
	}


	public void setTpR(double tpR) {
		this.tpR = tpR;
	}


	@Override
	public int doEvaluateEntries(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			boolean canTrade, StratPerformance sp) {
		if (i<lt) return 0;
		if (i<st) return 0;
		
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		//miramos long-term direction
		
		int trades=0;
		
		values.add(q.getOpen5());
		int sma = (int) MathUtils.average(values, values.size()-st, values.size()-1);
		
		boolean aboveSMA = (q.getOpen5()-sma)>=thrlt*atrArray.get(atrArray.size()-1) ? true : false;
		boolean belowSMA = (sma-q.getOpen5())>=thrlt*atrArray.get(atrArray.size()-1) ? true : false;
		int rdistanceL = (int) (thrlt*atrArray.get(atrArray.size()-1));
		int rdistanceH = (int) (thrlt*atrArray.get(atrArray.size()-1));
		
		if (h>=h1 && h<=h2
				//&& (h!=11 && h!=12 && h!=13 && h!=14)
				)
		if (aboveSMA
				){							
			//abrimos posicion long
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.LONG);
				pos.setSl(q.getOpen5()-rdistanceL);
				pos.setTp((int) (q.getOpen5() + rdistanceL*tpR));
				if (reverse){
					pos.setSl((int) (q.getOpen5() - rdistanceL*tpR));
					pos.setTp(q.getOpen5()+rdistanceL);
				}
			}else{
				pos.setPositionType(PositionType.SHORT);
				pos.setSl(q.getOpen5()+rdistanceH);
				pos.setTp((int) (q.getOpen5()-rdistanceH*tpR));
				if (reverse){
					pos.setSl((int) (q.getOpen5() + rdistanceH*tpR));
					pos.setTp((int) (q.getOpen5() - rdistanceH));
				}
			}
			//calculate miniLots
			int microLots = 1;
			pos.setMicroLots(microLots);

			positions.add(pos);			
			if (day!=lastDayTrade) totalDaysTrading++;			
			lastDayTrade = day;
			trades++;
		}else if (belowSMA){
			//abrimos posicion long
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.SHORT);
				pos.setSl(q.getOpen5()+rdistanceH);
				pos.setTp((int) (q.getOpen5()-rdistanceH*tpR));
				if (reverse){
					pos.setSl((int) (q.getOpen5() + rdistanceH*tpR));
					pos.setTp((int) (q.getOpen5() - rdistanceH));
				}
			}else{
				pos.setPositionType(PositionType.LONG);
				pos.setSl(q.getOpen5()-rdistanceL);
				pos.setTp((int) (q.getOpen5() + rdistanceL*tpR));
				if (reverse){
					pos.setSl((int) (q.getOpen5() - rdistanceL*tpR));
					pos.setTp(q.getOpen5()+rdistanceL);
				}
			}
			//calculate miniLots
			int microLots = 1;
			pos.setMicroLots(microLots);

			positions.add(pos);	
			
			if (day!=lastDayTrade) totalDaysTrading++;
			
			lastDayTrade = day;
			trades++;

		}		
		return trades;
	}
	
	@Override
	public void doManagePositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions) {
		
		if (trailPer<=0.0) return;
		
		QuoteShort q = data.get(i);
		int j = 0;
		while (j<positions.size()){
			PositionShort p = positions.get(j);
			boolean isClosed = false;
			int pips = 0;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					int trail = q.getOpen5()-p.getEntry();
					if (trail>=200){
						int trailPips = (int) (trail*trailPer);
						if (trailPips>=20){
							if (p.getEntry()+trailPips>p.getSl() && p.getEntry()+trailPips<q.getOpen5()){
								p.setSl(p.getEntry()+trailPips);
							}
						}
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					int trail = -q.getOpen5()+p.getEntry();
					if (trail>=200){
						int trailPips = (int) (trail*trailPer);
						if (trailPips>=20){
							if (p.getEntry()-trailPips<p.getSl() && p.getEntry()-trailPips>q.getOpen5()){
								p.setSl(p.getEntry()-trailPips);
							}
						}
					}
				}
			}
			j++;
		}
		
	}

	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			StratPerformance sp) {
		
		
	}

	
	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		//String pathEURUSD = path0+"EURUSD_4 Hours_Bid_2003.12.31_2019.07.23.csv";
		//String pathEURUSD = path0+"EURUSD_Hourly_Bid_2004.01.01_2019.08.02.csv";
		String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2004.01.01_2019.08.02.csv";
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
								
				Minuteman60 mm = new Minuteman60();
				StratPerformance sp = new StratPerformance(); 
				
				//02-08-2019
				//eur 1min 30 0.30 4.00 10 22 || 1.70 29.52
				//eur 5min 10 0.30 4.00 10 22 || 1.65 29.47
				//eur 15min 6 0.30 4.00 10 22 || 1.51 31.34
				//eur 60min 3 0.30 4.00 10 22 || 1.30 24.88 
				for (int h1=10;h1<=10;h1++){
					int h2 = h1+12;
					for (int lt=1000;lt<=1000;lt+=100){
						for (int st=6;st<=100;st+=6){
							for (double thrlt=0.50;thrlt<=0.70;thrlt+=0.05){
								for (double tpR=4.00;tpR<=4.00;tpR+=0.10){	
									for (int y1=2004;y1<=2004;y1++){
										int y2 = y1+15;
										
										try {
											Sizeof.runGC ();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										String header="";										
										mm.setParameters(lt, st, thrlt, tpR,true,true,h1,h2);
										header=st+" "+PrintUtils.Print2dec(thrlt, false)
										+" "+PrintUtils.Print2dec(tpR, false)
										+" "+h1+" "+h2
										;
										//mm.doTest(header,data, 2009, 2019, 0, 11, sp, 0,true);
										
										mm.setParameters(lt, st, thrlt, tpR,true,false,h1,h2);
										header=st+" "+PrintUtils.Print2dec(thrlt, false)
										+" "+PrintUtils.Print2dec(tpR, false)
										+" "+h1+" "+h2
										+" "+y1+" "+y2
										;
										mm.doTest(header,data, y1, y2, 0, 11, sp, 0,true);		
										
										mm.setParameters(lt, st, thrlt, tpR,false,true,h1,h2);
										header=st+" "+PrintUtils.Print2dec(thrlt, false)
										+" "+PrintUtils.Print2dec(tpR, false)
										+" "+h1+" "+h2
										;
										//mm.doTest(header,data, 2009, 2019, 0, 11, sp, 0,true);		
										
										mm.setParameters(lt, st, thrlt, tpR,false,false,h1,h2);
										header=st+" "+PrintUtils.Print2dec(thrlt, false)
										+" "+PrintUtils.Print2dec(tpR, false)
										+" "+h1+" "+h2
										;
										//mm.doTest(header,data, 2009, 2019, 0, 11, sp, 0,true);	
									}
								}
							}
						}
					}
				}
				
			}

	}

}
