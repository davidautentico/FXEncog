package drosa.finance.main;

import java.io.BufferedWriter;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.scorecalc.RegressionScoreCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.regression.RegressionEvaluation.Metric;
//import org.deeplearning4j.ui.api.UIServer;
//import org.deeplearning4j.ui.stats.StatsListener;
//import org.deeplearning4j.ui.storage.FileStatsStorage;
//import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
//import org.deeplearning4j.ui.api.UIServer;
//import org.deeplearning4j.ui.stats.StatsListener;
//import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
//import org.nd4j.jita.conf.CudaEnvironment;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;




import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import drosa.finance.classes.DAO;
import drosa.finance.classes.QuoteShort;
import drosa.finance.classes.TestLines;
import drosa.finance.experiments.Experiment1;
import drosa.finance.experiments.Experiment4;
import drosa.finance.types.DataProvider;
import drosa.finance.utils.MathUtils;
import drosa.finance.utils.ModelListener;
import drosa.finance.utils.TradingUtils;


public class TestExperiments {
	
	
	/**
	 * Leemos los datos y los convertimos a la clase financiera
	 * @param fileName
	 */
	private static ArrayList<QuoteShort> readData(String fileName) {
		
		ArrayList<QuoteShort> dataI 		= DAO.retrieveDataShort5m(fileName, DataProvider.DUKASCOPY_FOREX4);		
		//System.out.println(dataI.size());
		TestLines.calculateCalendarAdjustedSinside(dataI);			
		
		return TradingUtils.cleanWeekendDataS(dataI);  		
	}
	
	/**
	 * Se extraen las características de los datos planos y la clase a la que pertenece cada vector input,
	 * basandose en si se optienen pripero pipsTarget pips comprando o vendiendo
	 * @param maxMins 
	 * @param dataTrainRaw
	 * @param fileNameTrainPro
	 * @throws IOException 
	 */
	private static void doExtractXfromData4b(
			ArrayList<QuoteShort> data,
			String fileName, 
			ArrayList<Integer> maxMins, 
			int pipsTarget,int pipsSL,
			int maxMinLimit,
			boolean isSell) {
		
		FileWriter fstream;
		try {
			fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			Calendar cal = Calendar.getInstance();
			int atr20 = 600;
			int high = -1;
			int low = -1;
			int lastHigh = -1;
			int lastLow = -1;
			int lastDay = -1;
			
			int wins = 0;
			int losses = 0;
			for (int i=20;i<data.size()-1;i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				int h	= cal.get(Calendar.HOUR_OF_DAY);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int maxMin = maxMins.get(i);
				
				if (maxMin>=maxMinLimit) maxMin = maxMinLimit;
				else if (maxMin<=-maxMinLimit) maxMin = -maxMinLimit;
				
				if (day!=lastDay){
					if (lastDay!=-1){
						lastHigh = high;
						lastLow = low;
					}
					high = -1;
					low = -1;
					lastDay = day;
				}
				
				
				if (high==-1 || q.getOpen5()>=high) high = q.getOpen5();
				if (low==-1 || q.getOpen5()<=low) low = q.getOpen5();
		
				
				double sma30 =  MathUtils.average(data, i-30, i, false);
				double diff30 = (int) (q.getOpen5()-sma30);
				double sma50 =  MathUtils.average(data, i-50, i, false);
				double diff50 = (int) (q.getOpen5()-sma50);
				double sma120 =  MathUtils.average(data, i-120, i, false);
				double diff120 = (int) (q.getOpen5()-sma120);
				
				//normalizamos..
				diff30 = diff30/600;
				diff50 = diff50/600;
				diff120 = diff120/600;
				
				
				String inputsDiff="";
				for (int j=1;j<=20;j++){
					double diff = (q.getOpen5()-data.get(i-j).getOpen5())/300;
					inputsDiff += ","+diff; 
				}
				
				int range = (high-low)/600;

				int labelf			 	= 0;
				int target	= q.getOpen5() + pipsTarget;
				int stop 	= q.getOpen5() - pipsSL;
				if (isSell){
					 target	= q.getOpen5() - pipsTarget;
					 stop 	= q.getOpen5() + pipsSL;
				}
				
				for (int j=i+1;j<data.size();j++){
					QuoteShort qj = data.get(j);
					
					if (!isSell){
						if (qj.getOpen5()>=target){
							labelf = 1;
							break;
						}else if (qj.getOpen5()<=stop){
							labelf = 0;
							break;
						}
					}else{
						if (qj.getOpen5()<=target){
							labelf = 1;
							break;
						}else if (qj.getOpen5()>=stop){
							labelf = 0;
							break;
						}
					}
				}
				
				double maxMinThr		= maxMin*1.0 / maxMinLimit;	
				String hourBinaryStr 	= getHourBinary(h);
				String dataStr	 		= labelf
						+","+maxMinThr// 1 representacion binaria de la hora del dia
						+","+hourBinaryStr//5
						+","+diff30//1
						+","+diff50//1
						+","+diff120//1
						+","+range
						+inputsDiff
						;
				//System.out.println(dataStr);
				out.write(dataStr);
				out.newLine();				
			}
			out.close();
			
			int totaltrades = wins+losses;
			//System.out.println(totaltrades+" win% "+(wins*100.0/totaltrades));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("[doExtractXfromData] Error: " + e.getMessage());
		}					
}
	

