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

public class MeanRevertingSMA2 extends AlgoBasic {
	
	private int nbars;
	private double fdiff;
	private double trail;
	private double fsl;
	private boolean forward;
	private int h1;
	private int h2;
	private int maxPositions;
	private ArrayList<Integer> values = new ArrayList<Integer>();
	
	private void setParameters(
			int nbars, 
			double fdiff,
			double fsl,
			double trail,	
			double risk,
			int maxPositions,
			boolean forward, int h1, int h2) {
		
		this.nbars = nbars;
		this.fdiff = fdiff;
		this.fsl = fsl;
		this.trail = trail;
		this.forward = forward;
		this.h1 = h1;
		this.h2 = h2;
		this.risk = risk;
		this.maxPositions = maxPositions;
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
		
		if (positions.size()<=0) return;
		QuoteShort q = data.get(i);
		int sma = (int) MathUtils.average(values, values.size()-nbars, values.size()-1);
		//evaluamos SL y Tp			
		int j = 0;
		while (j<positions.size()){
			PositionShort p = positions.get(j);
			boolean isClosed = false;
			int pips = 0;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					if (q.getOpen5()>=sma){
						pips = q.getOpen5()-p.getEntry();
						isClosed = true;
						//System.out.println("[long sl touched] "+pips+" "+(p.getTp()-p.getEntry()));
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					if (q.getOpen5()<=sma){
						pips = p.getEntry()-q.getOpen5();
						isClosed = true;
					}
				}
			}
			
			if (isClosed){
				int pipsSL = Math.abs(p.getEntry()-p.getSl());
				double rr = pips*1.0/pipsSL;
				sp.addTrade(p.getMicroLots(),pips,pipsSL,p.getMaxLoss(),p.getTransactionCosts(),cali);
				positions.remove(j);
			}else{
				j++;
			}
		}

	}

	@Override
	public int doEvaluateEntries(ArrayList<QuoteShort> data, int i,
			ArrayList<PositionShort> positions, boolean canTrade,
			StratPerformance sp) {
				
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cali, q);
		int y = cali.get(Calendar.YEAR);
		int h = cali.get(Calendar.HOUR_OF_DAY);
		int min = cali.get(Calendar.MINUTE);
		int day = cali.get(Calendar.DAY_OF_YEAR);
				
		values.add(q.getOpen5());
		int newtrades=0;
		
		if (i<nbars) return 0;
		if (h<h1 || h>h2 
				|| h==4
				) return 0;
		if (h==0 && min<15) return 0;
		if (positions.size()>=maxPositions) return 0;
					
		//System.out.println("aaded: "+values.get(values.size()-1)+" "+values.size());
		int sma = (int) MathUtils.average(values, values.size()-nbars, values.size()-1);
		//System.out.println("aaded: "+values.get(values.size()-1)+" "+values.size()+" "+sma );
		int pipsA = q.getOpen5()-sma;
		int pipsB = sma-q.getOpen5();
		int pipsAtr = this.atrArray.get(atrArray.size()-1);
		int pipsT = (int) (fdiff* pipsAtr);
		int transactionCosts = TradingUtils.getTransactionCosts(y, h,1);
		if (pipsA>=pipsT){
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.LONG);
				pos.setSl((int) (q.getOpen5()-fsl*pipsAtr));
				//pos.setTp((int) (q.getOpen5() + 0.1*slPips));
			}else{
				pos.setPositionType(PositionType.SHORT);
				pos.setSl((int) (q.getOpen5()+fsl*pipsAtr));
				//pos.setTp((int) (q.getOpen5()-1*distance));
				//System.out.println("[open short] "+q.getOpen5()+" "+sma+" "+pos.getSl());
			}
			//calculate miniLots
			int minPips = (int) (fsl*pipsAtr); 
			double riskPosition = sp.getActualBalance()*risk*1.0/100.0;
			double riskPip = riskPosition/(minPips*0.1);
			int microLots = (int) (riskPip/0.10);
			if (microLots<=0) microLots = 1;
			if (microLots>0){
				pos.setMicroLots(microLots);
				pos.setTransactionCosts(transactionCosts);
				positions.add(pos);	
				
				//if (day!=lastDayTrade) totalDaysTrading++;
				
				lastDayTrade = day;
				trades++;
				newtrades++;
			}
		}else if (pipsB>=pipsT){
			//distance =100;
			//abrimos posicion long
			PositionShort pos = new PositionShort();
			pos.setEntry(q.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.SHORT);
				pos.setSl((int) (q.getOpen5()+fsl*pipsAtr));
				//pos.setTp((int) (q.getOpen5()-0.1*slPips));
			}else{
				pos.setPositionType(PositionType.LONG);
				pos.setSl((int) (q.getOpen5()-fsl*pipsAtr));
				//pos.setTp((int) (q.getOpen5() + 1*distance));
			}
			//calculate miniLots
			int minPips = (int) (fsl*pipsAtr); 
			double riskPosition = sp.getActualBalance()*risk*1.0/100.0;
			double riskPip = riskPosition/(minPips*0.1);
			int microLots = (int) (riskPip/0.10);
			if (microLots<=0) microLots = 1;
			if (microLots>0){
				pos.setMicroLots(microLots);
				pos.setTransactionCosts(transactionCosts);
				positions.add(pos);	
				
				//if (day!=lastDayTrade) totalDaysTrading++;
				
				lastDayTrade = day;
				trades++;
				newtrades++;
			}
		}
		
		return newtrades;
	}

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		//String pathEURUSD = path0+"EURUSD_4 Hours_Bid_2003.12.31_2019.07.23.csv";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.08.06.csv";
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
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+8;
				for (int nbars=50;nbars<=50;nbars+=1){
					for (double fdiff=0.01;fdiff<=0.40;fdiff+=0.01){
						for (double fsl=0.60;fsl<=0.60;fsl+=0.05){	
							//double risk = 0.05;
							for (double risk=0.01;risk<=0.01;risk+=0.01){
								for (int maxPositions=200;maxPositions<=200;maxPositions+=10){
									for (int y1=2009;y1<=2009;y1++){
										int y2 = y1+10;
										for (int m1=0;m1<=0;m1+=1){
											int m2 = m1+11;
											mm = new MeanRevertingSMA2();
											sp = new StratPerformance();
											sp.setInitialBalance(4000);
											
											mm.setParameters(nbars,fdiff,fsl,0.0,risk,maxPositions,false,h1,h2);
											
											String header=nbars
											+" "+PrintUtils.Print2dec(fdiff, false)
											+" "+PrintUtils.Print2dec(fsl, false)
											+" "+PrintUtils.Print2dec(risk, false)
											+" "+maxPositions
											+" "+h1+" "+h2
											+" "+y1+" "+y2
											;
											mm.doTest(header,data, y1, y2, m1, m2, sp, 0,0);		
											try {
												Sizeof.runGC ();
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}//m
									}//y
								}//maxPos
							}//risk
						}//fsl
					}//fdiff
				}//nbars
			}//h
		}//i
	}

}
