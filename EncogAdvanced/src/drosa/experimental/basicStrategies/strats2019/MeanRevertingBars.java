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

public class MeanRevertingBars extends AlgoBasic {
	
	int h1=0;
	int h2=23;
	double fsl = 0.60;
	double fthr = 0.20;
	int nbars = 12;
	Calendar cal = Calendar.getInstance();
	QuoteShort qm = new QuoteShort();

	int lastDayTrade = -1;
	boolean forward = true;
	boolean reverse = false;
	int totalDays = 0;
	int totalDaysTrading = 0;
	
	public void setParameters(
			int nbars,
			double fsl,
			double fthr,
			double trailPer,
			boolean forward,
			int h1,int h2){
		
		this.nbars = nbars;
		this.fsl = fsl;
		this.fthr = fthr;
		this.forward = forward;
		this.h1 = h1;
		this.h2 = h2;
		this.trailPer = trailPer;
	}

	@Override
	public void doManagePositions(ArrayList<QuoteShort> data, int i,
			ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> data, int i,
			ArrayList<PositionShort> positions, StratPerformance sp) {
		// TODO Auto-generated method stub

	}

	@Override
	public int doEvaluateEntries(ArrayList<QuoteShort> data, int i,
			ArrayList<PositionShort> positions, boolean canTrade,
			StratPerformance sp) {
		// TODO Auto-generated method stub
		if (i<nbars) return 0;
		
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		//miramos long-term direction
		
		int trades=0;
		
		TradingUtils.getMaxMinShortOpen(data, qm, cal, i-nbars, i-1);
		
		int pipsAbove = q.getOpen5()-qm.getHigh5();
		int pipsBelow = qm.getLow5()-q.getOpen5();
		
				
		boolean aboveActivated = pipsAbove>=fthr*atrArray.get(atrArray.size()-1) ? true : false;
		boolean belowActivated = pipsBelow>=fthr*atrArray.get(atrArray.size()-1) ? true : false;
		
		//aboveActivated = pipsAbove>=50  ? true : false;
		//belowActivated = pipsBelow>=50 ? true : false;
		int slPips = (int) (fsl*atrArray.get(atrArray.size()-1)); 
		
		if (h>=h1 && h<=h2
				//&& (h!=11 && h!=12 && h!=13 && h!=14)
				)
		if (aboveActivated
				){	
			int distance = pipsAbove;
			
			//distance =100;
			//abrimos posicion long
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.LONG);
				pos.setSl(q.getOpen5()-slPips);
				pos.setTp((int) (q.getOpen5() + 0.1*slPips));
			}else{
				pos.setPositionType(PositionType.SHORT);
				pos.setSl(q.getOpen5()+slPips);
				pos.setTp((int) (q.getOpen5()-1*distance));
			}
			//calculate miniLots
			int microLots = 1;
			pos.setMicroLots(microLots);

			positions.add(pos);			
			if (day!=lastDayTrade) totalDaysTrading++;			
			lastDayTrade = day;
			trades++;
		}else if (belowActivated){
			int distance = pipsBelow;
			
			//distance =100;
			//abrimos posicion long
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.SHORT);
				pos.setSl(q.getOpen5()+slPips);
				pos.setTp((int) (q.getOpen5()-0.1*slPips));
			}else{
				pos.setPositionType(PositionType.LONG);
				pos.setSl(q.getOpen5()-slPips);
				pos.setTp((int) (q.getOpen5() + 1*distance));
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

	public static void main(String[] args) {
String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		//String pathEURUSD = path0+"EURUSD_4 Hours_Bid_2003.12.31_2019.07.23.csv";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.08.02.csv";
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
							
			MeanRevertingBars mm = new MeanRevertingBars();
			StratPerformance sp = new StratPerformance(); 
			
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+23;
				for (int nbars=1;nbars<=1;nbars+=10){
					for (double fsl=0.10;fsl<=0.00;fsl+=0.10){
						for (double fthr=0.10;fthr<=6.80;fthr+=0.10){	
							for (int y1=2004;y1<=2004;y1++){
								int y2 = y1+15;
								
								mm.setParameters(nbars,fsl,fthr,0.0,false,h1,h2);
								
								String header=nbars+" "+PrintUtils.Print2dec(fsl, false)
								+" "+PrintUtils.Print2dec(fthr, false)
								+" "+h1+" "+h2
								+" "+y1+" "+y2
								;
								mm.doTest(header,data, y1, y2, 0, 11, sp, 0,true);		
								try {
									Sizeof.runGC ();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			
		}

	}

}
