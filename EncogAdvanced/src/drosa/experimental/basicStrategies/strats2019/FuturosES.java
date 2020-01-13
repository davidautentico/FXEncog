package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class FuturosES {
	
	public static ArrayList<QuoteShort> convertToEncog(String source,String dest) {
		ArrayList<QuoteShort> data = DAO.retrieveDataESNQ(source, DataProvider.KIBOTES,0);
		
		return data;
	}
	
	public static void doTestMR(
			ArrayList<QuoteShort> data,
			int y1,
			int y2,
			int m1,int m2,
			int h1,int h2,
			int bars,
			double thr,
			int tp,
			double sl,
			int posOrder,
			int debug
			) {
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
				
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		Calendar calThr = Calendar.getInstance();
		
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		int lastDay = -1;
		int totalDays = 0;
		int lastDayPips = 0;
		//sp.reset();
		//sp.setInitialBalance(20000);
		//bucle de datos
		boolean dayTraded = false;
		int maxDayLossAcc=0;
		double maxDayLossAcc$$=0;
		double dayBalance = 0;
		int maxDayPositions = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int dayTrades = 0;
		int dayOpen = -1;
		QuoteShort qm = new QuoteShort();
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int atr = 50;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();		
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				//System.out.println("[NEW DAY]");
				if (lastDay!=-1) {
					//if (high>0 && low>0){
						int range = (high-low)/25;
						ranges.add(range);
					
						//System.out.println(range+" "+high+" "+low);
						atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					//}
					//System.out.println("[day range] "+range+" "+atr+" || "+q1.toString());					
					totalDays++;
					lastHigh = high;
					lastLow = low;
				}
				maxDayLossAcc=0;
				maxDayLossAcc$$ = 0;
				maxDayPositions = 0;
				dayTraded = false;
				dayTrades = 0;
				lastDay = day;
				high = -1;
				low = -1;
				dayOpen = q.getOpen5();
			}
			
			closeArr.add(q.getOpen5());
			
			int smaValue = (int) MathUtils.average(closeArr,closeArr.size()-bars,closeArr.size()-1);
			//System.out.println(smaValue);
			
			int diffH = (q.getOpen5()-smaValue)/25;
			int diffL = (smaValue-q.getOpen5())/25;
			int slvalue = 25*(int) (sl*atr);
			int tpvalue = 25*(int) (tp);
			
			if (debug==1)
			System.out.println(smaValue+" "+diffH+" "+diffL+" "+atr);
			
			if (h>=h1 && h<=h2
					&& h!=13
					&& h!=14
					&& h!=15
					) {
				if (diffH>=thr*atr) {
					PositionShort p = new PositionShort();
					p.setEntry(q.getOpen5());
					p.setSl(q.getOpen5()+slvalue);
					p.setTp(q.getOpen5()-tpvalue);
					p.setPositionType(PositionType.SHORT);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOrder( positions.size());
					positions.add(p);
					
					//System.out.println("[SHORT] SL "+(p.getSl()-p.getEntry())+" || "+q.toString());
				}else if (diffL>=thr*atr) {
					PositionShort p = new PositionShort();
					p.setEntry(q.getOpen5());
					p.setTp(q.getOpen5()+tpvalue);
					p.setSl(q.getOpen5()-slvalue);
					p.setPositionType(PositionType.LONG);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOrder( positions.size());
					positions.add(p);
				}
			}
			
			int j=0;
			while (j<positions.size()) {
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN) {
					int pips = 0;
					boolean isClosed = false;
					if (p.getPositionType()==PositionType.SHORT) {
						if (q.getOpen5()<=smaValue) {
							pips = (p.getEntry()-q.getOpen5())/25;
							isClosed = true;
						}else {
							pips = (p.getEntry()-q.getOpen5())/25;
							if (q.getOpen5()>=p.getSl()) {
								//System.out.println("[CLOSED] SL "+pips+" || "+(p.getSl()-p.getEntry())+" || "+p.getEntry()+" || "+p.getSl()+" || "+q.toString());
								isClosed = true;
							}else if (q.getOpen5()<=p.getTp()) {
								isClosed = true;
							}
						}
					}else if (p.getPositionType()==PositionType.LONG) {
						if (q.getOpen5()>=smaValue) {
							pips = (q.getOpen5()-p.getEntry())/25;
							isClosed = true;
						}else {
							pips =  (q.getOpen5()-p.getEntry())/25;
							if (q.getOpen5()<=p.getSl()) {
								isClosed = true;
							}else if (q.getOpen5()>=p.getTp()) {
								isClosed = true;
							}
						}
					}
					
					if (isClosed) {
						if (p.getOrder()>=posOrder) {
							if (pips>=0) {
								winPips += pips;
								wins++;
							}else {
								lostPips += -pips;
								losses++;
							}
						}
						positions.remove(j);
					}else {
						j++;
					}
				}
			}//j
			
			if (q.getClose5()>=high || high==-1) high = q.getClose5();
			if (q.getClose5()<=low || low==-1) low = q.getClose5();
		}	
		
		int trades =  wins+losses;
		double pf = winPips*1.0/lostPips;
		double avgW = (winPips)*1.0/wins;
		double avgL = (lostPips)*1.0/losses;
		double avg = (winPips-lostPips)*1.0/trades;
		double winPer = wins*100.0/trades;
		atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+bars+" "+PrintUtils.Print2dec(thr, false)
				+" "+tp
				+" "+PrintUtils.Print2dec(sl, false)
				+" || "
				+" "+trades+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgW, false)
				+" "+PrintUtils.Print2dec(avgL, false)
				+" ||| "+atr
				);
	}
	
	
	public static void doTestMR2(
			ArrayList<QuoteShort> data,
			int y1,
			int y2,
			int m1,int m2,
			int h1,int h2,
			int bars,
			double thr,
			int tp,
			double sl,
			int posOrder,
			int minTime,
			int debug
			) {
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
				
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		Calendar calThr = Calendar.getInstance();
		
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		int lastDay = -1;
		int totalDays = 0;
		int lastDayPips = 0;
		//sp.reset();
		//sp.setInitialBalance(20000);
		//bucle de datos
		boolean dayTraded = false;
		int maxDayLossAcc=0;
		double maxDayLossAcc$$=0;
		double dayBalance = 0;
		int maxDayPositions = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int dayTrades = 0;
		int dayOpen = -1;
		QuoteShort qm = new QuoteShort();
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		int atr = 50;
		int lastPrice = 0;
		int order = 0;
		int lastTouch = 0;
		int comm = 2;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();		
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				//System.out.println("[NEW DAY]");
				if (lastDay!=-1) {
					//if (high>0 && low>0){
						int range = (high-low)/25;
						ranges.add(range);
					
						//System.out.println(range+" "+high+" "+low);
						atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
					//}
					//System.out.println("[day range] "+range+" "+atr+" || "+q1.toString());					
					totalDays++;
					lastHigh = high;
					lastLow = low;
				}
				maxDayLossAcc=0;
				maxDayLossAcc$$ = 0;
				maxDayPositions = 0;
				dayTraded = false;
				lastDay = day;
				high = -1;
				low = -1;
				if (order>0) {
					dayTrades++;
				}
				order = 0;
				dayOpen = q.getOpen5();
			}
			
			closeArr.add(q.getOpen5());
			
			int smaValue = (int) MathUtils.average(closeArr,closeArr.size()-bars,closeArr.size()-1);
			//System.out.println(smaValue);
			
			int diffH = (q.getOpen5()-smaValue)/25;
			int diffL = (smaValue-q.getOpen5())/25;
			int slvalue = 25*(int) (sl*atr);
			int tpvalue = 25*(int) (tp);
			int diffLastPriceH = (q.getOpen5()-lastPrice)/25;
			int diffLastPriceL = (-q.getOpen5()+lastPrice)/25;
			int diffTime = 0;
			
			if (q1.getOpen5()<smaValue && q.getOpen5()>smaValue) lastTouch = i;
			else if (q1.getOpen5()>smaValue && q.getOpen5()<smaValue) lastTouch = i;
			
			diffTime = i-lastTouch;
			
			if (debug==1)
			System.out.println(smaValue+" "+diffH+" "+diffL+" "+atr);
			
			if (h>=h1 && h<=h2
					//&& h!=13
					//&& h!=14
					//&& h!=15
					) {
				if (diffTime>=minTime
						&& diffH>=thr*atr
						//&& diffH>=20
						) {
					PositionShort p = new PositionShort();
					p.setEntry(q.getOpen5());
					p.setSl(q.getOpen5()+slvalue);
					p.setTp(q.getOpen5()-tpvalue-comm);
					p.setPositionType(PositionType.SHORT);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOrder(order++);
					positions.add(p);
					lastPrice = q.getOpen5();
					//System.out.println("[SHORT] SL "+(p.getSl()-p.getEntry())+" || "+q.toString());
				}else if (diffTime>=minTime
						//&& q.getOpen5() < smaValue
						&& diffL>=thr*atr
						//&& diffL>=20
						) {
					PositionShort p = new PositionShort();
					p.setEntry(q.getOpen5());
					p.setTp(q.getOpen5()+tpvalue+comm);
					p.setSl(q.getOpen5()-slvalue);
					p.setPositionType(PositionType.LONG);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOrder(order++);
					positions.add(p);
					lastPrice = q.getOpen5();
				}
			}
			
			int j=0;
			while (j<positions.size()) {
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN) {
					double pips = 0;
					boolean isClosed = false;
					if (p.getPositionType()==PositionType.SHORT) {
						if (q.getOpen5()<=smaValue) {
							pips = (p.getEntry()-q.getOpen5())/25;
							isClosed = true;
						}
						
						if (!isClosed) {
							pips = (p.getEntry()-q.getOpen5())/25;
							if (q.getOpen5()>=p.getSl()) {
								//System.out.println("[CLOSED] SL "+pips+" || "+(p.getSl()-p.getEntry())+" || "+p.getEntry()+" || "+p.getSl()+" || "+q.toString());
								isClosed = true;
							}else if (q.getOpen5()<=p.getTp()) {
								isClosed = true;
							}
						}
					}else if (p.getPositionType()==PositionType.LONG) {
						if (q.getOpen5()>=smaValue) {
							pips = (q.getOpen5()-p.getEntry())/25;
							isClosed = true;
						}
						
						if (!isClosed) {
							pips =  (q.getOpen5()-p.getEntry())/25;
							if (q.getOpen5()<=p.getSl()) {
								isClosed = true;
							}else if (q.getOpen5()>=p.getTp()) {
								isClosed = true;
							}
						}
					}
					
					if (isClosed) {
						pips-=comm;
						if (p.getOrder()>=posOrder) {
							if (pips>=0) {
								winPips += pips;
								wins++;
							}else {
								lostPips += -pips;
								losses++;
							}
						}
						positions.remove(j);
					}else {
						j++;
					}
				}
			}//j
			
			if (q.getClose5()>=high || high==-1) high = q.getClose5();
			if (q.getClose5()<=low || low==-1) low = q.getClose5();
		}	
		
		int trades =  wins+losses;
		double pf = winPips*1.0/lostPips;
		double avgW = (winPips)*1.0/wins;
		double avgL = (lostPips)*1.0/losses;
		double avg = (winPips-lostPips)*1.0/trades;
		double winPer = wins*100.0/trades;
		atr = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+bars+" "+PrintUtils.Print2dec(thr, false)
				+" "+tp
				+" "+PrintUtils.Print2dec(sl, false)
				+" "+minTime
				+" || "
				+" "+trades+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgW, false)
				+" "+PrintUtils.Print2dec(avgL, false)
				+" ||| "+atr
				+" ||| "+dayTrades+" "+totalDays+" "+PrintUtils.Print2dec(dayTrades*100.0/totalDays, false)
				);
	}

	public static void main(String[] args) {
		String source = "c:\\fxdata\\futuros\\ES.txt";
		String dest = "c:\\fxdata\\futuros\\ES.csv";
		
		ArrayList<QuoteShort> data = FuturosES.convertToEncog(source, dest);
		System.out.println(data.size());
		
		for (int y1=2009;y1<=2009;y1++) {
			int y2 = y1+7;
			for (int h1=16;h1<=16;h1++) {
				int h2 = h1+7;
				for (int bars=90;bars<=90;bars+=5) {
					for (double thr=0.10;thr<=0.80;thr+=0.10) {
						for (double sl=0.5;sl<=0.5;sl+=0.1) {
							int tp =20;
							for (int posOrder=0;posOrder<=0;posOrder+=1) {
								for (int minTime=0;minTime<=0;minTime+=15) {
									FuturosES.doTestMR2(data, y1, y2, 0, 11,h1,h2, bars, thr, tp, sl,posOrder,minTime, 0);
								}
							}
						}
					}
				}
			}
		}
	}

}
