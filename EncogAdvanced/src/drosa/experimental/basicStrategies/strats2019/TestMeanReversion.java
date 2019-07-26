package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMeanReversion {
	
	public static void doTestAlphadudePort(String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			ArrayList<String> strats,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			double aRisk,
			int debug){
		
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
		for (int i=0;i<=399;i++){
			openArr.add(data.get(i).getOpen5());
		}
				
		int dayTrade = 0;
		int totalDaysTrade = 0;
		mode = 0;
		int modeIdx = 0;
		int dayPips = 0;
		int lastPips = 0;
		for (int i=400;i<data.size()-2;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
			qLast = q;
			
			comm = 20;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
					dayPipsArr.add(diffP);
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				dayPips = 0;
				totalDays++;
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			
			openArr.add(q.getOpen5());
					
			ishOk = h>=0 && h<=9;
			
			//evaluamos para cada estrategia
			for (int s=0;s<=strats.size()-1;s++){
				String str = strats.get(s);
				String[] values = str.split(" ");
				int period	= Integer.valueOf(values[0]);
				int value 	= Integer.valueOf(values[1]);
				double aF 		= Float.valueOf(values[2]);
				double fsl = 1.0;
				
				//valor de la sma
				int smaValue = (int) MathUtils.average(openArr, openArr.size()-period,openArr.size()-1);
				
				//vemos si hay cruce y anotamos el momento del cruce
				if (q.getOpen5()>=smaValue){				
					if (mode<=0) modeIdx = i;
					mode = 1;
				}else{
					if (mode>=0) modeIdx = i;
					mode = -1;
				}
								
				if (ishOk){
					int dist = i-modeIdx;
					int minPips = (int) (aF*range);
					int slMinPips = (int) ( fsl*range);
					
					if (!isMomentum) minPips = 99999999;
					if (mode==1 
							&& modeIdx>0 
							&& dist>=value//si la candle es la suya
							&& q.getOpen5()-smaValue>=aF*range
							){
					//if (spread<=-minPips){
						int entry = q.getOpen5();
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setMaxProfit(entry);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
						
						p.setPositionType(PositionType.SHORT);
						p.setTp(p.getEntry()-999999);
						p.setSl(p.getEntry()+slMinPips);
						if (isMomentum){
							p.setPositionType(PositionType.LONG);
							p.setTp(p.getEntry()+ 999 *minPips);
							p.setSl(p.getEntry()-minPips);
						}
						
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(minPips*0.1);
						int microLots = (int) (riskPip/0.10);
						p.setMicroLots(microLots);
						
						dayTrade = 1;
						//guardamos su sma
						p.setExtraParam(period);
						positions.add(p);
					}else if (mode==-1
							&& modeIdx>0 
							&& dist>=value
							&& -q.getOpen5()+smaValue>=aF*range
							){
					//}else if(spread>=minPips){
						int entry = q.getOpen5();
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setMaxProfit(entry);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
						
						p.setPositionType(PositionType.LONG);
						p.setTp(p.getEntry()+999999);
						p.setSl(p.getEntry()-slMinPips);
						if (isMomentum){
							p.setPositionType(PositionType.SHORT);
							p.setTp(p.getEntry()- 999 *minPips);
							p.setSl(p.getEntry()+minPips);
						}
						
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(minPips*0.1);
						int microLots = (int) (riskPip/0.10);
						p.setMicroLots(microLots);
						
						dayTrade = 1;
						//guardamos su sma
						p.setExtraParam(period);
						positions.add(p);
					}
				}//H
			}//strats
			
									
			int j = 0;
			boolean closeAll = false;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					int extraPeriod = p.getExtraParam();
					
					if (!isMomentum){
						int smaValue = (int) MathUtils.average(openArr, openArr.size()-extraPeriod,openArr.size()-1);
						if (q.getClose5()>=smaValue) mode = 1;
						else mode = -1;
					}
					
					//spread = smaValue - q.getClose5();					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  q.getClose5()-p.getEntry();
						if (	
								//i-p.getOpenIndex()>=12
								(mode==1 && !isMomentum) || (mode==-1 && isMomentum)
								//&& q.getClose5()-p.getEntry()>=50
								){
							p.setMaxProfit(q.getClose5());
							pips =  q.getClose5()-p.getEntry();
							isClose = true;
						}else{
							//time exits
							if (h==23 && min==55){
								pips =  q.getClose5()-p.getEntry();
								//isClose = true;
							}
							if (q.getHigh5()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (q.getLow5()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (q.getClose5()-p.getEntry()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(q.getClose5()-p.getEntry()));
									int newSl = p.getEntry()+toTrail;
									if (newSl>=p.getSl() && q.getClose5()-newSl>=20) p.setSl(newSl);
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-q.getClose5();
						if (//i-p.getOpenIndex()>=12
								(mode==-1 && !isMomentum) || (mode==1 && isMomentum)
								//&& p.getEntry()-q.getClose5()>=50
								){
							p.setMaxProfit(q.getClose5());
							pips = p.getEntry()-q.getClose5();
							isClose = true;
						}else{
							//time exits
							if (h==23 && min==55){
								pips = p.getEntry()-q.getClose5();
								//isClose = true;
							}
							if (q.getLow5()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (q.getHigh5()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-q.getClose5()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(-q.getClose5()+p.getEntry()));
									int newSl = p.getEntry()-toTrail;
									if (newSl<=p.getSl() && -q.getClose5()+newSl>=20) p.setSl(newSl);
								}
							}
						}
					}
					
					if (isClose){
						
						pips-=comm;
						
						dayPips += pips;
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							//double win$$ = p.getPip$$()*pips*0.1;
							//balance += win$$;
							//equitity += win$$;
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
		}
		
		//estudio de years
		int posYears = 0;
		Iterator it = yWinPips.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer,Integer> pair = (Map.Entry)it.next();
	        int year = pair.getKey();
	        int wPips = pair.getValue();
	        int lPips = 0;
	        if (yLostPips.containsKey(year))
	        	lPips = yLostPips.get(year);
	        int netPips = wPips-lPips;
	        if (netPips>=0) posYears++;
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		
		if (debug==2
				|| (avg>=3.0 && posYears>=12 && maxDD<70.0 && trades>=500 && perDays>=20.0)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+posYears
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				);
	}
	
	public static double doTestAlphadude(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int n,
			double aF,
			int backBars,
			int atrLimit,
			ArrayList<String> strat,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			int timeFrame,
			int maxOpenPositions,
			double aRisk,
			boolean isTransactionsCostIncluded,
			int debug,
			boolean printDetails,
			boolean printDailyPips,
			StratPerformance sp
			){
		
		Calendar cal = Calendar.getInstance();
		
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		//System.out.println(DateUtils.datePrint(calFrom)+" "+DateUtils.datePrint(calTo));
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		//medicion recuperacion
		int maxPips				= 0;
		int maxPipsIdx			= 0;
		int maxPeakPipsIdx 		= 0;
		int maxRecoveryTime 	= 0;
		int actualRecoveryTime 	= 0;
		
		int comm = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		HashMap<Integer,ArrayList<Integer>> yTrades = new HashMap<Integer,ArrayList<Integer>>();
		HashMap<Integer,ArrayList<Integer>> mTrades = new HashMap<Integer,ArrayList<Integer>>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
		ArrayList<Integer> smaArr = new ArrayList<Integer>();
	
		for (int i=0;i<=n-1;i++){
			openArr.add(data.get(i).getOpen5());
		}
		
		
		int dayTrade = 0;
		int totalDaysTrade = 0;
		mode = 0;
		int modeIdx = 0;
		int dayPips = 0;
		int lastPips = 0;
		String[] valuesH0 = strat.get(0).split(" ");String[] valuesH1 = strat.get(1).split(" ");String[] valuesH2 = strat.get(2).split(" ");
		String[] valuesH3 = strat.get(3).split(" ");String[] valuesH4 = strat.get(4).split(" ");String[] valuesH5 = strat.get(5).split(" ");
		String[] valuesH6 = strat.get(6).split(" ");String[] valuesH7 = strat.get(7).split(" ");String[] valuesH8 = strat.get(8).split(" ");
		String[] valuesH9 = strat.get(9).split(" ");String[] valuesH10 = strat.get(10).split(" ");String[] valuesH11 = strat.get(11).split(" ");
		String[] valuesH12 = strat.get(12).split(" ");String[] valuesH13 = strat.get(13).split(" ");String[] valuesH14 = strat.get(14).split(" ");
		String[] valuesH15 = strat.get(15).split(" ");String[] valuesH16 = strat.get(16).split(" ");String[] valuesH17 = strat.get(17).split(" ");
		String[] valuesH18 = strat.get(18).split(" ");String[] valuesH19 = strat.get(19).split(" ");String[] valuesH20 = strat.get(20).split(" ");
		String[] valuesH21 = strat.get(21).split(" ");String[] valuesH22 = strat.get(22).split(" ");String[] valuesH23 = strat.get(23).split(" ");
		
		boolean canTrade = true;
		int smaValue = -1;
		boolean newBar = true;
		QuoteShort actualBar = new QuoteShort();
		QuoteShort evaluateBar = new QuoteShort();
		//medicion recuperacion
		maxPips				= 0;
		maxPipsIdx			= n+1;
		maxRecoveryTime 	= 0;
		for (int i=n+1;i<data.size()-2;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			//if (y>y2) break;
			
			//if (y<y1 || y>y2) continue;
			 
			if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0) continue;
			
			
			qLast = q;
			
			comm = 00;
			
			if (day!=lastDay){	
				if (lastDay==-1){
					maxPipsIdx			= i;
				}
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
					dayPipsArr.add(diffP);
					
					/*if (diffP<=-range*5){
						System.out.println(diffP+" || "+q1.toString());
					}*/										
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				//mode = 0;
				dayPips = 0;
				totalDays++;
				
				newBar = true;
				actualBar.copy(q);
				
				if (printDailyPips){
					System.out.println(winPips-lostPips);
				}
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			
								
			String[] values = valuesH0;
			if (h==1) values = valuesH1;if (h==2) values = valuesH2;if (h==3) values = valuesH3;if (h==4) values = valuesH4;
			if (h==5) values = valuesH5;if (h==6) values = valuesH6;if (h==7) values = valuesH7;if (h==8) values = valuesH8;
			if (h==9) values = valuesH9;if (h==10) values = valuesH10;if (h==11) values = valuesH11;if (h==12) values = valuesH12;
			if (h==13) values = valuesH13;if (h==14) values = valuesH14;if (h==15) values = valuesH15;if (h==16) values = valuesH16;
			if (h==17) values = valuesH17;if (h==18) values = valuesH18;if (h==19) values = valuesH19;if (h==20) values = valuesH20;
			if (h==21) values = valuesH21;if (h==22) values = valuesH22;if (h==23) values = valuesH23;
			ishOk = values[0] !="-1";
			//valor de la sma
			
			boolean isFOMC = isFOMCDay(cal.get(Calendar.DAY_OF_MONTH),m+1,y);
			isFOMC = false;
			
			if (min%timeFrame==0){
				openArr.add(q.getOpen5());
				canTrade = true;
				newBar = true;
				//evaluateBar.copy(actualBar);
				//actualBar.copy(q);
				smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
				smaArr.add(smaValue);
				//vemos si hay cruce y anotamos el momento del cruce
				if (q.getOpen5()>=smaValue){				
					if (mode<=0){
						canTrade = true;
						modeIdx = i;
					}
					mode = 1;
				}else{
					if (mode>=0){
						canTrade = true;
						modeIdx = i;
					}
					mode = -1;
				}
			}
			
			if (ishOk){
				if (h==0 && min<15) ishOk=false;
				
			}
			
			int maxMin = maxMins.get(i-1);
					
			int smaDir = 0;
			
			if (i>=backBars)
			if (smaArr.size()>=20){
				int sma1 = data.get(i).getOpen5();
				int sma5 =data.get(i-backBars).getOpen5();
				if (sma1>=sma5) smaDir = 1;
				else smaDir = -1;
			}
			
			/*smaDir = 0;
			if (maxMin>=backBars) smaDir = 1;
			else if (maxMin<=-backBars) smaDir = -1;*/
			
			isMomentum = false;
			if (ishOk){
				//if (h>=10 && h<=21) isMomentum = true;
			}
			
			if (ishOk
					&& !isFOMC
					&& positions.size()<=maxOpenPositions
					&& range<=atrLimit
					//&& canTrade
					//&& dayTrade==0
					){												
				int dist = i-modeIdx;
				int value = Integer.valueOf(values[1]);
				int minPips = (int) (aF*range);
				int slMinPips = (int) (1.0*range);
				int transactionCosts = TradingUtils.getTransactionCosts(y, h,1);
				//transactionCosts = 0;
				//if (!isMomentum) minPips = 99999999;
				//System.out.println(DateUtils.datePrint(cal)+" minPips= "+minPips+" rango= "+range+" smaValue= "+smaValue
						//+" || "+(q.getOpen5()-smaValue)+" "+(smaValue-q.getOpen5()));
				if (mode==1 
						&& modeIdx>0 
						&& ((!isMomentum && dist>=value)  || (isMomentum && dist<=value))//si la candle es la suya
						&& ((n==0 && maxMin>=backBars) ||
							(n>0 && q.getOpen5()-smaValue>=minPips)
								&& (smaDir==1 || backBars==0))
						//&& q.getOpen5()-doValue>=0.7*range
						//&& q1.getClose5()<=q.getOpen5()-50
						){
				//if (spread<=-minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.SHORT);
					p.setTp((int) (p.getEntry()-10.5*range));
					p.setSl((int) (p.getEntry()+0.6*range));
					
					if (n==0){
						p.setTp((int) (p.getEntry()-0.15*range));
						p.setSl((int) (p.getEntry()+0.45*range));
					}
					if (isMomentum){
						p.setPositionType(PositionType.LONG);
						p.setTp((int) (p.getEntry()+5.0*range));
						p.setSl((int) (p.getEntry()-1.0*range));
					}
					
					minPips = p.getSl()-p.getEntry();
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);
					p.setTransactionCosts(transactionCosts);
					p.setExtraParam(n);
				
				
					dayTrade = 1;
					positions.add(p);
					
					canTrade = false;
				}else if (mode==-1
						&& modeIdx>0 
						&& ((!isMomentum && dist>=value)  || (isMomentum && dist<=value))
						&& ((n==0 && maxMin<=-backBars) ||
							(n>0 && -q.getOpen5()+smaValue>=minPips)
								&& (smaDir==-1 || backBars==0))
						//&& -q.getOpen5()+doValue>=0.7*range
						
						//&& q1.getClose5()>=q.getOpen5()+50
						){
				//}else if(spread>=minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.LONG);
					p.setTp((int) (p.getEntry()+10.5*range));
					p.setSl((int) (p.getEntry()-0.6*range));
					
					if (n==0){
						p.setTp((int) (p.getEntry()+0.15*range));
						p.setSl((int) (p.getEntry()-0.2*range));
					}
					if (isMomentum){
						p.setPositionType(PositionType.SHORT);
						p.setTp((int) (p.getEntry()-5*range));
						p.setSl((int) (p.getEntry()+1.0*range));
					}
					
					minPips = p.getEntry()-p.getSl();
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					if (microLots<1) microLots = 1;
					p.setMicroLots(microLots);
					p.setTransactionCosts(transactionCosts);
					
					dayTrade = 1;
					positions.add(p);
					
					canTrade = false;
				}
			}//H
			
						
			int j = 0;
			boolean closeAll = false;
			QuoteShort qe = q;
			//if (newBar)//solo se evalua al cierre de cada timeframe
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					int tcosts = p.getTransactionCosts();
					
					//n = p.getExtraParam();
					//int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
					boolean isClose = false;
					
					//spread = smaValue - q.getClose5();					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  qe.getClose5()-p.getEntry();
						if (n>0){
							if ((mode==1 && !isMomentum) || (mode==-1 && isMomentum)
									){
								p.setMaxProfit(qe.getClose5());
								pips =  qe.getClose5()-p.getEntry();
								isClose = true;
							}
						}

						if (!isClose){
							//time exits
							if (h==23 && min>=55){
								pips =  qe.getClose5()-p.getEntry();
								//isClose = true;
							}
							if (qe.getHigh5()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (qe.getLow5()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (qe.getClose5()-p.getEntry()>=0){
								if (isMomentum
									 && qe.getClose5()-p.getEntry()>=20000	
										){
									int tpips = (int) (0.10*(qe.getClose5()-p.getEntry()));
									int newSL = p.getEntry()+10;
									if (tpips>=10 && newSL>=p.getSl()){
										p.setSl(newSL);
									}
								}else if (qe.getClose5()-p.getEntry()>=10 && !isMomentum){
									pips =  qe.getClose5()-p.getEntry();
									//isClose = true;
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-qe.getClose5();
						
						if (n>0){
							if ((mode==-1 && !isMomentum) || (mode==1 && isMomentum)
									){
								p.setMaxProfit(qe.getClose5());
								pips = p.getEntry()-qe.getClose5();
								isClose = true;
							}
						}
						
						if (!isClose){
							//time exits
							if (h==23 && min>=55){
								pips = p.getEntry()-qe.getClose5();
								//isClose = true;
							}
							if (qe.getLow5()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (qe.getHigh5()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-q.getClose5()>=00){
								if (p.getEntry()-q.getClose5()>=20000 && isMomentum){
									int tpips = (int) (0.10*(-qe.getClose5()+p.getEntry()));
									int newSL = p.getEntry()-10;
									if (tpips>=10 && newSL<=p.getSl()){
										p.setSl(newSL);
									}									
								}else if (p.getEntry()-q.getClose5()>=10 && !isMomentum){
									pips = p.getEntry()-qe.getClose5();
									//isClose = true;
								}
							}
						}
					}
					
					if (isClose){
						
						if (!isTransactionsCostIncluded) tcosts = 0;
						//tcosts = p.getTransactionCosts();
						
						pips-=tcosts;
						
						if (!yTrades.containsKey(y)) yTrades.put(y,new ArrayList<Integer>());
						ArrayList<Integer> trades = yTrades.get(y);
						trades.add(pips);
						
						//por mes
						if (!mTrades.containsKey(y)){
							mTrades.put(y,new ArrayList<Integer>());
							for (int t=0;t<=11;t++){
								mTrades.get(y).add(0);
							}
						}						
						trades = mTrades.get(y);
						int accm = trades.get(month);
						trades.set(month, accm+pips);
						
						dayPips += pips;
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
	
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
						
						
																													
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
			
			//actualizacion pips
			int totalPips = winPips-lostPips;
			int rt = i-maxPipsIdx;
			if (rt>maxRecoveryTime){
				maxRecoveryTime = rt;
				//System.out.println("New Peak: "+q.toString()+" || "+maxRecoveryTime*1.0/288
					//	+" "+i+" "+maxPipsIdx);
			}
			if (totalPips>maxPips){
				maxPips = totalPips;
				maxPipsIdx = i;
				//System.out.println("New Peak: "+q.toString()+" || "+maxRecoveryTime*1.0/288
						//	+" "+i+" "+maxPipsIdx);
			}
		}
		
		//estudio de years
		int posYears = 0;
		double accPf = 0;
		int countPf = 0;
		List sortedKeys=new ArrayList(yTrades.keySet());
		Collections.sort(sortedKeys);
		
		for (int k=0;k<sortedKeys.size();k++){		
		//Iterator it = yTrades.entrySet().iterator();
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = yTrades.get(year);//pair.getValue();
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);
	        	
	        	if (pips>=0) wPips+=pips;
	        	else lPips-=pips;
	        }
	        
	        double yPf = wPips*1.0/lPips;
	        int netPips = wPips-lPips;
	        double avgPips = (wPips-lPips)*0.1/trades.size();
	        if (avgPips>=0.0) posYears++;//al menos un pip de margen
	        if (lPips>0){
	        	accPf += wPips*1.0/lPips;
	        	countPf++;
	        	if (printDetails)
	        	System.out.println(year
	        			+" avgpf= "+PrintUtils.Print2dec(wPips*1.0/lPips, false)
	        			+" "+trades.size()
	        			+" "+PrintUtils.Print2dec(avgPips, false)
	        			+" "+wPips
	        			+" "+lPips
	        			);
	        }
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int posMonths = 0;
		int negMonths = 0;
		int totalMonths = 0;
		sortedKeys.clear();
		sortedKeys=new ArrayList(mTrades.keySet());
		Collections.sort(sortedKeys);
		//it = mTrades.entrySet().iterator();
		for (int k=0;k<sortedKeys.size();k++){	
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = mTrades.get(year);
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);	        	
	        	if (pips>0) posMonths++;
	        	if (pips!=0) totalMonths++;
	        }	      
	       // it.remove(); // avoids a ConcurrentModificationException
	    }
		
		
		ArrayList<Integer> cleanArr = new ArrayList<Integer>();
		for (int i=0;i<dayPipsArr.size();i++){
			int pips = dayPipsArr.get(i);
			if (pips!=0){
				cleanArr.add(pips);
			}
		}
	
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		double avgPf = accPf/countPf;
		double avgRecoveryTime = 0.0;
		double ddRT = maxRecoveryTime*1.0/288;
		
		if (sp!=null){
			sp.setPf(pf);
			sp.setMaxDD(maxDD);
			sp.setWinPips(winPips);
			sp.setLostPips(lostPips);
			sp.setTrades(trades);
		}
		
		if (debug==2
				|| (avg>=0.0 
				//&& pf>=1.4
				//&& maxDD<=25 
				//&& ff>=10
				//&& posYears>=80 
				//&& ff>=5.0
				//&& trades>=300 
				&& ddRT<=200.0
				&& perDays>=1000.0
				)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				DateUtils.datePrint(calFrom)
				+" "+DateUtils.datePrint(calTo)
				+" "+header
				+" "+PrintUtils.Print2dec(aRisk, false)
				+" "+timeFrame
				+" "+maxOpenPositions
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "+(winPips-lostPips)
				+" || "+PrintUtils.Print2dec(maxRecoveryTime*1.0/288, false)
				+" || "
				+" "+PrintUtils.Print2dec(posMonths*100.0/totalMonths, false)
				+" "+posYears
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(avgPf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(perR, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				+" || "+PrintUtils.Print2dec(maxRecoveryTime*1.0/288, false)//en dias
				);
		
		
		return pf;
	}
	
	
		

	//next FOMC DAY
	private static boolean isFOMCDay(int dd, int m, int y) {
		// TODO Auto-generated method stub
		if (y>=2015){
			if (y==2019 && m==6 && dd==20) return true;
			if (y==2019 && m==5 && dd==2) return true;
			if (y==2019 && m==3 && dd==21) return true;
			
			if (y==2018 && m==12 && dd==20) return true;
			if (y==2018 && m==11 && dd==9) return true;
			if (y==2018 && m==9 && dd==27) return true;
			if (y==2018 && m==8 && dd==2) return true;
			if (y==2018 && m==5 && dd==3) return true;
			if (y==2018 && m==3 && dd==22) return true;
			if (y==2018 && m==2 && dd==1) return true;
			
			if (y==2017 && m==12 && dd==14) return true;
			if (y==2017 && m==11 && dd==2) return true;
			if (y==2017 && m==9 && dd==21) return true;
			if (y==2017 && m==7 && dd==27) return true;
			if (y==2017 && m==6 && dd==15) return true;
			if (y==2017 && m==5 && dd==4) return true;
			if (y==2017 && m==3 && dd==16) return true;
			if (y==2017 && m==2 && dd==2) return true;
			
			if (y==2016 && m==12 && dd==15) return true;
			if (y==2016 && m==11 && dd==3) return true;
			if (y==2016 && m==9 && dd==22) return true;
			if (y==2016 && m==7 && dd==28) return true;
			if (y==2016 && m==6 && dd==16) return true;
			if (y==2016 && m==4 && dd==28) return true;
			if (y==2016 && m==3 && dd==17) return true;
			if (y==2016 && m==1 && dd==28) return true;
			
			
			if (y==2015 && m==12 && dd==17) return true;
			if (y==2015 && m==10 && dd==29) return true;
			if (y==2015 && m==9 && dd==18) return true;
			if (y==2015 && m==7 && dd==30) return true;
			if (y==2015 && m==4 && dd==30) return true;
			if (y==2015 && m==3 && dd==19) return true;
			if (y==2015 && m==1 && dd==29) return true;
		}
		
		
		return false;
	}

	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			ArrayList<String> strat,
			boolean isMomentum,
			double aRisk,
			int debug
			){
	
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 20;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		//
		
		
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
		int n = 400;
		for (int i=0;i<=n-1;i++){
			openArr.add(data.get(i).getOpen5());
		}
		
		String[] valuesH0 = strat.get(0).split(" ");String[] valuesH1 = strat.get(1).split(" ");String[] valuesH2 = strat.get(2).split(" ");
		String[] valuesH3 = strat.get(3).split(" ");String[] valuesH4 = strat.get(4).split(" ");String[] valuesH5 = strat.get(5).split(" ");
		String[] valuesH6 = strat.get(6).split(" ");String[] valuesH7 = strat.get(7).split(" ");String[] valuesH8 = strat.get(8).split(" ");
		String[] valuesH9 = strat.get(9).split(" ");String[] valuesH10 = strat.get(10).split(" ");String[] valuesH11 = strat.get(11).split(" ");
		String[] valuesH12 = strat.get(12).split(" ");String[] valuesH13 = strat.get(13).split(" ");String[] valuesH14 = strat.get(14).split(" ");
		String[] valuesH15 = strat.get(15).split(" ");String[] valuesH16 = strat.get(16).split(" ");String[] valuesH17 = strat.get(17).split(" ");
		String[] valuesH18 = strat.get(18).split(" ");String[] valuesH19 = strat.get(19).split(" ");String[] valuesH20 = strat.get(20).split(" ");
		String[] valuesH21 = strat.get(21).split(" ");String[] valuesH22 = strat.get(22).split(" ");String[] valuesH23 = strat.get(23).split(" ");
		int dayTrade = 0;
		int totalDaysTrade = 0;
		for (int i=n;i<data.size()-2;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
			qLast = q;
			
			comm = 20;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			
			openArr.add(q.getOpen5());
			//int spread = smaValue - q.getOpen5();
			//System.out.println(spread);
			
			
			String[] values = valuesH0;
			if (h==1) values = valuesH1;if (h==2) values = valuesH2;if (h==3) values = valuesH3;if (h==4) values = valuesH4;
			if (h==5) values = valuesH5;if (h==6) values = valuesH6;if (h==7) values = valuesH7;if (h==8) values = valuesH8;
			if (h==9) values = valuesH9;if (h==10) values = valuesH10;if (h==11) values = valuesH11;if (h==12) values = valuesH12;
			if (h==13) values = valuesH13;if (h==14) values = valuesH14;if (h==15) values = valuesH15;if (h==16) values = valuesH16;
			if (h==17) values = valuesH17;if (h==18) values = valuesH18;if (h==19) values = valuesH19;if (h==20) values = valuesH20;
			if (h==21) values = valuesH21;if (h==22) values = valuesH22;if (h==23) values = valuesH23;
			ishOk = values[0] !="-1";
			
			
			if (ishOk
					&& range>=1000
					){
				n		= Integer.valueOf(values[0]);
				double fMinPips 	= Float.valueOf(values[1]);
				int minPips = (int) (fMinPips*range);						
				int tpmult = Integer.valueOf(values[2]);
				
				int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);			
				int spread = q.getOpen5() - smaValue;
				if (spread>=minPips){
				//if (spread<=-minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.SHORT);
					p.setTp(p.getEntry()- tpmult *minPips);
					p.setSl(p.getEntry()+minPips);
					if (isMomentum){
						p.setPositionType(PositionType.LONG);
						p.setTp(p.getEntry()+ tpmult *minPips);
						p.setSl(p.getEntry()-minPips);
					}
					
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);
					
					dayTrade = 1;
					positions.add(p);
				}else if (spread<=-minPips){
				//}else if(spread>=minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.LONG);
					p.setTp(p.getEntry()+ tpmult *minPips);
					p.setSl(p.getEntry()-minPips);
					if (isMomentum){
						p.setPositionType(PositionType.SHORT);
						p.setTp(p.getEntry()- tpmult *minPips);
						p.setSl(p.getEntry()+minPips);
					}
					
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);
					
					dayTrade = 1;
					positions.add(p);
				}
			}//H
			
			int j = 0;
			boolean closeAll = false;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					//spread = smaValue - q.getClose5();
					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  q.getClose5()-p.getEntry();
						if (0>=999999990
								//&& q.getClose5()-p.getEntry()>=minPips
								){
							p.setMaxProfit(q.getClose5());
							pips =  q.getClose5()-p.getEntry();
							isClose = true;
						}else{
							//time exits
							if (h==23 && min==55){
								pips =  q.getClose5()-p.getEntry();
								//isClose = true;
							}
							if (q.getHigh5()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (q.getLow5()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (q.getClose5()-p.getEntry()>=200){
								int toTrail = (int) (0.1*(q.getClose5()-p.getEntry()));
								int newSl = p.getEntry()+toTrail;
								if (newSl>=p.getSl() && q.getClose5()-newSl>=20) p.setSl(newSl);
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-q.getClose5();
						if (0>=999999990
								//&& p.getEntry()-q.getClose5()>=minPips
								){
							p.setMaxProfit(q.getClose5());
							pips = p.getEntry()-q.getClose5();
							isClose = true;
						}else{
							//time exits
							if (h==23 && min==55){
								pips = p.getEntry()-q.getClose5();
								//isClose = true;
							}
							if (q.getLow5()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (q.getHigh5()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-q.getClose5()>=200){
								int toTrail = (int) (0.1*(-q.getClose5()+p.getEntry()));
								int newSl = p.getEntry()-toTrail;
								if (newSl<=p.getSl() && -q.getClose5()+newSl>=20) p.setSl(newSl);
							}
						}
					}
					
					if (isClose){
						
						pips-=comm;
						
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							//double win$$ = p.getPip$$()*pips*0.1;
							//balance += win$$;
							//equitity += win$$;
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
						
						/*int totalPips = winPips-lostPips;
						if (totalPips>=maxPips){
							int rt = i-maxPipsIdx;
							maxPipsIdx = i;
							if (rt>=maxRecovery){
								maxRecovery = rt;
							}
						}*/
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
		}
		
		//estudio de years
		int posYears = 0;
		Iterator it = yWinPips.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer,Integer> pair = (Map.Entry)it.next();
	        int year = pair.getKey();
	        int wPips = pair.getValue();
	        int lPips = 0;
	        if (yLostPips.containsKey(year))
	        	lPips = yLostPips.get(year);
	        int netPips = wPips-lPips;
	        if (netPips>=0) posYears++;
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		
		if (debug==2
				|| (pf>=1.20 && posYears>=12 && maxDD<70.0 && trades>=200)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+posYears
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				);
	}
	
	
	public static void doTestAlphadudeStrats(String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int mult,
			ArrayList<String> strat,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			double aRisk,
			int maxPositions,
			int debug,
			boolean printDetails,
			boolean printPips
			){
		
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		HashMap<Integer,ArrayList<Integer>> yTrades = new HashMap<Integer,ArrayList<Integer>>();
		HashMap<Integer,ArrayList<Integer>> mTrades = new HashMap<Integer,ArrayList<Integer>>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
	
		for (int i=0;i<=100-1;i++){
			openArr.add(data.get(i).getOpen5());
		}
		
		
		int dayTrade = 0;
		int totalDaysTrade = 0;
		int mode20 = 0;
		int modeIdx20 = 0;
		int mode30 = 0;
		int modeIdx30 = 0;
		int mode40 = 0;
		int modeIdx40 = 0;
		int mode50 = 0;
		int modeIdx50 = 0;
		int mode60 = 0;
		int modeIdx60 = 0;
		int mode70 = 0;
		int modeIdx70 = 0;
		int mode80 = 0;
		int modeIdx80 = 0;
		int mode90 = 0;
		int modeIdx90 = 0;
		int mode100 = 0;
		int modeIdx100 = 0;
		int dayPips = 0;
		int lastPips = 0;
		
		ArrayList<Integer> ns = new ArrayList<Integer> ();
		//ArrayList<Integer> nbars = new ArrayList<Integer> ();
		ArrayList<Double> fMinPips = new ArrayList<Double> ();
		for (int i=0;i<strat.size();i++){
			String[] values = strat.get(i).split(" ");
			ns.add(Integer.valueOf(values[0]));
			fMinPips.add(Double.valueOf(values[1]));
		}
	
		//int mult = 5;
		for (int i=150+1;i<data.size()-2;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			//System.out.println(cal.getTimeInMillis());
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
			qLast = q;
			
			comm = 00;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
					dayPipsArr.add(diffP);
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				//mode = 0;
				dayPips = 0;
				totalDays++;
				
				if (printPips){
					System.out.println(winPips-lostPips);
				}
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			
			openArr.add(q.getOpen5());
			
			int smaValue20 = (int) MathUtils.average(openArr, openArr.size()-20*mult,openArr.size()-1);
			int smaValue30 = (int) MathUtils.average(openArr, openArr.size()-30*mult,openArr.size()-1);
			int smaValue40 = (int) MathUtils.average(openArr, openArr.size()-40*mult,openArr.size()-1);
			int smaValue50 = (int) MathUtils.average(openArr, openArr.size()-50*mult,openArr.size()-1);
			int smaValue60 = (int) MathUtils.average(openArr, openArr.size()-60*mult,openArr.size()-1);
			int smaValue70 = (int) MathUtils.average(openArr, openArr.size()-70*mult,openArr.size()-1);
			int smaValue80 = (int) MathUtils.average(openArr, openArr.size()-80*mult,openArr.size()-1);
			int smaValue90 = (int) MathUtils.average(openArr, openArr.size()-90*mult,openArr.size()-1);
			int smaValue100 = (int) MathUtils.average(openArr, openArr.size()-100*mult,openArr.size()-1);
			
			if (q.getOpen5()>=smaValue20){				
				if (mode20<=0) modeIdx20 = i;
				mode20 = 1;
			}else{
				if (mode20>=0) modeIdx20 = i;
				mode20 = -1;
			}
			
			if (q.getOpen5()>=smaValue30){				
				if (mode30<=0) modeIdx30 = i;
				mode30 = 1;
			}else{
				if (mode30>=0) modeIdx30 = i;
				mode30 = -1;
			}
			
			if (q.getOpen5()>=smaValue40){				
				if (mode40<=0) modeIdx40 = i;
				mode40 = 1;
			}else{
				if (mode40>=0) modeIdx40 = i;
				mode40 = -1;
			}
			
			if (q.getOpen5()>=smaValue50){				
				if (mode50<=0) modeIdx50 = i;
				mode50 = 1;
			}else{
				if (mode50>=0) modeIdx50 = i;
				mode50 = -1;
			}
			
			if (q.getOpen5()>=smaValue60){				
				if (mode60<=0) modeIdx60 = i;
				mode60 = 1;
			}else{
				if (mode60>=0) modeIdx60 = i;
				mode60 = -1;
			}
			
			if (q.getOpen5()>=smaValue70){				
				if (mode70<=0) modeIdx70 = i;
				mode70 = 1;
			}else{
				if (mode70>=0) modeIdx70 = i;
				mode70 = -1;
			}
			
			if (q.getOpen5()>=smaValue80){				
				if (mode80<=0) modeIdx80 = i;
				mode80 = 1;
			}else{
				if (mode80>=0) modeIdx80 = i;
				mode80 = -1;
			}
			
			if (q.getOpen5()>=smaValue90){				
				if (mode90<=0) modeIdx90 = i;
				mode90 = 1;
			}else{
				if (mode90>=0) modeIdx90 = i;
				mode90 = -1;
			}
			
			if (q.getOpen5()>=smaValue100){				
				if (mode100<=0) modeIdx100 = i;
				mode100 = 1;
			}else{
				if (mode100>=0) modeIdx100 = i;
				mode100 = -1;
			}
								
			//valor de la sma
			boolean canTrade=true;
			for (int z = 0;z<strat.size();z++){
				int n = ns.get(z);
				int bars = 0;//nbars.get(z);
				double aF = fMinPips.get(z);
				ishOk = h>=h1 && h<=h2;
				
				int modeIdx = -1;
				int mode = -1;
				int smaValue = -1;
				
				if (n==50){
					modeIdx = modeIdx50;
					mode = mode50;
					smaValue = smaValue50;
				}else if (n==60){
					modeIdx = modeIdx60;
					mode = mode60;
					smaValue = smaValue60;
				}else if (n==70){
					modeIdx = modeIdx70;
					mode = mode70;
					smaValue = smaValue70;
				}else if (n==80){ 
					modeIdx = modeIdx80;
					mode = mode80;
					smaValue = smaValue80;
				}else if (n==20){
					modeIdx = modeIdx20;
					mode = mode20;
					smaValue = smaValue20;
				}else if (n==30){
					modeIdx = modeIdx30;
					mode = mode30;
					smaValue = smaValue30;
				}else if (n==40){
					modeIdx = modeIdx40;
					mode = mode40;
					smaValue = smaValue40;
				}else if (n==90){
					modeIdx = modeIdx90;
					mode = mode90;
					smaValue = smaValue90;
				}else if (n==100){
					modeIdx = modeIdx100;
					mode = mode100;
					smaValue = smaValue100;
				}
				
				
				if (ishOk){
					if (h==0 && min<15) ishOk = false;
				}
				
				if (ishOk
						&& positions.size()<=maxPositions
						//&& canTrade
						){		
					//System.out.println("trade..");
					
					int dist = i-modeIdx;
					int minPips = (int) (aF*range);
					int slMinPips = (int) (0.6*range);
					int transactionCosts = TradingUtils.getTransactionCosts(y, h,1);
															
					if (mode==1 
							&& modeIdx>0 
							&& dist>=bars//si la candle es la suya
							&& q.getOpen5()-smaValue>=minPips
							//&& q1.getClose5()>=q1.getOpen5()
							){
					//if (spread<=-minPips){
						int entry = q.getOpen5();
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setMaxProfit(entry);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
						
						p.setPositionType(PositionType.SHORT);
						p.setTp(p.getEntry()-100000);
						p.setSl((int) (p.getEntry()+0.6*range));
						if (isMomentum){
							p.setPositionType(PositionType.LONG);
							p.setTp(p.getEntry()+ 100 *minPips);
							p.setSl(p.getEntry()-minPips);
						}
						
						minPips = p.getSl()-p.getEntry();
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(minPips*0.1);
						int microLots = (int) (riskPip/0.10);
						p.setMicroLots(microLots);
						p.setTransactionCosts(transactionCosts);
						p.setExtraParam(n);
					
					
						dayTrade = 1;
						positions.add(p);
						canTrade = false;
					}else if (mode==-1
							&& modeIdx>0 
							&& dist>=bars
							&& -q.getOpen5()+smaValue>=minPips
							//&& q1.getClose5()<=q1.getOpen5()+0
							){
					//}else if(spread>=minPips){
						int entry = q.getOpen5();
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setMaxProfit(entry);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
						
						p.setPositionType(PositionType.LONG);
						p.setTp(p.getEntry()+100000);
						p.setSl((int) (p.getEntry()-0.6*range));
						if (isMomentum){
							p.setPositionType(PositionType.SHORT);
							p.setTp(p.getEntry()- 999 *minPips);
							p.setSl(p.getEntry()+minPips);
						}
						
						minPips = p.getEntry()-p.getSl();
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(minPips*0.1);
						int microLots = (int) (riskPip/0.10);
						if (microLots<1) microLots = 1;
						p.setMicroLots(microLots);
						p.setTransactionCosts(transactionCosts);
						
						dayTrade = 1;
						positions.add(p);
						canTrade = false;
					}
				}//H
			}//strats
			
			int j = 0;
			boolean closeAll = false;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					int tcosts = p.getTransactionCosts();
					int n = p.getExtraParam();
					//int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
					
					int modeIdx = modeIdx50;
					int mode = mode50;
					int smaValue = smaValue50;
					
					if (n==20){
						modeIdx = modeIdx20;
						mode = mode20;
						smaValue = smaValue20;
					}
					if (n==30){
						modeIdx = modeIdx30;
						mode = mode30;
						smaValue = smaValue30;
					}
					if (n==40){
						modeIdx = modeIdx40;
						mode = mode40;
						smaValue = smaValue40;
					}
					if (n==50){
						modeIdx = modeIdx50;
						mode = mode50;
						smaValue = smaValue50;
					}
					if (n==60){
						modeIdx = modeIdx60;
						mode = mode60;
						smaValue = smaValue60;
					}
					if (n==70){
						modeIdx = modeIdx70;
						mode = mode70;
						smaValue = smaValue70;
					}
					if (n==80){ 
						modeIdx = modeIdx80;
						mode = mode80;
						smaValue = smaValue80;
					}
					if (n==90){ 
						modeIdx = modeIdx90;
						mode = mode90;
						smaValue = smaValue90;
					}
					if (n==100){ 
						modeIdx = modeIdx100;
						mode = mode100;
						smaValue = smaValue100;
					}
										
					boolean isClose = false;					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  q.getClose5()-p.getEntry();
						if (mode==1
								){
							p.setMaxProfit(q.getClose5());
							pips =  q.getClose5()-p.getEntry();
							isClose = true;
						}

						if (!isClose){
							//time exits
							if (h==23 && min==55){
								pips =  q.getClose5()-p.getEntry();
								isClose = true;
							}
							if (q.getHigh5()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (q.getLow5()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (q.getClose5()-p.getEntry()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(q.getClose5()-p.getEntry()));
									int newSl = p.getEntry()+toTrail;
									if (newSl>=p.getSl() && q.getClose5()-newSl>=20) p.setSl(newSl);
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-q.getClose5();
						if (mode==-1
								){
							p.setMaxProfit(q.getClose5());
							pips = p.getEntry()-q.getClose5();
							isClose = true;
						}
						
						if (!isClose){
							//time exits
							if (h==23 && min==55){
								pips = p.getEntry()-q.getClose5();
								isClose = true;
							}
							if (q.getLow5()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (q.getHigh5()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-q.getClose5()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(-q.getClose5()+p.getEntry()));
									int newSl = p.getEntry()-toTrail;
									if (newSl<=p.getSl() && -q.getClose5()+newSl>=20) p.setSl(newSl);
								}
							}
						}
					}
					
					if (isClose){
						
						//tcosts = 0;
						pips-=tcosts;
						
						if (!yTrades.containsKey(y)) yTrades.put(y,new ArrayList<Integer>());
						ArrayList<Integer> trades = yTrades.get(y);
						trades.add(pips);
						
						//por mes
						if (!mTrades.containsKey(y)){
							mTrades.put(y,new ArrayList<Integer>());
							for (int t=0;t<=11;t++){
								mTrades.get(y).add(0);
							}
						}						
						trades = mTrades.get(y);
						int accm = trades.get(month);
						trades.set(month, accm+pips);
						
						dayPips += pips;
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)+" || "+y
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
		}
		
		//estudio de years
		int posYears = 0;
		double accPf = 0;
		int countPf = 0;
		List sortedKeys=new ArrayList(yTrades.keySet());
		Collections.sort(sortedKeys);
		
		for (int k=0;k<sortedKeys.size();k++){		
		//Iterator it = yTrades.entrySet().iterator();
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = yTrades.get(year);//pair.getValue();
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);
	        	
	        	if (pips>=0) wPips+=pips;
	        	else lPips-=pips;
	        }
	        
	        double yPf = wPips*1.0/lPips;
	        int netPips = wPips-lPips;
	        double avgPips = (wPips-lPips)*0.1/trades.size();
	        if (avgPips>=1.0) posYears++;//al menos un pip de margen
	        if (lPips>0){
	        	accPf += wPips*1.0/lPips;
	        	countPf++;
	        	if (printDetails)
	        	System.out.println(year
	        			+" avgpf= "+PrintUtils.Print2dec(wPips*1.0/lPips, false)
	        			+" "+trades.size()
	        			+" "+PrintUtils.Print2dec(avgPips, false)
	        			+" "+wPips
	        			+" "+lPips
	        			);
	        }else if (wPips>0 && lPips==0){
	        	accPf += 2.0;
	        	countPf++;
	        	//posYears++;
	        	if (printDetails)
		        	System.out.println(year
		        			+" avgpf= -----"
		        			+" "+trades.size()
		        			+" "+PrintUtils.Print2dec(avgPips, false)
		        			+" "+wPips
		        			+" "+lPips
		        			);
	        }
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int posMonths = 0;
		int negMonths = 0;
		int totalMonths = 0;
		sortedKeys.clear();
		sortedKeys=new ArrayList(mTrades.keySet());
		Collections.sort(sortedKeys);
		//it = mTrades.entrySet().iterator();
		for (int k=0;k<sortedKeys.size();k++){	
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = mTrades.get(year);
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);	        	
	        	if (pips>0) posMonths++;
	        	if (pips!=0) totalMonths++;
	        }	      
	       // it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		double avgPf = accPf/countPf;
		
		if (debug==2
				|| (avg>=1.0 
				//&& pf>=2.0
				&& posYears>=7 
				&& ff>=7.0
				&& trades>=300 
				&& perDays>=0.0)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				+" "+maxPositions
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "+(winPips-lostPips) 
				+" || "
				+" "+PrintUtils.Print2dec(posMonths*100.0/totalMonths, false)
				+" "+posYears
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(avgPf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				);
	}

		

	
	public static void doTestAlphadudeStratsTicks(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			double aF,
			ArrayList<String> strat,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			double aRisk,
			int debug,
			boolean printDetails
			){
		
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		/*for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}*/
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		HashMap<Integer,ArrayList<Integer>> yTrades = new HashMap<Integer,ArrayList<Integer>>();
		HashMap<Integer,ArrayList<Integer>> mTrades = new HashMap<Integer,ArrayList<Integer>>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();

		int dayTrade = 0;
		int totalDaysTrade = 0;
		int mode20 = 0;
		int modeIdx20 = 0;
		int mode30 = 0;
		int modeIdx30 = 0;
		int mode40 = 0;
		int modeIdx40 = 0;
		int mode50 = 0;
		int modeIdx50 = 0;
		int mode60 = 0;
		int modeIdx60 = 0;
		int mode70 = 0;
		int modeIdx70 = 0;
		int mode80 = 0;
		int modeIdx80 = 0;
		int mode90 = 0;
		int modeIdx90 = 0;
		int mode100 = 0;
		int modeIdx100 = 0;
		int dayPips = 0;
		int lastPips = 0;
		
		ArrayList<Integer> ns = new ArrayList<Integer> ();
		ArrayList<Integer> nbars = new ArrayList<Integer> ();
		for (int i=0;i<strat.size();i++){
			String[] values = strat.get(i).split(" ");
			ns.add(Integer.valueOf(values[0]));
			nbars.add(Integer.valueOf(values[1]));
			//System.out.println(ns.get(i)+" "+nbars.get(i));
		}
	
		//guardamos valor actual de minutos y el minuto en cuestion
		QuoteShort.getCalendar(cal, data.get(0));
		int actualMin = cal.get(Calendar.MINUTE);	
		closeArr.add(data.get(0).getBid());
		boolean canTrade = false;
		int smaValue20 = -1;
		int smaValue30 = -1;
		int smaValue40 = -1;
		int smaValue50 = -1;
		int smaValue60 = -1;
		int smaValue70 = -1;
		int smaValue80 = -1;
		int smaValue90 = -1;
		int smaValue100 = -1;
		actualMin = -1;
		int mult = 1;
		for (int i=1;i<data.size()-2;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);			
			QuoteShort.getCalendar(cal, q);			
			y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
						
			comm = 00;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
					dayPipsArr.add(diffP);
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				lastDay = day;
				dayPips = 0;
				canTrade = true;
				totalDays++;
			}
			//uso el bid como referencia para rangos
			if (high==-1 || q.getBid()>=high) high = q.getBid();
			if (low==-1 || q.getBid()<=low) low = q.getBid();	
			
			//la media solo se calcula 1 vez cada x minutos
			if (min!=actualMin
					&& min%15==0 //cada 15 minutos
					){
				openArr.add(q.getBid());
				actualMin = min;
				canTrade = true;
				smaValue20 = (int) MathUtils.average(openArr, openArr.size()-20*mult,openArr.size()-1);
				smaValue30 = (int) MathUtils.average(openArr, openArr.size()-30*mult,openArr.size()-1);
				smaValue40 = (int) MathUtils.average(openArr, openArr.size()-40*mult,openArr.size()-1);
				smaValue50 = (int) MathUtils.average(openArr, openArr.size()-50*mult,openArr.size()-1);
				smaValue60 = (int) MathUtils.average(openArr, openArr.size()-60*mult,openArr.size()-1);
				smaValue70 = (int) MathUtils.average(openArr, openArr.size()-70*mult,openArr.size()-1);
				smaValue80 = (int) MathUtils.average(openArr, openArr.size()-80*mult,openArr.size()-1);
				smaValue90 = (int) MathUtils.average(openArr, openArr.size()-90*mult,openArr.size()-1);
				smaValue100 = (int) MathUtils.average(openArr, openArr.size()-100*mult,openArr.size()-1);
				
				if (debug==1)
				System.out.println(DateUtils.datePrint(cal)+" || "+q.getBid() +" || "+smaValue20);
			}
			
						
			if (q.getBid()>=smaValue20){				
				if (mode20<=0) modeIdx20 = i;
				mode20 = 1;
			}else{
				if (mode20>=0) modeIdx20 = i;
				mode20 = -1;
			}
			
			if (q.getBid()>=smaValue30){				
				if (mode30<=0) modeIdx30 = i;
				mode30 = 1;
			}else{
				if (mode30>=0) modeIdx30 = i;
				mode30 = -1;
			}
			
			if (q.getBid()>=smaValue40){				
				if (mode40<=0) modeIdx40 = i;
				mode40 = 1;
			}else{
				if (mode40>=0) modeIdx40 = i;
				mode40 = -1;
			}
			
			if (q.getBid()>=smaValue50){				
				if (mode50<=0) modeIdx50 = i;
				mode50 = 1;
			}else{
				if (mode50>=0) modeIdx50 = i;
				mode50 = -1;
			}
			
			if (q.getBid()>=smaValue60){				
				if (mode60<=0) modeIdx60 = i;
				mode60 = 1;
			}else{
				if (mode60>=0) modeIdx60 = i;
				mode60 = -1;
			}
			
			if (q.getBid()>=smaValue70){				
				if (mode70<=0) modeIdx70 = i;
				mode70 = 1;
			}else{
				if (mode70>=0) modeIdx70 = i;
				mode70 = -1;
			}
			
			if (q.getBid()>=smaValue80){				
				if (mode80<=0) modeIdx80 = i;
				mode80 = 1;
			}else{
				if (mode80>=0) modeIdx80 = i;
				mode80 = -1;
			}
			
			if (q.getBid()>=smaValue90){				
				if (mode90<=0) modeIdx90 = i;
				mode90 = 1;
			}else{
				if (mode90>=0) modeIdx90 = i;
				mode90 = -1;
			}
			
			if (q.getBid()>=smaValue100){				
				if (mode100<=0) modeIdx100 = i;
				mode100 = 1;
			}else{
				if (mode100>=0) modeIdx100 = i;
				mode100 = -1;
			}
								
			//valor de la sma
			if (canTrade){
				for (int z = 0;z<strat.size();z++){
					int n = ns.get(z);
					int bars = nbars.get(z);
					ishOk = h>=h1 && h<=h2;
					
					int modeIdx = -1;
					int mode = -1;
					int smaValue = -1;
					
					if (n==50){
						modeIdx = modeIdx50;
						mode = mode50;
						smaValue = smaValue50;
					}else if (n==60){
						modeIdx = modeIdx60;
						mode = mode60;
						smaValue = smaValue60;
					}else if (n==70){
						modeIdx = modeIdx70;
						mode = mode70;
						smaValue = smaValue70;
					}else if (n==80){ 
						modeIdx = modeIdx80;
						mode = mode80;
						smaValue = smaValue80;
					}else if (n==20){
						modeIdx = modeIdx20;
						mode = mode20;
						smaValue = smaValue20;
					}else if (n==30){
						modeIdx = modeIdx30;
						mode = mode30;
						smaValue = smaValue30;
					}else if (n==40){
						modeIdx = modeIdx40;
						mode = mode40;
						smaValue = smaValue40;
					}else if (n==90){
						modeIdx = modeIdx90;
						mode = mode90;
						smaValue = smaValue90;
					}else if (n==100){
						modeIdx = modeIdx100;
						mode = mode100;
						smaValue = smaValue100;
					}
					
					
					if (ishOk
							&& positions.size()<=0
							//&& canTrade//solo 1 vez por minuto
							){		
						//System.out.println("trade..");
											
						int dist = i-modeIdx;
						int minPips = (int) (aF*range);
						int slMinPips = (int) (1.0*range);
						int transactionCosts = TradingUtils.getTransactionCosts(y, h,1);
						
						//transactionCosts = 0;
						
						if (mode==1 
								&& modeIdx>0 
								&& dist>=bars//si la candle es la suya
								&& q.getBid()-smaValue>=minPips
								){
						//if (spread<=-minPips){
							int entry = q.getBid();//vendo en el BID
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setMaxProfit(entry);
							
							p.setPositionStatus(PositionStatus.OPEN);
							p.setOpenIndex(i);
							
							p.setPositionType(PositionType.SHORT);
							p.setTp(p.getEntry()-20);
							p.setSl((int) (p.getEntry()+0.2*range));
							if (isMomentum){
								p.setPositionType(PositionType.LONG);
								p.setTp(p.getEntry()+ 100 *minPips);
								p.setSl(p.getEntry()-minPips);
							}
							
							minPips = p.getSl()-p.getEntry();
							double riskPosition = balance*aRisk*1.0/100.0;
							double riskPip = riskPosition/(minPips*0.1);
							int microLots = (int) (riskPip/0.10);
							p.setMicroLots(microLots);
							p.setTransactionCosts(transactionCosts);
							p.setExtraParam(n);
							p.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						
							dayTrade = 1;
							positions.add(p);
							
							if (debug==1){
								System.out.println("[SHORT OPEN ] min="+actualMin+" i="+i+" || "+q.getBid());
							}
						}else if (mode==-1
								&& modeIdx>0 
								&& dist>=bars
								&& -q.getAsk()+smaValue>=minPips
								){
						//}else if(spread>=minPips){
							int entry = q.getAsk();//compro en el ASK
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setMaxProfit(entry);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setOpenIndex(i);
							
							p.setPositionType(PositionType.LONG);
							p.setTp(p.getEntry()+20);
							p.setSl((int) (p.getEntry()-0.2*range));
							if (isMomentum){
								p.setPositionType(PositionType.SHORT);
								p.setTp(p.getEntry()- 999 *minPips);
								p.setSl(p.getEntry()+minPips);
							}
							
							minPips = p.getEntry()-p.getSl();
							double riskPosition = balance*aRisk*1.0/100.0;
							double riskPip = riskPosition/(minPips*0.1);
							int microLots = (int) (riskPip/0.10);
							if (microLots<1) microLots = 1;
							p.setMicroLots(microLots);
							p.setTransactionCosts(transactionCosts);
							p.setExtraParam(n);
							p.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
							
							dayTrade = 1;
							positions.add(p);
							
							if (debug==1){
								System.out.println("[LONG OPEN ] min="+actualMin+" i="+i+" || "+q.getAsk());
							}
						}//isOk
					}//H
				}//strats
			}//canTrade
			
			canTrade = false;//sólo hay una oportunidad en la nueva barra
			
			int j = 0;
			boolean closeAll = false;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					int tcosts = p.getTransactionCosts();
					int n = p.getExtraParam();
					//int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
					
					int modeIdx = modeIdx50;
					int mode = mode50;
					int smaValue = smaValue50;
					
					if (n==20){
						modeIdx = modeIdx20;
						mode = mode20;
						smaValue = smaValue20;
					}
					if (n==30){
						modeIdx = modeIdx30;
						mode = mode30;
						smaValue = smaValue30;
					}
					if (n==40){
						modeIdx = modeIdx40;
						mode = mode40;
						smaValue = smaValue40;
					}
					if (n==50){
						modeIdx = modeIdx50;
						mode = mode50;
						smaValue = smaValue50;
					}
					if (n==60){
						modeIdx = modeIdx60;
						mode = mode60;
						smaValue = smaValue60;
					}
					if (n==70){
						modeIdx = modeIdx70;
						mode = mode70;
						smaValue = smaValue70;
					}
					if (n==80){ 
						modeIdx = modeIdx80;
						mode = mode80;
						smaValue = smaValue80;
					}
					if (n==90){ 
						modeIdx = modeIdx90;
						mode = mode90;
						smaValue = smaValue90;
					}
					if (n==100){ 
						modeIdx = modeIdx100;
						mode = mode100;
						smaValue = smaValue100;
					}
										
					boolean isClose = false;					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  q.getBid()-p.getEntry();
						//isClose = true;//para comprobar el spread
						//System.out.println("[CLOSED LONG] "+pips+" || "+q.getAsk()+" "+q.getBid());
						
						if (!isClose){
							if (mode==1
									){
								pips =  q.getBid()-p.getEntry();
								isClose = true;
							}
						}
						
						if (!isClose){
							//time exits
							if (h==23 && min==55){
								pips =  q.getBid()-p.getEntry();
								//isClose = true;
							}
							if (q.getBid()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (q.getBid()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (q.getBid()-p.getEntry()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(q.getBid()-p.getEntry()));
									int newSl = p.getEntry()+toTrail;
									if (newSl>=p.getSl() && q.getBid()-newSl>=20) p.setSl(newSl);
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-q.getAsk();
						//isClose = true;//para comprobar el spread
						
						if (!isClose){
							if (mode==-1
									){
								pips = p.getEntry()-q.getAsk();
								isClose = true;
							}
						}
						
						if (!isClose){
							//time exits
							if (h==23 && min==55){
								pips = p.getEntry()-q.getAsk();
								//isClose = true;
							}
							if (q.getAsk()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (q.getAsk()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-q.getAsk()>=200){
								if (isMomentum){
									int toTrail = (int) (0.1*(-q.getAsk()+p.getEntry()));
									int newSl = p.getEntry()-toTrail;
									if (newSl<=p.getSl() && -q.getAsk()+newSl>=20) p.setSl(newSl);
								}
							}
						}
					}
					
					if (isClose){
						
						//tcosts = 0;
						pips-=tcosts;
						
						if (!yTrades.containsKey(y)) yTrades.put(y,new ArrayList<Integer>());
						ArrayList<Integer> trades = yTrades.get(y);
						trades.add(pips);
						
						//por mes
						if (!mTrades.containsKey(y)){
							mTrades.put(y,new ArrayList<Integer>());
							for (int w=0;w<=11;w++){
								mTrades.get(y).add(0);
							}
						}						
						trades = mTrades.get(y);
						int accm = trades.get(month);
						trades.set(month, accm+pips);
						
						dayPips += pips;
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)+" || "+y
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										+" SMA= "+p.getExtraParam()
										+" OPEN DATE= "+DateUtils.datePrint(p.getOpenCal())
										+" CLOSE DATE= "+DateUtils.datePrint(cal)
										+" OPENINDEX= "+p.getOpenIndex()
										+" CLOSEINDEX= "+i
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" SMA= "+p.getExtraParam()
										+" OPEN DATE= "+DateUtils.datePrint(p.getOpenCal())
										+" CLOSE DATE= "+DateUtils.datePrint(cal)
										+" OPENINDEX= "+p.getOpenIndex()
										+" CLOSEINDEX= "+i
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
		}
		
		//estudio de years
		int posYears = 0;
		double accPf = 0;
		int countPf = 0;
		List sortedKeys=new ArrayList(yTrades.keySet());
		Collections.sort(sortedKeys);
		
		for (int k=0;k<sortedKeys.size();k++){		
		//Iterator it = yTrades.entrySet().iterator();
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = yTrades.get(year);//pair.getValue();
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);
	        	
	        	if (pips>=0) wPips+=pips;
	        	else lPips-=pips;
	        }
	        
	        double yPf = wPips*1.0/lPips;
	        int netPips = wPips-lPips;
	        double avgPips = (wPips-lPips)*0.1/trades.size();
	        if (avgPips>=1.0) posYears++;//al menos un pip de margen
	        if (lPips>0){
	        	accPf += wPips*1.0/lPips;
	        	countPf++;
	        	if (printDetails)
	        	System.out.println(year
	        			+" avgpf= "+PrintUtils.Print2dec(wPips*1.0/lPips, false)
	        			+" "+trades.size()
	        			+" "+PrintUtils.Print2dec(avgPips, false)
	        			+" "+wPips
	        			+" "+lPips
	        			);
	        }else if (wPips>0 && lPips==0){
	        	accPf += 2.0;
	        	countPf++;
	        	//posYears++;
	        	if (printDetails)
		        	System.out.println(year
		        			+" avgpf= -----"
		        			+" "+trades.size()
		        			+" "+PrintUtils.Print2dec(avgPips, false)
		        			+" "+wPips
		        			+" "+lPips
		        			);
	        }
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int posMonths = 0;
		int negMonths = 0;
		int totalMonths = 0;
		sortedKeys.clear();
		sortedKeys=new ArrayList(mTrades.keySet());
		Collections.sort(sortedKeys);
		//it = mTrades.entrySet().iterator();
		for (int k=0;k<sortedKeys.size();k++){	
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = mTrades.get(year);
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);	        	
	        	if (pips>0) posMonths++;
	        	if (pips!=0) totalMonths++;
	        }	      
	       // it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		double avgPf = accPf/countPf;
		double perWin = wins*100.0/trades;
		
		if (debug!=0
				|| (avg>=1.0 
				//&& pf>=2.0
				&& posYears>=7 
				&& ff>=7.0
				&& trades>=300 
				&& perDays>=0.0)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+PrintUtils.Print2dec(posMonths*100.0/totalMonths, false)
				+" "+posYears
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(avgPf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				);
	}
	
	public static void doTestAlphadudeSD(String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int n,
			double aF,
			int atrLimit,
			ArrayList<String> strat,//
			ArrayList<Integer> dayPipsArr,
			boolean isMomentum,
			int timeFrame,
			double aRisk,
			boolean isTransactionsCostIncluded,
			int debug,
			boolean printDetails,
			boolean printDailyPips
			){
		
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = 5000;
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double equitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		HashMap<Integer,ArrayList<Integer>> yTrades = new HashMap<Integer,ArrayList<Integer>>();
		HashMap<Integer,ArrayList<Integer>> mTrades = new HashMap<Integer,ArrayList<Integer>>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
	
		for (int i=0;i<=n-1;i++){
			openArr.add(data.get(i).getOpen5());
		}
		
		
		int dayTrade = 0;
		int totalDaysTrade = 0;
		mode = 0;
		int modeIdx = 0;
		int dayPips = 0;
		int lastPips = 0;
		String[] valuesH0 = strat.get(0).split(" ");String[] valuesH1 = strat.get(1).split(" ");String[] valuesH2 = strat.get(2).split(" ");
		String[] valuesH3 = strat.get(3).split(" ");String[] valuesH4 = strat.get(4).split(" ");String[] valuesH5 = strat.get(5).split(" ");
		String[] valuesH6 = strat.get(6).split(" ");String[] valuesH7 = strat.get(7).split(" ");String[] valuesH8 = strat.get(8).split(" ");
		String[] valuesH9 = strat.get(9).split(" ");String[] valuesH10 = strat.get(10).split(" ");String[] valuesH11 = strat.get(11).split(" ");
		String[] valuesH12 = strat.get(12).split(" ");String[] valuesH13 = strat.get(13).split(" ");String[] valuesH14 = strat.get(14).split(" ");
		String[] valuesH15 = strat.get(15).split(" ");String[] valuesH16 = strat.get(16).split(" ");String[] valuesH17 = strat.get(17).split(" ");
		String[] valuesH18 = strat.get(18).split(" ");String[] valuesH19 = strat.get(19).split(" ");String[] valuesH20 = strat.get(20).split(" ");
		String[] valuesH21 = strat.get(21).split(" ");String[] valuesH22 = strat.get(22).split(" ");String[] valuesH23 = strat.get(23).split(" ");
		
		boolean canTrade = true;
		int smaValue = -1;
		int sdValue = -1;
		boolean newBar = true;
		QuoteShort actualBar = new QuoteShort();
		QuoteShort evaluateBar = new QuoteShort();
		for (int i=n+1;i<data.size()-2;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
			qLast = q;
			
			comm = 00;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
					dayPipsArr.add(diffP);
				}			
				
				if (dayTrade==1) totalDaysTrade++;
				dayTrade = 0;
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				//mode = 0;
				dayPips = 0;
				totalDays++;
				
				newBar = true;
				actualBar.copy(q);
				
				if (printDailyPips){
					System.out.println(winPips-lostPips);
				}
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			
								
			String[] values = valuesH0;
			if (h==1) values = valuesH1;if (h==2) values = valuesH2;if (h==3) values = valuesH3;if (h==4) values = valuesH4;
			if (h==5) values = valuesH5;if (h==6) values = valuesH6;if (h==7) values = valuesH7;if (h==8) values = valuesH8;
			if (h==9) values = valuesH9;if (h==10) values = valuesH10;if (h==11) values = valuesH11;if (h==12) values = valuesH12;
			if (h==13) values = valuesH13;if (h==14) values = valuesH14;if (h==15) values = valuesH15;if (h==16) values = valuesH16;
			if (h==17) values = valuesH17;if (h==18) values = valuesH18;if (h==19) values = valuesH19;if (h==20) values = valuesH20;
			if (h==21) values = valuesH21;if (h==22) values = valuesH22;if (h==23) values = valuesH23;
			ishOk = values[0] !="-1";
			//valor de la sma
			
			if (min%timeFrame==0){
				openArr.add(q.getOpen5());
				canTrade = true;
				newBar = true;
				//evaluateBar.copy(actualBar);
				//actualBar.copy(q);
				smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
				double variance = MathUtils.variance(openArr, openArr.size()-n,openArr.size()-1);
				
				sdValue = (int) Math.sqrt(MathUtils.variance(openArr, openArr.size()-n,openArr.size()-1));
				//System.out.println(sdValue);
				//vemos si hay cruce y anotamos el momento del cruce
				if (q.getOpen5()>=smaValue){				
					if (mode<=0){
						canTrade = true;
						modeIdx = i;
					}
					mode = 1;
				}else{
					if (mode>=0){
						canTrade = true;
						modeIdx = i;
					}
					mode = -1;
				}
			}
			
			if (ishOk){
				if (h==0 && min<15) ishOk=false;
				
			}
			
			
			if (ishOk
					&& positions.size()<=200
					&& range<=atrLimit
					//&& canTrade
					//&& dayTrade==0
					){												
				int dist = i-modeIdx;
				int value = Integer.valueOf(values[1]);
				int minPips = (int) (aF*sdValue);//(int) (aF*range);
				int slMinPips = (int) (1.0*range);
				int transactionCosts = TradingUtils.getTransactionCosts(y, h,1);
				//transactionCosts = 0;
				//if (!isMomentum) minPips = 99999999;
				/*System.out.println(" "+dist
						+" sd= "+sdValue+" "
						+" "+value+" "
				+(q.getOpen5()-smaValue)+" "+minPips
				+" "+range+" "+aF);
				*/
				if (mode==1 
						&& modeIdx>0 
						&& ((!isMomentum && dist>=value)  || (isMomentum && dist<=value))//si la candle es la suya
						&& q.getOpen5()-smaValue>=minPips
						){
				//if (spread<=-minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.SHORT);
					p.setTp((int) (p.getEntry()-10.5*range));
					p.setSl((int) (p.getEntry()+10.6*range));
					if (isMomentum){
						p.setPositionType(PositionType.LONG);
						p.setTp((int) (p.getEntry()+5.0*range));
						p.setSl((int) (p.getEntry()-1.0*range));
					}
					
					minPips = p.getSl()-p.getEntry();
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);
					p.setTransactionCosts(transactionCosts);
					p.setExtraParam(n);
				
				
					dayTrade = 1;
					positions.add(p);
					
					canTrade = false;
				}else if (mode==-1
						&& modeIdx>0 
						&& ((!isMomentum && dist>=value)  || (isMomentum && dist<=value))
						&& -q.getOpen5()+smaValue>=minPips
						){
				//}else if(spread>=minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.LONG);
					p.setTp((int) (p.getEntry()+10.5*range));
					p.setSl((int) (p.getEntry()-10.6*range));
					if (isMomentum){
						p.setPositionType(PositionType.SHORT);
						p.setTp((int) (p.getEntry()-5*range));
						p.setSl((int) (p.getEntry()+3.0*range));
					}
					
					minPips = p.getEntry()-p.getSl();
					double riskPosition = balance*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					if (microLots<1) microLots = 1;
					p.setMicroLots(microLots);
					p.setTransactionCosts(transactionCosts);
					
					dayTrade = 1;
					positions.add(p);
					
					canTrade = false;
				}
			}//H
			
						
			int j = 0;
			boolean closeAll = false;
			QuoteShort qe = q;
			//if (newBar)//solo se evalua al cierre de cada timeframe
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					int tcosts = p.getTransactionCosts();
					
					//n = p.getExtraParam();
					//int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);
					boolean isClose = false;
					
					//spread = smaValue - q.getClose5();					
					if (p.getPositionType()==PositionType.LONG){	
						pips =  qe.getClose5()-p.getEntry();
						if ((mode==1 && !isMomentum) || (mode==-1 && isMomentum)
								){
							p.setMaxProfit(qe.getClose5());
							pips =  qe.getClose5()-p.getEntry();
							isClose = true;
						}

						if (!isClose){
							//time exits
							if (h==23 && min>=55){
								pips =  qe.getClose5()-p.getEntry();
								//isClose = true;
							}
							if (qe.getHigh5()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
							}else if (qe.getLow5()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
							}else if (qe.getClose5()-p.getEntry()>=0){
								if (isMomentum
									 && qe.getClose5()-p.getEntry()>=20000	
										){
									int tpips = (int) (0.10*(qe.getClose5()-p.getEntry()));
									int newSL = p.getEntry()+10;
									if (tpips>=10 && newSL>=p.getSl()){
										p.setSl(newSL);
									}
								}else if (qe.getClose5()-p.getEntry()>=10 && !isMomentum){
									pips =  qe.getClose5()-p.getEntry();
									//isClose = true;
								}
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-qe.getClose5();
						if ((mode==-1 && !isMomentum) || (mode==1 && isMomentum)
								){
							p.setMaxProfit(qe.getClose5());
							pips = p.getEntry()-qe.getClose5();
							isClose = true;
						}
						
						if (!isClose){
							//time exits
							if (h==23 && min>=55){
								pips = p.getEntry()-qe.getClose5();
								//isClose = true;
							}
							if (qe.getLow5()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
							}else if (qe.getHigh5()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
							}else if (p.getEntry()-q.getClose5()>=00){
								if (p.getEntry()-q.getClose5()>=20000 && isMomentum){
									int tpips = (int) (0.10*(-qe.getClose5()+p.getEntry()));
									int newSL = p.getEntry()-10;
									if (tpips>=10 && newSL<=p.getSl()){
										p.setSl(newSL);
									}									
								}else if (p.getEntry()-q.getClose5()>=10 && !isMomentum){
									pips = p.getEntry()-qe.getClose5();
									//isClose = true;
								}
							}
						}
					}
					
					if (isClose){
						
						if (!isTransactionsCostIncluded) tcosts = 0;
						//tcosts = p.getTransactionCosts();
						
						pips-=tcosts;
						
						if (!yTrades.containsKey(y)) yTrades.put(y,new ArrayList<Integer>());
						ArrayList<Integer> trades = yTrades.get(y);
						trades.add(pips);
						
						//por mes
						if (!mTrades.containsKey(y)){
							mTrades.put(y,new ArrayList<Integer>());
							for (int t=0;t<=11;t++){
								mTrades.get(y).add(0);
							}
						}						
						trades = mTrades.get(y);
						int accm = trades.get(month);
						trades.set(month, accm+pips);
						
						dayPips += pips;
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
							int ya = yWinPips.get(y);
							yWinPips.put(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
	
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										//+" "+PrintUtils.Print2dec(win$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							//totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
							int ya = yLostPips.get(y);
							yLostPips.put(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							//double pip$$ = p.getPip$$()*pips*0.1;
							//balance += pip$$;
							//equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										//+" "+PrintUtils.Print2dec(pip$$, false)
										//+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						
						balance += p.getMicroLots()*0.10*pips*0.10;
						if (balance<=maxBalance){
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxDD) maxDD = actualDD;
						}else{
							maxBalance = balance;
						}
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions

		}
		
		//estudio de years
		int posYears = 0;
		double accPf = 0;
		int countPf = 0;
		List sortedKeys=new ArrayList(yTrades.keySet());
		Collections.sort(sortedKeys);
		
		for (int k=0;k<sortedKeys.size();k++){		
		//Iterator it = yTrades.entrySet().iterator();
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = yTrades.get(year);//pair.getValue();
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);
	        	
	        	if (pips>=0) wPips+=pips;
	        	else lPips-=pips;
	        }
	        
	        double yPf = wPips*1.0/lPips;
	        int netPips = wPips-lPips;
	        double avgPips = (wPips-lPips)*0.1/trades.size();
	        if (avgPips>=0.0) posYears++;//al menos un pip de margen
	        if (lPips>0){
	        	accPf += wPips*1.0/lPips;
	        	countPf++;
	        	if (printDetails)
	        	System.out.println(year
	        			+" avgpf= "+PrintUtils.Print2dec(wPips*1.0/lPips, false)
	        			+" "+trades.size()
	        			+" "+PrintUtils.Print2dec(avgPips, false)
	        			+" "+wPips
	        			+" "+lPips
	        			);
	        }
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int posMonths = 0;
		int negMonths = 0;
		int totalMonths = 0;
		sortedKeys.clear();
		sortedKeys=new ArrayList(mTrades.keySet());
		Collections.sort(sortedKeys);
		//it = mTrades.entrySet().iterator();
		for (int k=0;k<sortedKeys.size();k++){	
		//while (it.hasNext()) {
	        //Map.Entry<Integer,ArrayList<Integer>> pair = (Map.Entry)it.next();
	        int year = (int) sortedKeys.get(k);
	        ArrayList<Integer> trades = mTrades.get(year);
	        int wPips = 0;
	        int lPips = 0;
	        for (int i=0;i<trades.size();i++){
	        	int pips = trades.get(i);	        	
	        	if (pips>0) posMonths++;
	        	if (pips!=0) totalMonths++;
	        }	      
	       // it.remove(); // avoids a ConcurrentModificationException
	    }
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double perDays = totalDaysTrade*100.0/totalDays;
		double perR = balance*100.0/initialBalance-100.0;
		double ff = perR/maxDD;
		double avgPf = accPf/countPf;
		double avgRecoveryTime = 0.0;
		
		if (debug==2
				|| (avg>=1.0 
				//&& pf>=1.4
				//&& maxDD<=25 
				//&& ff>=10
				&& posYears>=8 
				//&& ff>=5.0
				//&& trades>=300 
				&& perDays>=20.0
				)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				+" "+timeFrame
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+PrintUtils.Print2dec(posMonths*100.0/totalMonths, false)
				+" "+posYears
				+" "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(avgPf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				+" || "+PrintUtils.Print2dec(avgRecoveryTime, false)			
				);
	}

		
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
				
				
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
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
				Calendar cal = Calendar.getInstance();
				System.out.println("path: "+path+" "+data.size());
				double aMaxFactorGlobal = -9999;
			
				ArrayList<String> strat = new ArrayList<String>();
				for (int j=0;j<=23;j++) strat.add("-1");
			
				//strat.set(9,"50 0.40 5");
				strat.set(10,"270 0.40 3");//13 6668 1.43 5.81 24.76 ||  039662,04 047013,65 27.08 || 25.60
				strat.set(11,"285 0.60 2");//14 2730 1.28 4.32 10.12 ||  008942,61 009443,41 11.68 || 6.75
				//strat.set(13,"115 0.60 4");
				//strat.set(14,"15 0.40 2");
				strat.set(15,"40 0.50 4");
				strat.set(16,"15 0.30 3");
				strat.set(17,"75 0.70 3");
				strat.set(18,"105 0.70 3");
				strat.set(19,"75 0.70 1");
				strat.set(20,"110 0.70 1");
				strat.set(21,"55 0.40 5");
				strat.set(22,"40 0.30 5");
				
				//PROFITABLE
				//0-2 : 43-1 30-1 
				//3 : 80-0.25
				//9 : 90-0.30
								
				ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
			
				ArrayList<String> strat3 = new ArrayList<String>();
				for (int j=0;j<=23;j++) strat3.add("-1");
				//VESSION SIMPLE
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+9;

					for (int n=54;n<=54;n+=1){
						for (int nBars=0;nBars<=0;nBars+=1){
							for (int backBars=0;backBars<=0;backBars+=100){
								String params =n+" "+nBars;
								for (int j=0;j<=23;j++) strat3.set(j,"-1");
								for (int j=h1;j<=h2;j++) strat3.set(j,params);
								for (double fMinPips=0.25;fMinPips<=0.25;fMinPips+=0.01){
									for (int atrLimit=9900;atrLimit<=9900;atrLimit+=100){
										for (int timeFrame=5;timeFrame<=5;timeFrame+=5){
											for (int maxOpenPositions=30;maxOpenPositions<=30;maxOpenPositions+=5){
												for (double aRisk = 0.5;aRisk<=0.5;aRisk+=0.25){
													String str = h1+" "+n+" "+nBars
															+" "+PrintUtils.Print2dec(fMinPips, false)
															+" "+backBars+" "+maxOpenPositions
															;
													for (int y1=2009;y1<=2019;y1++){
														int y2 = y1+0;
														for (int m1=0;m1<=0;m1+=1){
															int m2 = m1+11;
			
															TestMeanReversion.doTestAlphadude(str, 
																	data,maxMins,
																	y1, y2, m1, m2,
																	n,fMinPips,backBars,
																	atrLimit, 
																	strat3,dayPips1,
																	false,timeFrame,
																	maxOpenPositions,
																	aRisk,true,
																	2,false,false,null);
															
															int offset = 100;
															
															
															/*ArrayList<Integer> clean = new ArrayList<Integer>();
															for (int d =0;d<=dayPips1.size()-1;d++){
																int value = dayPips1.get(d); 
																if (value!=0){
																	clean.add(value);
																}
															}
															
															int countN = 0;
															int countNN = 0;
															for (int d =0;d<=clean.size()-4;d++){
																int value = dayPips1.get(d);
																int value1 = dayPips1.get(d+1);
																int value2 = dayPips1.get(d+2);
																int value3 = dayPips1.get(d+3);
																
																if (value>0 
																		&& value1>0
																		&& value2>0
																		){
																	countN++;
																	if (value3<0){
																		countNN++;
																	}
																}
															}
															
															System.out.println(
																	countN+" "+countNN
																	+" || "+PrintUtils.Print2dec(countNN*100.0/countN, false)
																	);*/
															
															/*for (int d =0;d<=dayPips1.size()-2;d++){
																int value = dayPips1.get(d); 
																int value1 = dayPips1.get(d+1);
																		
																int begin = d-100;
																if (begin<=0) begin = 0;
																int max = -9999;
																int min = 9999;
																int acc = 0;
																int maxBal = 0;
																int maxDiff = 0;
																for (int d1=begin;d1<=d;d1++){
																	int pips = dayPips1.get(d1);
																	acc+=pips;
																	
																	if (pips>=0){
																		if (acc>=maxBal) maxBal = acc;
																	}else{
																		int diff = maxBal-acc;
																		if (diff>=maxDiff) maxDiff = diff;
																	}
																}
																if (dayPips1.get(d)!=0){
																	
																	if (value>0){
																		countP++;
																	}else if (value<0){
																		countN++;
																		if (value1<0){
																			
																		}
																	}
																	
																	System.out.println(d
																			+" || "+dayPips1.get(d)
																			//+" || "+PrintUtils.Print2dec(maxBal*1.0/maxDiff, false)
																	);
																}
															}*/
															
														}
													
													}
												}
											}
										}
									}
								}
							}
						}
					}
					//System.out.println("");
				}
				
				//VERSION SD
				/*for (int j=0;j<=23;j++) strat3.add("-1");
				for (int h1=6;h1<=6;h1++){
					int h2 = h1+2;

					for (int n=10;n<=400;n+=5){
						for (int nBars=0;nBars<=0;nBars+=1){
							String params =n+" "+nBars;
							for (int j=0;j<=23;j++) strat3.set(j,"-1");
							for (int j=h1;j<=h2;j++) strat3.set(j,params);
							for (double fMinPips=0.25;fMinPips<=5.00;fMinPips+=0.25){
								for (int atrLimit=9900;atrLimit<=9900;atrLimit+=100){
									for (int timeFrame=5;timeFrame<=5;timeFrame+=5){
										for (double aRisk = 0.5;aRisk<=0.5;aRisk+=0.10){
											String str = h1+" "+n+" "+nBars+" "+PrintUtils.Print2dec(fMinPips, false);
											for (int y1=2009;y1<=2009;y1++){
												int y2 = y1+10;
												for (int m1=0;m1<=0;m1++){
													int m2 = m1+11;
													TestMeanReversion.doTestAlphadudeSD(str, data, y1, y2, m1, m2,n,fMinPips,atrLimit, 
															strat3,dayPips1,
															false,timeFrame,
															aRisk,true,
															0,false,false);
												}
											}
										}
									}
								}
							}
						}
					}
					//System.out.println("");
				}*/
				
				//0-2:
				//7-8: 20-18 30-18 40-18 50-17 60-20 70-21 80-28
				
				//for (int j=0;j<=23;j++) strat3.set(j,"-1");
				//for (int j=0;j<=2;j++) strat3.set(j,"40 1");
				//TestMeanReversion.doTestAlphadude("", data, 2009, 2017, 0, 11,30,0.14, strat3,dayPips1,false,0.5, 2,false);
				//TestMeanReversion.doTestAlphadude("", data, 2018, 2019, 0, 11,30,0.14, strat3,dayPips1,false,0.5, 2,false);
				//3 40 100 0.25 0.20 ||  67.71 9 9483 1.60 3.12 14.31 12.58 8.00
				
				ArrayList<String> strat4 = new ArrayList<String>();
				//para 0 y 2: 20-90 y 0
				
				//7-8 a 0.18?
				/*strat4.add("30 0.15");
				strat4.add("40 0.20");
				strat4.add("50 0.15");
				strat4.add("60 0.15");
				strat4.add("70 0.20");*/				
				//strat4.add("80 0");
				
				//0-2 30 posiciones
				/*strat4.add("20 0.10");
				strat4.add("30 0.10");
				strat4.add("40 0.10");
				strat4.add("50 0.15");*/
			
				//20 18
				//30 18
				//40 18
				//50 17
				//60 20
				//70 21
				//80 28
				
				//de 0-8
				//60 0.18
				//30 0.18
				//40 0.26
				//50 0.14
				//70 0.36
				//80 0.44
				//90 0.50
				//100 0.54
				strat4.add("40 0.20");
				strat4.add("40 0.20");
				strat4.add("40 0.20");
				////strat4.add("50 0.20");
				//strat4.add("50 0.20");
				//strat4.add("50 0.20");
				//strat4.add("50 0.20");
				for (double fMinPips=0.15;fMinPips<=0.10;fMinPips+=0.01){
					for (double aRisk = 0.10;aRisk<=0.10;aRisk+=0.10){						
						for (int y1=2004;y1<=2009;y1++){
							int y2 = y1+0;
							for (int n=0;n<=0;n+=10){
								for (int maxPositions = 30;maxPositions<=30;maxPositions+=10){
									for (int bars=1;bars<=1;bars+=2){
										String params1 = n+" "+bars;
										//strat4.set(0, params1);
										//String params2 = "50 "+bars;
										//strat4.set(1, params2);
										//String params3 = "60 "+bars;
										//strat4.set(2, params3);
										//String params4 = "70 "+bars;
										//strat4.set(3, params4);
										String str = params1+" "+PrintUtils.Print2dec(fMinPips, false);
										for (int h1=0;h1<=0;h1++){
											int h2 = h1+8;
											str = h1+" "+h2+" "+params1+" "+PrintUtils.Print2dec(fMinPips, false);
											TestMeanReversion.doTestAlphadudeStrats(str, data, y1, y2, 0, 11,h1,h2,
													1,strat4,dayPips1,false,aRisk,maxPositions, 0,false,false);
										}								
									}//bar
								}//maxPositions
							}
						}
					}
				}
				
				//TestMeanReversion.doTestAlphadudeStrats("", data, 2009, 2019, 0, 11,6,9,0.20,5, strat4,dayPips1,false,0.5, 2,false);
				
			}
		}
}
