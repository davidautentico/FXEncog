package drosa.experimental.EAS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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

public class DaveTrade2019_vol2 {

	
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
		
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		
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
			 if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0) continue;
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
			
			
			if (ishOk){
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
	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.07.23.csv";
						
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
			
			for (int y1=2004;y1<=2019;y1++){
				int y2 = y1+0;
				for (int m1=0;m1<=8;m1+=1){
					int m2 = m1+3;
					DaveTrade2019_vol2.doTest("", data, y1, y2, m1, m2, strat, true, 0.2, 2);
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