	/**
	 * Se extraen las características de los datos planos y la clase a la que pertenece cada vector input,
	 * basandose en si se optienen pripero pipsTarget pips comprando o vendiendo
	 * @param maxMins 
	 * @param dataTrainRaw
	 * @param fileNameTrainPro
	 * @throws IOException 
	 */
	private static void doExtractXfromData4(
			ArrayList<QuoteShort> data,
			String fileName, 
			ArrayList<Integer> maxMins, 
			int pipsTarget,int pipsSL,
			int maxMinLimit,
			boolean isSell) {
		
		FileWriter fstream;
		try {
			fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			Calendar cal = Calendar.getInstance();
			int atr20 = 600;
			int high = -1;
			int low = -1;
			int lastHigh = -1;
			int lastLow = -1;
			int lastDay = -1;
			
			int wins = 0;
			int losses = 0;
			for (int i=20;i<data.size()-1;i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				int h	= cal.get(Calendar.HOUR_OF_DAY);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int maxMin = maxMins.get(i);
				
				if (maxMin>=maxMinLimit) maxMin = maxMinLimit;
				else if (maxMin<=-maxMinLimit) maxMin = -maxMinLimit;
				
				if (day!=lastDay){
					if (lastDay!=-1){
						lastHigh = high;
						lastLow = low;
					}
					high = -1;
					low = -1;
					lastDay = day;
				}
				
				
				if (high==-1 || q.getOpen5()>=high) high = q.getOpen5();
				if (low==-1 || q.getOpen5()<=low) low = q.getOpen5();
		
				
				double sma30 =  MathUtils.average(data, i-30, i, false);
				double diff30 = (int) (q.getOpen5()-sma30);
				double sma50 =  MathUtils.average(data, i-50, i, false);
				double diff50 = (int) (q.getOpen5()-sma50);
				double sma120 =  MathUtils.average(data, i-120, i, false);
				double diff120 = (int) (q.getOpen5()-sma120);
				
				//normalizamos..
				diff30 = diff30/600;
				diff50 = diff50/600;
				diff120 = diff120/600;
				
				int range = (high-low)/600;

				int labelf			 	= 0;
				int target	= q.getOpen5() + pipsTarget;
				int stop 	= q.getOpen5() - pipsSL;
				if (isSell){
					 target	= q.getOpen5() - pipsTarget;
					 stop 	= q.getOpen5() + pipsSL;
				}
				
				for (int j=i+1;j<data.size();j++){
					QuoteShort qj = data.get(j);
					
					if (!isSell){
						if (qj.getOpen5()>=target){
							labelf = 1;
							break;
						}else if (qj.getOpen5()<=stop){
							labelf = 0;
							break;
						}
					}else{
						if (qj.getOpen5()<=target){
							labelf = 1;
							break;
						}else if (qj.getOpen5()>=stop){
							labelf = 0;
							break;
						}
					}
				}
				
				double maxMinThr		= maxMin*1.0 / maxMinLimit;	
				String hourBinaryStr 	= getHourBinary(h);
				String dataStr	 		= labelf
						+","+maxMinThr// 1 representacion binaria de la hora del dia
						+","+hourBinaryStr//5
						+","+diff30//1
						+","+diff50//1
						+","+diff120//1
						+","+range
						;
				//System.out.println(dataStr);
				out.write(dataStr);
				out.newLine();				
			}
			out.close();
			
			int totaltrades = wins+losses;
			//System.out.println(totaltrades+" win% "+(wins*100.0/totaltrades));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("[doExtractXfromData] Error: " + e.getMessage());
		}					
}

	
	/**
	 * Se extraen las características de los datos planos y la clase a la que pertenece cada vector input,
	 * basandose en si se optienen pripero pipsTarget pips comprando o vendiendo
	 * @param maxMins 
	 * @param dataTrainRaw
	 * @param fileNameTrainPro
	 * @throws IOException 
	 */
	private static void doExtractXfromData2(
			ArrayList<QuoteShort> data,
			String fileName, ArrayList<Integer> maxMins, 
			int pipsTarget,int pipsSL,
			int maxMinThr1,
			boolean isSell) {
		
		FileWriter fstream;
		try {
			fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			Calendar cal = Calendar.getInstance();
			int atr20 = 600;
			int high = -1;
			int low = -1;
			int lastHigh = -1;
			int lastLow = -1;
			int lastDay = -1;
			
			int wins = 0;
			int losses = 0;
			for (int i=0;i<data.size();i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				int h	= cal.get(Calendar.HOUR_OF_DAY);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int maxMin = maxMins.get(i);
				
				if (day!=lastDay){
					if (lastDay!=-1){
						lastHigh = high;
						lastLow = low;
					}
					high = -1;
					low = -1;
					lastDay = day;
				}
				String tradeHourStr = getHourBinary(h);
				
				int sma20 = (int) MathUtils.average(data, i-20, i, false);
				int sma30 = (int) MathUtils.average(data, i-30, i, false);
				int sma40 = (int) MathUtils.average(data, i-40, i, false);
				int sma50 = (int) MathUtils.average(data, i-50, i, false);
				int sma60 = (int) MathUtils.average(data, i-60, i, false);
				
				//int diff50 = q.getOpen5()-sma50;
				double sma50val = 0;
				double sma60val = 0;
				sma50val = 0;
				int diff50 = q.getOpen5()-sma50;
				int diff20 = q.getOpen5()-sma20;
				int diff30 = q.getOpen5()-sma30;
				double diff50atr = diff50*1.0/atr20;
				double diff20atr = 0;
				double diff30atr = 0;
				int maxMinThr = 0;
				

				double upperThr = 0.0;
				double lowerThr = 0.0;
				if (diff50>=0){
					upperThr = diff50*1.0/atr20;
				}else{
					lowerThr = -diff50*1.0/atr20;
				}
				
				int diffHigh = q.getOpen5()-lastHigh;
				int diffLow = lastLow-q.getOpen5();
				
				int hval5 = 0;
				int lval5 = 0;
				int hval10 = 0;
				int lval10 = 0;
				int diffPips = 0;
				if (q.getOpen5()>=lastHigh+50){
					hval5 = 1;
					diffPips = diffHigh;					
				}
				if (q.getOpen5()<=lastLow-50){
					lval5 = 1;
					diffPips = diffLow;
				}
				if (q.getOpen5()>=lastHigh+100){
					hval10 = 1;					
				}
				if (q.getOpen5()<=lastLow-100){
					lval10 = 1;
				}
				
				int trade400 = -1;
				if (maxMin>=maxMinThr1){
					trade400 = 1;
					//System.out.println("new short "+q.toString());
				}else if (maxMin<=-maxMinThr1){
					trade400 = 0;
					//System.out.println("new long "+q.toString());
				}
				
				int rangeVal20=0;
				int rangeVal30=0;
				int rangeVal40=0;
				int rangeVal50=0;
				int range = high-low;
				if (high!=-1){
					if (range>=200) rangeVal20 = 1;
					else rangeVal20 = 0;
					if (range>=300) rangeVal30 = 1;
					else rangeVal20 = 0;
					if (range>=400) rangeVal40 = 1;
					else rangeVal20 = 0;
					if (range>=500) rangeVal50 = 1;
					else rangeVal20 = 0;
				}
				
				int trade = -1;
				if (trade400==1) trade = 1;
				else if (trade400==0) trade = 0; 
				
				if (trade>-1){
					//evaluamos target
					int label = 0;//por defecto NO sell
					boolean labelFound = false;					
					for (int j=i;j<data.size() || labelFound;j++){
						QuoteShort qj = data.get(j);						
						if (trade==1){
							int slValue = q.getOpen5()-pipsSL;
							int tpValue 	= q.getOpen5()+pipsTarget;
							if (qj.getHigh5()>=tpValue){
								label = 1;
								wins++;
								labelFound = true;
								break;
							}else if (qj.getLow5()<=slValue){
								label = 0;
								losses++;
								labelFound = true;
								break;
							}
						}else if (trade==0){
							int slValue = q.getOpen5()+pipsSL;
							int tpValue = q.getOpen5()-pipsTarget;
							if (qj.getHigh5()>=slValue){
								label = 0;
								losses++;
								labelFound = true;
								break;
							}else if (qj.getLow5()<=tpValue){
								label = 1;
								wins++;
								labelFound = true;
								break;
							}
						}
					}
			
					
					String dataStr = label
							+","+tradeHourStr// 5 representacion binaria de la hora del dia
							+","+trade400//1
							+","+hval5//1
							+","+lval5//1
							+","+hval10//1
							+","+lval10//1
							+","+rangeVal20//1
							+","+rangeVal30//1
							+","+rangeVal40//1
							+","+rangeVal50//1
							;
					
					out.write(dataStr);
					out.newLine();
				}
				
				if (high==-1 || q.getOpen5()>=high) high = q.getOpen5();
				if (low==-1 || q.getOpen5()<=low) low = q.getOpen5();
			}
			out.close();
			
			int totaltrades = wins+losses;
			//System.out.println(totaltrades+" win% "+(wins*100.0/totaltrades));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("[doExtractXfromData] Error: " + e.getMessage());
		}
		
		
		
}
	
