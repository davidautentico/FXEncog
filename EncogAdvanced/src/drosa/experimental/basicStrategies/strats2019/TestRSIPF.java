package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestRSIPF {
	
	//RSI = 100 – 100/ (1 + RS)
	//RS =  Average Gain of n days UP  / Average Loss of n days DOWN
	/*
	 * change = change(close)
gain = change >= 0 ? change : 0.0
loss = change < 0 ? (-1) * change : 0.0
avgGain = rma(gain, 14)
avgLoss = rma(loss, 14)
rs = avgGain / avgLoss
rsi = 100 - (100 / (1 + rs))	
	 */
	public static double doTest(String header,
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
