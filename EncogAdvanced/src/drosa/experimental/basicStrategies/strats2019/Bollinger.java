package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.basicStrategies.TestPriceBufferGlobal$$;
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

public class Bollinger extends AlgoBasic  {

	int nbars = 50;
	double dt = 2.0;
	int tp = 0;
	double sl = 0;
	int maxPositions = 0;
	double distance = 0.5;
	int h1=0;
	int h2=23;
	int thr = -1;
	int lastModeCrossed = 0;
	int minDiff = 12;
	boolean forward = false;
	private ArrayList<Integer> values = new ArrayList<Integer>();
	
	//para testear meses concretos
	int testMonth1 = 0;
	int testMonth2 = 11;
	
	public void setParameters(int anbars,double adt,int atp,double asl,int amaxPositions,int adistance,int aThr,int ah1,int ah2,
			boolean aForward,double aRisk,int aMinDiff) {
		nbars = anbars;
		dt = adt;
		tp = atp;
		sl = asl;
		maxPositions = amaxPositions;
		distance = adistance;
		h1 = ah1;
		h2 = ah2;
		forward = aForward;
		risk = aRisk;
		thr = aThr;
		minDiff = aMinDiff;
	}

	@Override
	public void doManagePositions(ArrayList<QuoteShort> dataBid,ArrayList<QuoteShort> dataAsk, int i, ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEvaluateExits(
			ArrayList<QuoteShort> dataBid,
			ArrayList<QuoteShort> dataAsk, 
			int i, ArrayList<PositionShort> positions,
			StratPerformance sp) {
		
		if (positions.size()<=0) return;
		QuoteShort qb = dataBid.get(i);
		QuoteShort qa = dataAsk.get(i);
		int sma = (int) MathUtils.average(values, values.size()-nbars, values.size()-1);
		int pipsAtr = this.atrArray.get(atrArray.size()-1);
		int maxAdv = (int) (0.10*pipsAtr);
		//evaluamos SL y Tp			
		int j = 0;
		while (j<positions.size()){
			PositionShort p = positions.get(j);
			boolean isClosed = false;
			int pips = 0;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					if (!forward) {
						if (qb.getOpen5()>=sma){
							pips = qb.getOpen5()-p.getEntry();
							isClosed = true;
							//System.out.println("[long sma touched] "+pips+" "+pips);
						}
					}else {
						if (qb.getOpen5()<=sma){
							pips = qb.getOpen5()-p.getEntry();
							isClosed = true;
							//System.out.println("[short sl touched] "+pips+" "+pips);
						}
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					if (!forward) {
						if (qa.getOpen5()<=sma){
							pips = p.getEntry()-qa.getOpen5();
							isClosed = true;
							//System.out.println("[short sl touched] "+pips+" "+pips);
						}
					}else {
						if (qa.getOpen5()>=sma){
							pips = p.getEntry()-qa.getOpen5();
							isClosed = true;
							//System.out.println("[long sl touched] "+pips+" "+pips);
						}
					}
				}
			}
			
			if (isClosed){
				int pipsSL = Math.abs(p.getEntry()-p.getSl());
				double rr = pips*1.0/pipsSL;
				
				sp.addTrade(p.getMicroLots(),pips,pipsSL,p.getMaxLoss(),p.getTransactionCosts(),cali);
				
				if (debug==1) {
					System.out.println("[CLOSED] "+DateUtils.datePrint(cali)+" || "+p.toString2()+" || "+pips+" || "+PrintUtils.Print2dec(sp.getActualBalance(), false));
				}
				positions.remove(j);
			}else{
				j++;
			}
		}
		
	}

