package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

public class TestMeanReversion2 {
	
	public static void doTest(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int nSMA,
			double aF,
			int thr,
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
	
		for (int i=0;i<=nSMA-1;i++){
			openArr.add(data.get(i).getOpen5());
		}
		
		
		int dayTrade = 0;
		int totalDaysTrade = 0;
		mode = 0;
		int modeIdx = 0;
		int dayPips = 0;
		int lastPips = 0;
		
		boolean canTrade = true;
		int smaValue = -1;
		boolean newBar = true;
		QuoteShort actualBar = new QuoteShort();
		QuoteShort evaluateBar = new QuoteShort();
		for (int i=nSMA+1;i<data.size()-2;i++){
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
			
			
			qLast = q;			
			comm = 00;			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);	
					
					int diffP = dayPips-lastPips;
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
			
			//valor de la sma			
			if (min%timeFrame==0){
				openArr.add(q.getOpen5());
				canTrade = true;
				newBar = true;
				//evaluateBar.copy(actualBar);
				//actualBar.copy(q);
				smaValue = (int) MathUtils.average(openArr, openArr.size()-nSMA,openArr.size()-1);
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
			
			ishOk = h>=h1 && h<=h2;
			if (ishOk){
				if (h==0 && min<15) ishOk=false;				
			}
			
			if (ishOk){
				if (y==y1 && ( month<m1 ||  month>m2)){
					//System.out.println("no puede tradear: "+DateUtils.datePrint(cal)+" "+month+" "+m1+" "+m2);
					ishOk = false;
				}
				if (y==y2 && (m<m1 || m>m2)){
					//System.out.println("no puede tradear2: "+DateUtils.datePrint(cal)+" "+month+" "+m1+" "+m2);
					ishOk = false;
				}
			}
			
			if (ishOk
					&& positions.size()<=30
					){												
				//int dist = i-modeIdx;
				//int value = Integer.valueOf(values[1]);
				int maxMin = maxMins.get(i-1);
				int minPips = (int) (aF*range);
				int slMinPips = (int) (1.0*range);
				int transactionCosts = TradingUtils.getTransactionCosts(y, h,1);
				//transactionCosts = 0;
				//if (!isMomentum) minPips = 99999999;
			//	System.out.println(DateUtils.datePrint(cal)+" minPips= "+minPips+" rango= "+range+" smaValue= "+smaValue
				//		+" || "+(q.getOpen5()-smaValue)+" "+(smaValue-q.getOpen5()));
				if (mode==1 
						&& modeIdx>0 
						&& (low!=-1)//si la candle es la suya
						&& q.getOpen5()-smaValue>=minPips
						&& maxMin>=thr
						//&& q.getOpen5()-doValue>=0.7*range
						){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.SHORT);
					p.setTp((int) (p.getEntry()-10.2*range));
					p.setSl((int) (p.getEntry()+0.6*range));
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
					//p.setExtraParam(n);
								
					dayTrade = 1;
					positions.add(p);
					
					canTrade = false;
					
					//System.out.println("[SHORT OPEN] "+DateUtils.datePrint(cal)+" "+q.getOpen5());
				}else if (mode==-1
						&& (high!=-1)//si la candle es la suya
						&& smaValue-q.getOpen5()>=minPips
						&& maxMin<=-thr
						){
				//}else if(spread>=minPips){
					int entry = q.getOpen5();
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setMaxProfit(entry);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(i);
					
					p.setPositionType(PositionType.LONG);
					p.setTp((int) (p.getEntry()+10.2*range));
					p.setSl((int) (p.getEntry()-0.6*range));
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
							}
						}
						
						if (!isClose){
							if (h>=10 && -qe.getClose5()+smaValue>=0.90*range){
								pips =  qe.getClose5()-p.getEntry();
								isClose = true;
							}
							
							if (smaValue<=qe.getClose5()){
								pips =  qe.getClose5()-p.getEntry();
								isClose = true;
							}
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = p.getEntry()-qe.getClose5();
						
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
							}
						}
						
						if (!isClose){
							if (h>=10 && qe.getClose5()-smaValue>=0.90*range){
								pips =  p.getEntry()-qe.getClose5();
								isClose = true;
							}
							
							if (smaValue>=qe.getClose5()){
								pips =  -qe.getClose5()+p.getEntry();
								isClose = true;
								/*System.out.println("[SHORT CLOSE 4] "
										+DateUtils.datePrint(cal)
										+" "+q.getOpen5()+" "+qe.getClose5()+" "+smaValue
										+" || "+pips);*/
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
				|| (avg>=3.0 
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
				y1+" "+y2
				+" "+m1+" "+m2
				+" "+header+" "+PrintUtils.Print2dec(aRisk, false)
				+" "+timeFrame
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
				+" || "+PrintUtils.Print2dec(avgRecoveryTime, false)
				);
	}

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2009.01.01_2019.06.10.csv";
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
				
											
				ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
			
				ArrayList<String> strat3 = new ArrayList<String>();
				for (int j=0;j<=23;j++) strat3.add("-1");
				//VESSION SIMPLE
				for (int h1=0;h1<=23;h1++){
					int h2 = h1+0;

					for (int n=50;n<=50;n+=1){
						for (int nBars=0;nBars<=0;nBars+=10){
							String params =n+" "+nBars;
							for (double fMinPips=0.01;fMinPips<=0.01;fMinPips+=0.05){
								for (int thr=0;thr<=1000;thr+=100){
									for (int timeFrame=5;timeFrame<=5;timeFrame+=5){
										for (double aRisk = 1.5;aRisk<=1.5;aRisk+=0.10){
											String str = h1+" "+n
													+" "+nBars
													+" "+PrintUtils.Print2dec(fMinPips, false)
													+" "+thr
													;
											for (int y1=2009;y1<=2009;y1++){
												int y2 = y1+10;
												for (int m1=0;m1<=0;m1+=4){
													int m2 = m1+11;
	
													/*ArrayList<QuoteShort> data,
													ArrayList<Integer> maxMins,
													int y1,int y2,
													int m1,int m2,
													int nSMA,
													double aF,
													boolean isMomentum,
													int timeFrame,
													double aRisk,
													boolean isTransactionsCostIncluded,
													int debug,
													boolean printDetails,
													boolean printDailyPips
													*/
													TestMeanReversion2.doTest(
															str, data,maxMins, 
															y1, y2, m1, m2,h1,h2,
															n,fMinPips,thr,
															false,timeFrame,
															aRisk,true,
															2,false,false);
													
												}
											}
										}
									}
								}
							}//thr
						}
					}
				}
				
			
		}

	}

}
