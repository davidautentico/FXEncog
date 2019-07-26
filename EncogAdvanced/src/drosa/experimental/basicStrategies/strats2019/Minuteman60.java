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

public class Minuteman60 extends AlgoBasic {

	int lt = 1000;
	int st = 48;
	double thrlt = 1.0;
	double tpR = 5.0;
	int lastDayTrade = -1;
	boolean forward = true;
	Calendar cal = Calendar.getInstance();
	
	int totalDays = 0;
	int totalDaysTrading = 0;
	
	public void setParameters(int lt,int st,double thrlt,double tpR,boolean forward){
		this.lt = lt;
		this.st = st;
		this.thrlt = thrlt;
		this.tpR = tpR;
		this.forward = forward;
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
		//miramos long-term direction
		
		int trades=0;
		
		//TEST LONG
		int difflt = q.getOpen5()-data.get(i-lt).getOpen5();
		int diffst = q.getOpen5()-data.get(i-st).getOpen5();
		
		if (difflt>=0 && diffst>=0 
				//&& h>=10
				//&& day!=lastDayTrade
				){		
			//Test BULL
			boolean isltl = difflt>=thrlt*atrArray.get(atrArray.size()-1) ? true:false;
			boolean isstl = diffst>=0 ? true:false;
			
			boolean shortTermVol = (q.getOpen5()-data.get(i-this.st).getOpen5())>=thrlt*atrArray.get(atrArray.size()-1) ? true : false;
			int rdistance = q.getOpen5()-data.get(i-st).getLow5();
			if (shortTermVol
					//&& isltl 
					//&& isstl 
					){							
				//abrimos posicion long
				PositionShort pos = new PositionShort();
				pos.setEntry(q.getOpen5());
				pos.setPositionStatus(PositionStatus.OPEN);
				if (forward){
					pos.setPositionType(PositionType.LONG);
					pos.setSl(data.get(i-st).getLow5());
					pos.setTp((int) (q.getOpen5() + rdistance*tpR));
				}else{
					pos.setPositionType(PositionType.SHORT);
					pos.setTp(data.get(i-st).getLow5());
					pos.setSl((int) (q.getOpen5() + rdistance*tpR));
				}
				positions.add(pos);
				
				if (day!=lastDayTrade) totalDaysTrading++;
				
				lastDayTrade = day;
				trades++;
			}
		}else if (difflt<=0 
				&& diffst<=0 
				//&& day!=lastDayTrade
				//&& h>=10
				){
			boolean isltl = -difflt>=thrlt*atrArray.get(atrArray.size()-1) ? true:false;
			boolean isstl = -diffst>=0 ? true:false;
			boolean shortTermVol = (data.get(i-this.st).getOpen5()-q.getOpen5())>=thrlt*atrArray.get(atrArray.size()-1) ? true : false;
					
			int rdistance = data.get(i-st).getHigh5()-q.getOpen5();
			if (	shortTermVol
					//isltl 
					//&& isstl
					){
				//abrimos posicion long
				PositionShort pos = new PositionShort();
				pos.setEntry(q.getOpen5());
				pos.setPositionStatus(PositionStatus.OPEN);
				if (forward){
					pos.setPositionType(PositionType.SHORT);
					pos.setSl(data.get(i-st).getHigh5());
					pos.setTp((int) (q.getOpen5() - rdistance*tpR));
				}else{
					pos.setPositionType(PositionType.LONG);
					pos.setTp(data.get(i-st).getHigh5());
					pos.setSl((int) (q.getOpen5() - rdistance*tpR));
				}
				positions.add(pos);	
				
				if (day!=lastDayTrade) totalDaysTrading++;
				
				lastDayTrade = day;
				trades++;
			}
		}		
		return trades;
	}
	
	@Override
	public void doManagePositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			StratPerformance sp) {
		
		
	}

	
	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		//String pathEURUSD = path0+"EURUSD_4 Hours_Bid_2003.12.31_2019.07.23.csv";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.07.23.csv";
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
				for (int lt=1000;lt<=1000;lt+=100){
					for (int st=48;st<=48;st+=100){
						for (double thrlt=0.1;thrlt<=6.5;thrlt+=0.1){
							for (double tpR=0.50;tpR<=0.50;tpR+=0.1){
								mm.setParameters(lt, st, thrlt, tpR,false);
								String header=st+" "+PrintUtils.Print2dec(thrlt, false)+" "+PrintUtils.Print2dec(tpR, false);
								mm.doTest(header,data, 2014, 2019, 0, 11, sp, 0, true);								
							}
						}
					}
				}
				
			}

	}




}