	@Override
	public int doEvaluateEntries(
			ArrayList<QuoteShort> dataBid, 
			ArrayList<QuoteShort> dataAsk,
			ArrayList<Integer> maxMins, 
			HashMap<Integer,ArrayList<Double>> spreads,
			int i,
			ArrayList<PositionShort> positions, 
			boolean canTrade, 
			StratPerformance sp) {
				
		QuoteShort qb = dataBid.get(i);
		QuoteShort qa = dataAsk.get(i);
		QuoteShort.getCalendar(cali, qb);
		int y = cali.get(Calendar.YEAR);
		int h = cali.get(Calendar.HOUR_OF_DAY);
		int min = cali.get(Calendar.MINUTE);
		int day = cali.get(Calendar.DAY_OF_YEAR);
				
		values.add(qb.getOpen5());
		int newtrades=0;
		
		if (i<nbars) return 0;
		if (h<h1 || h>h2 
				//|| h==4
				) return 0;
		if (h==0 && min<15) return 0;
		if (positions.size()>=maxPositions) return 0;
					
		//System.out.println("aaded: "+values.get(values.size()-1)+" "+values.size());
		int sma = (int) MathUtils.average(values, values.size()-nbars, values.size()-1);
		int dtpips = (int) Math.sqrt(MathUtils.variance(values, values.size()-nbars, values.size()-1));
		//System.out.println("aaded: "+values.get(values.size()-1)+" "+values.size()+" "+sma );
		int pipsAtr = this.atrArray.get(atrArray.size()-1);
		dtpips = pipsAtr;
		int slPips = (int) (sl*dtpips);
		//upper band
		int upperBand = (int) (sma + this.dt*dtpips);
		//down band
		int lowerBand = (int) (sma - this.dt*dtpips);
		//diferencia
		int pipsA = qb.getOpen5()-upperBand;
		int pipsB = lowerBand-qa.getOpen5();
		int maxMin = maxMins.get(i-1);
		
		//System.out.println(DateUtils.datePrint(cali)+" || "+sma+" "+upperBand+" "+lowerBand+"  || "+qa.getOpen5()+" "+qb.getOpen5()+" || "+pipsA+" "+pipsB);
		
		int transactionCosts = TradingUtils.getTransactionCosts(spreads,y, h,3);
		if (isTransactionHours!=1)
			transactionCosts = TradingUtils.getTransactionCostsMinutes(spreads,y, h*12+min/5,3);
		
		//transactionCosts = 0;
		//System.out.println(y+" || "+h+" || "+transactionCosts);
		if (true
				&& pipsA>=20
				//&& (q.getOpen5()-sma)>=30
				//&& (thr<0 || maxMin>=thr)
				){
			if (lastModeCrossed<=0) {
				lastCrossed = i;
				lastModeCrossed = 1;
			}
			PositionShort pos = new PositionShort();
			pos.setEntry(qb.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.LONG);
				//pos.setSl((sma));
				pos.setTp((int) (qb.getOpen5() + slPips));
				pos.setSl((int) (qb.getOpen5() - slPips));
			}else{
				pos.setPositionType(PositionType.SHORT);
				pos.setSl(pos.getEntry()+slPips);

				//pos.setTp((int) (q.getOpen5()-1*distance));
				//System.out.println("[open short] "+q.getOpen5()+" "+sma+" "+pos.getSl()+" "+pipsAtr+" "+pipsT+" "+q.toString());
			}
			//calculate miniLots 0.01
			int minPips = slPips; 
			double riskPosition = sp.getActualBalance()*risk*1.0/100.0;
			double riskPip = riskPosition/(minPips*0.1);
			int microLots = (int) (riskPip/0.10);
			if (microLots<=0) microLots = 1;
			
			if (actualM<testMonth1 || actualM>testMonth2) microLots=0;
			
			if (microLots>0					
					){
				int diff = i-lastCrossed;
				long  actualSize = TradingUtils.getOpenSize(positions);
				double actualLeverage = (actualSize+microLots)*1000.0/sp.getActualBalance();
				//System.out.println(PrintUtils.Print2dec(actualSize,false)+" || "+PrintUtils.Print2dec(actualLeverage,false));
				if (actualLeverage<30.0
						&& diff>=minDiff
						) {
					pos.setMicroLots(microLots);
					pos.setTransactionCosts(transactionCosts);
					positions.add(pos);	
					lastDayTrade = day;
					trades++;
					newtrades++;
					
					if (debug==1) {
						System.out.println("[open SHORT] "+DateUtils.datePrint(cali)
						+" "+pos.getEntry()
						+" "+pos.getMicroLots()+" "+riskPosition+" || "+slPips+" || "+risk+" || "+sp.getActualBalance());
					}
				}
			}
				
				//
		}else if (
				true
				&& pipsB>=20
				//&& (sma-q.getOpen5())>=30
				//&& (thr<0 || maxMin<=-thr)
				){
			//distance =100;
			if (lastModeCrossed>=0) {
				lastCrossed = i;
				lastModeCrossed = -1;
			}

			//abrimos posicion long
			PositionShort pos = new PositionShort();
			pos.setEntry(qa.getOpen5());
			pos.setPositionStatus(PositionStatus.OPEN);
			if (forward){
				pos.setPositionType(PositionType.SHORT);
				//pos.setSl((int) (sma));
				pos.setTp((int) (qa.getOpen5()-slPips));
				pos.setTp((int) (qa.getOpen5()+slPips));
			}else{
				pos.setPositionType(PositionType.LONG);
				pos.setSl(qa.getOpen5()-slPips);
				//pos.setTp((int) (q.getOpen5() + 1*distance));
				//System.out.println("[open long] "+q.getOpen5()+" "+sma+" "+pos.getSl());
			}
			//calculate miniLots
			int minPips = slPips;  
			double riskPosition = sp.getActualBalance()*risk*1.0/100.0;
			double riskPip = riskPosition/(minPips*0.1);
			int microLots = (int) (riskPip/0.10);
			if (microLots<=0) microLots = 1;
			
			if (actualM<testMonth1 || actualM>testMonth2) microLots=0;
			
			if (microLots>0){
				long  actualSize = TradingUtils.getOpenSize(positions);
				double actualLeverage = (actualSize+microLots)*1000.0/sp.getActualBalance();
				int diff = i-lastCrossed;
				if (actualLeverage<30.0
						&& diff>=minDiff
						) {
					pos.setMicroLots(microLots);
					pos.setTransactionCosts(transactionCosts);
					positions.add(pos);	
					lastDayTrade = day;
					trades++;
					newtrades++;
					
					if (debug==1) {
						/*
						 System.out.println("[open LONG] "+DateUtils.datePrint(cali)
						+" "+pos.getEntry()
						+" "+pos.getMicroLots()+" "+riskPosition+" || "+slPips+" || "+risk+" || "+sp.getActualBalance());
						*/
					}
				}
				
				//System.out.println("[open] "+microLots+" "+riskPosition+" || "+slPips+" || "+risk+" || "+sp.getActualBalance());
			}
		}
		