	/**
	 * Se extraen las características de los datos planos y la clase a la que pertenece cada vector input,
	 * basandose en si se optienen pripero pipsTarget pips comprando o vendiendo
	 * @param maxMins 
	 * @param dataTrainRaw
	 * @param fileNameTrainPro
	 * @throws IOException 
	 */
	private static void doExtractXfromData3(
			ArrayList<QuoteShort> data,
			String fileName) {
		
		FileWriter fstream;
		try {
			fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			Calendar cal = Calendar.getInstance();
			int atr20 = 600;
			int high = -1;
			int low = -1;
			int lastHigh = -1;
			int lastLow = -1;
			int lastDay = -1;
			
			int wins = 0;
			int losses = 0;
			for (int i=20;i<data.size()-20;i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				int h	= cal.get(Calendar.HOUR_OF_DAY);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				
				if (day!=lastDay){
					if (lastDay!=-1){
						lastHigh = high;
						lastLow = low;
					}
					high = -1;
					low = -1;
					lastDay = day;
				}
				
				//if (h==0) continue;
				
				int divisor = 200000;
				double labelf = data.get(i+0).getOpen5()*1.0/divisor;//q.getOpen5()*1.0/divisor;	
				String hourBinaryStr 	= getHourBinary(h);
				String dataStr = labelf
						+","+data.get(i-1).getOpen5()*1.0/divisor// 5 representacion binaria de la hora del dia
						+","+data.get(i-2).getOpen5()*1.0/divisor//1
						+","+data.get(i-3).getOpen5()*1.0/divisor//1
						+","+data.get(i-4).getOpen5()*1.0/divisor//1
						+","+data.get(i-5).getOpen5()*1.0/divisor//1
						+","+data.get(i-6).getOpen5()*1.0/divisor// 5 representacion binaria de la hora del dia
						+","+data.get(i-7).getOpen5()*1.0/divisor//1
						+","+data.get(i-8).getOpen5()*1.0/divisor//1
						+","+data.get(i-9).getOpen5()*1.0/divisor//1
						+","+data.get(i-10).getOpen5()*1.0/divisor//1
						+","+data.get(i-11).getOpen5()*1.0/divisor// 5 representacion binaria de la hora del dia
						+","+data.get(i-12).getOpen5()*1.0/divisor//1
						+","+data.get(i-13).getOpen5()*1.0/divisor//1
						+","+data.get(i-14).getOpen5()*1.0/divisor//1
						+","+data.get(i-15).getOpen5()*1.0/divisor//1
						+","+data.get(i-16).getOpen5()*1.0/divisor// 5 representacion binaria de la hora del dia
						+","+data.get(i-17).getOpen5()*1.0/divisor//1
						+","+data.get(i-18).getOpen5()*1.0/divisor//1
						+","+data.get(i-19).getOpen5()*1.0/divisor//1
						+","+data.get(i-20).getOpen5()*1.0/divisor//1
						+","+hourBinaryStr 
						;
				//System.out.println(dataStr);
				out.write(dataStr);
				out.newLine();				
			}
			out.close();
			
			int totaltrades = wins+losses;
			//System.out.println(totaltrades+" win% "+(wins*100.0/totaltrades));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("[doExtractXfromData] Error: " + e.getMessage());
		}
		
		
		
}
	
