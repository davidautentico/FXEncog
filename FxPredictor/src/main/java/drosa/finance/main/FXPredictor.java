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
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
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
			String fileName, ArrayList<Integer> maxMins, int pipsTarget,int pipsSL,
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
				
				//if (h>=10) continue;
				
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
				
				//evaluamos target
				int label = 0;//por defecto NO sell
				
				int sellThr = q.getOpen5()-pipsSL;
				int buyThr 	= q.getOpen5()+pipsTarget;
				for (int j=i;j<data.size();j++){
					QuoteShort qj = data.get(j);
					if (qj.getHigh5()>=buyThr){
						label = 1;
						break;
					}else if (qj.getLow5()<=sellThr){
						label = 0;
						break;
					}
				}
		
				
				String dataStr = label
						+","+h
						+","+maxMin
						+","+diff50
						//+","+diff50
						+","+diffHigh
						+","+diffLow
						//+","+maxMinThr
						
						//+","+diff30atr
						;
				out.write(dataStr);
				out.newLine();		
				
				if (high==-1 || q.getOpen5()>=high) high = q.getOpen5();
				if (low==-1 || q.getOpen5()<=low) low = q.getOpen5();
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("[doExtractXfromData] Error: " + e.getMessage());
		}
		
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
		}else if (numLayers==8){
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
	        .layer(new DenseLayer.Builder()
	        	.nIn(numHiddenNodes)
	        	.nOut(numHiddenNodes)
	        	.activation(Activation.RELU)
	        	.build()
	        )
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
		String fileNameTrainRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2016.12.31.csv";
		String fileNameTestRaw15	= "C:\\fxdata\\EURUSD_15 Mins_Bid_2017.01.01_2019.08.25.csv";
		String fileNameTrainPro15 = "C:\\fxdata\\EURUSD_15 Mins_Bid_2009.01.01_2016.12.31_pro.csv";
		String fileNameTestPro15  = "C:\\fxdata\\EURUSD_15 Mins_Bid_2017.01.01_2019.08.25_pro.csv";
				
		boolean is15m = false;
		
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
        int numInputs 		= 5;
        int numOutputs 		= 1;
        int numHiddenNodes 	= 5;
        int pipsTarget		= 500;
        
        //si el batchsize es grande.. entonces se necesitan mas epochs para 
        //en cambio con un batchsize mas pequeño, se necesitan menos epochs..
        
        //tablas : 
        //hiddenNodes-accuracy, probar caso de 1 layer
        //numLayers-accuracy
        
        for (pipsTarget = 200;pipsTarget<=200;pipsTarget+=100){ 
        	int pipsSL = 1*pipsTarget;
        	for (numHiddenNodes = 1;numHiddenNodes<=50;numHiddenNodes+=1){
	        	for (int numLayers = 5;numLayers<=5;numLayers+=1){
	        		for (batchSize=128;batchSize<=128;batchSize+=1){
	        			for (nEpochs=10;nEpochs<=10;nEpochs+=1){
	        				for (learningRate=0.001;learningRate<=0.001;learningRate+=0.001){
						        //preprocesamiento calculando indicadores del dataset
						  		doExtractXfromData2(dataTrainRaw,fileNameTrainPro,maxMinsRaw,pipsTarget,pipsSL,true);
						  		doExtractXfromData2(dataTrainTest,fileNameTestPro,maxMinsTest,pipsTarget,pipsSL,true);
						  		
								//1) obtenemos los dataset de los datos preprocesados	
						        //1.1) TRAIN
						        RecordReader rr = new CSVRecordReader();
						        rr.initialize(new FileSplit(new File(fileNameTrainPro)));
						        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,1);
						        
						        //1.2) TEST
						        RecordReader rrTest = new CSVRecordReader();
						        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
						        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
						        
						        //2) CONSTRUIMOS EL MODELO
						        MultiLayerNetwork model = buildModel(numInputs,numHiddenNodes,numOutputs,numLayers,learningRate);			       
						        model.init();
							
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
						        			nEpochs+";"+numHiddenNodes+";"+numLayers
						        			+";"+pipsTarget+";"+pipsSL
						        			+";"+tp+";"+fp+";"+tn+";"+fn
						        			+";"+format(df, accuracy*100.0)
						        			+";"+format(df, pf)
						        			//+" || "+format(df, pipsTarget*tp*1.0/(pipsSL*fp))
						        			//+" || "+format(df, model.score())
						        			);
	        				}//learningRate
	        			}//nEpochs
	        		}//batchSize			        
	        	}//numLayers
        	}//numHiddenNodes
        }
    

		
		System.out.println("Programa finalizado");
	}

	

	


}
