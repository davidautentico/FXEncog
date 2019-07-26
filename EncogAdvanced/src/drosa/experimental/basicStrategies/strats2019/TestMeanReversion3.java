package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMeanReversion3 {
	
	public static void doTestMR(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int nPeriods,//número de periodos
			int oYears,//espacio entre años
			int debug
			){
		
		ArrayList<PeriodTest> periodArr = new ArrayList<PeriodTest>();
		//generamos los nPeriods de forma aleatoria
		for (int i=1;i<=nPeriods;i++){
			int y11 = MathUtils.getRandomNumberInRange(y1, y2);
			int y21 = y11 + 1;
			
			if (y21>=y2) y21 = y2;
			
			PeriodTest pt = new PeriodTest();
			pt.setY1(y11);
			pt.setY2(y21);
			periodArr.add(pt);			
		}
		
		for (int i=1;i<=nPeriods;i++){
			int y11 = MathUtils.getRandomNumberInRange(y1, y2);
			int y21 = y11 + 2;
			
			if (y21>=y2) y21 = y2;
			
			PeriodTest pt = new PeriodTest();
			pt.setY1(y11);
			pt.setY2(y21);
			periodArr.add(pt);			
		}
		
		for (int i=1;i<=nPeriods;i++){
			int y11 = MathUtils.getRandomNumberInRange(y1, y2);
			int y21 = y11 + 9;
			
			if (y21>=y2) y21 = y2;
			
			PeriodTest pt = new PeriodTest();
			pt.setY1(y11);
			pt.setY2(y21);
			periodArr.add(pt);			
		}
		
		for (int i=1;i<=nPeriods;i++){
			int y11 = MathUtils.getRandomNumberInRange(y1, y2);
			int y21 = y11 + 4;
			
			if (y21>=y2) y21 = y2;
			
			PeriodTest pt = new PeriodTest();
			pt.setY1(y11);
			pt.setY2(y21);
			periodArr.add(pt);			
		}
		
		double aRisk = 0.3;
		int barsOffset = 0;
		ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
		ArrayList<String> strat = new ArrayList<String>();
		for (int j=0;j<=23;j++) strat.add("-1");
		
		double bestPF = -999999;
		for (int nbars=0;nbars<=0;nbars+=1){
			for (double offset=0.25;offset<=0.25;offset+=0.05){
				for (int thr=50;thr<=4000;thr+=12){
					for (int j=0;j<=23;j++) strat.set(j,"-1");
					String params =nbars+" "+barsOffset;
					for (int j=0;j<=9;j++) strat.set(j,params);
					String header = nbars+" "+PrintUtils.Print2dec(offset, false)+" "+thr;
					//testeamos cada modelo en los periodos y extraemos la media
					double acc = 0;
					for (int i=0;i<=periodArr.size()-1;i++){
						int a1 = periodArr.get(i).getY1();
						int a2 = periodArr.get(i).getY2();
						//test de estrategia y resultado
						double pf =TestMeanReversion.doTestAlphadude(header, 
								data,maxMins,
								y1, y2, 0, 11,
								nbars,offset,thr,
								999999, 
								strat,dayPips1,
								false,5,
								30,
								aRisk,true,
								2,false,false,null);
						
						acc += pf;					
					}
					
					double avgPf = acc/periodArr.size();
					//si mejora la mejor calculamo en todo el periodo y actualizamos si obtiene un minimo
					//porcentaje de días
					if (avgPf>=1.40){
						//System.out.println("avgPf: "+avgPf);
						//bestPF = avgPf;
						double realPF = TestMeanReversion.doTestAlphadude(header, 
								data,maxMins,
								y1, y2, 0, 11,
								nbars,offset,0,
								999999, 
								strat,dayPips1,
								false,5,
								30,
								aRisk,true,
								2,false,false,null);
					
						if (realPF>=1.40){
							bestPF = realPF;
							TestMeanReversion.doTestAlphadude(header, data,maxMins,
									y1, y2, 0, 11,
									nbars,offset,0,
									999999, 
									strat,dayPips1,
									false,5,
									30,
									aRisk,true,
									2,false,false,null);
						}
					}
				}//thr
			}
		}
	}
	
	
	
	public static void doTestMR2(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int nPeriods,//número de periodos
			int oYears,//espacio entre años
			int debug
			){
		
		ArrayList<PeriodTest> periodArr = new ArrayList<PeriodTest>();
		
		for (int y=y1;y<=y2;y++){
			for (int m=0;m<=11;m++){
				int y11 = y;
				int m11 = m;
				int m21 = (m + 9)%12;
				int y21 = y11;
				if (m+9>11) y21 = y11+1;
				
				if (y21<=y2){					
					PeriodTest pt = new PeriodTest();
					pt.setY1(y11);
					pt.setY2(y21);
					pt.setM1(m11);
					pt.setM2(m21);
					periodArr.add(pt);		
				}
			}
		}
		
		
		double aRisk = 0.3;
		int barsOffset = 0;
		ArrayList<Integer> dayPips1 = new ArrayList<Integer>();
		ArrayList<String> strat = new ArrayList<String>();
		for (int j=0;j<=23;j++) strat.add("-1");
		
		double bestPF = -999999;

		StratPerformance sp = new StratPerformance();
		for (int nbars=50;nbars<=50;nbars+=1){
			for (double offset=0.29;offset<=0.29;offset+=0.01){
				for (int thr=400;thr<=400;thr+=12){
					for (int j=0;j<=23;j++) strat.set(j,"-1");
					String params =nbars+" "+barsOffset;
					for (int j=0;j<=9;j++) strat.set(j,params);
					String header = nbars+" "+PrintUtils.Print2dec(offset, false)+" "+thr;
					//testeamos cada modelo en los periodos y extraemos la media
					double acc = 0;
					int totalCases = 0;
					int totalPositives = 0;
					double accMaxDD = 0;
					for (int i=0;i<=periodArr.size()-1;i++){
						int a1 = periodArr.get(i).getY1();
						int a2 = periodArr.get(i).getY2();
						int m1 = periodArr.get(i).getM1();
						int m2 = periodArr.get(i).getM2();
						sp.reset();
						//test de estrategia y resultado
						double pf =TestMeanReversion.doTestAlphadude(header, 
								data,maxMins,
								a1, a2, m1, m2,
								nbars,offset,thr,
								999999, 
								strat,dayPips1,
								false,5,
								30,
								aRisk,true,
								2,false,false,sp);
						
						if (sp.getTrades()>=50){
							totalCases++;
							double pfc = 3.0;
							if (sp.getLostPips()>0){
								pfc = sp.getWinPips()*1.0/sp.getLostPips();
							}
							
							if (pfc>=3.0) pfc = 3.0;
							
							acc += pfc;
							accMaxDD += sp.getMaxDD();
							//System.out.println(a1+"-"+m1+" "+a2+"-"+m2+" || "+sp.getPf());
							if (pfc>=1.0){
								totalPositives++;
							}
						}
					}
					
					double avgPf = acc/totalCases;
					double winPer = totalPositives*100.0/totalCases;
					double avgMaxDD = accMaxDD/totalCases; 
					System.out.println(
							header
							+" || "+totalCases
							+" "+PrintUtils.Print2dec(winPer, false)
							+" "+PrintUtils.Print2dec(avgPf, false)
							+" "+PrintUtils.Print2dec(avgMaxDD, false)
							);
					//si mejora la mejor calculamo en todo el periodo y actualizamos si obtiene un minimo
					//porcentaje de días
					/*if (avgPf>=1.40){
						//System.out.println("avgPf: "+avgPf);
						//bestPF = avgPf;
						double realPF = TestMeanReversion.doTestAlphadude(header, 
								data,maxMins,
								y1, y2, 0, 11,
								nbars,offset,0,
								999999, 
								strat,dayPips1,
								false,5,
								30,
								aRisk,true,
								2,false,false);
					
						if (realPF>=1.40){
							bestPF = realPF;
							TestMeanReversion.doTestAlphadude(header, data,maxMins,
									y1, y2, 0, 11,
									nbars,offset,0,
									999999, 
									strat,dayPips1,
									false,5,
									30,
									aRisk,true,
									2,false,false);
						}
					}*/
				}//thr
			}
		}
	}

	public static void main(String[] args) {
		//Buscamos la mejor implementación usando múltiples periodos de test
		
		String path0 ="C:\\fxdata\\";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.06.24.csv";
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
			
			TestMeanReversion3.doTestMR2(data,maxMins, 2009, 2019, 6, 1, 0);
			
		}
			
		
		

	}

}
