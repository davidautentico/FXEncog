package drosa.experimental.EAS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.basicStrategies.strats2019.StratPerformance;
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

public class DaveTrade2019_vol2 {

	
	public static double doTest(
			String header,
			StratPerformance sp,
			ArrayList<QuoteShort> dataBid,
			ArrayList<QuoteShort> dataAsk,
			int y1,int y2,
			int m1,int m2,
			ArrayList<String> strat,
			boolean isMomentum,
			int debug
			){
	
		Calendar cal = Calendar.getInstance();
		
		double initialBalance = sp.getInitialBalance();
		double balance = initialBalance;
		double maxBalance = initialBalance;
		double maxDD = 0;
		double actualEquitity = initialBalance;
		double maxEquitity = initialBalance;
		
		int comm = 20;
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
		for (int i=0;i<dataBid.size()-1;i++){
			closeArr.add(dataBid.get(i).getClose5());
		}
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
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		ArrayList<Integer> openArr = new ArrayList<Integer>();
		int n = 400;
		for (int i=0;i<=n-1;i++){
			openArr.add(dataBid.get(i).getOpen5());
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
		
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		
		int minSizeData = dataBid.size();
		if (dataAsk.size()<minSizeData) minSizeData = dataAsk.size();
		QuoteShort qLast = null;
		for (int i=n;i<minSizeData-2;i++){
			QuoteShort qb1	= dataBid.get(i-1);
			QuoteShort qb 	= dataBid.get(i);
			QuoteShort qb_1 = dataBid.get(i+1);
			QuoteShort qa1 	= dataAsk.get(i-1);
			QuoteShort qa 	= dataAsk.get(i);
			QuoteShort qa_1 = dataAsk.get(i+1);
			QuoteShort.getCalendar(cal, qb);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			month = cal.get(Calendar.MONTH);
			if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0) continue;
			qLast = qb;
			
			comm = TradingUtils.getTransactionCosts(null,y, h,3);//cambiar
			
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
				doValue = qb.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			if (high==-1 || qa.getHigh5()>=high) high = qa.getHigh5();
			if (low==-1 || qb.getLow5()<=low) low = qb.getLow5();	
			
			openArr.add(qb.getOpen5());
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
			
			
			if (ishOk){
				n		= Integer.valueOf(values[0]);
				double fMinPips 	= Float.valueOf(values[1]);
				int minPips = (int) (fMinPips*range);						
				int tpmult = Integer.valueOf(values[2]);
				double aRisk = Float.valueOf(values[3]);
				
				int smaValue = (int) MathUtils.average(openArr, openArr.size()-n,openArr.size()-1);			
				int spreadAsk = qa.getOpen5() - smaValue;
				int spreadBid = qb.getOpen5() - smaValue;
				if (spreadAsk>=minPips){
				//if (spread<=-minPips){
					int entry = qa.getOpen5();
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
					
					double maxRisk$$ =actualEquitity*aRisk/100.0;
					double pipValue = maxRisk$$*1.0/minPips*0.1;//sl en formato pip
					int miniLots = (int) (pipValue/0.10);//1 mini lot es $0.10
					pipValue = miniLots*0.10;
					if (pipValue<=0.10) pipValue = 0.10;//como minimo 0.01 lots
					if (miniLots<1) miniLots = 1;
					p.setMicroLots(miniLots);
					
					dayTrade = 1;
					positions.add(p);
				}else if (spreadBid<=-minPips){
				//}else if(spread>=minPips){
					int entry = qb.getOpen5();
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
					
					double riskPosition = actualEquitity*aRisk*1.0/100.0;
					double riskPip = riskPosition/(minPips*0.1);
					int microLots = (int) (riskPip/0.10);
					p.setMicroLots(microLots);
					
					dayTrade = 1;
					positions.add(p);
				}
			}//H
			
			int j = 0;
			boolean closeAll = false;
			actualEquitity = balance;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					//spread = smaValue - q.getClose5();
					String strClosed = "";
					if (p.getPositionType()==PositionType.LONG){	
						strClosed = "LONG ";
						pips =  qb.getClose5()-p.getEntry();
						if (0>=999999990
								//&& q.getClose5()-p.getEntry()>=minPips
								){
							p.setMaxProfit(qb.getClose5());
							pips =  qb.getClose5()-p.getEntry();
							isClose = true;
						}else{
							//time exits
							if (h==23 && min==55){
								pips =  qb.getClose5()-p.getEntry();
								//isClose = true;
							}
							if (qb.getHigh5()>=p.getTp()){
								pips =  p.getTp()-p.getEntry();
								isClose = true;
								strClosed += "CLOSED TP";
							}else if (qb.getLow5()<=p.getSl()){
								pips =  p.getSl()-p.getEntry();
								isClose = true;
								strClosed += "CLOSED SL";
							}else if (qb.getClose5()-p.getEntry()>=200){
								int toTrail = (int) (0.1*(qb.getClose5()-p.getEntry()));
								int newSl = p.getEntry()+toTrail;
								if (newSl>=p.getSl() && qb.getClose5()-newSl>=20) p.setSl(newSl);
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						strClosed = "SHORT ";
						pips = p.getEntry()-qa.getClose5();
						if (0>=999999990
								//&& p.getEntry()-q.getClose5()>=minPips
								){
							p.setMaxProfit(qa.getClose5());
							pips = p.getEntry()-qa.getClose5();
							isClose = true;
						}else{
							//time exits
							if (h==23 && min==55){
								pips = p.getEntry()-qa.getClose5();
								//isClose = true;
							}
							if (qa.getLow5()<=p.getTp()){
								pips =  p.getEntry()-p.getTp();
								isClose = true;
								strClosed += "CLOSED TP";
							}else if (qa.getHigh5()>=p.getSl()){
								pips =  p.getEntry()-p.getSl();
								isClose = true;
								strClosed += "CLOSED SL";
							}else if (p.getEntry()-qa.getClose5()>=200){
								int toTrail = (int) (0.1*(-qa.getClose5()+p.getEntry()));
								int newSl = p.getEntry()-toTrail;
								if (newSl<=p.getSl() && -qa.getClose5()+newSl>=20) p.setSl(newSl);
							}
						}
					}
					//actualizacion de equitity
					actualEquitity = actualEquitity + p.getMicroLots()*0.10*(pips-comm)*0.10;
					
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
							double win$$ = p.getMicroLots()*0.10*pips*0.10;
							//balance += win$$;
							double eq = balance+win$$;
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" "+strClosed
										+" || "+pips+" "+Math.abs(p.getTp()-p.getEntry())
										+" "+PrintUtils.Print2dec(win$$, false)
										+" || "+(winPips-lostPips)
										+" "+PrintUtils.Print2dec(eq, false)
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
							double pip$$ = p.getMicroLots()*0.10*pips*0.10;
							//balance += pip$$;
							double eq = balance+pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" || "+(winPips-lostPips)
										+" "+PrintUtils.Print2dec(eq, false)
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
						
						sp.addTrade((long)p.getMicroLots(),pips,Math.abs(p.getEntry()-p.getSl()),Math.abs(p.getEntry()-p.getTp()),(int)(comm),cal);
						
																		
						positions.remove(j);
					}else{
						j++;
					}//isClose
				}//isOpen
			}//positions
		}
		
