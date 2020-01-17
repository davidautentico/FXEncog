package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public abstract class AlgoBasic {
	
	ArrayList<Integer> atrArray = new ArrayList<Integer>();	
	int high = -1;
	int low = -1;
	Calendar cali = Calendar.getInstance();
	int comm = 15;
	double trailPer = 0.00;
	int trades = 0;
	int lastDayTrade = -1;
	int totalDaysTrading = 0;
	double risk = 0.20;
	boolean printAnyway = true;	
	HashMap<Integer,ArrayList<Double>> spreads = null;
	int isTransactionHours = 1;
	int debug =0;
	int lastCrossed = 0;
	int atrRange20 = 800;
	int actualH = 0;
	int actualY = 0;
	int actualM = 0;
	
	public void doTrailPositions(ArrayList<QuoteShort> dataBid,ArrayList<QuoteShort> dataASK, int i, ArrayList<PositionShort> positions) {
		
		/*if (trailPer<=0.0) return;
		
		QuoteShort q = dataid.get(i);
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
		}*/		
	}
		
	public double doTest(			
			String header,
			ArrayList<QuoteShort> dataBid,
			ArrayList<QuoteShort> dataAsk,
			ArrayList<Integer> maxMins,
			HashMap<Integer,ArrayList<Double>> spreads,
			int y1,int y2,
			int m1,int m2,
			StratPerformance sp,
			int isTrasactionHours,
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
		totalDaysTrading = 0;
		int lastDayPips = 0;
		this.spreads = spreads;
		this.isTransactionHours = isTrasactionHours;
		this.debug = debug;
		this.lastCrossed = 0; //para bollinger cuando se cruza por ultima vez

		atrArray.add(800);
		//sp.reset();
		//sp.setInitialBalance(20000);
		//bucle de datos
		boolean dayTraded = false;
		int maxDayLossAcc=0;
		double maxDayLossAcc$$=0;
		double dayBalance = 0;
		int maxDayPositions = 0;
		
		int minSize = dataBid.size();
		if (dataAsk.size()<minSize) minSize = dataAsk.size();
		
		for (int i=1;i<minSize;i++){
			QuoteShort qb	= dataBid.get(i);
			QuoteShort qb1 	= dataBid.get(i-1);
			QuoteShort qa 	= dataAsk.get(i);
			QuoteShort qa1	= dataAsk.get(i-1);
			QuoteShort.getCalendar(cal, qb);
			QuoteShort.getCalendar(cal1, qb1);
			boolean canTrade = true;
			
			//System.out.println(DateUtils.datePrint(cal)+" "+positions.size());
			if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0) continue;
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			actualH = cal.get(Calendar.HOUR_OF_DAY);
			actualY = cal.get(Calendar.YEAR);
			actualM = cal.get(Calendar.MONTH);
			if (day!=lastDay){
				if (lastDay!=-1){
					int range = high-low;
					ranges.add(range);
					
					int atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					if (debug==1)
						System.out.println("[day range] "+range+" "+atr+" || "+qb1.toString());
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
									
			//evaluamos entradas
			int ntrades = doEvaluateEntries(dataBid,dataAsk,maxMins,spreads,i,positions,canTrade,sp);
			if (ntrades>0){
				dayTraded = true;
			}
			
			//evaluamos SL y Tp		
			sp.resetEquitity();			
			int j = 0;
			int lossAccumulated = 0;
			double lossAccumulated$$ = 0;
			if (positions.size()>maxDayPositions) maxDayPositions = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClosed = false;
				int pips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					if (p.getPositionType()==PositionType.LONG){
						int testValue = qb.getOpen5();
						pips = testValue-p.getEntry();
						if (testValue<=p.getSl() && p.getSl()>=0){
							pips = testValue-p.getEntry();
							isClosed = true;
							//System.out.println("[long sl touched] "+pips);
						}else if (testValue>=p.getTp() && p.getTp()>=0){
							pips = testValue-p.getEntry();
							isClosed = true;
						}
						
						if (p.getEntry()-testValue>=p.getMaxLoss()){
							p.setMaxLoss(p.getEntry()-testValue);
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						int testValue = qa.getOpen5();
						pips = -testValue+p.getEntry();
						if (testValue>=p.getSl() && p.getSl()>=0){
							pips = -testValue+p.getEntry();
							isClosed = true;
						}else if (testValue<=p.getTp() && p.getTp()>=0){
							pips = -testValue+p.getEntry();
							isClosed = true;
						}
						
						if (-p.getEntry()+testValue>=p.getMaxLoss()){
							p.setMaxLoss(-p.getEntry()+testValue);
						}
					}
				}
				
				sp.updateEquitity(pips,p.getMicroLots());
				if (isClosed){
					int pipsSL = Math.abs(p.getEntry()-p.getSl());
					double rr = pips*1.0/pipsSL;
					//System.out.println(rr);
					sp.addTrade(p.getMicroLots(),pips,pipsSL,p.getMaxLoss(),p.getTransactionCosts(),cal);
					
					if (debug==1) {
						System.out.println(" [CLOSED trade] "+DateUtils.datePrint(cal)
							+" || pips = "+pips
							+" || "+p.toString2()
						);
					}
					positions.remove(j);
				}else{
					int pipsSL = Math.abs(p.getEntry()-p.getSl());
					//System.out.println(pipsSL+" || "+p.getMicroLots());
					lossAccumulated += pips;
					lossAccumulated$$ += pips*p.getMicroLots()*0.10*0.10; 
					if (lossAccumulated<maxDayLossAcc) maxDayLossAcc=lossAccumulated;
					if (lossAccumulated$$<maxDayLossAcc$$) maxDayLossAcc$$ =lossAccumulated$$;
					j++;
				}
			}
			
			//evaluamos salidas especiales
			doEvaluateExits(dataBid,dataAsk,i,positions,sp);	
			
			//custom manage
			doManagePositions(dataBid,dataAsk,i,positions);
			
			doTrailPositions(dataBid,dataAsk,i,positions);
			
			//actualizamos high low
			if (high==-1 || qb.getHigh5()>=high) high = qb.getHigh5();
			if (low==-1 || qb.getLow5()<=low) low = qb.getLow5();
		}
		
		int actualDayPips = sp.getWinPips()-sp.getLostPips();
		int diffPips = actualDayPips-lastDayPips;
		//if (diffPips<0)
		//System.out.println(DateUtils.datePrint(cal)+" "+diffPips);
			
		//if (printResults){
			double winPer = sp.getWins()*100.0/sp.getTrades();
			double pf = sp.getWinPips()*1.0/sp.getLostPips();
			double pf$$ = sp.getWinPips$()*1.0/sp.getLostPips$();
			double avgPips = (sp.getWinPips()-sp.getLostPips())*0.1/sp.getTrades();
			double avgWin = sp.getWinPips()*0.1/sp.getWins();
			double avgLoss = sp.getLostPips()*0.1/sp.getLosses();
			double perDays = totalDaysTrading*100.0/totalDays;
			double factor = sp.getProfitPer()/sp.getMaxDD();
			double avgMaxAdversion = sp.getMaxAdversionAvg();
			//double var95 = sp.getMonthDataDD(2);
			double var95 = sp.getMonthDataDDRR(sp.getInitialBalance(),this.risk,2);
			if (true 
					&& (printOptions==0 || (printOptions==2 
									&& sp.getMaxDD()<=30.0 
										//&& sp.getProfitPer()>=100.0 
										&& perDays>=30.0)
					)
				)
			System.out.println(
					header
					+" || "
					+sp.getTrades()
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(pf$$, false)
					+" "+PrintUtils.Print2dec(avgPips, false)
					+" "+PrintUtils.Print2dec(avgWin, false)
					+" "+PrintUtils.Print2dec(avgLoss, false)
					+" "+PrintUtils.Print2dec(avgMaxAdversion, false)
					+" || "
					+" "+PrintUtils.Print2dec(sp.getActualBalance(), false)
					+" "+PrintUtils.Print2dec(sp.getProfitPer(), false)
					+" "+PrintUtils.Print2dec(sp.getMaxDD(), false)
					+" || "
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(perDays, false)
					+" "+PrintUtils.Print2dec(sp.getMaxDD(), false)
					+" "+PrintUtils.Print2dec(sp.getProfitPer()/sp.getMaxDD(), false)
					+" "+PrintUtils.Print2dec(var95,false)
					);
		//}
		return pf$$;
	}

	abstract public void doManagePositions(ArrayList<QuoteShort> dataBid,ArrayList<QuoteShort> dataAsk, int i, ArrayList<PositionShort> positions);
	
	abstract public void doEvaluateExits(
			ArrayList<QuoteShort> dataBid, 
			ArrayList<QuoteShort> dataAsk, 
			int i, ArrayList<PositionShort> positions,
			StratPerformance sp);

	abstract public int doEvaluateEntries(
			ArrayList<QuoteShort> dataBid, 
			ArrayList<QuoteShort> dataAsk, 
			ArrayList<Integer> maxMins,
			HashMap<Integer,ArrayList<Double>> spreads,
			int i, ArrayList<PositionShort> positions,
			boolean canTrade,StratPerformance sp);

}
