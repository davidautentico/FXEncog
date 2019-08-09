package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

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
	
	public void doTrailPositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions) {
		
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
		
	public void doTest(			
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,int m1,int m2,
			StratPerformance sp,
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

		atrArray.add(800);
		//sp.reset();
		//sp.setInitialBalance(20000);
		//bucle de datos
		boolean dayTraded = false;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			boolean canTrade = true;
			
			//System.out.println(DateUtils.datePrint(cal)+" "+positions.size());
			if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0) continue;
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					int range = high-low;
					ranges.add(range);
					
					int atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					
					atrArray.add(atr);
					totalDays++;
					if (dayTraded) totalDaysTrading++;
					
					int actualDayPips = sp.getWinPips()-sp.getLostPips();
					int diffPips = actualDayPips-lastDayPips;
					//if (diffPips<0)
					//System.out.println(DateUtils.datePrint(cal1)+" "+diffPips);
					lastDayPips = actualDayPips;
				}
				dayTraded = false;
				lastDay = day;
				high = -1;
				low = -1;
			}
						
			//evaluamos entradas
			int ntrades = doEvaluateEntries(data,i,positions,canTrade,sp);
			if (ntrades>0){
				dayTraded = true;
			}
			
			//evaluamos SL y Tp			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean isClosed = false;
				int pips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					if (p.getPositionType()==PositionType.LONG){
						int testValue = q.getLow5();
						if (
								testValue<=p.getSl() && p.getSl()>=0){
							pips = p.getSl()-p.getEntry();
							isClosed = true;
							//System.out.println("[long sl touched] "+pips+" "+(p.getTp()-p.getEntry()));
						}else if (q.getOpen5()>=p.getTp() && p.getTp()>=0){
							pips = p.getTp()-p.getEntry();
							isClosed = true;
						}
						
						if (p.getEntry()-testValue>=p.getMaxLoss()){
							p.setMaxLoss(p.getEntry()-testValue);
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						int testValue = q.getHigh5();
						if (testValue>=p.getSl() && p.getSl()>=0){
							pips = -p.getSl()+p.getEntry();
							isClosed = true;
						}else if (q.getOpen5()<=p.getTp() && p.getTp()>=0){
							pips = -p.getTp()+p.getEntry();
							isClosed = true;
						}
						
						if (-p.getEntry()+testValue>=p.getMaxLoss()){
							p.setMaxLoss(-p.getEntry()+testValue);
						}
					}
				}
				
				if (isClosed){
					sp.addTrade(p.getMicroLots(),pips,p.getMaxLoss(),p.getTransactionCosts(),cal);
					positions.remove(j);
				}else{
					j++;
				}
			}
			
			//evaluamos salidas especiales
			doEvaluateExits(data,i,positions,sp);	
			
			//custom manage
			doManagePositions(data,i,positions);
			
			doTrailPositions(data,i,positions);
			
			//actualizamos high low
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		}
		
		int actualDayPips = sp.getWinPips()-sp.getLostPips();
		int diffPips = actualDayPips-lastDayPips;
		//if (diffPips<0)
		//System.out.println(DateUtils.datePrint(cal)+" "+diffPips);
			
		//if (printResults){
			double winPer = sp.getWins()*100.0/sp.getTrades();
			double pf = sp.getWinPips()*1.0/sp.getLostPips();
			double avgPips = (sp.getWinPips()-sp.getLostPips())*0.1/sp.getTrades();
			double avgWin = sp.getWinPips()*0.1/sp.getWins();
			double avgLoss = sp.getLostPips()*0.1/sp.getLosses();
			double perDays = totalDaysTrading*100.0/totalDays;
			double factor = sp.getProfitPer()/sp.getMaxDD();
			double avgMaxAdversion = sp.getMaxAdversionAvg();
			double var95 = sp.getMonthDataDD(2);
			if (true 
					&& printOptions==0 
					||
					(factor>=30.0
					&& perDays>=30.0 
					//&& pf>=1.40
					&& var95<=10.0)
					)
			System.out.println(
					header
					+" || "
					+sp.getTrades()
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
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
					+" "+PrintUtils.Print2dec(sp.getMonthDataDD(2), false)
					);
		//}
	}

	abstract public void doManagePositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions);
	
	abstract public void doEvaluateExits(ArrayList<QuoteShort> data, 
			int i, ArrayList<PositionShort> positions,
			StratPerformance sp);

	abstract public int doEvaluateEntries(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			boolean canTrade,StratPerformance sp);

}