		//estudio de years
		int posYears = 0;
		int posYears16 = 0;
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
	        if (year>=2016 && netPips>=0) posYears16++;
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
				|| (debug==3 
				&& pf>=1.15 && perDays>=5.0
				&& (posYears>=9 || posYears16>=3)
				)// && ff>=15000 && (ff>=25000 || pf>=2.05 || trades>=20000))
			)
		System.out.println(
				y1+" "+y2+" "+header
				//+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+h1+" "+h2
				//+" "+n
				//+" "+PrintUtils.Print2dec(fMinPips, false)
				//+" "+aMult
				+" || "
				+" "+posYears
				+" "+posYears16
				+" "+PrintUtils.Print2dec(sp.getMonthDataWinPer(), false)
				+" || "+trades						
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(perDays, false)
				+" || "
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(perR, false)
				+" "+PrintUtils.Print2dec(maxDD, false)
				+" || "+PrintUtils.Print2dec(ff, false)
				);
		
		return pf;
	}
	
	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		String currency = "eurusd";
		String pathBid = path0+currency+"_5 Mins_Bid_2004.01.01_2020.01.06.csv";
		String pathAsk = path0+currency+"_5 Mins_Ask_2004.01.01_2020.01.06.csv";
						
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathBid);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 0;i<=0;i++){		
			ArrayList<QuoteShort> dataBid = null;
			ArrayList<QuoteShort> dataAsk = null;
			ArrayList<QuoteShort> dataNoise = null;
			
			dataI 		= DAO.retrieveDataShort5m(pathBid, DataProvider.DUKASCOPY_FOREX4);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			dataBid = dataS;
			
			dataI 		= DAO.retrieveDataShort5m(pathAsk, DataProvider.DUKASCOPY_FOREX4);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			dataAsk = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataBid);
			Calendar cal = Calendar.getInstance();
			double aMaxFactorGlobal = -9999;
		
			ArrayList<String> strat = new ArrayList<String>();
			for (int j=0;j<=23;j++) strat.add("-1");
		
			
			//strat.set(23,"108 0.45 1");
			ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
			
			/*for (int y1=2004;y1<=2020;y1++){
				int y2 = y1+0;
				for (int m1=0;m1<=0;m1+=1){
					int m2 = m1+11;
					DaveTrade2019_vol2.doTest("", data, y1, y2, m1, m2, strat, true, 0.1, 2);
				}				
			}*/
			//best : 16 15249 1.63 7.22 50.99 
			//Optimizacion
			StratPerformance sp = new StratPerformance();
			for (int h1=9;h1<=9;h1++){
				//settings a 12/01/2020
				strat.set(9,"36 0.30 4 0.10");
				strat.set(10,"108 0.35 5 0.1");
				strat.set(11,"60 0.40 2 0.1");			
				strat.set(12,"72 0.45 2 0.1");			
				strat.set(13,"96 0.55 2 0.2");
				strat.set(14,"132 0.60 4 0.3");			
				strat.set(15,"84 0.50 5 0.3");			
				strat.set(16,"132 0.75 1 0.3");			
				strat.set(17,"36 0.45 4 0.2");			
				strat.set(18,"120 0.70 3 0.1");			
				strat.set(19,"108 0.65 1 0.2");			
				strat.set(20,"84 0.55 1 0.3");			
				strat.set(21,"108 0.60 1 0.3");
				strat.set(22,"96 0.50 1 0.3");
				/*for (int j=0;j<=23;j++){
					if (j!=h1)
					strat.set(j,"-1");
				}*/
				for (int n=24;n<=24;n+=12){
					for (double fMinPips=0.30;fMinPips<=0.30;fMinPips+=0.05){
						for (int mult=1;mult<=1;mult+=1){
							String str = n+" "+PrintUtils.Print2dec(fMinPips, false)+" "+mult;
							//strat.set(h1, str);
							int totalPositives = 0;
							int total2016 = 0;
							double accPf=0;
							double acc2016=0;
							double accTotal = 0;
							for (int y1=2004;y1<=2020;y1++){
								int y2 = y1+0;
								for (int m1=0;m1<=8;m1+=4){
									int m2 = m1+3;
									sp.reset();
									sp.setInitialBalance(3000);
									double pf = DaveTrade2019_vol2.doTest("",sp, dataBid,dataAsk, y1, y2, m1, m2, 
											strat, true, 2);									
									if (pf>=1.0){
										totalPositives++;
										if (y1>=2016){
											total2016++;
										}
									}
								}				
							}
							//double pfTotal = DaveTrade2019_vol2.doTest("", data, 2004, 2019, 0, 11, strat, true, 0.1, 2);
							//double pf0912 = DaveTrade2019_vol2.doTest("", data, 2009, 2012, 0, 11, strat, true, 0.1, 2);
							//double pf1315 = DaveTrade2019_vol2.doTest("", data, 2013, 2015, 0, 11, strat, true, 0.1, 2);
							sp.reset();
							sp.setInitialBalance(4000);
							double pf0407 = DaveTrade2019_vol2.doTest(h1+" "+str,sp, dataBid,dataAsk, 2004, 2007, 0, 11, strat, true, 2);
							double pf0811 = DaveTrade2019_vol2.doTest(h1+" "+str,sp, dataBid,dataAsk, 2008, 2011, 0, 11, strat, true, 2);
							double pf1215 = DaveTrade2019_vol2.doTest(h1+" "+str,sp, dataBid,dataAsk, 2012, 2015, 0, 11, strat, true, 2);
							double pf1619 = DaveTrade2019_vol2.doTest(h1+" "+str,sp, dataBid,dataAsk, 2016, 2019, 0, 11, strat, true, 2);
							/*System.out.println(h1+" "+str
									+" || "+totalPositives+" "+total2016
									+" || "
									//+" "+PrintUtils.Print2dec(pfTotal, false)
									//+" "+PrintUtils.Print2dec(pf0912, false)
									//+" "+PrintUtils.Print2dec(pf1315, false)
									+" "+PrintUtils.Print2dec(pf1619, false)
									);*/
						}
					}
				}
			}
		
			/*ArrayList<String> strat3 = new ArrayList<String>();
			for (int j=0;j<=23;j++) strat3.add("-1");
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+2;

				for (int n=10;n<=3000;n+=10){
					for (int nBars=1;nBars<=1;nBars+=1){
						String params =n+" "+nBars;
						for (int j=0;j<=23;j++) strat3.set(j,"-1");
						for (int j=h1;j<=h2;j++) strat3.set(j,params);
						for (double fMinPips=0.16;fMinPips<=0.16;fMinPips+=0.05){
							for (double aRisk = 0.2;aRisk<=0.2;aRisk+=0.10){
							
								String str = h1+" "+n+" "+nBars+" "+PrintUtils.Print2dec(fMinPips, false);
								for (int y1=2009;y1<=2009;y1++){
									int y2 = y1+9;
									TestMeanReversion.doTestAlphadude(str, data, y1, y2, 0, 11,n,fMinPips, strat3,dayPips1,false,aRisk, 2,false);
								}
							}
						}
					}
				}
				System.out.println("");
			}*/
		
		}

	}

}