	/**
	 * Convierte
	 * @param h
	 * @return
	 */
	private static String getHourBinary(int h) {
		// TODO Auto-generated method stub
		String res = "0,0,0,0,0";
		if (h==1) return "0,0,0,0,1";
		if (h==2) return "0,0,0,1,0";
		if (h==3) return "0,0,0,1,1";
		if (h==4) return "0,0,1,0,0";
		if (h==5) return "0,0,1,0,1";
		if (h==6) return "0,0,1,1,0";
		if (h==7) return "0,0,1,1,1";
		if (h==8) return "0,1,0,0,0";
		if (h==9) return "0,1,0,0,1";
		if (h==10) return "0,1,0,1,0";
		if (h==11) return "0,1,0,1,1";
		if (h==12) return "0,1,1,0,0";
		if (h==13) return "0,1,1,0,1";
		if (h==14) return "0,1,1,1,0";
		if (h==15) return "0,1,1,1,1";
		if (h==16) return "1,0,0,0,0";
		if (h==17) return "1,0,0,0,1";
		if (h==18) return "1,0,0,1,0";
		if (h==19) return "1,0,0,1,1";
		if (h==20) return "1,0,1,0,0";
		if (h==21) return "1,0,1,0,1";
		if (h==22) return "1,0,1,1,0";
		if (h==23) return "1,0,1,1,1";
				
		return res;
	}


