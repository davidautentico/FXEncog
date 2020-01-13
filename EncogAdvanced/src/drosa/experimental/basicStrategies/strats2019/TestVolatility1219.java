package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.HashMap;

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
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestVolatility1219  extends AlgoBasic  {
	
	int h1 = 0;
	int h2 = 23;
	int nbars = 0;
	double volThr = 1.0;
	int maxBars = 0;
	boolean isForward = true;
	
	public void setParameters(
			int ah1,
			int ah2,
			int aNBars,
			int aMaxBars,
			double aVolThr,
			boolean aIsForward) {
		
		h1 = ah1;
		h2 = ah2;
		nbars = aNBars;
		volThr = aVolThr;
		maxBars = aMaxBars;
		isForward = aIsForward;
	}

	@Override
	public void doManagePositions(
			ArrayList<QuoteShort> dataBid, ArrayList<QuoteShort> dataAsk, int i,
			ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> dataBid, ArrayList<QuoteShort> dataAsk, int i,
			ArrayList<PositionShort> positions, StratPerformance sp) {
		
		if (positions.size()<=0) return;
		QuoteShort qb = dataBid.get(i);
		QuoteShort qa = dataAsk.get(i);
		
		//evaluamos SL y Tp			
		int j = 0;
		while (j<positions.size()){
			PositionShort p = positions.get(j);
			boolean isClosed = false;
			int pips = 0;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				int diff = (int) (i-p.getOpenIndex());
				if (p.getPositionType()==PositionType.LONG){
					if (diff>=maxBars) {
						pips = qb.getOpen5()-p.getEntry();
						isClosed = true;
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					if (diff>=maxBars) {
						pips = p.getEntry()-qa.getOpen5();
						isClosed = true;
					}
				}
			}
			
			if (isClosed){
				int pipsSL = Math.abs(p.getEntry()-p.getSl());
				double rr = pips*1.0/pipsSL;
				sp.addTrade(p.getMicroLots(),pips,pipsSL,p.getMaxLoss(),p.getTransactionCosts(),cali);
				
				if (debug==1) {
					System.out.println("[CLOSED] "+p.toString2()+" || "+pips+" || "+PrintUtils.Print2dec(sp.getActualBalance(), false));
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
			HashMap<Integer, 
			ArrayList<Double>> spreads, 
			int i,
			ArrayList<PositionShort> positions, 
			boolean canTrade, 
			StratPerformance sp
	) {
		int trades = 0;
		if (i<nbars) return 0;
		
		QuoteShort qb = dataBid.get(i);
		QuoteShort qa = dataAsk.get(i);
		
		int pipsAtr = this.atrArray.get(atrArray.size()-1);
		
		int diffUpAsk	= qa.getOpen5()-dataAsk.get(i-nbars).getOpen5(); 
		int diffUpBid	= qb.getOpen5()-dataBid.get(i-nbars).getOpen5(); 
		int diffDownBid = dataBid.get(i-nbars).getOpen5()-qb.getOpen5();
		int diffDownAsk = dataAsk.get(i-nbars).getOpen5()-qa.getOpen5();
		
		int minEntryPips = (int) (volThr*pipsAtr);
		int transactionCosts = TradingUtils.getTransactionCosts(spreads,actualY,actualH,3);
		if (actualH>=h1 && actualH<=h2) {
			if (isForward && diffUpAsk>=minEntryPips
					|| (!isForward && diffUpBid>=minEntryPips)
					) {
				
				PositionShort pos = new PositionShort();
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenIndex(i);
				
				if (isForward) {
					pos.setEntry(qa.getOpen5());
					pos.setPositionType(PositionType.LONG);
					pos.setTp(qa.getOpen5()+999999);
					pos.setSl(qa.getOpen5()-999999);
				}else {
					pos.setEntry(qb.getOpen5());
					pos.setPositionType(PositionType.SHORT);
					pos.setTp(qb.getOpen5()-999999);
					pos.setSl(qb.getOpen5()+999999);
				}
				pos.setTransactionCosts(transactionCosts);
				trades++;
				positions.add(pos);
			}else if ((isForward && diffDownBid>=minEntryPips)
					|| (!isForward && diffDownAsk>=minEntryPips) 
					) {
				
				PositionShort pos = new PositionShort();
				pos.setEntry(qb.getOpen5());
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenIndex(i);
				
				if (isForward) {
					pos.setEntry(qb.getOpen5());
					pos.setPositionType(PositionType.SHORT);
					pos.setTp(qb.getOpen5()-999999);
					pos.setSl(qb.getOpen5()+999999);
				}else {
					pos.setEntry(qa.getOpen5());
					pos.setPositionType(PositionType.LONG);
					pos.setTp(qa.getOpen5()+999999);
					pos.setSl(qa.getOpen5()-999999);
				}
				pos.setTransactionCosts(transactionCosts);
				trades++;
				positions.add(pos);
			} 
		}
		
		return trades;
	}
	
	
	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		String currency = "usdjpy";

		String pathSpread = path0+currency+"_spreads_2014_2019.csv";
		//String pathSpread = path0+currency+"_spreads_minutes_2012_2019.csv";
		//currency="eurchf";
		String pathBid = path0+currency+"_5 Mins_Bid_2012.01.01_2019.12.02.csv";
		String pathAsk = path0+currency+"_5 Mins_Ask_2012.01.01_2019.12.02.csv";
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
							
			TestVolatility1219 tv = new TestVolatility1219();
			StratPerformance sp = new StratPerformance(); 
			for (int h1=8;h1<=8;h1++){
				int h2 = h1+7;
				for (int nbars=120;nbars<=120;nbars+=12){
					for (double dt=0.1;dt<=1.6;dt+=0.10){
						for (int maxbars=120;maxbars<=120;maxbars+=12){
							sp.reset();
							sp.setInitialBalance(4000);
							tv = new TestVolatility1219();
							
							tv.setParameters(h1,h2,nbars,maxbars,dt,true);
							
							String header=h1+" "+nbars+" "+PrintUtils.Print2dec(dt, false)+" "+maxbars;
							
							tv.doTest(header, dataBid, dataAsk, maxMins, spreads, 2012, 2019, 0, 11, sp,0, 0, 0);
						}
					}
				}
			}
		}//i

	}

}
