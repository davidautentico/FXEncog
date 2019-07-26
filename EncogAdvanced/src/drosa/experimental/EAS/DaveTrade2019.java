package drosa.experimental.EAS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.basicStrategies.MaxMinConfig;
import drosa.experimental.basicStrategies.TestMoves;
import drosa.experimental.basicStrategies.strats2018.EvaluationStrat;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.experimental.zznbrum.TrendInfo;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DaveTrade2019 {
	
	public static void testTicks(ArrayList<QuoteShort> data
			//ArrayList<QuoteShort> data
			){
		

		Calendar cal = Calendar.getInstance();
		
		int lastDay = -1;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);								
		}
				
	}
	
	public static void testPercent(ArrayList<QuoteShort> data,
			int y1,int y2,int tp,int sl,int n,double per){
		
		
		Calendar cal = Calendar.getInstance();
		int comm = 20;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1);i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		for (int i=n;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			int sumU = 0;
			int sumD = 0;
			for (int j=i-n;j<i;j++){
				
				if (data.get(j).getClose5()>=data.get(j).getOpen5()){
					sumU += data.get(j).getClose5()-data.get(j).getOpen5();
				}else{
					sumD += data.get(j).getOpen5()-data.get(j).getClose5();
				}
			}
			int sumT = sumU + sumD;
			double perU = sumU*100.0/sumT;
			double perD = sumD*100.0/sumT;
			
			if (perU>=per){
				int entry = q.getOpen5();
				int tpvalue = entry+tp;
				int slvalue = entry-sl;

				int close = q.getClose5();
				for (int j = i;j<data.size()-1;j++){
					QuoteShort qj = data.get(j);
					
					if (qj.getHigh5()>=tpvalue){
						close = tpvalue;
						break;
					}else if (qj.getLow5()<=slvalue){
						close = slvalue;
						break;
					}
				}
				
				int pips = entry-close;
				if (pips>=0){
					winPips += pips;
					wins++;
					
					int yo = y-y1;
					int ya = yearWinPips.get(yo);
					yearWinPips.set(yo, ya+pips);
				}else{
					lostPips += -pips;
					losses++;
					
					int yo = y-y1;
					int ya = yearLostPips.get(yo);
					yearLostPips.set(yo, ya-pips);
				}
			}else if (perD>=per){
				int entry = q.getOpen5();
				int tpvalue = entry-tp;
				int slvalue = entry+sl;
				int close = q.getClose5();
				for (int j = i;j<data.size()-1;j++){
					QuoteShort qj = data.get(j);	
					if (qj.getHigh5()>=slvalue){
						close = slvalue;
						break;
					}else if (qj.getLow5()<=tpvalue){
						close = tpvalue;
						break;
					}
					close = qj.getClose5();
				}
				
				int pips = entry-close-comm;
				if (pips>=0){
					winPips += pips;
					wins++;
					
					int yo = y-y1;
					int ya = yearWinPips.get(yo);
					yearWinPips.set(yo, ya+pips);
				}else{
					lostPips += -pips;
					losses++;
					
					int yo = y-y1;
					int ya = yearLostPips.get(yo);
					yearLostPips.set(yo, ya-pips);
				}
			}			
		}
		
		
		int ywins=0;
		for (int i=0;i<=(y2-y1);i++){
			double pf = yearWinPips.get(i)*1.0/yearLostPips.get(i);
			if (pf>=1.0) ywins++;
		}
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		//if (pf>=1.3 && avg>=3.8)
		System.out.println(
				tp+" "+sl+" "+n+" "+PrintUtils.Print2dec(per, false)
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+ywins
				);	
			
	}
	
	
	public static double testPercent2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int m1,int m2,
			int h1,int h2,int min1,int min2,
			double tpf,double slf,
			int n,
			double perMin,double perMax,double per3,
			int thr,
			int arange,
			int pipsThr,
			double maxDD,
			int minSl,
			int minDesv,
			boolean isFloating,
			boolean isReversing,
			double aRisk,
			double maxOpenRisk,
			double aBalanceInicial,
			int debug
			){
		double balanceInicial = aBalanceInicial;
		double balance = aBalanceInicial;
		double maxBalance = balance;
		double equitity = balance;
		double maxBalanceDD = 0.0;
		double balanceAdded = 0;
		
		Calendar cal = Calendar.getInstance();
		
		int comm = 25;
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
		int tp = (int) (range*tpf);
		int sl = (int) (range*tpf); 
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
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		for (int i=n;i<data.size()-1;i++){
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
			
			if (day!=lastDay){
				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-5,rangeArr.size()-1);
					
					tp = (int) (tpf*range);	
					if (tp<=40) tp=40;
					sl = (int) (slf*range);	
					if (sl<=40) sl=40;
					
					lastHigh = high;
					lastLow = low;
				}
				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			double avgp = 0;
			if (perArray.size()>1){
				avgp = MathUtils.averageD(perArray, perArray.size()-36, perArray.size()-1);
				//System.out.println(avgp+" "+perArray.size());
			}
			
			if (  true 
					&& h>=h1 && h<=h2 
					&& min>=min1 && min<=min2
					&& (h>0 || min>=15)
					//&& range>=arange
					){
						
				double avg = MathUtils.average(closeArr, i-n, i-1);
				avg = data.get(i-n).getOpen5();//n ultimas vars
				//perArray.add(avg);
				//avg = MathUtils.averageD(perArray, i-n, i-1);
				
				
				int diffUp = (int) (q.getOpen5()-avg);
				int diffDown = (int) (avg-q.getOpen5());
				
				double per1 = diffUp*100.0/range;
				double per2 = diffDown*100.0/range;
				int diffH1 = q1.getClose5()-q1.getOpen5();
				int diffHC = q1.getHigh5()-q1.getClose5();
				int diffL1 = q1.getOpen5()-q1.getClose5();
				int diffLC = q1.getOpen5()-q1.getLow5();
				
				
				
				int isOk = 0;
				if (true
						&& avgp<=per3
				){
					if (per1>=perMin && per1<=perMax){
						isOk = 1;
					}else if (per2>=perMin && per2<=perMax){
						isOk = 2;
					}
				}
								
				perArray.add(Math.abs(per1));
				
				//System.out.println("agregando: "+per1);
				
				if (isOk>0){
					if (h==0 && min<15) isOk =0;
				}
				
				//criterio linea high low
				/*isOk = 0;
				if (q1.getOpen5()<=lastHigh && q1.getHigh5()>=lastHigh) isOk=1;
				if (q1.getOpen5()>=lastLow && q1.getLow5()<=lastLow) isOk = 2;
				*/
				double actualOpenRiskPer = actualOpenRisk*100.0/equitity;
				if (true
						&& isOk==1
						&& actualOpenRiskPer<=maxOpenRisk
						//&& diffHC>=minDesv
						){
					//System.out.println(actualOpenRisk+" "+equitity+" "+actualOpenRiskPer);
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
					}else{
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
		
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
									+" "+entry
									+" || "+PrintUtils.Print2dec(riskPip, false)
									+" "+PrintUtils.Print2dec(equitity, false)
									+" || "+(winPips-lostPips)+" "+actualFloatingPips
									);
						}
						
						mode = 1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}else if (true
						&& isOk==2
						&& actualOpenRiskPer<maxOpenRisk
						){
													
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
					}else{					
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
							+" "+entry
							+" || "+PrintUtils.Print2dec(riskPip, false)
							+" "+PrintUtils.Print2dec(equitity, false)
							+" || "+(winPips-lostPips)+" "+actualFloatingPips
							);
						}						
						mode = -1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}	
			}
			
			
			int j = 0;
			int yr = y-y1;
			int ymr = yr*12+month;
			mWinPipsO.set(ymr, 0l);
			mLostPipsO.set(ymr, 0l);
			boolean closeAll = false;
			if (month!=lastCloseMonth){
				if (lastCloseMonth!=-1){					
					closeAll = true;
				}
				lastCloseMonth = month;
			}
			closeAll = false;
			equitity = balance;
			actualOpenRisk = 0;
			int closedLosses = 0;
			int totalClosedLossesPips = 0;
			actualFloatingPips = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						
						if (q.getClose5()>=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());

						if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry();
							isClose = true;
						}else if (q.getLow5()<=p.getSl()){
							pips = p.getSl()-p.getEntry()-comm;
							isClose = true;
													
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (p.getMaxProfit()-q.getClose5()>=700
								){
							//pips =q.getClose5()-p.getEntry()-comm;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						
						if (q.getClose5()<=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());
						
						if (q.getLow5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry();
							isClose = true;
						}else if (q.getHigh5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (-p.getMaxProfit()+q.getClose5()>=700
								){
							//pips = -q.getClose5()+p.getEntry()-comm;
							//isClose = true;
						}
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							equitity += win$$;
							/*if (equitity>=maxBalance){
								maxBalance = equitity;
							}*/
							
							accPositions += p.getPip$$();
							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							equitity += pip$$;
							/*double actualDD = 100.0-equitity*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}*/
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						positions.remove(j);
					}
					else{
						
						
						//acumulamos floating
						if (floatingPips>=0){							
							int yo = y-y1;
							
							long ma = mWinPipsO.get(yo*12+month);
							mWinPipsO.set(yo*12+month, ma+floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[WIN floating] "+floatingPips+" ["+yo*12+month+"] "+(ma+floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							int yo = y-y1;
							
							long ma = mLostPipsO.get(yo*12+month);
							mLostPipsO.set(yo*12+month, ma-floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[LOSS floating] "+floatingPips+" ["+yo*12+month+"] "+(ma-floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						actualFloatingPips += floatingPips;
						
						isClose = false;
						//trailing Stop
						if (p.getPositionType()==PositionType.LONG){
							//actualOpenRisk
							
							if (q.getClose5()>=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((q.getClose5()-p.getEntry())*(maxDD/100.0));
								int newSl = p.getEntry()+pipsToTrail;
								if (newSl>=p.getSl() 
										&& newSl<=q.getClose5()-20
										&& newSl>=p.getEntry()+minSl
										){
									p.setSl(p.getEntry()+pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(p.getEntry()-p.getSl())*0.1;
						}else if (p.getPositionType()==PositionType.SHORT){
							if (q.getClose5()<=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((-q.getClose5()+p.getEntry())*(maxDD/100.0));
								
								int newSl = p.getEntry()-pipsToTrail;
								if (newSl<=p.getSl() 
										&& newSl>=q.getClose5()+20
										&& newSl<=p.getEntry()-minSl
										){
									p.setSl(p.getEntry()-pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(-p.getEntry()+p.getSl())*0.1;
						}
						if (isClose){
							positions.remove(j);
						}else{
							j++;
						}
					}
				}
			}//positions
			
			
			double amount = equitity;
			double minEquitity = 1000;
			if (amount<balanceInicial
					&& amount<=minEquitity
					){
				balanceAdded += minEquitity-amount ;
				balance += minEquitity-amount ;
				equitity += minEquitity-amount ;
			}
			
			if (balance>maxBalance){
				maxBalance = amount ;
				if (debug==1){
					System.out.println("[NEW MAXEQUITITY] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							);
				}
		    }
			
			//amount = balance;
			amount = equitity;
			double actualDD = 100.0-amount*100.0/maxBalance;
			if (actualDD>maxBalanceDD){
				maxBalanceDD = actualDD;
				if (debug==1){
					System.out.println("[NEW MAXDD] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+positions.size()
							+" "+PrintUtils.Print2dec(amount, false)
							+" "+PrintUtils.Print2dec(balance, false)
							+" "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(actualDD, false)
							+" || "+PrintUtils.Print2dec(actualOpenRisk, false)
							);
				}
			}
			
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		int unclosedPositions = 0;
		if (isFloating){
			int j = 0;
			q = q1;
			QuoteShort.getCalendar(cal, q1);
			month = cal.get(Calendar.MONTH);
			 y = cal.get(Calendar.YEAR);
			//System.out.println(month);
			unclosedPositions = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						pips = q.getClose5()-p.getEntry()-comm;
						isClose = true;
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = -q.getClose5()+p.getEntry()-comm;
						isClose = true;
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							if (balance>=maxBalance){
								maxBalance = balance;
							}
							
							if (debug==1){
								System.out.println("[WIN] "+pips
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}else{
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}
							
							if (debug==1){
								System.out.println("[LOST] "+pips
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						j++;
					}
				}
			}
		}//isFloating
				
		int ywins=0;
		int yTotal = 0;
		for (int i=0;i<=(y2-y1);i++){
			double pf = yearWinPips.get(i)*1.0/yearLostPips.get(i);
			if (pf>=1.0
					|| (yearWinPips.get(i)>=0 && yearLostPips.get(i)==0) 
					){
				ywins++;
			}
			if (yearWinPips.get(i)>0 || yearLostPips.get(i)>0) yTotal++;
		}
		
		int mwins=0;
		int mTotal = 0;
		long accW = 0;
		long accL = 0;
		long lastPips = 0;
		long lastMonth = 0;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			double pf = mWinPips.get(i)*1.0/mLostPips.get(i);
			double pfreal = (mWinPips.get(i)+mWinPipsO.get(i))*1.0/(mLostPips.get(i)+mLostPipsO.get(i));
			boolean isWin = false;
			boolean isTotal = false;
			accW += mWinPips.get(i);
			accL += mLostPips.get(i);
			
			long accMonth = accW-accL+mWinPipsO.get(i)-mLostPipsO.get(i);
			
			long netMonth = accMonth-lastMonth;
			
			
			if (netMonth>0){
				mwins++;
				//System.out.println("[win] "+accMonth+" "+lastMonth);
			}else if (netMonth<0){
				//System.out.println("[loss] "+accMonth+" "+lastMonth);
			}
			lastMonth = accMonth;
			
			long global = mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i);
			if ((mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i))>0){				
				mTotal++;
			}
		}
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		//if (pf>=1.3 && avg>=3.8)
		double perDays = totalTradeDays*100.0/totalDays;
		double factor = perDays*1.0/pf;
		double fm = maxBalance/(balanceInicial+balanceAdded);
		double fmdd = (maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD;
		
		ArrayList<Integer> hs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hs.add(0);
		int total = 0;
		for (int i=0;i<closedTimes.size();i++){
			long millis = closedTimes.get(i);
			cal.setTimeInMillis(millis);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int acc = hs.get(h);
			hs.set(h, acc+closedPips.get(i));
			total+=closedPips.get(i);
		}
		
		String values = "";
		for (int i=0;i<=23;i++){
			double pr = hs.get(i)*100.0/total;
			values = values +" "+PrintUtils.Print2dec(pr, false);
		}
		
		//System.out.println(values);
		
		if (true 
				//&& pf>=1.6
				&& (debug==0
					|| (
						debug==10
						 //&& ywins>=12 
						//&& mwins>=0 && perDays>=40 && avg>=0.0 
						//&& avg>=4.0 
						//&& factor>=50
						//&& mwins>=80 
						//&& pf>=1.50
						&& perDays>=20.0
						&& maxBalanceDD<=35.0
						//&& fm>=1000
						//&& fmdd>=1000.0
						)
					)
				) 
		System.out.println(
				header
				+" "+y1+" "+y2+" "+m1+" "+m2
				+" "+h1+" "+h2
				+" "+PrintUtils.Print2dec(tpf, false)
				+" "+sl
				+" "+n
				+" "+PrintUtils.Print2dec(perMin, false)
				+" "+PrintUtils.Print2dec(perMax, false)
				//+" "+PrintUtils.Print2dec(maxDD, false)
				//+" "+minSl
				+" "+PrintUtils.Print2dec(aRisk, false)
				//+" "+PrintUtils.Print2dec(maxOpenRisk, false)
				+" || "
				+" "+trades
				//+" "+unclosedPositions
				+" "+PrintUtils.Print2dec(winPer, false)+" "+losses
				+" "+PrintUtils.Print2dec(avg, false)
				//+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				//+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				//+" "+PrintUtils.Print2dec(accPositions/trades, false)
				+" || "				
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(1.0/pf, false)
				//+" || "+mwins+" / "+mTotal
				+" || "+PrintUtils.Print2dec(perDays, false)
				//+" || "+PrintUtils.Print2dec(perDays*1.0/pf, false)
				+" ||| "
				+" "+PrintUtils.Print2dec2(balanceInicial, true)
				+" "+PrintUtils.Print2dec2(balanceInicial+balanceAdded, true)
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec((maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec2(maxBalance/(balanceInicial+balanceAdded), true)
				);	
		
		if (true
				//&& (maxBalanceDD>=100.0 || 
				&& perDays<40.0
				) return -1.0;
		
		return fmdd;		
	}
	
	public static double testPercent2Closes(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int m1,int m2,
			int h1,int h2,int min1,int min2,
			double tpf,double slf,
			int n,
			double perMin,double perMax,double per3,
			int thr,
			int arange,
			int pipsThr,
			double maxDD,
			int minSl,
			int minDesv,
			boolean isFloating,
			boolean isReversing,
			double aRisk,
			double maxOpenRisk,
			double aBalanceInicial,
			int debug,
			boolean printAlways
			){
		double balanceInicial = aBalanceInicial;
		double balance = aBalanceInicial;
		double maxBalance = balance;
		double equitity = balance;
		double maxBalanceDD = 0.0;
		double balanceAdded = 0;
		
		Calendar cal = Calendar.getInstance();
		
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
		int tp = (int) (range*tpf);
		int sl = (int) (range*tpf); 
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
		
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hours.add(0);
		int totalH=0;
		int actualH = h1;
		while (actualH!=h2){
			hours.set(actualH,1);
			actualH++;
			if (actualH>=23) actualH=0;
			//System.out.println(actualH+" "+h2);
		}
		//System.out.println("saliendo");
		hours.set(actualH,1);
		double lastEquitity=balance;
		double lastYearEquitity=balance;
		int posYears = 0;
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		for (int i=n;i<data.size()-1;i++){
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
			
			if (y!=lastYear){
				if (lastYear!=-1){
					if (equitity > lastYearEquitity) posYears++;					
				}
				lastYearEquitity= equitity;
				lastYear = y;
			}
			
			qLast = q;
			
			comm = 20;
			if (h==0) comm=30;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);
					
					tp = (int) (tpf*range);	
					if (tp<=40) tp=40;
					sl = (int) (slf*range);	
					if (sl<=40) sl=40;
					
					lastHigh = high;
					lastLow = low;
				}				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			ishOk = hours.get(h)==1;
			if (  true 
					&& ishOk
					&& min>=min1 && min<=min2
					&& (h>0 || min>=15)
					//&& range>=arange
					){
						
				//double avg = MathUtils.average(closeArr, i-n, i-1);
				double avg = data.get(i-n).getOpen5();//n ultimas vars			
				
				int diffUp = (int) (q.getOpen5()-avg);
				int diffDown = (int) (avg-q.getOpen5());
				double per1 = diffUp*100.0/range;
				double per2 = diffDown*100.0/range;
				
						
				//System.out.println(q.toString()+" || "+q.getClose5()+" "+avg+" || "+per1);
				int isOk = 0;
				if (true
				){
					if (per1>=perMin && per1<=perMax){
						isOk = 1;
						//System.out.println("[LONG] "+q.toString()+" || "+q.getClose5()+" "+avg+" || "+per1);
					}else if (per1<=-perMin){
						isOk = 2;
						//System.out.println("[SHORT] "+q.toString()+" || "+q.getClose5()+" "+avg+" || "+per1);
					}
				}
								
				perArray.add(Math.abs(per1));
				
				//System.out.println("agregando: "+per1);
				
				if (isOk>0){
					if (h==0 && min<15) isOk =0;
				}
				
				//criterio linea high low
				/*isOk = 0;
				if (q1.getOpen5()<=lastHigh && q1.getHigh5()>=lastHigh) isOk=1;
				if (q1.getOpen5()>=lastLow && q1.getLow5()<=lastLow) isOk = 2;
				*/
				double actualOpenRiskPer = actualOpenRisk*100.0/equitity;
				if (true
						&& isOk==1
						&& actualOpenRiskPer<=maxOpenRisk
						//&& diffHC>=minDesv
						){
					//System.out.println(actualOpenRisk+" "+equitity+" "+actualOpenRiskPer);
					if (isReversing){
						int entry =q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
					}else{
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
		
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
									+" "+entry
									+" || "+PrintUtils.Print2dec(riskPip, false)
									+" "+PrintUtils.Print2dec(equitity, false)
									+" || "+(winPips-lostPips)+" "+actualFloatingPips
									);
						}
						
						mode = 1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}else if (true
						&& isOk==2
						&& actualOpenRiskPer<maxOpenRisk
						){
													
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
					}else{					
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
							+" "+entry
							+" || "+PrintUtils.Print2dec(riskPip, false)
							+" "+PrintUtils.Print2dec(equitity, false)
							+" || "+(winPips-lostPips)+" "+actualFloatingPips
							);
						}						
						mode = -1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}	
			}
			
			
			int j = 0;
			int yr = y-y1;
			int ymr = yr*12+month;
			mWinPipsO.set(ymr, 0l);
			mLostPipsO.set(ymr, 0l);
			boolean closeAll = false;
			if (month!=lastCloseMonth){
				if (lastCloseMonth!=-1){					
					closeAll = true;
				}
				lastCloseMonth = month;
			}
			closeAll = false;
			equitity = balance;
			actualOpenRisk = 0;
			int closedLosses = 0;
			int totalClosedLossesPips = 0;
			actualFloatingPips = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						
						if (q.getClose5()>=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());

						if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry();
							isClose = true;
						}else if (q.getLow5()<=p.getSl()){
							pips = -p.getEntry()+p.getSl()-comm;
							isClose = true;
													
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (p.getMaxProfit()-q.getClose5()>=700
								){
							//pips =q.getClose5()-p.getEntry()-comm;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						
						if (q.getClose5()<=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());
						
						if (q.getLow5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry();
							isClose = true;
						}else if (q.getHigh5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (-p.getMaxProfit()+q.getClose5()>=700
								){
							//pips = -q.getClose5()+p.getEntry()-comm;
							//isClose = true;
						}
					}
					
					if (isClose){
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
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							equitity += win$$;
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							totalClosedLossesPips += -pips;
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
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						positions.remove(j);
					}
					else{
						
						
						//acumulamos floating
						if (floatingPips>=0){							
							int yo = y-y1;
							
							long ma = mWinPipsO.get(yo*12+month);
							mWinPipsO.set(yo*12+month, ma+floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[WIN floating] "+floatingPips+" ["+yo*12+month+"] "+(ma+floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							int yo = y-y1;
							
							long ma = mLostPipsO.get(yo*12+month);
							mLostPipsO.set(yo*12+month, ma-floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[LOSS floating] "+floatingPips+" ["+yo*12+month+"] "+(ma-floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						actualFloatingPips += floatingPips;
						
						isClose = false;
						//trailing Stop
						if (p.getPositionType()==PositionType.LONG){
							//actualOpenRisk
							
							if (q.getClose5()-p.getEntry()>=200){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((q.getClose5()-p.getEntry())*(maxDD/100.0));
								int newSl = p.getEntry()+pipsToTrail;
								if (newSl>=p.getSl() 
										&& newSl<=q.getClose5()-20
										&& newSl>=p.getEntry()+minSl
										){
									p.setSl(p.getEntry()+pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(p.getEntry()-p.getSl())*0.1;
						}else if (p.getPositionType()==PositionType.SHORT){
							if (-q.getClose5()+p.getEntry()>=200){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((-q.getClose5()+p.getEntry())*(maxDD/100.0));
								
								int newSl = p.getEntry()-pipsToTrail;
								if (newSl<=p.getSl() 
										&& newSl>=q.getClose5()+20
										&& newSl<=p.getEntry()-minSl
										){
									p.setSl(p.getEntry()-pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(-p.getEntry()+p.getSl())*0.1;
						}
						if (isClose){
							positions.remove(j);
						}else{
							j++;
						}
					}
				}
			}//positions
			
			
			double amount = equitity;
			double minEquitity = 1000;
			if (amount<balanceInicial
					&& amount<=minEquitity
					){
				balanceAdded += minEquitity-amount ;
				balance += minEquitity-amount ;
				equitity += minEquitity-amount ;
			}
			
			if (balance>maxBalance){
				maxBalance = amount ;
				if (debug==1){
					System.out.println("[NEW MAXEQUITITY] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							);
				}
		    }
			
			//amount = balance;
			amount = equitity;
			double actualDD = 100.0-amount*100.0/maxBalance;
			if (actualDD>maxBalanceDD){
				maxBalanceDD = actualDD;
				if (debug==1){
					System.out.println("[NEW MAXDD] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+positions.size()
							+" "+PrintUtils.Print2dec(amount, false)
							+" "+PrintUtils.Print2dec(balance, false)
							+" "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(actualDD, false)
							+" || "+PrintUtils.Print2dec(actualOpenRisk, false)
							);
				}
			}
			
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		int unclosedPositions = 0;
		if (isFloating){
			int j = 0;
			q = q1;
			QuoteShort.getCalendar(cal, q1);
			month = cal.get(Calendar.MONTH);
			 y = cal.get(Calendar.YEAR);
			//System.out.println(month);
			unclosedPositions = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						pips = q.getClose5()-p.getEntry()-comm;
						isClose = true;
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = -q.getClose5()+p.getEntry()-comm;
						isClose = true;
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							//long ya = yearWinPips.get(y);
							//yearWinPips.set(y, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							if (balance>=maxBalance){
								maxBalance = balance;
							}
							
							if (debug==1){
								System.out.println("[WIN] "+pips
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}else{
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							//long ya = yearLostPips.get(y);
							//yearLostPips.set(y, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}
							
							if (debug==1){
								System.out.println("[LOST] "+pips
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						j++;
					}
				}
			}
		}//isFloating
				
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		//if (pf>=1.3 && avg>=3.8)
		double perDays = totalTradeDays*100.0/totalDays;
		double factor = perDays*1.0/pf;
		double fm = maxBalance/(balanceInicial+balanceAdded);
		double fmdd = (maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD;
		
		ArrayList<Integer> hs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hs.add(0);
		int total = 0;
		for (int i=0;i<closedTimes.size();i++){
			long millis = closedTimes.get(i);
			cal.setTimeInMillis(millis);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int acc = hs.get(h);
			hs.set(h, acc+closedPips.get(i));
			total+=closedPips.get(i);
		}
		
		String values = "";
		for (int i=0;i<=23;i++){
			double pr = hs.get(i)*100.0/total;
			values = values +" "+PrintUtils.Print2dec(pr, false);
		}
		
		//estudio de years
		posYears = 0;
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
		
		//System.out.println(values);
		double profitPer = balance*100.0/(balanceInicial+balanceAdded)-100.0;
		double ff = profitPer/maxBalanceDD;
		double score = EvaluationStrat.getScore(ff, maxBalanceDD, perDays);
		
		if (true 
				//&& pf>=1.6
				&& (debug==0
					|| (
						debug==10
						 //&& ywins>=12 
						//&& mwins>=0 && perDays>=40 && avg>=0.0 
						//&& avg>=4.0 
						//&& factor>=50
						//&& mwins>=80 
						//&& pf>=1.0
						//&& perDays>=10.0
						//&& maxBalanceDD<=35.0
						
						//&& fmdd>=1000.0
						)
					)
				) {

			
			if (printAlways 
					//|| (ff>=20.0 && posYears>=12 && perDays>=20.0 && maxBalanceDD<=35.0)
					|| posYears>=12 && pf>=1.21 && trades>=300
					){
				System.out.println(
						header
						+" "+y1+" "+y2+" "+m1+" "+m2
						+" "+h1+" "+h2+" "+isReversing
						+" "+PrintUtils.Print2dec(tpf, false)
						+" "+PrintUtils.Print2dec(slf, false)
						+" "+n
						+" "+PrintUtils.Print2dec(perMin, false)
						+" "+PrintUtils.Print2dec(perMax, false)
						//+" "+PrintUtils.Print2dec(maxDD, false)
						//+" "+minSl
						+" "+PrintUtils.Print2dec(aRisk, false)
						//+" "+PrintUtils.Print2dec(maxOpenRisk, false)
						+" || "	
						//+" "+PrintUtils.Print2dec(score, false)
						+" "+posYears
						+" "+PrintUtils.Print2dec(pf, false)
						+" "+PrintUtils.Print2dec(ff, false)
						+" "+PrintUtils.Print2dec(perDays, false)
						+" || "
						+" "+trades
						//+" "+unclosedPositions
						+" "+PrintUtils.Print2dec(winPer, false)+" "+losses
						+" "+PrintUtils.Print2dec(avg, false)
						//+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
						//+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
						//+" "+PrintUtils.Print2dec(accPositions/trades, false)
						
						+" ||| "
						+" "+PrintUtils.Print2dec2(balanceInicial, true)
						+" "+PrintUtils.Print2dec2(balanceInicial+balanceAdded, true)
						+" "+PrintUtils.Print2dec2(balance, true)
						+" "+PrintUtils.Print2dec2(maxBalance, true)
						+"|| "+PrintUtils.Print2dec(profitPer,false)
						+" "+PrintUtils.Print2dec(maxBalanceDD, false)
						+"|| "+PrintUtils.Print2dec(ff, false)
						+"|| "+PrintUtils.Print2dec2(maxBalance/(balanceInicial+balanceAdded), true)
						);	
			}
			return ff;
		}
		
		/*if (true
				//&& (maxBalanceDD>=100.0 || 
				&& perDays<40.0
				) return -1.0;*/
		
		//para que no desvirtue los calculos mucho
		//if (pf>=2.5) pf = 2.5;
		
		return ff;		
	}
	
	public static double testPercent2ALL(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int m1,int m2,
			ArrayList<String> strat,
			double maxDD,
			boolean isFloating,
			boolean isReversing,
			double aRisk,
			double maxOpenRisk,
			double aBalanceInicial,
			int debug,
			boolean printAlways
			){
		double balanceInicial = aBalanceInicial;
		double balance = aBalanceInicial;
		double maxBalance = balance;
		double equitity = balance;
		double maxBalanceDD = 0.0;
		double balanceAdded = 0;
		
		Calendar cal = Calendar.getInstance();
		
		int comm = 20;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
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
		
		double lastEquitity=balance;
		double lastYearEquitity=balance;
		int posYears = 0;
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		
		String[] valuesH0 = strat.get(0).split(" ");String[] valuesH1 = strat.get(1).split(" ");String[] valuesH2 = strat.get(2).split(" ");
		String[] valuesH3 = strat.get(3).split(" ");String[] valuesH4 = strat.get(4).split(" ");String[] valuesH5 = strat.get(5).split(" ");
		String[] valuesH6 = strat.get(6).split(" ");String[] valuesH7 = strat.get(7).split(" ");String[] valuesH8 = strat.get(8).split(" ");
		String[] valuesH9 = strat.get(9).split(" ");String[] valuesH10 = strat.get(10).split(" ");String[] valuesH11 = strat.get(11).split(" ");
		String[] valuesH12 = strat.get(12).split(" ");String[] valuesH13 = strat.get(13).split(" ");String[] valuesH14 = strat.get(14).split(" ");
		String[] valuesH15 = strat.get(15).split(" ");String[] valuesH16 = strat.get(16).split(" ");String[] valuesH17 = strat.get(17).split(" ");
		String[] valuesH18 = strat.get(18).split(" ");String[] valuesH19 = strat.get(19).split(" ");String[] valuesH20 = strat.get(20).split(" ");
		String[] valuesH21 = strat.get(21).split(" ");String[] valuesH22 = strat.get(22).split(" ");String[] valuesH23 = strat.get(23).split(" ");
		
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1, m1, 1);
		calTo.set(y2,m2,31);
		int lastYear = -1;
		for (int i=200;i<data.size()-1;i++){
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
			
			 boolean canTrade = true;
			if (cal.compareTo(calFrom)<0 || cal.compareTo(calTo)>0){
				canTrade = false;
			}
			
			if (y!=lastYear){
				if (lastYear!=-1){
					if (equitity > lastYearEquitity) posYears++;					
				}
				lastYearEquitity= equitity;
				lastYear = y;
			}
			
			qLast = q;
			
			comm = 20;
			if (h==0) comm=30;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);					
				}				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
						
			String[] values = valuesH0;
			if (h==1) values = valuesH1;if (h==2) values = valuesH2;if (h==3) values = valuesH3;if (h==4) values = valuesH4;
			if (h==5) values = valuesH5;if (h==6) values = valuesH6;if (h==7) values = valuesH7;if (h==8) values = valuesH8;
			if (h==9) values = valuesH9;if (h==10) values = valuesH10;if (h==11) values = valuesH11;if (h==12) values = valuesH12;
			if (h==13) values = valuesH13;if (h==14) values = valuesH14;if (h==15) values = valuesH15;if (h==16) values = valuesH16;
			if (h==17) values = valuesH17;if (h==18) values = valuesH18;if (h==19) values = valuesH19;if (h==20) values = valuesH20;
			if (h==21) values = valuesH21;if (h==22) values = valuesH22;if (h==23) values = valuesH23;
			ishOk = values[0] !="-1";
			if (  true 
					&& canTrade
					&& ishOk
					//&& min>=min1 && min<=min2
					&& (h>0 || min>=15)
					//&& range>=arange
					){
						
				int n		= Integer.valueOf(values[2]);
				double per 	= Float.valueOf(values[3]);
				int tp 		= (int) (Float.valueOf(values[0])*range);
				int sl 		= (int) (Float.valueOf(values[1])*range);
				
				//double avg = MathUtils.average(closeArr, i-n, i-1);
				double avg = data.get(i-n).getOpen5();//n ultimas vars			
				
				int diffUp = (int) (q.getOpen5()-avg);
				int diffDown = (int) (avg-q.getOpen5());
				double per1 = diffUp*100.0/range;
				double per2 = diffDown*100.0/range;
				
				int isOk = 0;
				if (true
				){
					if (per1>=per){
						isOk = 1;
						//System.out.println("[LONG] "+q.toString()+" || "+q.getClose5()+" "+avg+" || "+per1);
					}else if (per1<=-per){
						isOk = 2;
						//System.out.println("[SHORT] "+q.toString()+" || "+q.getClose5()+" "+avg+" || "+per1);
					}
				}
								
				perArray.add(Math.abs(per1));
				
				
				if (isOk>0){
					if (h==0 && min<15) isOk =0;
				}
				
				//criterio linea high low
				/*isOk = 0;
				if (q1.getOpen5()<=lastHigh && q1.getHigh5()>=lastHigh) isOk=1;
				if (q1.getOpen5()>=lastLow && q1.getLow5()<=lastLow) isOk = 2;
				*/
				double actualOpenRiskPer = actualOpenRisk*100.0/equitity;
				if (true
						&& isOk==1
						&& actualOpenRiskPer<=maxOpenRisk
						//&& diffHC>=minDesv
						){
					//System.out.println(actualOpenRisk+" "+equitity+" "+actualOpenRiskPer);
					if (isReversing){
						int entry =q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = balance*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
					}else{
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
		
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						p.setOpenIndex(i);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
									+" "+entry
									+" || "+PrintUtils.Print2dec(riskPip, false)
									+" "+PrintUtils.Print2dec(equitity, false)
									+" || "+(winPips-lostPips)+" "+actualFloatingPips
									);
						}
						
						mode = 1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}else if (true
						&& isOk==2
						&& actualOpenRiskPer<maxOpenRisk
						){
													
					if (isReversing){
						int entry = q.getOpen5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
					}else{					
						int entry = q.getOpen5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double riskPosition = equitity*aRisk*1.0/100.0;
						double riskPip = riskPosition/(sl*0.1);
						
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setPip$$(riskPip);
						positions.add(p);
						
						if (debug==11){
							System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
							+" "+entry
							+" || "+PrintUtils.Print2dec(riskPip, false)
							+" "+PrintUtils.Print2dec(equitity, false)
							+" || "+(winPips-lostPips)+" "+actualFloatingPips
							);
						}						
						mode = -1;
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}	
			}
			
			
			int j = 0;
			int yr = y-y1;
			int ymr = yr*12+month;
			boolean closeAll = false;
			if (month!=lastCloseMonth){
				if (lastCloseMonth!=-1){					
					closeAll = true;
				}
				lastCloseMonth = month;
			}
			closeAll = false;
			equitity = balance;
			actualOpenRisk = 0;
			int closedLosses = 0;
			int totalClosedLossesPips = 0;
			actualFloatingPips = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;
						
						if (q.getClose5()>=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());

						if (q.getHigh5()>=p.getTp()){
							pips = p.getTp()-p.getEntry()-comm;
							isClose = true;
						}else if (q.getLow5()<=p.getSl()){
							pips = -p.getEntry()+p.getSl()-comm;
							isClose = true;
													
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (p.getMaxProfit()-q.getClose5()>=700
								){
							//pips =q.getClose5()-p.getEntry()-comm;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						
						if (q.getClose5()<=p.getMaxProfit())
							p.setMaxProfit(q.getClose5());
						
						if (q.getLow5()<=p.getTp()){
							pips = -p.getTp()+p.getEntry()-comm;
							isClose = true;
						}else if (q.getHigh5()>=p.getSl()){
							pips = -p.getSl()+p.getEntry()-comm;
							isClose = true;
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (-p.getMaxProfit()+q.getClose5()>=700
								){
							//pips = -q.getClose5()+p.getEntry()-comm;
							//isClose = true;
						}
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
														
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							equitity += win$$;
							
							accPositions += p.getPip$$();							
							
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips
										//+" ["+yo*12+month+"] "+(ma+pips)
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							equitity += pip$$;
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips
										//+" ["+yo*12+month+"] "
										//+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						positions.remove(j);
					}
					else{
						
						
						//acumulamos floating
						if (floatingPips>=0){							
							int yo = y-y1;
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[WIN floating] "+floatingPips+" ["+yo*12+month+"] "
										//+(ma+floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							int yo = y-y1;
							
							
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[LOSS floating] "+floatingPips+" ["+yo*12+month+"] "
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						actualFloatingPips += floatingPips;
						
						isClose = false;
						//trailing Stop
						if (p.getPositionType()==PositionType.LONG){
							//actualOpenRisk
							
							if (q.getClose5()-p.getEntry()>=200){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((q.getClose5()-p.getEntry())*(maxDD/100.0));
								int newSl = p.getEntry()+pipsToTrail;
								if (newSl>=p.getSl() 
										&& newSl<=q.getClose5()-20
										&& newSl>=p.getEntry()+20
										){
									p.setSl(p.getEntry()+pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(p.getEntry()-p.getSl())*0.1;
						}else if (p.getPositionType()==PositionType.SHORT){
							if (-q.getClose5()+p.getEntry()>=200){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((-q.getClose5()+p.getEntry())*(maxDD/100.0));
								
								int newSl = p.getEntry()-pipsToTrail;
								if (newSl<=p.getSl() 
										&& newSl>=q.getClose5()+20
										&& newSl<=p.getEntry()-20
										){
									p.setSl(p.getEntry()-pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(-p.getEntry()+p.getSl())*0.1;
						}
						if (isClose){
							positions.remove(j);
						}else{
							j++;
						}
					}
				}
			}//positions
			
			
			double amount = equitity;
			double minEquitity = 1000;
			if (amount<balanceInicial
					&& amount<=minEquitity
					){
				balanceAdded += minEquitity-amount ;
				balance += minEquitity-amount ;
				equitity += minEquitity-amount ;
			}
			
			if (balance>maxBalance){
				maxBalance = amount ;
				if (debug==1){
					System.out.println("[NEW MAXEQUITITY] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							);
				}
		    }
			
			//amount = balance;
			amount = equitity;
			double actualDD = 100.0-amount*100.0/maxBalance;
			if (actualDD>maxBalanceDD){
				maxBalanceDD = actualDD;
				if (debug==1){
					System.out.println("[NEW MAXDD] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+positions.size()
							+" "+PrintUtils.Print2dec(amount, false)
							+" "+PrintUtils.Print2dec(balance, false)
							+" "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(actualDD, false)
							+" || "+PrintUtils.Print2dec(actualOpenRisk, false)
							);
				}
			}
			
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		int unclosedPositions = 0;
		if (isFloating){
			int j = 0;
			q = q1;
			QuoteShort.getCalendar(cal, q1);
			month = cal.get(Calendar.MONTH);
			 y = cal.get(Calendar.YEAR);
			//System.out.println(month);
			unclosedPositions = positions.size();
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						pips = q.getClose5()-p.getEntry()-comm;
						isClose = true;
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = -q.getClose5()+p.getEntry()-comm;
						isClose = true;
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							//long ya = yearWinPips.get(y);
							//yearWinPips.set(y, ya+pips);
							
							//long ma = mWinPips.get(yo*12+month);
							//mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							if (balance>=maxBalance){
								maxBalance = balance;
							}
							
							if (debug==1){
								System.out.println("[WIN] "+pips
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}else{
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							//long ya = yearLostPips.get(y);
							//yearLostPips.set(y, ya-pips);
							
							//long ma = mLostPips.get(yo*12+month);
							//mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}
							
							if (debug==1){
								System.out.println("[LOST] "+pips
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						j++;
					}
				}
			}
		}//isFloating
				
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		//if (pf>=1.3 && avg>=3.8)
		double perDays = totalTradeDays*100.0/totalDays;
		double factor = perDays*1.0/pf;
		double fm = maxBalance/(balanceInicial+balanceAdded);
		double fmdd = (maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD;
		
		ArrayList<Integer> hs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hs.add(0);
		int total = 0;
		for (int i=0;i<closedTimes.size();i++){
			long millis = closedTimes.get(i);
			cal.setTimeInMillis(millis);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int acc = hs.get(h);
			hs.set(h, acc+closedPips.get(i));
			total+=closedPips.get(i);
		}
		
		String values = "";
		for (int i=0;i<=23;i++){
			double pr = hs.get(i)*100.0/total;
			values = values +" "+PrintUtils.Print2dec(pr, false);
		}
		
		//estudio de years
		posYears = 0;
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
		
		//System.out.println(values);
		double profitPer = balance*100.0/(balanceInicial+balanceAdded)-100.0;
		double ff = profitPer/maxBalanceDD;
		double score = EvaluationStrat.getScore(ff, maxBalanceDD, perDays);
		
		if (true 
				//&& pf>=1.6
				&& (debug==0
					|| (
						debug==10
						 //&& ywins>=12 
						//&& mwins>=0 && perDays>=40 && avg>=0.0 
						//&& avg>=4.0 
						//&& factor>=50
						//&& mwins>=80 
						//&& pf>=1.0
						//&& perDays>=10.0
						//&& maxBalanceDD<=35.0
						
						//&& fmdd>=1000.0
						)
					)
				) {

			
			if (printAlways 
					//|| (ff>=20.0 && posYears>=12 && perDays>=20.0 && maxBalanceDD<=35.0)
					//|| posYears>=12 && pf>=1.21 && trades>=300
					//|| pf>=2.0 && (ff>=9000 
						//|| (ff>=8000 && maxBalanceDD<=15.0)
						//|| (ff>=7000 && maxBalanceDD<=10.0))
					|| posYears>=12
						
					){
				System.out.println(
						header
						+" "+y1+" "+y2+" "+m1+" "+m2
						//+" "+h1+" "+h2
						+" "+isReversing
						//+" "+PrintUtils.Print2dec(maxDD, false)
						//+" "+minSl
						+" "+PrintUtils.Print2dec(aRisk, false)
						//+" "+PrintUtils.Print2dec(maxOpenRisk, false)
						+" || "	
						//+" "+PrintUtils.Print2dec(score, false)
						+" "+posYears
						+" pf="+PrintUtils.Print2dec(pf, false)
						+" ff="+PrintUtils.Print2dec(ff, false)
						+" dd="+PrintUtils.Print2dec(maxBalanceDD, false)
						+" perDays="+PrintUtils.Print2dec(perDays, false)
						+" || "
						+" "+trades
						//+" "+unclosedPositions
						+" "+PrintUtils.Print2dec(winPer, false)+" "+losses
						+" "+PrintUtils.Print2dec(avg, false)
						//+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
						//+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
						//+" "+PrintUtils.Print2dec(accPositions/trades, false)
						
						+" ||| "
						+" "+PrintUtils.Print2dec2(balanceInicial, true)
						+" "+PrintUtils.Print2dec2(balanceInicial+balanceAdded, true)
						+" "+PrintUtils.Print2dec2(balance, true)
						+" "+PrintUtils.Print2dec2(maxBalance, true)
						+"|| "+PrintUtils.Print2dec(profitPer,false)
						+" "+PrintUtils.Print2dec(maxBalanceDD, false)
						+"|| "+PrintUtils.Print2dec(ff, false)
						+"|| "+PrintUtils.Print2dec2(maxBalance/(balanceInicial+balanceAdded), true)
						);	
			}
			return ff;
		}
		
		/*if (true
				//&& (maxBalanceDD>=100.0 || 
				&& perDays<40.0
				) return -1.0;*/
		
		//para que no desvirtue los calculos mucho
		//if (pf>=2.5) pf = 2.5;
		
		return ff;		
	}
	
	public static double getRisk(double per1){
		
		double aRisk = 0.0;
		if (per1>=90.0){
			aRisk = 5.4;
		}else if (per1>=85.0){
			aRisk = 4.80;
		}else if (per1>=80.0){
			aRisk = 4.20;
		}else if (per1>=70.0){
			aRisk = 3.60;
		}else if (per1>=65.0){
			aRisk = 2.8;
		}else if (per1>=60.0){
			aRisk = 2.40;
		}else if (per1>=55.0){
			aRisk = 1.70;
		}else if (per1>=50.0){
			aRisk = 1.10;
		}else if (per1>=45.0){
			aRisk = 0.80;
		}else if (per1>=40.0){
			aRisk = 0.5;
		}else if (per1>=30.0){
			aRisk = 0.3;
		}else if (per1>=25.0){
			aRisk = 0.2;
		}
		
		aRisk = 0.0;
		if (per1>=30.0){
			aRisk = 0.50;
		}else if (per1>=50){
			aRisk = 1.25;
		}else if (per1>=70){
			aRisk = 2.75;
		}else if (per1>=80){
			aRisk = 8.5;
		}
		
		return aRisk;
		
	}
	
	public static double testPercentCompleteClose(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,int min1,int min2,
			double tpf,int sl,int n,
			double per,
			int thr,
			int arange,
			int pipsThr,
			double maxDD,
			int minSl,
			boolean isFloating,
			boolean isReversing,
			//double aRisk,
			double maxOpenRisk,
			double aBalanceInicial,
			int debug
			){
		double balanceInicial = aBalanceInicial;
		double balance = aBalanceInicial;
		double maxBalance = balance;
		double equitity = balance;
		double maxBalanceDD = 0.0;
		double balanceAdded = 0;
		
		Calendar cal = Calendar.getInstance();
		
		int comm = 25;
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
		int range = 800;
		int tp = (int) (range*tpf);
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
		
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		for (int i=n;i<data.size()-1;i++){
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
			
			if (day!=lastDay){
				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-n,rangeArr.size()-1);
					
					tp = (int) (tpf*range);	
					if (tp<=40) tp=40;
				}
				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			if (h>=h1 && h<=h2 && min>=min1 && min<=min2
					&& (high-low)>=arange
					){
				
		
				double avg = MathUtils.average(closeArr, i-n, i-1);
				avg = data.get(i-n).getOpen5();//n ultimas vars
				
				
				int diffUp = (int) (q.getClose5()-avg);
				int diffDown = (int) (avg-q.getClose5());
				
				double per1 = diffUp*100.0/range;
				double per2 = diffDown*100.0/range;
				

				double actualOpenRiskPer = actualOpenRisk*100.0/equitity;
				if (true
						//&& maxMin<=-thr
						&& mode>=0
						//&& diffdo<=-800
						&& per1>=per
						&& actualOpenRiskPer<=maxOpenRisk
						
						//&& per2>=per
						//&& per2>=per
						){
					//System.out.println(actualOpenRisk+" "+equitity+" "+actualOpenRiskPer);
					if (isReversing){
						int entry = q.getClose5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double aRisk = DaveTrade2019.getRisk(per1);
						
						if (aRisk>0){						
							double riskPosition = balance*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
							
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.SHORT);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							p.setOpenIndex(i);
							positions.add(p);
						}
					}else{
						int entry = q.getClose5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double aRisk = DaveTrade2019.getRisk(per1);
						
						if (aRisk>0){	
							double riskPosition = balance*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
			
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.LONG);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							p.setOpenIndex(i);
							positions.add(p);
						
							if (debug==1){
								System.out.println("[OPEN LONG] "+DateUtils.datePrint(cal)
										+" "+entry
										+" "+PrintUtils.Print2dec(riskPip, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
							
							mode = 1;
						}
					}
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}else if (true
						//&& maxMin>=thr
						&& mode<=0
						//&& diffdo>=800
						&& per2>=per
						//&& per1>=per
						&& actualOpenRiskPer<maxOpenRisk
						){
													
					if (isReversing){
						int entry = q.getClose5();
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						
						double aRisk = DaveTrade2019.getRisk(per2);
						
						if (aRisk>0){	
							double riskPosition = equitity*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
							
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.LONG);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							positions.add(p);
						}
					}else{					
						int entry = q.getClose5();
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						
						double aRisk = DaveTrade2019.getRisk(per2);
						
						if (aRisk>0){	
							double riskPosition = equitity*aRisk*1.0/100.0;
							double riskPip = riskPosition/(sl*0.1);
							
							PositionShort p = new PositionShort();
							p.setEntry(entry);
							p.setTp(tpvalue);
							p.setSl(slvalue);
							p.setMaxProfit(entry);
							p.setPositionType(PositionType.SHORT);
							p.setPositionStatus(PositionStatus.OPEN);
							p.setPip$$(riskPip);
							positions.add(p);
							
							if (debug==1){
								System.out.println("[OPEN SHORT] "+DateUtils.datePrint(cal)
								+" "+entry
								+" "+PrintUtils.Print2dec(riskPip, false)
								+" "+PrintUtils.Print2dec(equitity, false)
								);
							}
							
							mode = -1;
						}
					}
					
																			
					//if (h==0) mode = -1;
					if (day!=lastTradeDay){
						totalTradeDays++;
						lastTradeDay = day;
					}
				}	
			}
			
			
			int j = 0;
			int yr = y-y1;
			int ymr = yr*12+month;
			mWinPipsO.set(ymr, 0l);
			mLostPipsO.set(ymr, 0l);
			boolean closeAll = false;
			if (month!=lastCloseMonth){
				if (lastCloseMonth!=-1){					
					closeAll = true;
				}
				lastCloseMonth = month;
			}
			closeAll = false;
			equitity = balance;
			actualOpenRisk = 0;
			int closedLosses = 0;
			int totalClosedLossesPips = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int actualSl = 0;
				long duration = i-p.getOpenIndex();
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						floatingPips = q.getClose5()-p.getEntry()-comm;

						if (q.getClose5()>=p.getTp()){
							pips = p.getTp()-p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()<=p.getSl()){
							pips = q.getClose5()-p.getEntry()-comm;
							isClose = true;
													
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (duration>=288 && floatingPips<=100
								&& h>=15 && h<=16
								){
							pips =floatingPips;
							//isClose = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						floatingPips = -q.getClose5()+p.getEntry()-comm;
						if (q.getClose5()<=p.getTp()){
							pips = -q.getClose5()+p.getEntry()-comm;
							isClose = true;
						}else if (q.getClose5()>=p.getSl()){
							pips = -q.getClose5()+p.getEntry()-comm;
							isClose = true;
						}else if (closeAll){
							pips =floatingPips;
							isClose = true;
						}else if (duration>=288 && floatingPips<=100
								&& h>=15 && h<=16
								){
							pips =floatingPips;
							//isClose = true;
						}
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							equitity += win$$;
							/*if (equitity>=maxBalance){
								maxBalance = equitity;
							}*/
							
							accPositions += p.getPip$$();
														
							if (debug==1){
								System.out.println("[WIN] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma+pips)
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							totalClosedLossesPips += -pips;
							closedTimes.add(cal.getTimeInMillis());
							closedPips.add(-pips);
							
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							equitity += pip$$;
							/*double actualDD = 100.0-equitity*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}*/
							
							accPositions += p.getPip$$();
							
							if (debug==1){
								System.out.println("[LOST] "
										+" "+DateUtils.datePrint(cal)
										+" || "+pips+" ["+yo*12+month+"] "+(ma-pips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						//acumulamos floating
						if (floatingPips>=0){							
							int yo = y-y1;
							
							long ma = mWinPipsO.get(yo*12+month);
							mWinPipsO.set(yo*12+month, ma+floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[WIN floating] "+floatingPips+" ["+yo*12+month+"] "+(ma+floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}else{
							int yo = y-y1;
							
							long ma = mLostPipsO.get(yo*12+month);
							mLostPipsO.set(yo*12+month, ma-floatingPips);
							
							double pip$$ = p.getPip$$()*floatingPips*0.1;
							equitity += pip$$;
							
							if (debug==2){
								System.out.println("[LOSS floating] "+floatingPips+" ["+yo*12+month+"] "+(ma-floatingPips)
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(equitity, false)
										);
							}
						}
						
						isClose = false;
						//trailing Stop
						if (p.getPositionType()==PositionType.LONG){
							//actualOpenRisk
							
							if (q.getClose5()>=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((q.getClose5()-p.getEntry())*(maxDD/100.0));
																							
								int newSl = p.getEntry()+pipsToTrail;
								if (newSl>=p.getSl() 
										&& newSl<=q.getClose5()-20
										&& newSl>=p.getEntry()+minSl
										){
									p.setSl(p.getEntry()+pipsToTrail);
								}	
							}
							actualOpenRisk += p.getPip$$()*(p.getEntry()-p.getSl())*0.1;
						}else if (p.getPositionType()==PositionType.SHORT){
							if (q.getClose5()<=p.getMaxProfit()){
								p.setMaxProfit(q.getClose5());
								int pipsToTrail = (int) ((-q.getClose5()+p.getEntry())*(maxDD/100.0));
								

								int newSl = p.getEntry()-pipsToTrail;
								if (newSl<=p.getSl() 
										&& newSl>=q.getClose5()+20
										&& newSl<=p.getEntry()-minSl
										){
									p.setSl(p.getEntry()-pipsToTrail);
								}
							}
							actualOpenRisk += p.getPip$$()*(-p.getEntry()+p.getSl())*0.1;
						}
						if (isClose){
							positions.remove(j);
						}else{
							j++;
						}
					}
				}
			}//positions
			
			
			double amount = equitity;
			
			if (amount<balanceInicial){
				balanceAdded += balanceInicial-amount ;
				balance += balanceInicial-amount ;
				equitity += balanceInicial-amount ;
			}
			
			if (amount>maxBalance){
				maxBalance = amount ;
				if (debug==1){
					System.out.println("[NEW MAXEQUITITY] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							);
				}
		    }
			
			double actualDD = 100.0-amount*100.0/maxBalance;
			if (actualDD>maxBalanceDD){
				maxBalanceDD = actualDD;
				if (debug==1){
					System.out.println("[NEW MAXDD] "
							+" "+DateUtils.datePrint(cal)
							+" || "
							+" "+PrintUtils.Print2dec(amount, false)
							+" "+PrintUtils.Print2dec(balance, false)
							+" "+PrintUtils.Print2dec(maxBalance, false)
							+" "+PrintUtils.Print2dec(actualDD, false)
							+" || "+PrintUtils.Print2dec(actualOpenRisk, false)
							);
				}
			}
			
			
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();			
		}
		
		if (isFloating){
			int j = 0;
			q = q1;
			QuoteShort.getCalendar(cal, q1);
			month = cal.get(Calendar.MONTH);
			 y = cal.get(Calendar.YEAR);
			//System.out.println(month);
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					int pips = 0;
					int floatingPips = 0;
					boolean isClose = false;
					
					if (p.getPositionType()==PositionType.LONG){
						pips = q.getClose5()-p.getEntry()-comm;
						isClose = true;
					}else if (p.getPositionType()==PositionType.SHORT){
						pips = -q.getClose5()+p.getEntry()-comm;
						isClose = true;
					}
					
					if (isClose){
						if (pips>=0){
							winPips += pips;
							wins++;
							
							int yo = y-y1;
							long ya = yearWinPips.get(yo);
							yearWinPips.set(yo, ya+pips);
							
							long ma = mWinPips.get(yo*12+month);
							mWinPips.set(yo*12+month, ma+pips);
							
							//actualizamos balance
							double win$$ = p.getPip$$()*pips*0.1;
							balance += win$$;
							if (balance>=maxBalance){
								maxBalance = balance;
							}
							
							if (debug==1){
								System.out.println("[WIN] "+pips
										+" "+PrintUtils.Print2dec(win$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}else{
							lostPips += -pips;
							losses++;
							
							int yo = y-y1;
							long ya = yearLostPips.get(yo);
							yearLostPips.set(yo, ya-pips);
							
							long ma = mLostPips.get(yo*12+month);
							mLostPips.set(yo*12+month, ma-pips);
							
							//actualizamos balance
							double pip$$ = p.getPip$$()*pips*0.1;
							balance += pip$$;
							double actualDD = 100.0-balance*100.0/maxBalance;
							if (actualDD>=maxBalanceDD){
								maxBalanceDD = actualDD;
							}
							
							if (debug==1){
								System.out.println("[LOST] "+pips
										+" "+PrintUtils.Print2dec(pip$$, false)
										+" "+PrintUtils.Print2dec(balance, false)
										);
							}
						}
						positions.remove(j);
					}
					else{												
						j++;
					}
				}
			}
		}//isFloating
				
		int ywins=0;
		int yTotal = 0;
		for (int i=0;i<=(y2-y1);i++){
			double pf = yearWinPips.get(i)*1.0/yearLostPips.get(i);
			if (pf>=1.0
					|| (yearWinPips.get(i)>=0 && yearLostPips.get(i)==0) 
					){
				ywins++;
			}
			if (yearWinPips.get(i)>0 || yearLostPips.get(i)>0) yTotal++;
		}
		
		int mwins=0;
		int mTotal = 0;
		long accW = 0;
		long accL = 0;
		long lastPips = 0;
		long lastMonth = 0;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			double pf = mWinPips.get(i)*1.0/mLostPips.get(i);
			double pfreal = (mWinPips.get(i)+mWinPipsO.get(i))*1.0/(mLostPips.get(i)+mLostPipsO.get(i));
			boolean isWin = false;
			boolean isTotal = false;
			accW += mWinPips.get(i);
			accL += mLostPips.get(i);
			
			long accMonth = accW-accL+mWinPipsO.get(i)-mLostPipsO.get(i);
			
			long netMonth = accMonth-lastMonth;
			
			
			if (netMonth>0){
				mwins++;
				//System.out.println("[win] "+accMonth+" "+lastMonth);
			}else if (netMonth<0){
				//System.out.println("[loss] "+accMonth+" "+lastMonth);
			}
			lastMonth = accMonth;
			
			long global = mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i);
			if ((mWinPips.get(i)+mLostPips.get(i)+mWinPipsO.get(i)+mLostPipsO.get(i))>0){				
				mTotal++;
				
				/*System.out.println(mWinPips.get(i)+" "+mWinPipsO.get(i)
					+" | "+mLostPips.get(i)+" "+mLostPipsO.get(i)
						+" || "+PrintUtils.Print2dec(pfreal, false)
						+" ||| "+accW+" "+accL
				);*/
			}
			
			/*if (isWin && !isTotal){
				System.out.println(mWinPips.get(i)
						+" "+mLostPips.get(i)
						+" "+mWinPipsO.get(i)
						+" "+mLostPipsO.get(i)
						+" || "+global
				);
			}*/
		}
		
		int trades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		//if (pf>=1.3 && avg>=3.8)
		double perDays = totalTradeDays*100.0/totalDays;
		double factor = perDays*1.0/pf;
		double fm = maxBalance/(balanceInicial+balanceAdded);
		
		ArrayList<Integer> hs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hs.add(0);
		int total = 0;
		for (int i=0;i<closedTimes.size();i++){
			long millis = closedTimes.get(i);
			cal.setTimeInMillis(millis);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int acc = hs.get(h);
			hs.set(h, acc+closedPips.get(i));
			total+=closedPips.get(i);
		}
		
		String values = "";
		for (int i=0;i<=23;i++){
			double pr = hs.get(i)*100.0/total;
			values = values +" "+PrintUtils.Print2dec(pr, false);
		}
		
		//System.out.println(values);
		
		if (true && debug!=10
				 //&& ywins>=12 
				//&& mwins>=0 && perDays>=40 && avg>=0.0 
				//&& avg>=4.0 
				//&& factor>=50
				//&& mwins>=80 
				//&& pf>=1.45
				//&& fm>=400.0
				&& perDays>=30.0
				&& maxBalanceDD<=30.0
				) 
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+PrintUtils.Print2dec(tpf, false)
				+" "+sl
				+" "+n
				//+" "+PrintUtils.Print2dec(per, false)				
				+" "+PrintUtils.Print2dec(maxDD, false)
				//+" "+minSl
				//+" "+PrintUtils.Print2dec(aRisk, false)
				+" "+PrintUtils.Print2dec(maxOpenRisk, false)
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(winPips*0.1/wins, false)
				+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
				//+" "+PrintUtils.Print2dec(accPositions/trades, false)
				+" || "				
				+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(1.0/pf, false)
				//+" || "+mwins+" / "+mTotal
				+" || "+PrintUtils.Print2dec(perDays, false)
				//+" || "+PrintUtils.Print2dec(perDays*1.0/pf, false)
				+" ||| "
				+" "+PrintUtils.Print2dec2(balanceInicial, true)
				+" "+PrintUtils.Print2dec2(balanceInicial+balanceAdded, true)
				+" "+PrintUtils.Print2dec2(balance, true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec(maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec((maxBalance*100.0/(balanceInicial+balanceAdded))/maxBalanceDD, false)
				+"|| "+PrintUtils.Print2dec2(maxBalance/(balanceInicial+balanceAdded), true)
				);	
		
		return maxBalance/(balanceInicial+balanceAdded);		
	}
	
	public static void writeBricks(ArrayList<QuoteShort> data,String FileName){
		
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
		}
		
	}
	

	public static void main(String[] args) {
		
		String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.07.23.csv";
		
		//String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2009.01.01_2018.12.12.csv";
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2018.12.12.csv";
		
		String pathEURUSD_bricks5 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_50_bricks.csv";
		String pathEURUSD_bricks10 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_100_bricks.csv";
		String pathEURUSD_bricks20 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_200_bricks.csv";
		String pathEURUSD_bricks40 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_400_bricks.csv";
		String pathEURUSD_bricks60 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_600_bricks.csv";
		String pathEURUSD_bricks80 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_800_bricks.csv";
		String pathEURUSD_bricks100 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1000_bricks.csv";
		String pathEURUSD_bricks150 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1500_bricks.csv";
		//String pathEURUSD = path0+"EURUSD_Ticks_2018.10.04_2018.10.04.csv_50_bricks.csv";
		
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
			
			//Tick.readFromDiskToQuoteShort(dataI, path, 5);
			
			/*for (int sizeBrick=50;sizeBrick<=50;sizeBrick+=50){
				dataI.clear();
				
				Tick.createBricks(dataI, path, 5,sizeBrick);	
				
				TestLines.calculateCalendarAdjustedSinside(dataI);			
				dataS = TradingUtils.cleanWeekendDataS(dataI);  
				ArrayList<QuoteShort> data = null;
				data = dataS;			
				System.out.println("Total size: "+data.size());	
				
				QuoteShort.saveToDisk(data, pathEURUSD+'_'+sizeBrick+"_bricks.csv");
				System.out.println("Total size: "+data.size());	
			}*/
			
															
			//ArrayList<QuoteShort> data = QuoteShort.readFromDisk(pathEURUSD_bricks5);
			
			/*ArrayList<QuoteShort> data2 = QuoteShort.createBricksFromBricks(data, 1000);
			QuoteShort.saveToDisk(data2,pathEURUSD_bricks100);
			
			data2 = QuoteShort.createBricksFromBricks(data, 1500);
			QuoteShort.saveToDisk(data2,pathEURUSD_bricks150);*/
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			Calendar cal = Calendar.getInstance();
			System.out.println("path: "+path+" "+data.size());
			double aMaxFactorGlobal = -9999;
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+0;
				System.out.println(h1);
			double aMaxFactor = -9999;
			double aRiskSelected = 0.0;
			double aBalanceInicial = 5000;
			double aMaxOpenRiskPar = 50.0;
			double aTpf = 1.0;
			int totalHours = 0;
			int totalTests = 0;
			int totalHoursWins = 0;
			
			//mean-reverting 0.1-1.0 0.3-1.0 50-100 10.0
			
			//MOMENTUM
			//3:  0.20 0.60 24 50.00 1050.00 0.20 ||  11 2.24 4.11 1.51 ||  276 88.04 33 10.47
			//4:  0.20 0.50 36 50.00 1050.00 0.20 ||  11 1.57 2.29 2.95 ||  632 78.80 134 6.74
			//5:  2.20 0.80 36 50.00 1050.00 0.20 ||  11 1.82 3.22 2.92 ||  676 82.25 120 13.12
			//6:  0.60 0.60 36 50.00 1050.00 0.20 ||  11 1.61 2.05 1.84 ||  415 76.39 98 9.41
			//9:  0.20 0.50 33 50.00 1050.00 0.20 ||  13 1.56 4.59 2.92 ||  437 82.61 76 6.56
			//10: 0.80 0.80 54 60.00 1060.00 0.20 ||  14 1.83 4.91 4.13 ||  802 82.29 142 11.65
			//11: 1.00 0.40 30 60.00 1060.00 0.20 ||  10 1.32 2.08 5.48 ||  1080 64.35 385 5.11
			//12: 4.00 0.50 21 50.00 1050.00 0.20 ||  8 1.42 1.63 6.41 ||  1106 62.66 413 8.05
			//13:
			
			//11: 0.75 0.30 72 70.00 2004 2018 0 11 false 0.20 ||  12 pf=1.66 ff=7.46 dd=9.97 perDays=4.25 ||  1055 66.92 349 7.67 
			//12: 0.10 0.70 36 50.00 2004 2018 0 11 false 0.20 ||  12 pf=1.38 ff=7.13 dd=4.07 perDays=14.40
			//13: 
			
			//14: 0.90 0.40 75 100.00 1100.00 0.20 ||  13 1.85 3.76 2.43 ||  609 67.98 195 10.01 
			//15: 1.10 0.80 21 60.00 1060.00 0.20 ||  14 1.83 4.94 6.17 ||  1080 79.72 219 12.10
			//16: 0.30 0.50 48 70.00 1070.00 0.20 ||  15 1.77 8.95 10.20 ||  2451 75.76 594 9.32
			//17: 3.50 0.60 39 80.00 1080.00 0.20 ||  14 1.81 7.80 7.25 ||  1873 73.09 504 13.42
			//18: 0.30 0.80 30 80.00 1080.00 0.20 ||  15 1.92 9.97 5.94 ||  1056 85.32 155 10.99
			//19; 0.20 0.80 57 70.00 1070.00 0.20 ||  14 1.58 6.28 13.96 ||  4033 86.49 545 6.83
			//20: 0.20 0.70 33 70.00 1070.00 0.20 ||  15 3.06 9.13 3.25 ||  536 92.16 42 13.09
			//21: 0.30 0.70 63 90.00 1090.00 0.20 ||  15 1.44 3.26 5.69 ||  1313 78.83 278 6.91
			//22: 1.00 0.70 57 80.00 1080.00 0.20 ||  15 2.18 6.53 3.72 ||  976 80.53 190 16.16
			//23: 0.40 0.80 69 90.00 1090.00 0.20 ||  14 2.18 3.56 2.95 ||  737 86.16 102 13.72
			
			//REVERSE
			//0: 0.10 0.90 24 20.00 1020.00 0.20 ||  15 2.11 9.17 13.27 ||  2520 95.04 125 6.28
			//1
			//0.10 1.70 36 20.00 1020.00 0.20 ||  15 1.68 8.71 20.19 ||  4369 96.68 145 4.88
			
			ArrayList<String> strat = new ArrayList<String>();
			for (int j=0;j<=23;j++) strat.add("-1");
			strat.set(9,"5.00 0.30 33 45.00");
			strat.set(10,"0.80 0.40 39 50.00");
			strat.set(14,"4.80 0.30 75 100.00");
			strat.set(15,"9.00 0.50 24 80.00");
			strat.set(16,"6.00 0.40 45 65.00");
			strat.set(17,"3.50 0.60 18 60.00");
			strat.set(18,"5.50 0.40 90 100.00");
			strat.set(19,"0.10 0.80 45 60.00");
			strat.set(20,"0.30 0.80 45 65.00");
			strat.set(21,"0.30 0.70 69 95.00");
			strat.set(22,"1.10 0.20 33 80.00");
			strat.set(23,"0.30 0.40 69 90.00");			
			//pf=2.03 ff=10012.89 dd=21.70 perDays=34.64 
			
			//for (int j=0;j<=23;j++) strat.set(j,"-1");
			
			for (int h=9;h<=9;h++){
				for (double fTp=5.0;fTp<=5.0;fTp+=0.10){
					for (double fSl=0.5;fSl<=0.5;fSl+=0.1){
						for (int bbars=22;bbars<=22;bbars+=1){
							for (double per=40.0;per<=40.0;per+=1.0){
								//String str = "0.4 0.80 "+bbars+" "+PrintUtils.Print2dec(per,false);
								//for (int j=0;j<=23;j++) strat.set(j,"-1");
								String str = PrintUtils.Print2dec(fTp,false)
											+" "+PrintUtils.Print2dec(fSl,false)
											+" "+bbars
											+" "+PrintUtils.Print2dec(per,false)
											;
								//for (int he=h;he<23;he++)
									//strat.set(he,str);
								for (int y1=2004;y1<=2019;y1+=1){
									int y2 = y1+0;	
									for (int m1=0;m1<=9;m1+=3){
										int m2 = m1+2;
										if (m2>11){
											m2 = m2 % 12;
											y2=y1+1;
										}
										String header = y1+" "+y2+" || "+h+" || "+str;
										for (double aRisk=0.3;aRisk<=0.3;aRisk+=0.1)
											DaveTrade2019.testPercent2ALL(header, data, maxMins, y1, y2, m1, m2, strat, 20.0, true,false, aRisk, 9999.0, aBalanceInicial, 0,true);
									}
								}
							}
						}
					}
				}
			}
			
			/*for (double tpf=1.0;tpf<=5.0;tpf+=0.05){		//TP		
				for (int n =12;n<=100;n+=6){ //N
						//System.out.println("n.."+n);
						for (double slf = 0.40;slf<=2.00;slf+=0.20){ //SL
						for (double perMin =50.0;perMin<=100.0;perMin +=10.0){ //PER
							double perMax = perMin + 1000.0;
								for (int min1 = 00;min1<=00;min1+=5){
									int min2 = min1+55;
									for (int thr =0;thr<=0;thr+=100){

										double accYearsPf = 0;
										int positiveYears = 0;
										int totalSum = 0;
										double yActual = 1;
										double accScore =0.0;
										for (int y1=2004;y1<=2004;y1+=1){
											int y2 = y1+14;	
											for (int m1=0;m1<=0;m1+=1){
												int m2 = m1+11;
												if (m2>11){
													m2 = m2 % 12;
													y2=y1+1;
												}
												//if (y1>=2019 && m1>=1) continue;
												for (int arange =0;arange<=0;arange+=100){
													for (int pipsThr =0;pipsThr<=0;pipsThr+=100){
														for (double maxDD = 1000.0;maxDD<=1000.0;maxDD +=1.0){
															for (int minSL =25;minSL<=25;minSL+=10){
																for (int minDesv =0;minDesv<=0;minDesv+=10){
																	for (double aRisk= 0.2;aRisk<=0.2;aRisk +=0.01){
																		for (double aMaxOpenRisk= 800.0;aMaxOpenRisk<=800.0;aMaxOpenRisk +=5.0){
																			for (double per3=1000.0;per3<=1000.0;per3+=1.0){
																				for (double balanceInicial = 5000;balanceInicial<=5000;balanceInicial+=500){																				
																					if (true
																							//&& maxFactor>=aMaxFactorGlobal
																							//&& maxFactor>=0.0
																							){//imprimimos cuando se supere
																						aRiskSelected = aRisk;
																						aMaxFactorGlobal =1.0;
																						aTpf = tpf;
																						String header = "";
																						double rt =StudyTicksStrats.testPercent2Closes(
																								header,
																								data,maxMins, y1, y2,m1,m2,
																								h1,h2,min1,min2,
																								aTpf, slf, n, 
																								perMin,perMax,per3,																							
																								thr,arange,pipsThr,maxDD,
																								minSL,
																								minDesv,
																								true,
																								false,
																								aRisk,
																								aMaxOpenRisk,
																								5000,
																								10,
																								false
																						);
																						totalTests++;
																						if (rt>=0.0){
																							totalHoursWins++;
																							accYearsPf += rt;
																							positiveYears++;
																						}
																					}//if
																				}//balanceInicial
																			}//per3
																		}//aMaxOpenRisk																
																	}//aRisk
																}//minDesv
															}//minSL
														}//maxdd
													}//pipthr
												}//arange
											}//m1
											
										}//y1
										double avgPf = accYearsPf/(positiveYears);
										if (positiveYears>=9
												//|| positiveYears>=15
												|| (positiveYears>=14 && avgPf>=2.5)
												|| (positiveYears>=13 && avgPf>=3.0)
												|| (positiveYears>=12 && avgPf>=4.0)
												)
										System.out.println(
												n
												+" "+PrintUtils.Print2dec(tpf, false)
												+" "+PrintUtils.Print2dec(slf, false)
												+" "+PrintUtils.Print2dec(perMin, false)
												+" || "
												+" "+positiveYears
												+" "+PrintUtils.Print2dec(avgPf, false)
												);
									}//thr
								}//min1
							}//permin
							}//slf
						}//n
					}*/
			}
		
		}

	}

}
