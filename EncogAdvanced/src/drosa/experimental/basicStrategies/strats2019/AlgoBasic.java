package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public abstract class AlgoBasic {
	
	ArrayList<Integer> atrArray = new ArrayList<Integer>();	
	int high = -1;
	int low = -1;
	Calendar cali = Calendar.getInstance();
	int comm = 15;
		
	public void doTest(			
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,int m1,int m2,
			StratPerformance sp,
			int debug,
			boolean printResults
			){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
				
		Calendar cal = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		int lastDay = -1;
		int totalDays = 0;
		int totalDaysTrading = 0;


		atrArray.add(800);
		sp.reset();
		sp.setInitialBalance(5000);
		//bucle de datos
		boolean dayTraded = false;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			boolean canTrade = true;
			
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
						if (q.getLow5()<=p.getSl()){
							pips = p.getSl()-p.getEntry();
							isClosed = true;
							//System.out.println("[long sl touched] "+pips+" "+(p.getTp()-p.getEntry()));
						}else if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry();
							isClosed = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q.getHigh5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry();
							isClosed = true;
						}else if (q.getLow5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry();
							isClosed = true;
						}
					}
				}
				
				if (isClosed){
					sp.addTrade(p.getMicroLots(),pips,comm);
					positions.remove(j);
				}else{
					j++;
				}
			}
			
			//evaluamos salidas especiales
			doEvaluateExits(data,i,positions,sp);	
			
			//trails
			doManagePositions(data,i,positions);
			
			//actualizamos high low
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		}
			
		if (printResults){
			double winPer = sp.getWins()*100.0/sp.getTrades();
			double pf = sp.getWinPips()*1.0/sp.getLostPips();
			double avgPips = (sp.getWinPips()-sp.getLostPips())*0.1/sp.getTrades();
			double avgWin = sp.getWinPips()*0.1/sp.getWins();
			double avgLoss = sp.getLostPips()*0.1/sp.getLosses();
			double perDays = totalDaysTrading*100.0/totalDays;
			
			System.out.println(
					header
					+" || "
					+sp.getTrades()
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(avgPips, false)
					+" "+PrintUtils.Print2dec(avgWin, false)
					+" "+PrintUtils.Print2dec(avgLoss, false)
					+" "+PrintUtils.Print2dec(sp.getMaxDD(), false)
					+" || "+" "+PrintUtils.Print2dec(perDays, false)
					);
		}
	}

	abstract public void doManagePositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions);
	
	abstract public void doEvaluateExits(ArrayList<QuoteShort> data, 
			int i, ArrayList<PositionShort> positions,
			StratPerformance sp);

	abstract public int doEvaluateEntries(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			boolean canTrade,StratPerformance sp);

}