	/**
	 * Aquí creamos el modelo a entrenar y evaluar posteriormente
	 * @param numLayers 
	 * @param numOutputs 
	 * @param numHiddenNodes 
	 * @param numInputs 
	 * @return 
	 */
	private static MultiLayerNetwork buildModel(
			int numInputs, 
			int numHiddenNodes, 
			int numOutputs, 
			int numLayers,
			double learningRate,
			double momentumRate,
			Activation hiddenActivation,
			Activation finalActivation,
			LossFunction lossFunction
			) {
		
		MultiLayerNetwork model = null;
		MultiLayerConfiguration conf = null;
		
		
		if (numLayers==1){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, momentumRate))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())	         
	        .layer(new OutputLayer.Builder(lossFunction)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(finalActivation)
	            .build())
	        .build();
		}
		
		if (numLayers==2){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate,momentumRate))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new DenseLayer.Builder()
		            .nIn(numHiddenNodes)
		            .nOut(numHiddenNodes)
		            .activation(hiddenActivation)
		            .build())
	        .layer(new OutputLayer.Builder(lossFunction)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(finalActivation)
	            .build())
	        .build();
		}
		if (numLayers==3){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate,momentumRate))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new DenseLayer.Builder()
		            .nIn(numHiddenNodes)
		            .nOut(numHiddenNodes)
		            .activation(hiddenActivation)
		            .build())
	        .layer(new DenseLayer.Builder()
		            .nIn(numHiddenNodes)
		            .nOut(numHiddenNodes)
		            .activation(hiddenActivation)
		            .build())
	        .layer(new OutputLayer.Builder(lossFunction)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(finalActivation)
	            .build())
	        .build();
		}
		if (numLayers==4){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate,momentumRate))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new DenseLayer.Builder()
		            .nIn(numHiddenNodes)
		            .nOut(numHiddenNodes)
		            .activation(hiddenActivation)
		            .build())
	        .layer(new DenseLayer.Builder()
		            .nIn(numHiddenNodes)
		            .nOut(numHiddenNodes)
		            .activation(hiddenActivation)
		            .build())
	        .layer(new OutputLayer.Builder(lossFunction)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(finalActivation)
	            .build())
	        .build();
		}
		
		if (numLayers==5){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate,momentumRate))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	         .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(hiddenActivation)
	            .build())
	        .layer(new OutputLayer.Builder(lossFunction)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(finalActivation)
	            .build())
	        .build();
		}
		model = new MultiLayerNetwork(conf);
		
		return model;
	}
	
	private static void doTrain(MultiLayerNetwork model,DataSetIterator trainIter, int nEpochs) {
		// TODO Auto-generated method stub
		
	}

	private static void doEvaluate(MultiLayerNetwork model,DataSetIterator dataSet) {
		// TODO Auto-generated method stub
		Evaluation eval = model.evaluate(dataSet);
        System.out.println(eval.stats());
	}
	
	 private static String format(DecimalFormat f, double num) {
	        if (Double.isNaN(num) || Double.isInfinite(num))
	            return String.valueOf(num);
	        return f.format(num);
	    }
	 
	 /**
	  * Test de Regresion, intenta predecir el incremento 
	  * @param fileNameTrainPro
	  * @param fileNameTestPro
	  * @param dataTrainRaw
	  * @param dataTrainTest
	  * @param maxMinsRaw
	  * @param maxMinsTest
	  * @throws IOException
	  * @throws InterruptedException
	  */
	 public static void doTestAlgo2(
			 String fileNameTrainPro,
			 String fileNameTestPro,
			 ArrayList<QuoteShort> dataTrainRaw,
			 ArrayList<QuoteShort> dataTrainTest,
			 ArrayList<Integer> maxMinsRaw,
			 ArrayList<Integer> maxMinsTest
			 ) throws IOException, InterruptedException{
		
		 DecimalFormat df = new DecimalFormat("0.0000"); 
		 
		int limit		= 500;
		int numOutputs 	= 1;
		int numInputs 	= 20;//4:10,4b:20
		double momentumRate = 0.50;
		
		for (int pipsTarget = 300;pipsTarget<=500;pipsTarget+=50){ 
        	for (int factorSl=1;factorSl<=1;factorSl+=1){
	        	int pipsSL = factorSl*pipsTarget;
	        	//preprocesamiento calculando indicadores del dataset
		  		doExtractXfromData3(dataTrainRaw,fileNameTrainPro);
		  		doExtractXfromData3(dataTrainTest,fileNameTestPro);
		  		
		  		for (int numHiddenNodes = 5;numHiddenNodes<=80;numHiddenNodes+=5){
		        	for (int numLayers =1;numLayers<=1;numLayers+=1){
		        		for (int batchSize=160;batchSize<=160;batchSize+=8){
		        			for (int nEpochs=100;nEpochs<=100;nEpochs+=1){
		        				for (double learningRate=0.01;learningRate<=0.01;learningRate+=0.010){		        					
		        					for (int maxSeconds = 120;maxSeconds<=120;maxSeconds+=60){	
		        						for (double stratThr=0.50;stratThr<=0.50;stratThr+=0.01){
			        						//1.1) TRAIN
									        RecordReader rr = new CSVRecordReader();
									        rr.initialize(new FileSplit(new File(fileNameTrainPro)));
									        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,1);
									        
									        //1.2) TEST
									        RecordReader rrTest = new CSVRecordReader();
									        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
									        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
									        
									       //2) CONSTRUIMOS EL MODELO
									        MultiLayerNetwork model = buildModel(numInputs,numHiddenNodes,
									        		numOutputs,numLayers,learningRate,momentumRate,
									        		Activation.SIGMOID,Activation.RELU,LossFunction.MEAN_ABSOLUTE_ERROR);			       
									        model.init();
									        
									        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
									                .epochTerminationConditions(new MaxEpochsTerminationCondition(10000), 
									                		new ScoreImprovementEpochTerminationCondition(5)) //Max of 50 epochs
									                .evaluateEveryNEpochs(1)
									                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxSeconds, TimeUnit.SECONDS)) //Max of 20 minutes
									                .scoreCalculator(new DataSetLossCalculator(testIter, true))     //Calculate test set score
									                //.scoreCalculator(new RegressionScoreCalculator(Metric.MSE, testIter))//para regresion
									                .build();
	
									        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf,model,trainIter);
	
									        //Conduct early stopping training:
									        EarlyStoppingResult result = trainer.fit();
									        
									        MultiLayerNetwork bestModel =(MultiLayerNetwork) result.getBestModel();
									        
									        if (result.getBestModelEpoch()<0) continue;
									        
										       int wins = 0;
										        int losses = 0;
										        testIter.reset();
										        while(testIter.hasNext()){
										            //DataSet t = testIter.next();
										            DataSet t = testIter.next();
										            
										            INDArray features	= t.getFeatures();
										            INDArray labels 	= t.getLabels();
										            INDArray predicted	= bestModel.output(features,false);
										            //comparar SIGNO predicho con signo real
										            for (int b=1;b<=features.rows();b++){
											            for (int l=1;l<=1;l++){
											            	INDArray fr = features.getRow(b-1);
											            	INDArray lr = labels.getRow(b-1);
											            	INDArray pr = predicted.getRow(b-1);
											            	
											            	double realN_1		= fr.getDouble(0);
											            	double realN 		= lr.getDouble(0);
											            	double pred			= pr.getDouble(0);

											            	if (pred>=realN_1){//predecimos que sube
											            		if (realN>=realN_1){
											            			wins++;
											            		}else{
											            			losses++;
											            		}
											            	}else{//predecimos que baja
											            		if (realN<realN_1){
											            			wins++;
											            		}else{
											            			losses++;
											            		}
											            	}									            										            	
											            	//System.out.println(predInt+" "+realInt);
											            }
										            }
										        }
										        int trades = wins+losses;
										        System.out.println(
										        		pipsTarget+";"+pipsSL
										        		+";"+numHiddenNodes+";"+numLayers+";"+batchSize
										        		+";"+result.getTotalEpochs()
										        		+";"+result.getBestModelEpoch()
										        		+";"+format(df,result.getBestModelScore())
										        		+";"+trades+";"+format(df,wins*100.0/trades)
										        		+";"+format(df,wins*pipsTarget*1.0/(losses*pipsSL))
										        		+";"+result.getTerminationReason()
										        		);
		        						}//stratTHR
									        
		        					}//maxSeconds
		        				}//learningRate
		        			}//nEpochs
		        		}//batchsize
		        	}//numLayers
		  		}//numHiddem
        	}//factor SL
		}		
	 }
	 
	 /**
	  * Test de Regresion, obtencion del mismo signo con 20 precios anteriores y dia
	  * @param fileNameTrainPro
	  * @param fileNameTestPro
	  * @param dataTrainRaw
	  * @param dataTrainTest
	  * @param maxMinsRaw
	  * @param maxMinsTest
	  * @throws IOException
	  * @throws InterruptedException
	  */
	 public static void doTestAlgo3(
			 String fileNameTrainPro,
			 String fileNameTestPro,
			 ArrayList<QuoteShort> dataTrainRaw,
			 ArrayList<QuoteShort> dataTrainTest,
			 ArrayList<Integer> maxMinsRaw,
			 ArrayList<Integer> maxMinsTest
			 ) throws IOException, InterruptedException{
		
		 DecimalFormat df = new DecimalFormat("0.0000"); 
		 
		int limit		= 500;
		int numOutputs 	= 1;
		int numInputs 	= 25;//4:10,4b:20
		double momentumRate = 0.50;
		
		for (int pipsTarget = 50;pipsTarget<=300;pipsTarget+=50){ 
        	for (int factorSl=1;factorSl<=1;factorSl+=1){
	        	int pipsSL = factorSl*pipsTarget;
	        	//preprocesamiento calculando indicadores del dataset
		  		doExtractXfromData3(dataTrainRaw,fileNameTrainPro);
		  		doExtractXfromData3(dataTrainTest,fileNameTestPro);
		  		
		  		for (int numHiddenNodes = 10;numHiddenNodes<=10;numHiddenNodes+=5){
		        	for (int numLayers =5;numLayers<=5;numLayers+=1){
		        		for (int batchSize=64;batchSize<=64;batchSize+=8){
		        			for (int nEpochs=100;nEpochs<=100;nEpochs+=1){
		        				for (double learningRate=0.01;learningRate<=0.01;learningRate+=0.01){		        					
		        					for (int maxSeconds = 120;maxSeconds<=120;maxSeconds+=60){	
		        						for (double stratThr=0.005;stratThr<=0.005;stratThr+=0.01){
			        						//1.1) TRAIN
									        RecordReader rr = new CSVRecordReader();
									        rr.initialize(new FileSplit(new File(fileNameTrainPro)));
									        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,1);
									        
									        //1.2) TEST
									        RecordReader rrTest = new CSVRecordReader();
									        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
									        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
									        
									       //2) CONSTRUIMOS EL MODELO
									        MultiLayerNetwork model = buildModel(numInputs,numHiddenNodes,
									        		numOutputs,numLayers,learningRate,momentumRate,
									        		Activation.SIGMOID,Activation.RELU,LossFunction.MSE);			       
									        model.init();
									        
									        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
									                .epochTerminationConditions(new MaxEpochsTerminationCondition(10000), 
									                		new ScoreImprovementEpochTerminationCondition(5)) //Max of 50 epochs
									                .evaluateEveryNEpochs(1)
									                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxSeconds, TimeUnit.SECONDS)) //Max of 20 minutes
									                .scoreCalculator(new DataSetLossCalculator(testIter, true))     //Calculate test set score
									                //.scoreCalculator(new RegressionScoreCalculator(Metric.MSE, testIter))//para regresion
									                .build();
	
									        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf,model,trainIter);
	
									        //Conduct early stopping training:
									        EarlyStoppingResult result = trainer.fit();
									        
									        MultiLayerNetwork bestModel =(MultiLayerNetwork) result.getBestModel();
									        
									        if (result.getBestModelEpoch()<0) continue;
									        
										       int wins = 0;
										        int losses = 0;
										        testIter.reset();
										        while(testIter.hasNext()){
										            //DataSet t = testIter.next();
										            DataSet t = testIter.next();
										            
										            INDArray features	= t.getFeatures();
										            INDArray labels 	= t.getLabels();
										            INDArray predicted	= bestModel.output(features,false);
										            //comparar SIGNO predicho con signo real
										            for (int b=1;b<=features.rows();b++){
											            for (int l=1;l<=1;l++){
											            	INDArray fr = features.getRow(b-1);
											            	INDArray lr = labels.getRow(b-1);
											            	INDArray pr = predicted.getRow(b-1);
											            	
											            	double realN_1		= fr.getDouble(0);
											            	double realN 		= lr.getDouble(0);
											            	double pred			= pr.getDouble(0);

											            	if (pred>=realN_1){//predecimos que sube
											            		if (realN>=realN_1){
											            			wins++;
											            		}else{
											            			losses++;
											            		}
											            	}else{//predecimos que baja
											            		if (realN<realN_1){
											            			wins++;
											            		}else{
											            			losses++;
											            		}
											            	}									            										            	
											            	//System.out.println(predInt+" "+realInt);
											            }
										            }
										        }
										        int trades = wins+losses;
										        System.out.println(
										        		pipsTarget+";"+pipsSL
										        		+";"+numHiddenNodes+";"+numLayers+";"+batchSize
										        		+";"+result.getTotalEpochs()
										        		+";"+result.getBestModelEpoch()
										        		+";"+format(df,result.getBestModelScore())
										        		+";"+trades+";"+format(df,wins*100.0/trades)
										        		+";"+format(df,wins*pipsTarget*1.0/(losses*pipsSL))
										        		+";"+result.getTerminationReason()
										        		);
		        						}//stratTHR
									        
		        					}//maxSeconds
		        				}//learningRate
		        			}//nEpochs
		        		}//batchsize
		        	}//numLayers
		  		}//numHiddem
        	}//factor SL
		}		
	 }
	 
	 /**
	  * Classification Test. 1: se alcanza el objetivo de compra o venta. 0 : no se alcanza el objetivo
	  * @param fileNameTrainPro
	  * @param fileNameTestPro
	  * @param dataTrainRaw
	  * @param dataTrainTest
	  * @param maxMinsRaw
	  * @param maxMinsTest
	  * @throws IOException
	  * @throws InterruptedException
	  */
	 public static void doTestAlgo4(
			 String fileNameTrainPro,
			 String fileNameTestPro,
			 ArrayList<QuoteShort> dataTrainRaw,
			 ArrayList<QuoteShort> dataTrainTest,
			 ArrayList<Integer> maxMinsRaw,
			 ArrayList<Integer> maxMinsTest
			 ) throws IOException, InterruptedException{
		
		 DecimalFormat df = new DecimalFormat("0.0000"); 
		 
		int limit		= 500;
		int numOutputs 	= 1;
		int numInputs 	= 30;//4:10,4b:20
		double momentumRate = 0.50;
		
		for (int pipsTarget = 50;pipsTarget<=1000;pipsTarget+=50){ 
        	for (int factorSl=1;factorSl<=1;factorSl+=1){
	        	int pipsSL = factorSl*pipsTarget;
	        	
	        	//preprocesamiento calculando indicadores del dataset
		  		doExtractXfromData4b(dataTrainRaw,fileNameTrainPro,maxMinsRaw,pipsTarget,pipsSL,limit,false);
		  		doExtractXfromData4b(dataTrainTest,fileNameTestPro,maxMinsTest,pipsTarget,pipsSL,limit,false);
		  		
		  		for (int numHiddenNodes = 15;numHiddenNodes<=15;numHiddenNodes+=1){
		        	for (int numLayers =2;numLayers<=2;numLayers+=1){
		        		for (int batchSize=128;batchSize<=128;batchSize+=8){
		        			for (int nEpochs=100;nEpochs<=100;nEpochs+=1){
		        				for (double learningRate=0.02;learningRate<=0.02;learningRate+=0.010){		        					
		        					for (int maxSeconds = 240;maxSeconds<=240;maxSeconds+=60){	
		        						for (double stratThr=0.50;stratThr<=0.50;stratThr+=0.01){
			        						//1.1) TRAIN
									        RecordReader rr = new CSVRecordReader();
									        rr.initialize(new FileSplit(new File(fileNameTrainPro)));
									        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,1);
									        
									        //1.2) TEST
									        RecordReader rrTest = new CSVRecordReader();
									        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
									        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
									        
									       //2) CONSTRUIMOS EL MODELO
									        MultiLayerNetwork model = buildModel(numInputs,numHiddenNodes,
									        		numOutputs,numLayers,learningRate,momentumRate,
									        		Activation.RELU,Activation.SIGMOID,LossFunction.XENT);			       
									        model.init();
									        
									        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
									                .epochTerminationConditions(new MaxEpochsTerminationCondition(10000), 
									                		new ScoreImprovementEpochTerminationCondition(5)) //Max of 50 epochs
									                .evaluateEveryNEpochs(1)
									                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxSeconds, TimeUnit.SECONDS)) //Max of 20 minutes
									                .scoreCalculator(new DataSetLossCalculator(testIter, true))     //Calculate test set score
									                //.scoreCalculator(new RegressionScoreCalculator(Metric.MSE, testIter))//para regresion
									                .build();
	
									        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf,model,trainIter);
	
									        //Conduct early stopping training:
									        EarlyStoppingResult result = trainer.fit();
									       // System.out.println("Termination reason: " + result.getTerminationReason());
									      //  System.out.println("Termination details: " + result.getTerminationDetails());
									        //System.out.println("Total epochs: " + result.getTotalEpochs());
									        //System.out.println("Best epoch number: " + result.getBestModelEpoch());
									        //System.out.println("Score at best epoch: " + result.getBestModelScore());
									        
									        MultiLayerNetwork bestModel =(MultiLayerNetwork) result.getBestModel();
									        
									        if (result.getBestModelEpoch()<0) continue;
									        
										       int wins = 0;
										        int losses = 0;
										        testIter.reset();
										        while(testIter.hasNext()){
										            //DataSet t = testIter.next();
										            DataSet t = testIter.next();
										            
										            INDArray features	= t.getFeatures();
										            INDArray labels 	= t.getLabels();
										            INDArray predicted	= bestModel.output(features,false);
										            //comparar SIGNO predicho con signo real
										            for (int b=1;b<=features.rows();b++){
											            for (int l=1;l<=1;l++){
											            	INDArray fr = features.getRow(b-1);
											            	INDArray lr = labels.getRow(b-1);
											            	INDArray pr = predicted.getRow(b-1);
											            	
											            	double realN_1		= fr.getDouble(0);
											            	double realN 		= lr.getDouble(0);
											            	double pred			= pr.getDouble(0);
											            	
											            	int realInt = (int) lr.getDouble(0);
											            	int predInt = 0;
											            	if (pred>stratThr) predInt = 1;
											            	
											            	if (predInt==1){
											            		if (predInt == realInt){
											            			wins++;
											            		}else{
											            			losses++;
											            		}
											            	}									            										            	
											            	//System.out.println(predInt+" "+realInt);
											            }
										            }
										        }
										        int trades = wins+losses;
										        System.out.println(
										        		pipsTarget+";"+pipsSL
										        		+";"+numHiddenNodes+";"+numLayers+";"+batchSize
										        		+";"+stratThr
										        		+";"+result.getTotalEpochs()
										        		+";"+result.getBestModelEpoch()
										        		+";"+format(df,result.getBestModelScore())
										        		+";"+trades+";"+format(df,wins*100.0/trades)
										        		+";"+format(df,wins*pipsTarget*1.0/(losses*pipsSL))
										        		+";"+result.getTerminationReason()
										        		);
		        						}//stratTHR
									        
		        					}//maxSeconds
		        				}//learningRate
		        			}//nEpochs
		        		}//batchsize
		        	}//numLayers
		  		}//numHiddem
        	}//factor SL
		}		
	 }

	public static void main(String[] args) throws IOException, InterruptedException {
		
		 //Initialize the user interface backend
	 // UIServer uiServer = UIServer.getInstance();
	    //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
	 // StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
	  //Configure where the network information (gradients, activations, score vs. time etc) is to be stored
      //Then add the StatsListener to collect this information from the network, as it trains
      //StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
      //int listenerFrequency = 1;
	  
	    //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
	   //uiServer.attach(statsStorage);
	   //setListeners(new StatsListener(statsStorage, listenerFrequency));
		
		//CudaEnvironment.getInstance().getConfiguration().allowMultiGPU(true);
		
		//lectura de datos financieros
		//habra que partir los datos en varios años para probar
		
		//15min
		/*String fileNameTrainRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2017.01.01_2019.02.28.csv";
		String fileNameTestRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.03.01_2019.08.25.csv";
		String fileNameTrainPro15 = "C:\\fxdata\\EURUSD_15 Mins_Bid_2017.01.01_2019.02.28_pro.csv";
		String fileNameTestPro15  = "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.03.01_2019.08.25_pro.csv";*/
		
		//String fileNameTrainRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2018.12.31.csv";
		//String fileNameTestRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.01.01_2019.08.25.csv";
		//String fileNameTrainPro15 = "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2018.12.31_pro.csv";
		//String fileNameTestPro15  = "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.01.01_2019.08.25_pro.csv";
		
		String fileNameTrainRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2014.12.31.csv";
		String fileNameTestRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2015.01.01_2015.12.31.csv";
		String fileNameTrainPro15 = "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2014.12.31_pro.csv";
		String fileNameTestPro15  = "C:\\fxdata\\EURUSD_15 Mins_Bid_2015.01.01_2015.12.31_pro.csv";
				
		boolean is15m = true;
		
		String fileNameTrainRaw	= fileNameTrainRaw15;
		String fileNameTestRaw	= fileNameTestRaw15;
		String fileNameTrainPro = fileNameTrainPro15;
		String fileNameTestPro  = fileNameTestPro15;

		
		ArrayList<QuoteShort> dataTrainRaw = new ArrayList<QuoteShort>();
		ArrayList<QuoteShort> dataTestRaw = new ArrayList<QuoteShort>();
		
		dataTrainRaw	= readData(fileNameTrainRaw);
		dataTestRaw 	= readData(fileNameTestRaw);
		
		ArrayList<Integer> maxMinsRaw = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataTrainRaw);
		ArrayList<Integer> maxMinsTest = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataTestRaw);
		
		System.out.println("Leido raw data,tamaños datos leidos: "+dataTrainRaw.size()+" "+dataTestRaw.size());
		
		//EXPERIMENTOS
		
		//experimento de clasificación
		Experiment4.doTestAlgo(fileNameTrainPro, fileNameTestPro, dataTrainRaw, dataTestRaw, maxMinsRaw, maxMinsTest);
		//Experiment1.doTestAlgo(fileNameTrainPro, fileNameTestPro, dataTrainRaw, dataTestRaw, maxMinsRaw, maxMinsTest);
		
		//regresion
		//Experiment3.doTestAlgo(fileNameTrainPro, fileNameTestPro, dataTrainRaw, dataTestRaw, maxMinsRaw, maxMinsTest);

		
		
		System.out.println("Programa finalizado");
	}



}
