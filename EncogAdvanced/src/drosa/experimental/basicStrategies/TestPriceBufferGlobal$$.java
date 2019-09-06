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
					}else{
						if (q.getLow5()<=pos.getSl()){
							isClosed = true;
							pips = -pos.getEntry()+pos.getSl();
						}else if (q.getHigh5()>=pos.getTp()){
							isClosed = true;
							pips = -pos.getEntry()+pos.getTp();
						}
					}
					if (isClosed){
					}
				}
				
				//actualizacion equitity
				actualEquitity = actualEquitity + (pips-comm*10)*0.1*pos.getPipValue();
				
				if (isClosed){
					if (idxTest==-1 ||pos.getIndexMinMax()==idxTest){
						pips-=comm*10;
						
						if (pips>=0){
							wins++;
							winPips+=pips;
							winPips$$ += pips*0.1*pos.getPipValue();
						}else{
							losses++;
							lostPips+=-pips;
							lostPips$$ += -pips*0.1*pos.getPipValue();
						}
						//System.out.println("pips "+" "+pips+" || "+winPips+" "+lostPips);
						dayPips += pips;
							
						actualBalance += pips*0.1*pos.getPipValue();
						
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
					+" "+winPips+" "+lostPips
					+" "+PrintUtils.Print2dec(avg, false)
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

	public static void main(String[] args) throws Exception {
		//
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2017.01.03.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.01.01_2017.01.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2017.01.03.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_30 Secs_Bid_2012.01.01_2017.01.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_20 Secs_Bid_2012.01.01_2017.01.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_15 Secs_Bid_2012.01.01_2017.01.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_10 Secs_Bid_2012.01.01_2017.01.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Secs_Bid_2012.01.01_2017.01.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2017.02.13.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2017.07.31.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_5 Mins_Bid_2004.01.01_2019.09.03.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2018.01.04.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			dataNoise = data;
			
			//LOS AJUSTES AQUI
			for (int noisePips = 0;noisePips<=0;noisePips++){
				//dataNoise = TradingUtils.addNoise(data,0,23,noisePips);
				
				ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataNoise);
				
			
				
				ArrayList<StrategyConfig> configs = new ArrayList<StrategyConfig>();
				for (int c=0;c<=23;c++) configs.add(null);
				
				//DMO SETTINGS
				//2012-2016
				StrategyConfig config = new StrategyConfig();config.setParams(0, 188,35,100,215,8, true);configs.set(0, config);
				//StrategyConfig config = new StrategyConfig();config.setParams(0, 170,35,90,260,13, true);configs.set(0, config);
				//StrategyConfig config1 = new StrategyConfig();config1.setParams(1, 160,60,90,110,12, true);configs.set(1, config1);
				StrategyConfig config1 = new StrategyConfig();config1.setParams(1,145,60,90,110,12, true);configs.set(1, config1);
				StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 500,20,40,140,16, true);configs.set(2, config2);
				StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 1100,20,40,20,12, true);configs.set(3, config3);
				StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 450,11,75,90,1, true);configs.set(4, config4);
				StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 495,10,50,110,5, true);configs.set(5, config5);
				StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 200,20,50,73,3, true);configs.set(6, config6);
				//StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 400, 10, 10, 36, 1, true);configs.set(7, config7);
				StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 450,10,20,8,1, true);configs.set(7, config7);
				StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 550,10,40,12,3, true);configs.set(8, config8);
				//StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 550, 35, 25, 240, 6, true);configs.set(8, config8);
				StrategyConfig config9 = new StrategyConfig();config9.setParams(9,500,20,60,20,2, true);configs.set(9, config9);
				//StrategyConfig config22 = new StrategyConfig();config22.setParams(22, 50, 20, 60, 42, 1, true);configs.set(22, config22);
				StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 400,11,60,182,4, true);configs.set(23, config23);
				
				
				
				/*StrategyConfig config = new StrategyConfig();config.setParams(0, 188,35,100,215,1, true);configs.set(0, config);
				//StrategyConfig config = new StrategyConfig();config.setParams(0, 170,35,90,260,13, true);configs.set(0, config);
				//StrategyConfig config1 = new StrategyConfig();config1.setParams(1, 160,60,90,110,12, true);configs.set(1, config1);
				StrategyConfig config1 = new StrategyConfig();config1.setParams(1,145,60,90,110,1, true);configs.set(1, config1);
				StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 500,20,40,140,1, true);configs.set(2, config2);
				StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 1100,20,40,20,1, true);configs.set(3, config3);
				StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 450,11,75,90,1, true);configs.set(4, config4);
				StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 495,10,50,110,1, true);configs.set(5, config5);
				StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 200,20,50,73,1, true);configs.set(6, config6);
				//StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 400, 10, 10, 36, 1, true);configs.set(7, config7);
				StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 450,10,20,8,1, true);configs.set(7, config7);
				StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 550,10,40,12,1, true);configs.set(8, config8);
				//StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 550, 35, 25, 240, 6, true);configs.set(8, config8);
				StrategyConfig config9 = new StrategyConfig();config9.setParams(9,500,20,60,20,1, true);configs.set(9, config9);
				//StrategyConfig config22 = new StrategyConfig();config22.setParams(22, 50, 20, 60, 42, 1, true);configs.set(22, config22);
				StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 400,11,60,182,1, true);configs.set(23, config23);
				*/
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
				
				if (path.contains("1 Min")){//140 TRADES 0.46
					System.out.println("1 min mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5);
						}
					}
					maximunTrades = 84;
					maximunRisk = 0.35;
				}
				
				if (path.contains("5 Secs")){
					System.out.println("5 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*12);
						}
					}
				}
				
				if (path.contains("10 Secs")){
					System.out.println("10 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*6);
						}
					}
				}
				
				if (path.contains("15 Secs")){
					System.out.println("15 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*4);
						}
					}
				}
				
				if (path.contains("20 Secs")){
					System.out.println("20 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*3);
						}
					}
				}
				
				if (path.contains("30 Secs")){
					System.out.println("30 secs mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*2);
						}
					}
				}
				
				if (path.contains("1 Sec")){
					System.out.println("1 sec mode");
					for (int h=0;h<=23;h++){
						if (configs.get(h)!=null){
							configs.get(h).multiplyBars(5*60);
						}
					}
				}
				
				
				//for (int y1=2003;y1<=2016;y1++){
					//int y2 = y1+0;
					
					for (int h=0;h<=0;h++){
						/*for (int h0=0;h0<=23;h0++){						
							if (configs.get(h0)!=null){
								configs.get(h0).copy(defaultConfigs.get(h0));
								if (h0!=h) configs.get(h0).setEnabled(false);
							}
						}*/
						//if (h>=0 && configs.get(h)!=null)
							//configs.get(h).setEnabled(false);
						for (int tp=35;tp<=35;tp+=5){
							for (int sl=100;sl<=100;sl+=5){
								for (int maxBars=1;maxBars<=1;maxBars+=1){
									for (int barsBack=1;barsBack<=1;barsBack++){
										for (int thr=50;thr<=50;thr+=50){									
											if (configs.get(h)!=null){
												configs.get(h).setEnabled(true);
												
												//configs.get(h).setThr(thr);	
												//configs.get(h).setBarsBack(barsBack);
												//configs.get(h).setMaxBars(maxBars);
												//configs.get(h).setTp(tp);
												//configs.get(h).setSl(sl);
											}
										
											StratPerformance sp = new StratPerformance();
											for (int maxTrades=10;maxTrades<=10;maxTrades+=1){//17 7 2.25
												double maxRisk = 0.4;
												for (double risk = 0.4;risk<=maxRisk;risk+=0.10){
												//for (double risk = 1.0;risk<=1.0;risk+=0.01){
												//for (double risk=6.5;risk<=6.5;risk+=0.25){
													for (double comm=1.75;comm<=1.75;comm+=0.1){
														
														String header = maxTrades+" "+PrintUtils.Print2dec(risk,false)+" || "+configs.get(h).toString();
														
														int totalPositives = 0;
														double accProfit = 0;
														double balance = 5000;
														double accYear = 0;
														int totalY = 0;
														ArrayList<Double> maxDDs = new ArrayList<Double>();
														
														
														for (int dayWeek1=Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
															int dayWeek2 = dayWeek1+4;
															for (int sc=0;sc<=0;sc++){
																for (int hf=24;hf<=24;hf++){
																	for (double aStd=0.0;aStd<=0.0;aStd+=1.0){
																		for (int y1=2009;y1<=2019;y1+=1){
																			int y2 = y1+0;
																			for (int m1=0;m1<=11;m1+=1){
																				int m2 = m1+00;
																				String header1 = y1+" "+y2+" "+m1+" "+m2+" "+maxTrades
																						+" "+PrintUtils.Print2dec(risk, false)
																						;
																				sp.reset();
																				sp.setActualBalance(4000);																			
																				double pf = TestPriceBufferGlobal$$.doTest(header1,dataNoise,maxMins,
																						y1,y2,m1,m2,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																						false,aStd,balance,risk,comm,
																						false,true,0,dayTotalPips,
																						sp);
																				maxDDs.add(sp.getMaxDD());
																				if (pf>=1.00) totalPositives++;
																				//if (newBalance>=balance) totalPositives++;
																				//double per = newBalance*100.0/balance-100.0;
																				totalY++;
																				//accYear +=per;
																				//accProfit += newBalance-balance;
																				
																				//doAnalyzeDays(dayTotalPips);
																			}
																		}
																	}
																	if (totalPositives>=2){
																		//double res0 = TestPriceBufferGlobal$$.doTest("",dataNoise,maxMins,2003,2008,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																				//false,0,balance,risk,comm,false,false,0,dayTotalPips);
																		double res1 = TestPriceBufferGlobal$$.doTest("",dataNoise,maxMins,2009,2019,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																				false,0,balance,risk,comm,false,false,0,dayTotalPips,sp);														
																		double res2 = TestPriceBufferGlobal$$.doTest("",dataNoise,maxMins,2010,2012,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																				false,0,balance,risk,comm,false,false,0,dayTotalPips,sp);
																		double res3 = TestPriceBufferGlobal$$.doTest("",dataNoise,maxMins,2013,2019,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																				false,0,balance,risk,comm,false,false,0,dayTotalPips,sp);
																		double res4 = TestPriceBufferGlobal$$.doTest("",dataNoise,maxMins,2016,2019,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																				false,0,balance,risk,comm,false,false,0,dayTotalPips,sp);
																		double res5 = TestPriceBufferGlobal$$.doTest("",dataNoise,maxMins,2004,2017,0,11,dayWeek1,dayWeek2,configs,hf,maxTrades,-1,sc,
																				false,0,balance,risk,comm,false,false,0,dayTotalPips,sp);
																		
																		
																		double avg = (res1+res2+res3+res4)/4;
																		double avgMaxDD = MathUtils.average(maxDDs);
																		double dt1 = Math.sqrt(MathUtils.variance(maxDDs));
																		double var95 = avg+dt1*dt1;
																		
																		if (avg>=1.0){
																			System.out.println("RESULTS: "+header+" ||| "+totalPositives
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
																				+" "+PrintUtils.Print3dec(avg,  false)
																				+" || "+PrintUtils.Print3dec(avgMaxDD,  false)
																				+" "+PrintUtils.Print3dec(var95,  false)
																				);	
																		}
																			
																	}
																}//hf
															}//sc
														}//dayWeek1
													}
												}
											}//maxtrades
										}//thr
									}//barsBack
								}//maxbars
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
