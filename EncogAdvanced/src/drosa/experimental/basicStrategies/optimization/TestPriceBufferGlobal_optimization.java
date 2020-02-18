package drosa.experimental.basicStrategies.optimization;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.TradingUtils;

/**
 * La idea es hacer walk-forward, comprobando cual es el mejor periodo predictivo N para el futuro M
 * Se miden N y M en meses
 * @author PC01
 *
 */
public class TestPriceBufferGlobal_optimization {

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		String currency = "eurusd";
		String pathBid = path0+currency+"_5 Mins_Bid_2004.01.01_2020.01.13.csv";
		String pathAsk = path0+currency+"_5 Mins_Ask_2004.01.01_2020.01.13.csv";	
		
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
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
		
		ArrayList<StrategyConfig> configs = new ArrayList<StrategyConfig>();
		for (int c=0;c<=23;c++) configs.add(null);
		
		StrategyConfig config = new StrategyConfig();config.setParams(0,111,169,90,120,12,7, true);configs.set(0, config);//12
		StrategyConfig config1 = new StrategyConfig();config1.setParams(1,168,103,100,125,12,7, true);configs.set(1, config1);//12
		StrategyConfig config2 = new StrategyConfig();config2.setParams(2, 220,20,50,216,6,4, true);configs.set(2, config2);//6
		StrategyConfig config3 = new StrategyConfig();config3.setParams(3, 125,20,40,168,1,2, true);configs.set(3, config3);
		StrategyConfig config4 = new StrategyConfig();config4.setParams(4, 825,10,50,90,1,1, true);configs.set(4, config4);
		StrategyConfig config5 = new StrategyConfig();config5.setParams(5, 450,15,25,86,1,5, true);configs.set(5, config5);
		StrategyConfig config6 = new StrategyConfig();config6.setParams(6, 249,10,65,37,1,6, true);configs.set(6, config6);
		StrategyConfig config7 = new StrategyConfig();config7.setParams(7, 216,25,15,42,1,1, true);configs.set(7, config7);				
		StrategyConfig config8 = new StrategyConfig();config8.setParams(8, 545,20,15,16,1,1, true);configs.set(8, config8);				
	    StrategyConfig config9 = new StrategyConfig();config9.setParams(9, 434,25,35,17,1,1, true);configs.set(9, config9);
		StrategyConfig config23 = new StrategyConfig();config23.setParams(23, 142,10,80,156,1,5, true);configs.set(23, config23);
		
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
		
		//primero formamos parámetros de la estrategia
		
		//despues para cada periodo de lookback N sacamos su par futuro M y se meten en un array, al terminar se calcula el R^2
		Calendar cal1 = Calendar.getInstance();
		cal1.set(2009, 0, 1);
		Calendar calfromN = Calendar.getInstance();
		Calendar caltoN = Calendar.getInstance();
		Calendar calfromM = Calendar.getInstance();
		Calendar caltoM = Calendar.getInstance();
		for (int n=6;n<=24;n+=1) {
			for (int m=1;m<=5;m+=1) {
				
				//testeamos la estrategia para (n,m)
				for (int i=0;i<=500;i++) {
					calfromN.setTimeInMillis(cal1.getTimeInMillis());
					calfromN.add(Calendar.MONTH,i);		
					caltoN.setTimeInMillis(calfromN.getTimeInMillis());
					caltoN.add(Calendar.MONTH,n);
					calfromM.setTimeInMillis(caltoN.getTimeInMillis());
					calfromM.add(Calendar.MONTH,1);
					caltoM.setTimeInMillis(calfromM.getTimeInMillis());
					caltoM.add(Calendar.MONTH,m);
				
					if (caltoM>maxFecha) break; //nos pasamos del periodo salimos
					
					System.out.println("DATES: "
							+DateUtils.datePrint(calfromN)+" "+DateUtils.datePrint(caltoN)
							+" "+DateUtils.datePrint(calfromM)+" "+DateUtils.datePrint(caltoM)
					);
				}
			}
		}

	}

}
