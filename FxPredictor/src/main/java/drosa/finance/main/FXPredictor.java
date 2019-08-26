package drosa.finance.main;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
//import org.deeplearning4j.ui.api.UIServer;
//import org.deeplearning4j.ui.stats.StatsListener;
//import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
//import org.nd4j.jita.conf.CudaEnvironment;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;




import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import drosa.finance.classes.DAO;
import drosa.finance.classes.QuoteShort;
import drosa.finance.classes.TestLines;
import drosa.finance.types.DataProvider;
import drosa.finance.utils.MathUtils;
import drosa.finance.utils.ModelListener;
import drosa.finance.utils.TradingUtils;


public class FXPredictor {
	
	
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
			double learningRate
			) {
		
		MultiLayerNetwork model = null;
		MultiLayerConfiguration conf = null;
		
		if (numLayers==1){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(99975)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==2){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==3){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==4){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	         .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==5){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	         .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder()
	            .nIn(numHiddenNodes)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==6){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())	        
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==7){
				conf = new NeuralNetConfiguration.Builder()
		        .seed(123)
		        .weightInit(WeightInit.XAVIER)
		        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
		        .updater(new Nesterovs(learningRate, 0.9))
		        .list()
		        .layer(new DenseLayer.Builder()
		            .nIn(numInputs)
		            .nOut(numHiddenNodes)
		            .activation(Activation.RELU)
		            .build())
		        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
		        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
		        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
		        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
		        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
		        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
		        .layer(new OutputLayer.Builder(LossFunction.XENT)
		            .nIn(numHiddenNodes)
		            .nOut(numOutputs)
		            .activation(Activation.SIGMOID)
		            .build())
		        .build();
		}else if (numLayers==8){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==9){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==10){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())	        
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==15){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())	
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
	            .build())
	        .build();
		}else if (numLayers==20){
			conf = new NeuralNetConfiguration.Builder()
	        .seed(123)
	        .weightInit(WeightInit.XAVIER)
	        .updater(new Nesterovs(learningRate, 0.9))
	        .list()
	        .layer(new DenseLayer.Builder()
	            .nIn(numInputs)
	            .nOut(numHiddenNodes)
	            .activation(Activation.RELU)
	            .build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())
	        .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes).activation(Activation.RELU).build())	        
	        .layer(new OutputLayer.Builder(LossFunction.XENT)
	            .nIn(numHiddenNodes)
	            .nOut(numOutputs)
	            .activation(Activation.SIGMOID)
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

	public static void main(String[] args) throws IOException, InterruptedException {
		
		 //Initialize the user interface backend
	  //UIServer uiServer = UIServer.getInstance();
	    //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
	  //StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
	    //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
	   //uiServer.attach(statsStorage);
		
		//CudaEnvironment.getInstance().getConfiguration().allowMultiGPU(true);
		
		//lectura de datos financieros
		//habra que partir los datos en varios años para probar
		
		//5min
		//String fileNameTrainRaw5	= "C:\\fxdata\\EURUSD_5 Mins_Bid_2009.01.01_2017.12.31.csv";
		//String fileNameTestRaw5		= "C:\\fxdata\\EURUSD_5 Mins_Bid_2018.01.01_2019.08.25.csv";
		//String fileNameTrainPro5 	= "C:\\fxdata\\EURUSD_5 Mins_Bid_2009.01.01_2017.12.31_pro.csv";
		//String fileNameTestPro5  	= "C:\\fxdata\\EURUSD_5 Mins_Bid_2018.01.01_2019.08.25_pro.csv";
		
		String fileNameTrainRaw5	= "C:\\fxdata\\EURUSD_5 Mins_Bid_2017.01.01_2019.02.28.csv";
		String fileNameTestRaw5		= "C:\\fxdata\\EURUSD_5 Mins_Bid_2019.03.01_2019.08.25.csv";
		String fileNameTrainPro5 	= "C:\\fxdata\\EURUSD_5 Mins_Bid_2017.01.01_2019.02.28_pro.csv";
		String fileNameTestPro5  	= "C:\\fxdata\\EURUSD_5 Mins_Bid_2019.03.01_2019.08.25_pro.csv";
		
		//15min
		//String fileNameTrainRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2017.01.01_2019.02.28.csv";
		//String fileNameTestRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.03.01_2019.08.25.csv";
		//String fileNameTrainPro15 = "C:\\fxdata\\EURUSD_15 Mins_Bid_2017.01.01_2018.02.28_pro.csv";
		//String fileNameTestPro15  = "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.03.01_2019.08.25_pro.csv";
		
		//15min
		String fileNameTrainRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2018.12.31.csv";
		String fileNameTestRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.01.01_2019.08.25.csv";
		String fileNameTrainPro15 = "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2018.12.31_pro.csv";
		String fileNameTestPro15  = "C:\\fxdata\\EURUSD_15 Mins_Bid_2019.01.01_2019.08.25_pro.csv";
				
		boolean is15m = true;
		
		String fileNameTrainRaw	= fileNameTrainRaw15;
		String fileNameTestRaw	= fileNameTestRaw15;
		String fileNameTrainPro = fileNameTrainPro15;
		String fileNameTestPro  = fileNameTestPro15;
		
		if (!is15m){
			fileNameTrainRaw	= fileNameTrainRaw5;
			fileNameTestRaw	= fileNameTestRaw5;
			fileNameTrainPro = fileNameTrainPro5;
			fileNameTestPro  = fileNameTestPro5;
		}
		
		ArrayList<QuoteShort> dataTrainRaw = new ArrayList<QuoteShort>();
		ArrayList<QuoteShort> dataTrainTest = new ArrayList<QuoteShort>();
		
		dataTrainRaw	= readData(fileNameTrainRaw);
		dataTrainTest 	= readData(fileNameTestRaw);
		
		ArrayList<Integer> maxMinsRaw = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataTrainRaw);
		ArrayList<Integer> maxMinsTest = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataTrainRaw);
		
		System.out.println("Leido raw data,tamaños datos leidos: "+dataTrainRaw.size()+" "+dataTrainTest.size());
				
				
		//hiperparámetros
		int seed			= 123;
        double learningRate	= 0.010;
        int batchSize 		= 3;
        int nEpochs 		= 5;
        int numInputs 		= 14;
        int numOutputs 		= 1;
        int numHiddenNodes 	= 5;
        int pipsTarget		= 500;
        
        //si el batchsize es grande.. entonces se necesitan mas epochs para 
        //en cambio con un batchsize mas pequeño, se necesitan menos epochs..
        
        //tablas : 
        //hiddenNodes-accuracy, probar caso de 1 layer
        //numLayers-accuracy
        
        for (pipsTarget = 150;pipsTarget<=150;pipsTarget+=50){ 
        	for (int factorSl=3;factorSl<=3;factorSl+=1){
	        	int pipsSL = factorSl*pipsTarget;
	        	for (int maxMinThr1=700;maxMinThr1<=700;maxMinThr1+=50){  
		        	 //preprocesamiento calculando indicadores del dataset
			  		doExtractXfromData2(dataTrainRaw,fileNameTrainPro,maxMinsRaw,pipsTarget,pipsSL,maxMinThr1,true);
			  		doExtractXfromData2(dataTrainTest,fileNameTestPro,maxMinsTest,pipsTarget,pipsSL,maxMinThr1,true);
			  			  		
		        	for (numHiddenNodes = 10;numHiddenNodes<=10;numHiddenNodes+=5){
			        	for (int numLayers = 10;numLayers<=10;numLayers+=1){
			        		for (batchSize=32;batchSize<=256;batchSize+=32){
			        			for (nEpochs=1;nEpochs<=1;nEpochs+=1){
			        				for (learningRate=0.010;learningRate<=0.010;learningRate+=0.010){
								       
										//1) obtenemos los dataset de los datos preprocesados	
								        //1.1) TRAIN
								        RecordReader rr = new CSVRecordReader();
								        rr.initialize(new FileSplit(new File(fileNameTrainPro)));
								        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,1);
								        
								        //1.2) TEST
								        RecordReader rrTest = new CSVRecordReader();
								        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
								        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
								        
								        //Normalize the training data
								        DataNormalization normalizer = new NormalizerStandardize();
								        normalizer.fit(trainIter);              //Collect training data statistics
								        trainIter.reset();
								        //Use previously collected statistics to normalize on-the-fly. Each DataSet returned by 'trainData' iterator will be normalized
								        trainIter.setPreProcessor(normalizer);
								        
								      //Normalize the training data
								       // DataNormalization normalizer = new NormalizerStandardize();
								        normalizer.fit(testIter);              //Collect training data statistics
								        testIter.reset();
								        //Use previously collected statistics to normalize on-the-fly. Each DataSet returned by 'trainData' iterator will be normalized
								        testIter.setPreProcessor(normalizer);
								        
								        //2) CONSTRUIMOS EL MODELO
								        MultiLayerNetwork model = buildModel(numInputs,numHiddenNodes,numOutputs,numLayers,learningRate);			       
								        model.init();
								        
								      // model.setListeners(new StatsListener(statsStorage));
									
								        //3) APLICAMOS MODELO AL CONJUNTO DE ENTRENAMIENTO
								        for ( int n = 0; n < nEpochs; n++) {
								        	//System.out.println("Epoch.."+n);
								            model.fit(trainIter);
								        }
											
								       // System.out.println("Evaluate model....");
								        Evaluation eval = new Evaluation(numOutputs);
								        while(testIter.hasNext()){
								            DataSet t = testIter.next();
								            INDArray features = t.getFeatures();
								            INDArray labels = t.getLabels();
								            INDArray predicted = model.output(features,false);
								            eval.eval(labels, predicted);
								        }
											      
								        //Print the evaluation statistics
								        //System.out.println(eval.stats());
									        
								        int tp = (int) eval.getTruePositives().getCount(1);
								        int tn = (int) eval.getTrueNegatives().getCount(1);
								        int fp = (int) eval.getFalsePositives().getCount(1);
								        int fn = (int) eval.getFalseNegatives().getCount(1);
								        
								       // int totalLabels = eval.get       
								        
								        double precision = tp*1.0/(tp+fp);
								        double accuracy = (tp+tn)*1.0/(tp+tn+fp+fn); 
								        double recall = tp*1.0/(tp+fn); 
								        double f1Score = (2.0*(precision*recall))/(precision+recall);
								        double pf = accuracy*pipsTarget*1.0/((1.0-accuracy)*pipsSL);
								        
								        DecimalFormat df = new DecimalFormat("0.0000");
								        System.out.println(
								        			pipsTarget+";"+pipsSL
								        			+";"+maxMinThr1
								        			+";"+nEpochs+";"+batchSize
								        			+";"+numHiddenNodes+";"+numLayers							        			
								        			//+";"+tp+";"+fp+";"+tn+";"+fn
								        			+";"+format(df, accuracy*100.0)
								        			+";"+format(df, pf)
								        			//+";"+format(df, pf)
								        			//+" || "+format(df, pipsTarget*tp*1.0/(pipsSL*fp))
								        			//+" || "+format(df, model.score())
								        			);
			        				}//learningRate
			        			}//nEpochs
			        		}//batchSize			        
			        	}//numLayers
		        	}//numHiddenNodes
	        	}//maxMinThr1
        	}//factorSl
        }
    

		
		System.out.println("Programa finalizado");
	}

	

	


}
