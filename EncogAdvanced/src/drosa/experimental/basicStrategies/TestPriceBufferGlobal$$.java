package drosa.experimental.basicStrategies;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.data.DataUtils;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.experimental.CoreStrategies.TestPriceBuffer;
import drosa.experimental.basicStrategies.strats2019.StratPerformance;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPriceBufferGlobal$$ {
	
	public static double doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int dayWeek1,int dayWeek2,
			ArrayList<StrategyConfig> configs,
			int hf,
			int maxTrades,
			int idxTest,
			int sizeCandle,
			boolean isMa,
			double aStd,
			double balance,
			double risk,
			double comm,
			boolean debug,
			boolean printSummary,
			int returnMode,
			HashMap<Integer,Integer> dayTotalPips,
			StratPerformance sp
			){

		sp.reset();
		sp.setInitialBalance(balance);
		
		double balanceInicial = balance;
		double actualBalance = balance; //actual equitity
		double actualEquitity = balance;
		double maxBalance = balance;
		double actualDD = 0.0;
		double maxDD = 0.0;
		int actualDDPips = 0;
		int maxWinPips = 0;
		int maxLostPips = 0;
		int maxPips = 0;
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int lastDay = -1;
		int lastDayPips = 0;
		int dayPips = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		double winPips$$ = 0;
		double lostPips$$ = 0;
		int totalDays = 0;
		int totalL = 0;
		int totalLL = 0;
		int totalLLL = 0;
		int totalW = 0;
		int totalWL = 0;
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double ma0 = -1;
		double std0 = -1;
		dayTotalPips.clear();
		ArrayList<Integer> days = new ArrayList<Integer>();
		
		ArrayList<Integer> dayRes = new ArrayList<Integer>();
		ArrayList<Double> dds = new ArrayList<Double>();
		ArrayList<Integer> ddPips = new ArrayList<Integer>();
		ArrayList<Integer> ddWinPips = new ArrayList<Integer>();
		ArrayList<Integer> ddLostPips = new ArrayList<Integer>();
		ArrayList<Double> ddPfs = new ArrayList<Double>();
		double dayDD = 0.0;
		int dayDDPip = 0;
		for (int i=100;i<data.size()-1;i++){
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
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				
				if (lastDay>=0 && dayPips!=0){
					//System.out.println("pips: "+dayPips+" || "+(winPips-lostPips));
					
					if (lastDayPips<0){
						if (dayPips<0){
							totalLL++;
						}
						totalL++;
					}
					
					if (lastDayPips>0){
						if (dayPips<0){
							totalWL++;
						}
						totalW++;
					}
					
					dayRes.add(dayPips);
					
					totalDays++;
					
					int dayKey = cal.get(Calendar.MONTH)*31+cal.get(Calendar.DAY_OF_MONTH);
					
					if (!dayTotalPips.containsKey(dayKey)){
						dayTotalPips.put(dayKey, dayPips);
					}else{
						dayTotalPips.put(dayKey, dayTotalPips.get(dayKey)+dayPips);
					}
					
					lastDayPips = dayPips;
					
					double  dd = 100.0-actualBalance*100.0/maxBalance;
					dds.add(dd);
					dayDD = dd;
					
					int ddPip = maxPips-(winPips-lostPips);
					//int varWins = winPips-lastWinPips;
					//int varLosses = lostPips -lastLostPips;
					ddPips.add(ddPip);
					ddWinPips.add(winPips);
					ddLostPips.add(lostPips);
					dayDDPip = ddPip;
					//ddPfs.add(varWins*1.0/varLosses);
					
					//lastWin
				}
				ma0 = MathUtils.average(days, days.size()-14*288, days.size()-1);					
				std0 = Math.sqrt(MathUtils.variance(days, days.size()-14*288, days.size()-1));
				
				dayPips =0;
				lastDay = day;
			}
			
			StrategyConfig config = configs.get(h);
			
			//modulo de entrada
			if (positions.size()<maxTrades
					&& dayWeek1<=dayWeek && dayWeek<=dayWeek2
					&& (h>0 || min>=15)
					){
				if (config!=null && config.isEnabled()){
					int thr = config.getThr();
					int begin = i-config.getBarsBack();
					//begin = i-1;//debug
					int end = i-1;
					int index = TestPriceBuffer.getMinMaxBuff(maxMins,begin,end,thr);
					
					int HC = q1.getHigh5()-q1.getClose5();
					int CL = q1.getClose5()-q1.getLow5();
					if (index>=0
							//&& sizeCandle1<=sizeCandle*10
							){
						int maxMin = maxMins.get(index);
						//System.out.println("[INDEX>=0] "+DateUtils.datePrint(cal)+" "+thr+" "+(end-index)+" || "+data.get(index).toString()+" "+maxMin);
							
						
						double realRisk = risk;
						//if (dayDDPip<30000.0) realRisk =0.3;
						/*if (maxTrades*risk>=80){
							realRisk = 80.0/maxTrades;
						}*/
																
						double maxRisk$$ =realRisk*actualEquitity/100.0;
						double pipValue = maxRisk$$*1.0/config.getSl();
						int miniLots = (int) (pipValue/0.10);
						pipValue = miniLots*0.10;
						int sizeHL = q.getOpen5()-data.get(i-36).getLow5();
						int sizeLH = data.get(i-36).getHigh5()-q.getOpen5();
						if (pipValue<=0.10) pipValue = 0.10;//como minimo 0.01 lots
						if (maxMin>=thr
								//&& sizeHL==sizeCandle*10
								//&& HC>=sizeCandle*10
								//&& (!isMa || (isMa && ma0>-1 && q.getOpen5()<ma0-aStd*std0)) 
								//&& ma0>-1 && q.getOpen5()>ma0+aStd*std0
								
								){
							PositionCore pos = new PositionCore();
							pos.setEntry(q.getOpen5());
							pos.setTp(q.getOpen5()-10*config.getTp());
							pos.setSl(q.getOpen5()+10*config.getSl());
							pos.setEntryIndex(i);
							pos.setMaxIndex(i+config.getMaxBars());
							pos.setPositionType(PositionType.SHORT);
							pos.setIndexMinMax(end-index);
							pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
							totalRiskedPips += config.getSl();
							//pipValue
							pos.setPipValue(pipValue);
							pos.setMicroLots(miniLots);
					
							
							//System.out.println("[SHORT] "+maxRisk$$ +" "+pipValue+" "+miniLots+" "+10*config.getSl());
							positions.add(pos);
						}else if (maxMin<=-thr
								//&& sizeLH==sizeCandle*10
								//&& CL>=sizeCandle*10
								//&& (!isMa || (isMa && ma0>-1 && q.getOpen5()>ma0+aStd*std0)) 
								//&& ma0>-1 && q.getOpen5()<ma0-aStd*std0
								
								){
							//System.out.println("[OPEN LONG] "+q.toString()+" || "+actualEquitity+" "+pipValue);
							
							PositionCore pos = new PositionCore();
							pos.setEntry(q.getOpen5());
							pos.setTp(q.getOpen5()+10*config.getTp());
							pos.setSl(q.getOpen5()-10*config.getSl());
							pos.setEntryIndex(i);
							pos.setMaxIndex(i+config.getMaxBars());
							pos.setPositionType(PositionType.LONG);
							pos.setIndexMinMax(end-index);
							pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
							totalRiskedPips += config.getSl();
							//pipValue
							pos.setPipValue(pipValue);
							pos.setMicroLots(miniLots);
							
							//System.out.println("[LONG] "+q.toString());
							positions.add(pos);
						}
					}
					
				}
			}
						
			//evaluacion trades			
			int j = 0;		
			actualEquitity = actualBalance;
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				int closedMode = 0;
				if (pos.getPositionType()==PositionType.SHORT){
					if (i>=pos.getMaxIndex()|| (dayWeek==Calendar.FRIDAY && (h>=hf))
							){						
						isClosed = true;

						pips = q_1.getOpen5()-pos.getEntry();
					}else{
						if (q.getHigh5()>=pos.getSl()){
							isClosed = true;
							pips = pos.getEntry()-pos.getSl();
						}else if (q.getLow5()<=pos.getTp()){
							isClosed = true;
							pips = pos.getEntry()-pos.getTp();
						}
					}
					
					if (isClosed){
						pips = pos.getEntry()-q_1.getOpen5();
					}
				}else if (pos.getPositionType()==PositionType.LONG){
					if (i>=pos.getMaxIndex() || (dayWeek==Calendar.FRIDAY && (h>=hf))
						){						
						isClosed = true;

						pips = q_1.getOpen5()-pos.getEntry();
						closedMode = 1;
					}else{
						if (q.getLow5()<=pos.getSl()){
							isClosed = true;
							pips = -pos.getEntry()+pos.getSl();
							closedMode = 2;
						}else if (q.getHigh5()>=pos.getTp()){
							isClosed = true;
							pips = -pos.getEntry()+pos.getTp();
							closedMode = 3;
						}
					}
					if (isClosed){
					}
				}
				
				//actualizacion equitity
				actualEquitity = actualEquitity + (pips-comm*10)*0.1*pos.getMicroLots();
				
				if (isClosed){
					if (idxTest==-1 ||pos.getIndexMinMax()==idxTest){
						pips-=comm*10;
						
						if (pips>=0){
							wins++;
							winPips+=pips;
							winPips$$ += pips*0.1*pos.getMicroLots();
						}else{
							losses++;
							lostPips+=-pips;
							lostPips$$ += -pips*0.1*pos.getMicroLots();
							//System.out.println("[LOSS] "+-pips+" "+pos.getMicroLots()+" || "+pos.toString()+" || "+q_1.toString()+" || "+pos.getSl()+" || "+closedMode);
						}
						//System.out.println("pips "+" "+pips+" || "+winPips+" "+lostPips);
						dayPips += pips;
							
						actualBalance += pips*0.1*pos.getMicroLots();
						
						if (debug){
							System.out.println("[CLOSED] "+DateUtils.datePrint(cal)+" || "+PrintUtils.Print2dec(pips, false)+" "+PrintUtils.Print2dec(actualBalance, false)+" || "+pos.toString());
						}
						//if (actualBalance<=0) break;
						
						if (actualBalance>=maxBalance){
							maxBalance = actualBalance;
						}else{
							double dd = 100.0-actualBalance*100.0/maxBalance;
							if (dd>=maxDD){
								maxDD = dd;
							}
						}
						sp.addTrade((long)pos.getMicroLots(),pips,Math.abs(pos.getEntry()-pos.getTp()),Math.abs(pos.getEntry()-pos.getTp()),(int)(comm*10),cal);
						
						//para debug
					}
					positions.remove(j);
				}else{
					j++;
				}
				
				if (winPips-lostPips>=maxPips){
					maxWinPips = winPips;
					maxLostPips = lostPips;
					maxPips = winPips-lostPips;
				}
			}
		}//data
		
		totalLL = 0;
		totalLLL = 0;
		int totalLLLL = 0;
		for (int i=0;i<dayRes.size();i++){
			int pips = dayRes.get(i);
			
			if (i>=3){
				if (dayRes.get(i-1)<0 
						&& dayRes.get(i-2)<0
						&& dayRes.get(i-3)<0
						){
					totalLLL++;
					if (dayRes.get(i)<0){
						totalLLLL++;
					}
				}
				/*if (dayRes.get(i-1)<0 && dayRes.get(i-2)<0){
					totalLL++;
					if (dayRes.get(i)<0){
						totalLLL++;
					}
				}*/
			}
		}
		
		
		/*for (double af=0.0;af<=20.0;af+=0.5){
			int count = 0;
			double acc = 0;
			for (int i=0;i<dds.size();i++){
				double ddi = dds.get(i);			
				if (ddi>=af){
					//System.out.println(PrintUtils.Print2dec(acc*1.0/count, false));
					int j = i+10;
					if (j<=dds.size()-1){
						//System.out.println(PrintUtils.Print2dec(dds.get(j)-ddi, false));
						count++;
						acc+=dds.get(j)-ddi;
					}
				}
			}
			
			if (winPips-lostPips>=maxPips) maxPips = winPips-lostPips;
			
			System.out.println(PrintUtils.Print2dec(af, false)+";"+count+";"+PrintUtils.Print2dec(acc/count, false));
		}*/
		
		for (int af=0;af<=40000;af+=1000){
			int count = 0;
			double acc = 0;
			double accPf = 0;
			int accPfw = 0;
			int accPfl = 0;
			for (int i=0;i<ddPips.size();i++){
				int ddi = ddPips.get(i);			
				int wp = ddWinPips.get(i);
				int lp = ddLostPips.get(i);
				if (ddi>=af){
					//System.out.println(PrintUtils.Print2dec(acc*1.0/count, false));
					int j = i+30;
					if (j<=ddPips.size()-1){
						//System.out.println(PrintUtils.Print2dec(dds.get(j)-ddi, false));
						count++;
						acc+=ddPips.get(j)-ddi;
						accPf+=(ddWinPips.get(j)-wp)-(ddLostPips.get(j)-lp);
						accPfw+=(ddWinPips.get(j)-wp);
						accPfl+=(ddLostPips.get(j)-lp);
					}
				}
			}
								
			/*System.out.println(PrintUtils.Print2dec(af, false)
					+";"+count
					//+";"+PrintUtils.Print2dec(acc/count, false)
					//+";"+PrintUtils.Print2dec(accPf/count, false)
					+";"+PrintUtils.Print2dec(accPfw*1.0/accPfl, false)
					);*/
		}
				
		double perLL = totalLL*100.0/totalL;
		double perLLL = totalLLL*100.0/totalLL;
		double perLLLL = totalLLLL*100.0/totalLLL;
		double perWL = totalWL*100.0/totalW;
		/*System.out.println(totalL
				+" "+PrintUtils.Print2dec(perLL, false)
				+" || "+totalW+" "+PrintUtils.Print2dec(perWL, false)
		);*/
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double pf$$ = winPips$$*1.0/lostPips$$;
		double avg = (winPips-lostPips)*0.1/trades;
		
		double perWin = actualBalance*100.0/balance-100.0;
		double perMaxWin = maxBalance*100.0/balance-100.0;
		double actualBalance30 = actualBalance/(maxDD/30.0); //balance con max 30%
		double yield  = (winPips-lostPips)*0.1*100/totalRiskedPips;		
		
		int totalAños = y2-y1+1;		
		
		double tae = 100.0*(Math.pow(actualBalance/(balanceInicial), 1.0/totalAños)-1);
		double taeFactor = tae/maxDD;

		sp.setMaxDD(maxDD);
		double var95 = sp.getMonthDataDDRR(sp.getInitialBalance(),risk,2);
		if (printSummary){
			//if (pf<0.6)
			System.out.println(
					header
					+" || "
					+" "+trades
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(pf$$, false)
					//+" "+winPips+" "+lostPips
					//+" "+PrintUtils.Print2dec(winPips$$, false)
					//+" "+PrintUtils.Print2dec(lostPips$$, false)
					+" || "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(yield, false)
					+" || "
					+" "+PrintUtils.Print2dec2(actualBalance, true)
					+" "+PrintUtils.Print2dec2(maxBalance, true)
					+" "+PrintUtils.Print2dec(perMaxWin, false)
					+" || MaxDD="+PrintUtils.Print2dec(maxDD, false)
					+" || VAR95= "+PrintUtils.Print2dec(var95, false)
					+" || Factor="+PrintUtils.Print2dec(perMaxWin/maxDD, false)
					+" || "+PrintUtils.Print2dec(taeFactor, false)
					
					);
		}
		
		if (maxDD>=100.0) return 0.0;
		
		//return actualBalance; 
		return pf;
	}
	
	public static double doTestF(
			String header,
			ArrayList<QuoteShort> dataBid,
			ArrayList<QuoteShort> dataAsk,
			ArrayList<Integer> maxMins,
			HashMap<Integer,ArrayList<Double>> spreads,
			int y1,int y2,
			int m1,int m2,
			int dayWeek1,int dayWeek2,
			ArrayList<StrategyConfig> configs,
			int hf,
			int maxTrades,
			int idxTest,
			int sizeCandle,
			boolean isMa,
			double aStd,
			double balance,
			int hmax,
			int maxLostPositionPips,
			int maxDiff,
			boolean debug,
			boolean printSummary,
			int returnMode,
			HashMap<Integer,Integer> dayTotalPips,
			boolean isModeNormal,
			boolean isReverse,
			StratPerformance sp
			){
		
		
		sp.reset();
		sp.setInitialBalance(balance);
		
		double balanceInicial = balance;
		double actualBalance = balance; //actual equitity
		double actualEquitity = balance;
		double maxBalance = balance;
		double actualDD = 0.0;
		double maxDD = 0.0;
		int actualDDPips = 0;
		int maxWinPips = 0;
		int maxLostPips = 0;
		int maxPips = 0;
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		
		int lastDay = -1;
		int lastDayPips = 0;
		int dayPips = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		double winPips$$ = 0;
		double lostPips$$ = 0;
		int totalDays = 0;
		int totalL = 0;
		int totalLL = 0;
		int totalLLL = 0;
		int totalW = 0;
		int totalWL = 0;
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		calFrom.set(y1,m1, 1);
		calTo.set(y2,m2, 1);
		int maxTo = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
		calTo.set(y2,m2,maxTo);
		
		QuoteShort qm = new QuoteShort();
		double ma0 = -1;
		double std0 = -1;
		dayTotalPips.clear();
		ArrayList<Integer> days = new ArrayList<Integer>();
		
		ArrayList<Integer> dayRes = new ArrayList<Integer>();
		ArrayList<Double> dds = new ArrayList<Double>();
		ArrayList<Integer> ddPips = new ArrayList<Integer>();
		ArrayList<Integer> ddWinPips = new ArrayList<Integer>();
		ArrayList<Integer> ddLostPips = new ArrayList<Integer>();
		ArrayList<Double> ddPfs = new ArrayList<Double>();
		double dayDD = 0.0;
		int dayDDPip = 0;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int high = -1;
		int low = -1;
		int dayRange = 700;
		
		int minSizeData = dataBid.size();
		if (dataAsk.size()<minSizeData) minSizeData = dataAsk.size();
		
		for (int i=100;i<minSizeData-2;i++){
			QuoteShort qb1	= dataBid.get(i-1);
			QuoteShort qb 	= dataBid.get(i);
			QuoteShort qb_1 = dataBid.get(i+1);
			QuoteShort qa1 	= dataAsk.get(i-1);
			QuoteShort qa 	= dataAsk.get(i);
			QuoteShort qa_1 = dataAsk.get(i+1);
			QuoteShort.getCalendar(cal, qb);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			long timeInMillis = cal.getTimeInMillis();
			
			//if (m!=m1) continue;
			
			if (timeInMillis<calFrom.getTimeInMillis() || timeInMillis>calTo.getTimeInMillis()) continue;
			
			days.add(qb.getOpen5());
			
			if (day!=lastDay){
				if (lastDay>=0) {
					int actualRange = high-low;
					ranges.add(actualRange);
					dayRange = (int) MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);
				}
				
				if (lastDay>=0 && dayPips!=0){
					//System.out.println("pips: "+dayPips+" || "+(winPips-lostPips));									
					
					if (lastDayPips<0){
						if (dayPips<0){
							totalLL++;
						}
						totalL++;
					}
					
					if (lastDayPips>0){
						if (dayPips<0){
							totalWL++;
						}
						totalW++;
					}
					
					dayRes.add(dayPips);
					
					totalDays++;
					
					int dayKey = cal.get(Calendar.MONTH)*31+cal.get(Calendar.DAY_OF_MONTH);
					
					if (!dayTotalPips.containsKey(dayKey)){
						dayTotalPips.put(dayKey, dayPips);
					}else{
						dayTotalPips.put(dayKey, dayTotalPips.get(dayKey)+dayPips);
					}
					
					lastDayPips = dayPips;
					
					double  dd = 100.0-actualBalance*100.0/maxBalance;
					dds.add(dd);
					dayDD = dd;
					
					int ddPip = maxPips-(winPips-lostPips);
					//int varWins = winPips-lastWinPips;
					//int varLosses = lostPips -lastLostPips;
					ddPips.add(ddPip);
					ddWinPips.add(winPips);
					ddLostPips.add(lostPips);
					dayDDPip = ddPip;
					//ddPfs.add(varWins*1.0/varLosses);
					
					//lastWin
				}
				ma0 = MathUtils.average(days, days.size()-14*288, days.size()-1);					
				std0 = Math.sqrt(MathUtils.variance(days, days.size()-14*288, days.size()-1));
				
				high = -1;
				low = -1;
				dayPips =0;
				lastDay = day;
				
				//actualizamos day equitity
				sp.updateDailyEquitity(actualEquitity);
			}
			
			StrategyConfig config = configs.get(h);
			/*if (h==23) {
				if (config.isEnabled()) {
					System.out.println("enabled");
				}else {
					System.out.println("disabled");
				}
			}*/
			//modulo de entrada
			if (positions.size()<maxTrades
					&& dayWeek1<=dayWeek && dayWeek<=dayWeek2
					&& (h>0 || min>=15)
					){
				if (config!=null && config.isEnabled()){
					int thr = config.getThr();
					int begin = i-config.getBarsBack();
					//begin = i-1;//debug
					int end = i-1;
					int index = TestPriceBuffer.getMinMaxBuff(maxMins,begin,end,thr);
					double risk = config.getRisk();
					
					int HC = qb1.getHigh5()-qb1.getClose5();
					int CL = qb1.getClose5()-qb1.getLow5();
					if (index>=0
							//&& sizeCandle1<=sizeCandle*10
							&& risk >=0.1
							){
						int maxMin = maxMins.get(index);
						//System.out.println("[INDEX>=0] "+DateUtils.datePrint(cal)+" "+thr+" "+(end-index)+" || "+dataAsk.get(index).toString()+" "+maxMin);
							
						
						double realRisk = risk;
						//System.out.println(risk);
						//if (dayDDPip<30000.0) realRisk =0.3;
						/*if (maxTrades*risk>=80){
							realRisk = 80.0/maxTrades;
						}*/
						int tpPips =0;
						int slPips = 0;
						int tp1 = 0;
						int sl1 = 0;
						if (isModeNormal) {
							tpPips = (int) (config.getTp()*10);
							slPips = (int) (config.getSl()*10);
							tp1 = (int) (tpPips*0.1);
							sl1 = (int) (slPips*0.1);
						}else {
							tpPips = (int) (config.getTpf()*dayRange);
							slPips = (int) (config.getSlf()*dayRange);
							tp1 = (int) (tpPips*0.1);
							sl1 = (int) (slPips*0.1);
						}
																																
						double maxRisk$$ =realRisk*actualEquitity/100.0;
						//maxRisk$$ =realRisk*actualBalance/100.0;
						double pipValue = maxRisk$$*1.0/config.getSl();//sl en formato pip
						int miniLots = (int) (pipValue/0.10);//1 mini lot es $0.10
						pipValue = miniLots*0.10;
						int sizeHL = qb.getOpen5()-dataBid.get(i-36).getLow5();
						int sizeLH = dataBid.get(i-36).getHigh5()-qb.getOpen5();
						if (pipValue<=0.10) pipValue = 0.10;//como minimo 0.01 lots
						if (miniLots<1) miniLots = 1;
						
						//System.out.println(miniLots);
												
						if (maxMin>=thr		
								//&& high-qb.getOpen5()>=maxDiff
								){
							
							int transactionCosts = TradingUtils.getTransactionCosts(spreads,y, h,3);
							//transactionCosts = 0;
							
							PositionCore pos = new PositionCore();
							pos.setEntry(qb.getOpen5());
							pos.setEntryIndex(i);
							pos.setMaxIndex(i+config.getMaxBars());
							pos.setPositionStatus(PositionStatus.OPEN);
							
							if (isReverse) {
								pos.setTp(qb.getOpen5()-tpPips);
								pos.setSl(qb.getOpen5()+slPips);
								pos.setPositionType(PositionType.SHORT);
							}else {
								pos.setTp(qb.getOpen5()+slPips);
								pos.setSl(qb.getOpen5()-tpPips);
								pos.setPositionType(PositionType.LONG);
							}
																				
							pos.setIndexMinMax(end-index);
							pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
							totalRiskedPips += sl1;
							//pipValue
							pos.setPipValue(pipValue);
							pos.setMicroLots(miniLots);
							pos.setTransactionCosts(transactionCosts);							
							if (debug)
								System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" "+PrintUtils.Print2dec(actualEquitity, false)
								+" "+PrintUtils.Print2dec(maxRisk$$,false) +" "+miniLots
								+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl()
								+" || "+tpPips+" "+slPips			
								+" ||| "+qb1.toString()+" | "+qb.toString()
								);							
							long totalMicroLots = TradingUtils.getOpenSize2(positions);
							double leverage = (totalMicroLots+miniLots)*1000.0/actualEquitity;
							
							if (leverage<30.0) positions.add(pos);
						}else if (maxMin<=-thr									
								//&& qa.getOpen5()-low>=maxDiff
								){
							int transactionCosts = TradingUtils.getTransactionCosts(spreads,y, h,3);
							//transactionCosts = 0;
							
							//System.out.println("[OPEN LONG] "+q.toString()+" || "+actualEquitity+" "+pipValue);
							PositionCore pos = new PositionCore();
							pos.setEntry(qa.getOpen5());							
							pos.setEntryIndex(i);
							pos.setMaxIndex(i+config.getMaxBars());
							pos.setPositionStatus(PositionStatus.OPEN);							
							pos.setIndexMinMax(end-index);
							pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
							totalRiskedPips += sl1;
							//pipValue
							pos.setPipValue(pipValue);
							pos.setMicroLots(miniLots);
							pos.setTransactionCosts(transactionCosts);
							
							if (isReverse) {
								pos.setTp(qa.getOpen5()+tpPips);
								pos.setSl(qa.getOpen5()-slPips);
								pos.setPositionType(PositionType.LONG);
							}else {
								pos.setTp(qb.getOpen5()-slPips);
								pos.setSl(qb.getOpen5()+tpPips);
								pos.setPositionType(PositionType.SHORT);
							}
							
							if (debug)
								System.out.println("[LONG] "+DateUtils.datePrint(cal)+" "+PrintUtils.Print2dec(actualEquitity, false)+" "+PrintUtils.Print2dec(maxRisk$$,false)+" "+miniLots
										+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl()
										+" || "+tpPips+" "+slPips
										);
							long totalMicroLots = TradingUtils.getOpenSize2(positions);
							double leverage = (totalMicroLots+miniLots)*1000.0/actualEquitity;
							//System.out.println((totalMicroLots+miniLots)+" || "+);
							if (leverage<30.0) positions.add(pos);
						}
					}
					
				}
			}
						
			//evaluacion trades			
			int j = 0;		
			actualEquitity = actualBalance;
			while (j<positions.size()){				
				PositionCore pos = positions.get(j);				
				boolean isClosed = false;
				int pips = 0;
				int closedMode = 0;
				if (pos.getPositionType()==PositionType.SHORT){
					if (i>=pos.getMaxIndex()
							//|| (dayWeek==Calendar.FRIDAY && (h>=hf))
							){		
						closedMode =1;
						isClosed = true;
						pips = pos.getEntry()-qa_1.getOpen5();
					}
					if (!isClosed) {
						if (qa.getHigh5()>=pos.getSl()){
						//if (q_1.getOpen5()>=pos.getSl()){
							isClosed = true;
							pips = pos.getEntry()-pos.getSl();
							closedMode =2;
						}else if (qa.getLow5()<=pos.getTp()){
						//}else if (q_1.getOpen5()<=pos.getTp()){
							isClosed = true;
							pips = pos.getEntry()-pos.getTp();
							closedMode =3;
						}else {
							//aqui evaluamos si está en pérdidas y que hora es..
							pips = pos.getEntry()-qa_1.getOpen5();
							if (pips<-maxLostPositionPips && h>=hmax) {
								isClosed = true;
							}
						}
					}					
					if (isClosed){
						pips = pos.getEntry()-qa_1.getOpen5(); 
						if (closedMode==2) {
							pips = pos.getEntry()-pos.getSl();
						}else if(closedMode==3) {
							pips = pos.getEntry()-pos.getTp();
						}
					}
				}else if (pos.getPositionType()==PositionType.LONG){
					if (i>=pos.getMaxIndex() 
							//|| (dayWeek==Calendar.FRIDAY && (h>=hf))
						){	
						closedMode = 1;
						isClosed = true;
						pips = qb_1.getOpen5()-pos.getEntry();
					}
					
					if (!isClosed){
						if (qb.getLow5()<=pos.getSl()){
						//if (q_1.getOpen5()<=pos.getSl()){
							isClosed = true;
							pips = pos.getSl()-pos.getEntry();
							closedMode =2;
						}else if (qb.getHigh5()>=pos.getTp()){
						//}else if (q_1.getOpen5()>=pos.getTp()){
							isClosed = true;
							pips = pos.getTp()-pos.getEntry();
							closedMode = 3;
						}else {
							//aqui evaluamos si está en pérdidas y que hora es..
							pips = qb_1.getOpen5()-pos.getEntry();
							if (pips<-maxLostPositionPips && h>=hmax) {
								isClosed = true;
							}
						}
					}
					if (isClosed){
						pips = qb_1.getOpen5()-pos.getEntry();
						if (closedMode==2) {
							pips = pos.getSl()-pos.getEntry();
						}else if(closedMode==3) {
							pips = pos.getTp()-pos.getEntry();
						}
					}
				}				
				//actualizacion equitity
				actualEquitity = actualEquitity + (pips-pos.getTransactionCosts())*0.1*pos.getMicroLots()*0.1;
				
				if (isClosed){
					if (idxTest==-1 ||pos.getIndexMinMax()==idxTest){

						sp.addTrade((long)pos.getMicroLots(),pips,Math.abs(pos.getEntry()-pos.getTp()),Math.abs(pos.getEntry()-pos.getTp()),pos.getTransactionCosts(),cal);
						pips-=pos.getTransactionCosts();
						
						if (pips>=0){
							wins++;
							winPips+=pips;
							winPips$$ += pips*0.1*pos.getMicroLots()*0.1;//1 microLot = 0.1$/pip
						}else{
							losses++;
							lostPips+=-pips;
							lostPips$$ += -pips*0.1*pos.getMicroLots()*0.1;
							//System.out.println("[LOSS 2] "+-pips+" "+pos.getMicroLots()+" || "+pos.toString()+" || "+q_1.toString()+" || "+pos.getSl()+" || "+closedMode);
						}
						//System.out.println("pips "+" "+pips+" || "+winPips+" "+lostPips);
						dayPips += pips;
							
						actualBalance += pips*0.1*pos.getMicroLots()*0.1;
						
						
						//if (actualBalance<=0) break;
						
						if (actualBalance>=maxBalance){
							maxBalance = actualBalance;
						}else{
							double dd = 100.0-actualBalance*100.0/maxBalance;
							if (dd>=maxDD){
								maxDD = dd;
							}
						}
						
						
						if (debug){
							String str = "WIN";
							if (pips<0) str="LOSS";
							
							System.out.println("[CLOSED " +str+"] "+DateUtils.datePrint(cal)
							+" || "+closedMode+" || "+pips
							//+" || "+q_1.toString()
							//+" || open="+q_1.getOpen()+" tp="+pos.getTp()
							+" || "+pos.getMicroLots()+" "+PrintUtils.Print2dec(actualBalance, false)
							+" || "+pos.toString()
							+" || "+winPips+" "+lostPips
							+" || "+PrintUtils.Print2dec(winPips$$, false)+" "+PrintUtils.Print2dec(lostPips$$, false)
							+" || "+PrintUtils.Print2dec(maxDD, false)
							);
						}
						
						//para debug
					}
					positions.remove(j);
				}else{
					j++;
				}
				
				if (winPips-lostPips>=maxPips){
					maxWinPips = winPips;
					maxLostPips = lostPips;
					maxPips = winPips-lostPips;
				}
			}
			
			if (high==-1 || qb.getHigh5()>=high) high = qb.getHigh5();
			if (low==-1 || qb.getLow5()<=low) low = qb.getLow5();
		}//data
		
		totalLL = 0;
		totalLLL = 0;
		int totalLLLL = 0;
		for (int i=0;i<dayRes.size();i++){
			int pips = dayRes.get(i);
			
			if (i>=3){
				if (dayRes.get(i-1)<0 
						&& dayRes.get(i-2)<0
						&& dayRes.get(i-3)<0
						){
					totalLLL++;
					if (dayRes.get(i)<0){
						totalLLLL++;
					}
				}
				/*if (dayRes.get(i-1)<0 && dayRes.get(i-2)<0){
					totalLL++;
					if (dayRes.get(i)<0){
						totalLLL++;
					}
				}*/
			}
		}
		
		
		/*for (double af=0.0;af<=20.0;af+=0.5){
			int count = 0;
			double acc = 0;
			for (int i=0;i<dds.size();i++){
				double ddi = dds.get(i);			
				if (ddi>=af){
					//System.out.println(PrintUtils.Print2dec(acc*1.0/count, false));
					int j = i+10;
					if (j<=dds.size()-1){
						//System.out.println(PrintUtils.Print2dec(dds.get(j)-ddi, false));
						count++;
						acc+=dds.get(j)-ddi;
					}
				}
			}
			
			if (winPips-lostPips>=maxPips) maxPips = winPips-lostPips;
			
			System.out.println(PrintUtils.Print2dec(af, false)+";"+count+";"+PrintUtils.Print2dec(acc/count, false));
		}*/
		
		for (int af=0;af<=40000;af+=1000){
			int count = 0;
			double acc = 0;
			double accPf = 0;
			int accPfw = 0;
			int accPfl = 0;
			for (int i=0;i<ddPips.size();i++){
				int ddi = ddPips.get(i);			
				int wp = ddWinPips.get(i);
				int lp = ddLostPips.get(i);
				if (ddi>=af){
					//System.out.println(PrintUtils.Print2dec(acc*1.0/count, false));
					int j = i+30;
					if (j<=ddPips.size()-1){
						//System.out.println(PrintUtils.Print2dec(dds.get(j)-ddi, false));
						count++;
						acc+=ddPips.get(j)-ddi;
						accPf+=(ddWinPips.get(j)-wp)-(ddLostPips.get(j)-lp);
						accPfw+=(ddWinPips.get(j)-wp);
						accPfl+=(ddLostPips.get(j)-lp);
					}
				}
			}
								
			/*System.out.println(PrintUtils.Print2dec(af, false)
					+";"+count
					//+";"+PrintUtils.Print2dec(acc/count, false)
					//+";"+PrintUtils.Print2dec(accPf/count, false)
					+";"+PrintUtils.Print2dec(accPfw*1.0/accPfl, false)
					);*/
		}
				
		double perLL = totalLL*100.0/totalL;
		double perLLL = totalLLL*100.0/totalLL;
		double perLLLL = totalLLLL*100.0/totalLLL;
		double perWL = totalWL*100.0/totalW;
		/*System.out.println(totalL
				+" "+PrintUtils.Print2dec(perLL, false)
				+" || "+totalW+" "+PrintUtils.Print2dec(perWL, false)
		);*/
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double pf$$ = winPips$$*1.0/lostPips$$;
		double avg = (winPips-lostPips)*0.1/trades;
		
		double perWin = actualBalance*100.0/balance-100.0;
		double perMaxWin = maxBalance*100.0/balance-100.0;
		double actualBalance30 = actualBalance/(maxDD/30.0); //balance con max 30%
		double yield  = (winPips-lostPips)*0.1*100/totalRiskedPips;		
		
		int totalAños = y2-y1+1;		
		
		double tae = 100.0*(Math.pow(actualBalance/(balanceInicial), 1.0/totalAños)-1);
		double taeFactor = tae/maxDD;
		
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;

		sp.setMaxDD(maxDD);
		//double var95 = sp.getMonthDataDDRR(sp.getInitialBalance(),risk,2);
		if (printSummary){
			if (maxDD<3000.0 
					//&& perMaxWin>=500.0 
				)
			System.out.println(
					header
					+" || "
					+" "+trades
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+PrintUtils.Print2dec(pf$$, false)
					+" "+winPips+" "+lostPips
					+" "+PrintUtils.Print2dec(winPips$$, false)
					+" "+PrintUtils.Print2dec(lostPips$$, false)
					+"|| "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(avgWin, false)
					+" "+PrintUtils.Print2dec(avgLoss, false)
					+" "+PrintUtils.Print2dec(yield, false)
					+" || "
					+" "+PrintUtils.Print2dec2(actualBalance, true)
					+" "+PrintUtils.Print2dec2(maxBalance, true)
					+" "+PrintUtils.Print2dec(perMaxWin, false)
					+" || %Profit="+PrintUtils.Print2dec(actualBalance*100.0/balance-100.0, false)
					+" || MaxDD="+PrintUtils.Print2dec(maxDD, false)
					//+" || VAR95= "+PrintUtils.Print2dec(var95, false)
					+" || Factor="+PrintUtils.Print2dec(perMaxWin/maxDD, false)
					+" || "+PrintUtils.Print2dec(taeFactor, false)
					+" || "+sp.maxDDStats(20)
					+" || "+sp.maxDDStats(40)
					
					);
		}
		
		if (maxDD>=100.0) return 0.0;
		
		//return actualBalance; 
		return pf$$;
	}

	public static void main(String[] args) throws Exception {
		
		String path0 ="C:\\fxdata\\";
		String currency = "eurusd";
		String pathBid = path0+currency+"_5 Mins_Bid_2004.01.01_2020.01.13.csv";
		String pathAsk = path0+currency+"_5 Mins_Ask_2004.01.01_2020.01.13.csv";	
		String pathSpread = path0+currency+"_spreads_2009_2019.csv";
		
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
			
			
			//LOS AJUSTES AQUI
			for (int noisePips = 0;noisePips<=0;noisePips++){
				//dataNoise = TradingUtils.addNoise(data,0,23,noisePips);
				
				ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataBid);
				HashMap<Integer,ArrayList<Double>> spreads = new HashMap<Integer,ArrayList<Double>>();
				//DAO.readSpreads(pathSpread,2009,2019,spreads);
											
				ArrayList<StrategyConfig> configs = new ArrayList<StrategyConfig>();
				for (int c=0;c<=23;c++) configs.add(null);
				
				//DMO SETTINGS
				//2012-2016 TP SL NORMAL
				
				//EURUSD
				/*StrategyConfig config = new StrategyConfig();config.setParams(0, 182,100,90,120,12, true);configs.set(0, config);//12
				StrategyConfig config1 = new StrategyConfig();config1.setParams(1,200,120,100,125,12, true);configs.set(1, config1);//12
				StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 250,15,50,180,6, true);configs.set(2, config2);//6
				StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 191,20,40,195,1, true);configs.set(3, config3);//450
				StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 825,10,50,90,1, true);configs.set(4, config4);
				StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 450,10,40,86,1, true);configs.set(5, config5);
				StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 200,20,60,36,1, true);configs.set(6, config6);
				StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 202,25,74,44, 1, true);configs.set(7, config7);				
				StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 544,15,52,18,1, true);configs.set(8, config8);				
			    StrategyConfig config9 = new StrategyConfig();config9.setParams(9, 524,32,38,20,1, true);configs.set(9, config9);
				StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 170,7,70,43,1, true);configs.set(23, config23);*/
				
				StrategyConfig config = new StrategyConfig();config.setParams(0,111,169,90,120,12,7, true);configs.set(0, config);//12
				StrategyConfig config1 = new StrategyConfig();config1.setParams(1,168,103,100,125,12,7, true);configs.set(1, config1);//12
				StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 220,20,50,216,6,4, true);configs.set(2, config2);//6
				StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 125,20,40,190,1,2, true);configs.set(3, config3);
				StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 825,10,50,90,1,1, true);configs.set(4, config4);
				StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 450,15,25,86,1,5, true);configs.set(5, config5);
				StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 249,10,65,37,1,6, true);configs.set(6, config6);
				StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 216,25,15,42,1,1, true);configs.set(7, config7);				
				StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 545,20,15,16,1,1, true);configs.set(8, config8);				
			    StrategyConfig config9 = new StrategyConfig();config9.setParams(9, 434,25,35,17,1,1, true);configs.set(9, config9);
				StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 142,10,80,156,1,5, true);configs.set(23, config23);
				
				/*StrategyConfig config = new StrategyConfig();config.setParams(0,111,169,90,120,12,7, true);configs.set(0, config);//12
				StrategyConfig config1 = new StrategyConfig();config1.setParams(1,168,103,100,125,12,4, true);configs.set(1, config1);//12
				StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 216,15,50,180,6,4, true);configs.set(2, config2);//6
				StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 125,20,40,190,1,2, true);configs.set(3, config3);//450
				StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 825,10,50,90,1,1, true);configs.set(4, config4);
				StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 450,15,25,86,1,7, true);configs.set(5, config5);
				StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 249,10,65,37,1,7, true);configs.set(6, config6);
				StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 216,25,15,42,1,2, true);configs.set(7, config7);				
				StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 545,20,15,16,1,1, true);configs.set(8, config8);				
			    StrategyConfig config9 = new StrategyConfig();config9.setParams(9, 434,25,35,17,1,1, true);configs.set(9, config9);
				StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 142,10,80,156,1,5, true);configs.set(23, config23);*/
				
				StrategyConfig config10 = new StrategyConfig();config10.setParams(10, 2000,70,20,384,1,1, false);configs.set(10, config10);//12
				StrategyConfig config11 = new StrategyConfig();config11.setParams(11, 225,55,8,204,1,false);configs.set(11, config11);//12
				StrategyConfig config12 = new StrategyConfig();config12.setParams(12, 250,15,50,180,6,1,false);configs.set(12, config12);//6
				StrategyConfig config13 = new StrategyConfig();config13.setParams(13, 191,20,40,195,1,1,false);configs.set(13, config13);//450
				StrategyConfig config14 = new StrategyConfig();config14.setParams(14, 825,10,50,90,1,1,false);configs.set(14, config14);
				StrategyConfig config15 = new StrategyConfig();config15.setParams(15, 450,10,40,86,1,1,false);configs.set(15, config15);
				StrategyConfig config16 = new StrategyConfig();config16.setParams(16, 200,20,60,36,1,1,false);configs.set(16, config16);
				StrategyConfig config17 = new StrategyConfig();config17.setParams(17, 202,25,74,44, 1,1,false);configs.set(17, config17);				
				StrategyConfig config18 = new StrategyConfig();config18.setParams(18, 544,15,52,18,1,1,false);configs.set(18, config18);				
				StrategyConfig config19 = new StrategyConfig();config19.setParams(19, 524,32,38,20,1,1,false);configs.set(19, config19);
				StrategyConfig config20 = new StrategyConfig();config20.setParams(20, 202,25,74,44, 1,1,false);configs.set(20, config20);				
				StrategyConfig config21 = new StrategyConfig();config21.setParams(21, 544,15,52,18,1,1,false);configs.set(21, config21);				
				StrategyConfig config22 = new StrategyConfig();config22.setParams(22, 524,20,20,20,1,1,false);configs.set(22, config22);

				HashMap<Integer,Integer> dayTotalPips = new HashMap<Integer,Integer>();
				//guardamos defaults
				ArrayList<StrategyConfig> defaultConfigs = new ArrayList<StrategyConfig>();
				for (int c=0;c<=23;c++) defaultConfigs.add(null);
				for (int h=0;h<=23;h++){
					if (configs.get(h)!=null){
						defaultConfigs.set(h,new StrategyConfig(configs.get(h)));
					}
				}
				
				for (int j=0;j<=23;j++){
					if (defaultConfigs.get(j)!=null)
					defaultConfigs.get(j).setBarsBack(1);
				}
				
				int maximunTrades = 32;
				double maximunRisk = 1.39;
				
				if (pathBid.contains("1 Min")){//140 TRADES 0.46
					System.out.println("1 min mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5);
						}
					}
					maximunTrades = 84;
					maximunRisk = 0.35;
				}
				
				if (pathBid.contains("5 Secs")){
					System.out.println("5 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*12);
						}
					}
				}
				
				if (pathBid.contains("10 Secs")){
					System.out.println("10 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*6);
						}
					}
				}
				
				if (pathBid.contains("15 Secs")){
					System.out.println("15 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*4);
						}
					}
				}
				
				if (pathBid.contains("20 Secs")){
					System.out.println("20 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*3);
						}
					}
				}
				
				if (pathBid.contains("30 Secs")){
					System.out.println("30 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*2);
						}
					}
				}
				
				if (pathBid.contains("1 Sec")){
					System.out.println("1 sec mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*60);
						}
					}
				}
				
					
					for (int h=3;h<=3;h++){
						System.out.println("**testeando H="+h);
						for (int h0=0;h0<=23;h0++){						
							if (configs.get(h0)!=null){
								configs.get(h0).copy(defaultConfigs.get(h0));
								if (h0!=h) configs.get(h0).setEnabled(false);
							}
						}
						//if (h>=0 && configs.get(h)!=null)
							//configs.get(h).setEnabled(false);
						for (int tp=19;tp<=19;tp+=1){
							for (int sl=36;sl<=36;sl+=1){
						//for (double tpf=0.1;tpf<=0.1;tpf+=0.10){
							for (double risk=0.3;risk<=0.3;risk+=0.10){
								for (int maxBars=168;maxBars<=168;maxBars+=12){
									for (int barsBack=1;barsBack<=1;barsBack++){
										for (int thr=180;thr<=180;thr+=12){		
											for (int maxH=0; maxH<=0;maxH+=1){
												for (int lostPips=8000; lostPips<=8000;lostPips+=100){
													if (configs.get(h)!=null){
														configs.get(h).setEnabled(true);
														
														configs.get(h).setThr(thr);	
														////configs.get(h).setBarsBack(barsBack);
														configs.get(h).setMaxBars(maxBars);
														//configs.get(h).setTpf(tpf);
														//configs.get(h).setSlf(slf);
														//configs.get(h).setTp(tp);
														configs.get(h).setSl(sl);
														//configs.get(h).setRisk(risk);
													}
												
													StratPerformance sp = new StratPerformance();
													for (int maxTrades=25;maxTrades<=25;maxTrades+=5){//17 7 2.25
														//para testear un riesgo major
														double maxRisk =0.1;
														for (double grisk =0.1;grisk<=maxRisk;grisk+=0.10){
															for (int maxDiff=800;maxDiff<=800;maxDiff+=50){
																for (double comm=0;comm<=0;comm+=0.1){																	
																	String header = maxTrades+" "+PrintUtils.Print2dec(risk,false)+" || "+configs.get(h).toString();																	
																	int totalPositives = 0;
																	int total2016 = 0;
																	double accProfit = 0;
																	double balance = 4000;
																	double accYear = 0;
																	int totalY = 0;
																	ArrayList<Double> maxDDs = new ArrayList<Double>();
																															
																	for (int dayWeek1=Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
																		int dayWeek2 = dayWeek1+4;
																		for (int sc=0;sc<=0;sc++){
																			for (int hf=24;hf<=24;hf++){
																				for (double aStd=0.0;aStd<=0.0;aStd+=1.0){
																					for (int y1=2009;y1<=2020;y1+=1){
																						int y2 = y1+0;
																						for (int m1=0;m1<=0;m1+=1){
																							int m2 = m1+11;
																							String header1 = y1+" "+y2+" "+m1+" "+m2+" "+maxTrades
																									+" "+PrintUtils.Print2dec(risk, false)
																									+" | "+thr
																									+" "+PrintUtils.Print2dec(tp, false)
																									+' '+PrintUtils.Print2dec(sl, false)
																									+' '+maxBars+' '+barsBack+" "+maxH+" "+lostPips
																									;
																							sp.reset();
																							sp.setActualBalance( balance);	
																							double pf = TestPriceBufferGlobal$$.doTestF(header1,dataBid,dataAsk,maxMins,spreads,
																									y1,y2,m1,m2,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																									true,aStd,balance,
																									//risk,
																									maxH,lostPips,
																									maxDiff,
																									false,
																									false,0,dayTotalPips,																									
																									true,true,
																									sp);
																							maxDDs.add(sp.getMaxDD());
																							if (pf>=1.00) totalPositives++;
																							if (y1>=2016 && pf>=1.0) total2016++;
																							totalY++;
																						}
																					}
																				}
																				if (totalPositives>=0 || total2016>=0){
																					double res1 = TestPriceBufferGlobal$$.doTestF("",dataBid,dataAsk,maxMins,spreads,2004,2006,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																							false,0,balance,maxH,lostPips,maxDiff,false,false,0,dayTotalPips,true,true,sp);														
																					double res2 = TestPriceBufferGlobal$$.doTestF("",dataBid,dataAsk,maxMins,spreads,2007,2009,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																							false,0,balance,maxH,lostPips,maxDiff,false,false,0,dayTotalPips,true,true,sp);
																					double res3 = TestPriceBufferGlobal$$.doTestF("",dataBid,dataAsk,maxMins,spreads,2010,2012,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																							false,0,balance,maxH,lostPips,maxDiff,false,false,0,dayTotalPips,true,true,sp);
																					double res4 = TestPriceBufferGlobal$$.doTestF("",dataBid,dataAsk,maxMins,spreads,2013,2015,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																							false,0,balance,maxH,lostPips,maxDiff,false,false,0,dayTotalPips,true,true,sp);
																					double res5 = TestPriceBufferGlobal$$.doTestF("",dataBid,dataAsk,maxMins,spreads,2016,2020,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																							false,0,balance,maxH,lostPips,maxDiff,false,false,0,dayTotalPips,true,true,sp);
																																							
																					double avg = (res1+res2+res3+res4)/4;
																					double avgMaxDD = MathUtils.average(maxDDs);
																					double dt1 = Math.sqrt(MathUtils.variance(maxDDs));
																					double var95 = avg+dt1*dt1;
																					
																					double score = res5*0.50+res4*0.40+res3*0.30+res2*0.20+res1*0.10;
																					
																					if (avg>=0.0 && score>=0.0){
																						System.out.println("RESULTS: "+header
																								+" ||| "+totalPositives+" "+total2016
																							//+" "+PrintUtils.Print2dec(accProfit, false)
																							//+" "+PrintUtils.Print2dec(accProfit/balance, false)
																							//+" "+PrintUtils.Print2dec(accYear/totalY, false)
																							+" || "
																							//+" "+PrintUtils.Print3dec(res0, false)
																							+" "+PrintUtils.Print3dec(res1,  false)
																							+" "+PrintUtils.Print3dec(res2,  false)
																							+" "+PrintUtils.Print3dec(res3,  false)
																							+" "+PrintUtils.Print3dec(res4,  false)
																							+" "+PrintUtils.Print3dec(res5,  false)
																							+" || "
																							+" "+PrintUtils.Print3dec(score,  false)
																							+" || "+PrintUtils.Print3dec(avgMaxDD,  false)
																							+" "+PrintUtils.Print3dec(var95,  false)
																							);	
																					}																			
																				}
																			}//hf
																		}//sc
																	}//dayWeek1
																}//comm
															}//lostpIPS
														}//maxH
													}//maxDiff
												}//grisk
											}//maxtrades
										}//thr
									}//barsBack
								}//maxbars
							}//risk
							}//sl
						}//tp
					}

			}//NOISE PIPS
		}
		
		
		System.out.println("programa finalizado");

	}

	private static void doAnalyzeDays(HashMap<Integer, Integer> mp) {
		
		int lastMonth = -1;
		int acc = 0;
		 Iterator it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry pair = (HashMap.Entry)it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		        
		        int month = (int)pair.getKey()/31;
		        if (month!=lastMonth){
		        	if (lastMonth>=0){
		        		System.out.println(lastMonth+" "+acc);
		        	}
		        	lastMonth = month;
		        	acc = 0;
		        }
		        acc+= (int)pair.getValue();
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		
	}

}