		return newtrades;
	}

	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		String currency = "eurjpy";

		String pathSpread = path0+currency+"_spreads_2014_2019.csv";
		//String pathSpread = path0+currency+"_spreads_minutes_2012_2019.csv";
		//currency="eurchf";
		String pathBid = path0+currency+"_5 Mins_Bid_2009.01.01_2020.02.04.csv";
		String pathAsk = path0+currency+"_5 Mins_Ask_2009.01.01_2020.02.04.csv";
		String pathNews = path0+"News.csv";
		
		
		ArrayList<String> paths = new ArrayList<String>();
		
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
		HashMap<Integer,ArrayList<Double>> spreads = new HashMap<Integer,ArrayList<Double>>();
		
		int isHours=1;		
		//if (isHours==1) DAO.readSpreads(pathSpread,2014,2019,spreads);
		//else DAO.readSpreadsMinutes(pathSpread,2014,2019,spreads);
		
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=0;i++){
			String path = pathBid;				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  			
			ArrayList<QuoteShort> dataBid = dataS;
			
			path = pathAsk;				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  			
			ArrayList<QuoteShort> dataAsk = dataS;
			
			System.out.println("data: "+path+" "+dataBid.size()+" "+dataAsk.size());
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataBid);
							
			Bollinger mm = new Bollinger();
			StratPerformance sp = new StratPerformance(); 
			
			//30min
			//8 0.14 0.60 0.30 13 0 8 
			//8 0.15 0.60 0.10 20 0 8 
			//15
			//18 0.14 1.00 13 0.15
			
			//testear AN 17-23
			//AU 72 0.22 0.60
			
			//GU 20-23 48 0.10 0.4
			//AJ 20-23 60 0.20 0.2
			//EJ 19-23 66 0.17 0.2
			//EJ 0-1 40 0.10 0.60 0.5 25
			//E-J 20-23 52 0.20 0.60 0.30 25
			//EU 23H: 120 0.20 1.0
			//Eu 0-8: 52 0.18 0.70 0.10 35 
			//AN 20-22:72 0.20 0.60 0.40 35 
			
			//UJ 0-1 54 0.10 0.50 0.30 24
			//EJ 19-00 52 0.70 0.30 40
			//forward
			//***EJ***//
			//EJ 20-21 60 0.20 0.70 0.50 24
			//EJ 19  47 0.19 0.70 0.50 24
			//4  48 0.20 0.70 0.50 24 
			//5  48 0.20 0.70 0.50 24
			//6  48 0.20 0.70 0.50 24 
			//7  60 0.20 0.70 0.50 24  
			//19 50 0.18 0.70 0.40 60
			//20 50 0.20 0.70 0.40 60
			//21 50 0.18 0.70 0.40 60 
			//22
			//23
			//0-1 0.10 0.70 0.60
			sp = new StratPerformance();
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+1;
				for (int nbars=54;nbars<=54;nbars+=5){
					for (double dt=0.16;dt<=0.16;dt+=0.01){
						for (double sl=0.6;sl<=0.6;sl+=0.10){	
							for (int thr=-1;thr<=-1;thr+=100){
								for (int minDiff=0;minDiff<=0;minDiff+=6) {
									for (double risk=0.3;risk<=0.3;risk+=0.10){
										//System.out.println("risk= "+PrintUtils.Print2dec(risk, false));
										for (int maxPositions=50;maxPositions<=50;maxPositions+=5){																				
											String header = nbars+" "+PrintUtils.Print2dec(dt, false)
												+" "+PrintUtils.Print2dec(sl, false)
												+" "+PrintUtils.Print2dec(risk, false)
												+" "+maxPositions
											;
											int totalPositives = 0;
											int total2016 = 0;
											double accProfit = 0;
											double balance = 4000;
											double accYear = 0;
											int totalY = 0;
											ArrayList<Double> maxDDs = new ArrayList<Double>();
											ArrayList<Integer> tradess = new ArrayList<Integer>();
											for (int y1=2009;y1<=2020;y1++){
												int y2 = y1+0;
												for (int m1=0;m1<=0;m1+=0){
													int m2 = m1+11;
													mm = new Bollinger();
													
													sp.reset();
													sp.setInitialBalance(4000);
													
													mm.setParameters(nbars,dt,0,sl,maxPositions,0,thr,h1,h2,false,risk,minDiff);
													String header1 = y1+" "+y2+" "+h1+" "+h2+" || "+header;
													double pf =mm.doTest(header1,dataBid,dataAsk,maxMins,spreads,y1, y2, m1, m2, sp,isHours, 0,0);	
													maxDDs.add(sp.getMaxDD());
													tradess.add(sp.getTrades());
													//System.out.println(sp.getMaxDD());
													if (pf>=1.00) totalPositives++;
													if (y1>=2016 && pf>=1.0) total2016++;
													try {
														Sizeof.runGC ();
													} catch (Exception e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}//m
											}//y
											if (totalPositives>=5 || total2016>=2){
												sp.reset();sp.setActualBalance(4000);	
												double pf1 =mm.doTest(header,dataBid,dataAsk,maxMins,spreads,2012, 2019, 0, 11, sp,isHours, 0,1);
												sp.reset();sp.setActualBalance(4000);	
												double pf2 =mm.doTest(header,dataBid,dataAsk,maxMins,spreads,2012, 2015, 0, 11, sp,isHours, 0,1);
												sp.reset();sp.setActualBalance(4000);	
												double pf3 =mm.doTest(header,dataBid,dataAsk,maxMins,spreads,2014, 2017, 0, 11, sp,isHours, 0,1);
												sp.reset();sp.setActualBalance(4000);	
												double pf4 =mm.doTest(header,dataBid,dataAsk,maxMins,spreads,2016, 2019, 0, 11, sp,isHours, 0,1);
																														
												double avg = (pf1+pf2+pf3+pf4)/4;
												double avgMaxDD = MathUtils.average(maxDDs);
												double avgTrades = MathUtils.average(tradess);
												double dt1 = Math.sqrt(MathUtils.variance(maxDDs));
												double var95 = avg+dt1*dt1;
												
												if (avg>=0.0){
													System.out.println("RESULTS: "+header
															+" ||| "+totalPositives+" "+total2016
														+" || "
														//+" "+PrintUtils.Print3dec(res0, false)
														+" "+PrintUtils.Print3dec(pf1,  false)
														+" "+PrintUtils.Print3dec(pf2,  false)
														+" "+PrintUtils.Print3dec(pf3,  false)
														+" "+PrintUtils.Print3dec(pf4,  false)
														+" || "
														+" "+PrintUtils.Print3dec(avg,  false)
														+" || "
														+" "+PrintUtils.Print3dec(avgTrades,  false)
														+" || "+PrintUtils.Print3dec(avgMaxDD,  false)
														+" "+PrintUtils.Print3dec(var95,  false)
														);	
												}	
											}//totalpoistives
										}//maxPos
									}//risk
								}//minDiff
							}//thr
						}//fsl
					}//fdiff
				}//nbars
			}//h
		}//i

	}


}
