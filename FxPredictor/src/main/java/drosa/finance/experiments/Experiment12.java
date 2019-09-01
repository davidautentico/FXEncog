package drosa.finance.experiments;

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
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.scorecalc.RegressionScoreCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.stats.StatsListener;
import org.nd4j.evaluation.regression.RegressionEvaluation.Metric;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import drosa.finance.classes.HyperParameterConf;
import drosa.finance.classes.QuoteShort;
import drosa.finance.classes.StrategyPerformance;
import drosa.finance.utils.DateUtils;
import drosa.finance.utils.MathUtils;
import drosa.finance.utils.ModelHelper;
import drosa.finance.utils.PrintUtils;

public class Experiment12 {
	
	/**
	 * Se extraen las características de los datos planos y la clase a la que pertenece cada vector input,
	 * basandose en si se optienen pripero pipsTarget pips comprando o vendiendo
	 * @param maxMins 
	 * @param dataTrainRaw
	 * @param fileNameTrainPro
	 * @throws IOException 
	 */
	private static int doExtractXfromData(
			ArrayList<QuoteShort> data,
			String fileName, 
			ArrayList<Integer> maxMins, 
			int pipsTarget,int pipsSL,
			int maxMinLimit,
			boolean isSell,
			int typeExperiment
			) {
		
		int numInputs = 0;
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
		
				
				//calculamos diferencias con medias 30,50,120
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
				String hourBinaryStr 	= DateUtils.getHourBinary(h);
				numInputs = 30;
				String dataStr	 		= labelf
						+","+maxMinThr//1
						+","+hourBinaryStr//// 5 representacion binaria de la hora del dia
						+","+diff30//1
						+","+diff50//1
						+","+diff120//1
						+","+range//1
						+inputsDiff//20
						;
				
				if (typeExperiment==1){
					numInputs = 20;
					dataStr	= labelf
							+","+inputsDiff//20
							;
				}
				if (typeExperiment==2){
					numInputs = 3;
					dataStr	= labelf
							+","+diff30//1
							+","+diff50//1
							+","+diff120//1
							;
				}
				if (typeExperiment==3){
					numInputs = 6;
					dataStr	= labelf
							+","+maxMinThr
							+","+hourBinaryStr//// 1 representacion binaria de la hora del dia
							;
				}
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
		
		return numInputs;
}
	
	
	private static void doTestModel(
			String header,
			MultiLayerNetwork model,
			DataSetIterator trainIter, 
			DataSetIterator testIter,
			double minThr,double maxThr,double step) {
		
		StrategyPerformance trainPer = new StrategyPerformance();
		StrategyPerformance testPer = new StrategyPerformance();		  
		//mostramos en consola 	
	    DecimalFormat df = new DecimalFormat("0.0000");
		
		//PARA CADA VALOR DE UMBRAL
		for (double stratThr=minThr;stratThr<=maxThr;stratThr+=step){
			trainPer.reset();	
			testPer.reset();
			doCalculateWinPer(model,trainIter,stratThr,trainPer);
			doCalculateWinPer(model,testIter,stratThr,testPer);	
		        
	        System.out.println(
	        		header
	        		+";"+PrintUtils.format(df,stratThr)
	        		+";"+trainPer.getTrades()
	        		+";"+PrintUtils.format(df,trainPer.getWins()*100.0/trainPer.getTrades())
	        		+";"+testPer.getTrades()
	        		+";"+PrintUtils.format(df,testPer.getWins()*100.0/testPer.getTrades())
	        );
		}//stratTHR	
	}
	
	public static void doCalculateWinPer(
			MultiLayerNetwork model,
			DataSetIterator iterModel1,
			double stratThr,
			StrategyPerformance per) {
		
		int wins	= 0;
		int losses 	= 0;
		
		iterModel1.reset();
		while (iterModel1.hasNext()){
			DataSet tb = iterModel1.next();
		            
			INDArray featuresB	= tb.getFeatures();
		    INDArray labelsB 	= tb.getLabels();
		    INDArray predictedB	= model.output(featuresB,false);
		        
	        //aplicamos reglas dependiendo de los signos en BUY y SELL
	        for (int b=1;b<=featuresB.rows();b++){
	        	for (int l=1;l<=1;l++){
	            	INDArray frB = featuresB.getRow(b-1);
	            	INDArray lrB = labelsB.getRow(b-1);
	            	INDArray prB = predictedB.getRow(b-1);
	            	
	            	double predB	= prB.getDouble(0);		
	            	int realIntB 	= (int) lrB.getDouble(0);
	            	
	            	//ambos modelos predicen entrada
	            	if (predB>stratThr){
	            		//se realiza operación BUY, checqueamos con label de BUY
	            		//si es 1, será operación ganadora
	            		if (realIntB==1) wins++;
	            		else losses++;
	            	}
		        }//l
	        }//b
		}//ITER
		
		per.setTrades(wins+losses);
		per.setWins(wins);
		per.setLosses(losses);	
	}
	
	private static void doCalculateEnsembleWinPer(
			MultiLayerNetwork model1,
			MultiLayerNetwork model2,
			DataSetIterator iterModel1,
			DataSetIterator iterModel2,
			double stratThr,
			StrategyPerformance per) {
		
		int wins	= 0;
		int losses 	= 0;
		
		iterModel1.reset();
		iterModel2.reset();
		while (iterModel1.hasNext() && iterModel2.hasNext()){
			DataSet tb = iterModel1.next();
			DataSet ts = iterModel2.next();
		            
			INDArray featuresB	= tb.getFeatures();
		    INDArray labelsB 	= tb.getLabels();
		    INDArray predictedB	= model1.output(featuresB,false);
		        
	        INDArray featuresS	= ts.getFeatures();
	        INDArray labelsS 	= ts.getLabels();
	        INDArray predictedS	= model2.output(featuresS,false);
		        
	        //aplicamos reglas dependiendo de los signos en BUY y SELL
	        for (int b=1;b<=featuresB.rows();b++){
	        	for (int l=1;l<=1;l++){
	            	INDArray frB = featuresB.getRow(b-1);
	            	INDArray lrB = labelsB.getRow(b-1);
	            	INDArray prB = predictedB.getRow(b-1);
	            	
	            	INDArray frS = featuresS.getRow(b-1);
	            	INDArray lrS = labelsS.getRow(b-1);
	            	INDArray prS = predictedS.getRow(b-1);
	            	
	            	double predB	= prB.getDouble(0);	
	            	double predS	= prS.getDouble(0);		
	            	int realIntB 	= (int) lrB.getDouble(0);
	            	int realIntS 	= (int) lrS.getDouble(0);
	            	
	            		            	
	            	//ambos modelos predicen entrada
	            	if (predB>stratThr && predS>stratThr){
	            		//no se realiza operación porque se contradicen
	            		//System.out.println(predB+" "+predS+" || NONE");
	            	}else if (predB>stratThr){
	            		//se realiza operación BUY, checqueamos con label de BUY
	            		//si es 1, será operación ganadora
	            		if (realIntB==1) wins++;
	            		else losses++;
	            		
	            		//System.out.println(predB+" "+predS+" || BUY");
	            	}else if (predS>stratThr){
	            		//se realiza operación SELL, checqueamos con label de SELL
	            		//si es 1, será operación ganadora
	            		if (realIntS==1) wins++;
	            		else losses++;
	            		
	            		//System.out.println(predB+" "+predS+" || SELL");
	            	}
		        }//l
	        }//b
		}//ITER
		
		per.setTrades(wins+losses);
		per.setWins(wins);
		per.setLosses(losses);	
	}
	
	/**
	 * Test del modelo ensemble : model de BUY + modelo de SELL
	 * Calcula el porcentaje de predicciones correctas tanto en training como en test
	 * @param header
	 * @param modelBuy
	 * @param modelSell
	 * @param trainIterBuy
	 * @param testIterBuy
	 * @param trainIterSell
	 * @param testIterSell
	 * @param maxThr
	 * @param minThr
	 * @param stepThr
	 */
	private static void doTestEnsemble(String header,
			MultiLayerNetwork modelBuy, MultiLayerNetwork modelSell,
			DataSetIterator trainIterBuy, DataSetIterator testIterBuy,
			DataSetIterator trainIterSell, DataSetIterator testIterSell,
			double maxThr, double minThr, double stepThr) {
		
		//System.out.println(trainIterBuy.getLabels().size()+" "+testIterBuy.getLabels().size());
		//System.out.println(trainIterSell.getLabels().size()+" "+testIterSell.getLabels().size());
		
		StrategyPerformance trainBuyPer = new StrategyPerformance();
		StrategyPerformance testBuyPer = new StrategyPerformance();
		StrategyPerformance trainSellPer = new StrategyPerformance();
		StrategyPerformance testSellPer = new StrategyPerformance();
		StrategyPerformance trainPer = new StrategyPerformance();
		StrategyPerformance testPer = new StrategyPerformance();
		//PARA CADA VALOR DE UMBRAL
		for (double stratThr=minThr;stratThr<=maxThr;stratThr+=stepThr){
			//1) CALCULAMOS EL AJUSTE CON EL CONJUNTO DE ENTRENAMIENTO
		    //reseteamos los iter de Train tanto de buy como de test
		    //los dos deben de tener el mismo tamaño
		    trainPer.reset();
		    testPer.reset();
		    
		    //callos para modelbuy
		    doCalculateWinPer(modelBuy,trainIterBuy,stratThr,trainBuyPer);
		    doCalculateWinPer(modelBuy,testIterBuy,stratThr,testBuyPer);
		  //callos para modelsell
		    doCalculateWinPer(modelSell,trainIterSell,stratThr,trainSellPer);
		    doCalculateWinPer(modelSell,testIterSell,stratThr,testSellPer);
		    //ensemble		    
		    doCalculateEnsembleWinPer(modelBuy,modelSell,trainIterBuy,trainIterSell,stratThr,trainPer);
		    doCalculateEnsembleWinPer(modelBuy,modelSell,testIterBuy,testIterSell,stratThr,testPer);
		    
	        //mostramos en consola 	
		    DecimalFormat df = new DecimalFormat("0.0000");
	        
	        System.out.println(
	        		header
	        		+";"+PrintUtils.format(df,stratThr)
	        		+";"+PrintUtils.format(df,trainBuyPer.getWins()*100.0/trainBuyPer.getTrades())
	        		+";"+PrintUtils.format(df,testBuyPer.getWins()*100.0/testBuyPer.getTrades())
	        		+";"+PrintUtils.format(df,trainSellPer.getWins()*100.0/trainSellPer.getTrades())
	        		+";"+PrintUtils.format(df,testSellPer.getWins()*100.0/testSellPer.getTrades())
	        		+";"+trainPer.getTrades()
	        		+";"+PrintUtils.format(df,trainPer.getWins()*100.0/trainPer.getTrades())
	        		+";"+testPer.getTrades()
	        		+";"+PrintUtils.format(df,testPer.getWins()*100.0/testPer.getTrades())
	        );
		}//stratTHR			
	}
	

	/**
	 * Se entrena una red neuronal de acuerdo a los hiperparametros de hConf. Se devuelve el mejor modelo, siendo este
	 * el que mejor clasifica en el conjunto de entrenamiento. Se añade procedimiento de parada temprana para evitar
	 * overfitting
	 * @param trainIter
	 * @param testIter
	 * @param hConf
	 * @return
	 */
	private static MultiLayerNetwork doTrain(
			int n,
			DataSetIterator trainIter,
			DataSetIterator testIter,
			HyperParameterConf hConf,
			StatsStorage statsStorage) {
		
		MultiLayerNetwork bestModel = null;
		double bestWinPer = 0.0;
		StrategyPerformance per = new StrategyPerformance();
		int numInputs	= hConf.getNumInputs();
		int numOutputs 	= hConf.getNumOutputs();
		double mr		= hConf.getMomentumRate();
		for (int numHiddenNodes = hConf.getMinNodes();numHiddenNodes<=hConf.getMaxNodes();numHiddenNodes+=hConf.getStepNodes()){
        	for (int numLayers = hConf.getMinLayers();numLayers<=hConf.getMaxLayers();numLayers+=hConf.getStepLayers()){
        		for (int batchSize=hConf.getMinBatchSize();batchSize<=hConf.getMaxBatchSize();batchSize+=hConf.getStepBatchSize()){
        			for (int nEpochs=hConf.getMinEpochs();nEpochs<=hConf.getMaxEpochs();nEpochs+=hConf.getStepEpochs()){
        				for (double learningRate=hConf.getMinLR();learningRate<=hConf.getMaxLR();learningRate+=hConf.getStepLR()){		        					
        					for (int maxSeconds = hConf.getMinSeconds();maxSeconds<=hConf.getMaxSeconds();maxSeconds+=hConf.getStepSeconds()){	
        					
        						//2) CONSTRUIMOS EL MODELO
						        MultiLayerNetwork actualModel = ModelHelper.buildModel(numInputs,numHiddenNodes,
						        		numOutputs,numLayers,learningRate,mr,
						        		Activation.RELU,
						        		Activation.SIGMOID,LossFunction.XENT
						        		//Activation.SIGMOID,LossFunction.MEAN_ABSOLUTE_ERROR
						        		);			       
						        actualModel .init();
						        if (statsStorage!=null)
						        	actualModel.setListeners(new StatsListener(statsStorage, 1));
						        
						        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
						                .epochTerminationConditions(new MaxEpochsTerminationCondition(nEpochs), 
						                		new ScoreImprovementEpochTerminationCondition(10)) //Max of 50 epochs
						                .evaluateEveryNEpochs(1)
						                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxSeconds, TimeUnit.SECONDS)) //Max of 20 minutes
						                //.scoreCalculator(new DataSetLossCalculator(testIter, false))     //Calculate test set score
						                .scoreCalculator(new RegressionScoreCalculator(Metric.MSE, testIter))//para regresion
						                .build();

						        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf,actualModel ,trainIter);
						        //Early stopping training
						        EarlyStoppingResult result = trainer.fit();
						        if (result.getBestModelEpoch()<0) continue;
						        MultiLayerNetwork actualBestModel =(MultiLayerNetwork) result.getBestModel();
						        //calculamos el winRate del modelo
						        trainIter.reset();
						        per.reset();
						        
						        doCalculateWinPer(actualBestModel,trainIter,0.50,per);
						        
						        if (per.getTrades()>0){
						        	double tradesPer = per.getTrades()*100.0/n;
						        	double winPer = per.getWinRate();
						        	//System.out.println(n+" "+per.getTrades()+" "+tradesPer+" "+hConf.getMinTradesPer());
						        	if (winPer>bestWinPer && tradesPer>=hConf.getMinTradesPer()){
						        		bestModel = actualBestModel;
						        		bestWinPer = winPer;
						        		System.out.println("actualizado bestModel: "
						        				+bestWinPer+";"+numHiddenNodes+";"+numLayers+" || "+tradesPer);
						        	}
						        }						        						        						              						
        					}
        				}
        			}
        		}
        	}
		}
		
		return bestModel;
	}
	
	
	/**
	 * Procedimiento principal del experimento
	 * @param path
	 * @param dataTrainRaw
	 * @param dataTrainTest
	 * @param maxMinsRaw
	 * @param maxMinsTest
	 * @param modeTest
	 * @param statsStorage
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void doTestAlgo(
			String headerMain,
			 String path,
			 ArrayList<QuoteShort> dataTrainRaw,
			 ArrayList<QuoteShort> dataTrainTest,
			 ArrayList<Integer> maxMinsRaw,
			 ArrayList<Integer> maxMinsTest,
			 int modeTest,
			 StatsStorage statsStorage
			 ) throws IOException, InterruptedException{
		
		 DecimalFormat df = new DecimalFormat("0.0000"); 
		 
		int limit		= 500;
		int numOutputs 	= 1;
		int numInputs 	= 30;//4:10,4b:20
		double momentumRate = 0.90;
		boolean isSell	= modeTest==1;
		int batchSize	= 64;
		double minThr	= 0.20;
		double maxThr	= 1.01;
		double stepThr	= 0.05;
		int minSeconds	= 60;
		int maxSeconds	= 60;
		int stepSeconds	= 10;
		int n			= dataTrainRaw.size();
		
		String fileTrainProBuy		= path+"\\"+"trainBuy.csv";
		String fileTestProBuy 		= path+"\\"+"testBuy.csv";
		String fileTrainProSell 	= path+"\\"+"trainSell.csv";
		String fileTestProSell 		= path+"\\"+"testSell.csv";
		
		MultiLayerNetwork modelBuy		= null;
		DataSetIterator trainIterBuy	= null;
		DataSetIterator testIterBuy		= null;
		MultiLayerNetwork modelSell 	= null;
		DataSetIterator trainIterSell	= null;
		DataSetIterator testIterSell	= null;
		String header=headerMain;
		
		for (int pipsTarget = 200;pipsTarget<=200;pipsTarget+=100){ 
			for (int factorSl=1;factorSl<=1;factorSl+=1){
	        	int pipsSL = factorSl*pipsTarget;
	        	header = headerMain+";"+pipsTarget+";"+pipsSL;
	        	//1) Extaccion de caracteristicas y entrenamiento
				if (modeTest==0 || modeTest==2){
					 //System.out.println(dataTrainRaw.size()+" "+dataTrainTest.size());
					numInputs = doExtractXfromData(dataTrainRaw,fileTrainProBuy,maxMinsRaw,pipsTarget,pipsSL,limit,false,0);
			  		numInputs = doExtractXfromData(dataTrainTest,fileTestProBuy,maxMinsTest,pipsTarget,pipsSL,limit,false,0);
			  		
			  		HyperParameterConf hConf = new HyperParameterConf ();
			  		hConf.setNumInputs(numInputs);
			  		hConf.setNumOutputs(numOutputs);
			  		hConf.setMomentumRate(momentumRate);
			  		hConf.setMaxBatchSize(batchSize);
			  		hConf.setMinBatchSize(batchSize);
			  		hConf.setMinNodes(15);
			  		hConf.setMaxNodes(15);
			  		hConf.setStepNodes(5);
			  		hConf.setMinTradesPer(10.0);//10%
			  		hConf.setMinEpochs(50);
			  		hConf.setMaxEpochs(50);
			  		hConf.setStepEpochs(10);
			  		hConf.setMinLayers(1);
			  		hConf.setMaxLayers(1);
			  		hConf.setStepLayers(1);
			  		//1.1) TRAIN
			        RecordReader rr = new CSVRecordReader(0,",");
			        rr.initialize(new FileSplit(new File(fileTrainProBuy)));
			        trainIterBuy = new RecordReaderDataSetIterator(rr,batchSize,0,1);
			        //System.out.println(fileTrainProBuy);
			        //rr.reset();;
			        
			        //1.2) TEST
			        RecordReader rrTest = new CSVRecordReader();
			        rrTest.initialize(new FileSplit(new File(fileTestProBuy)));
			        testIterBuy = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
			        
			        //entrenamos
			  		modelBuy = doTrain(n,trainIterBuy,testIterBuy,hConf,statsStorage);
			  		
			  		if (modelBuy==null){
			  			System.out.println("MODELBUY A NULL");
			  		}
				}
				
				if (modeTest==1 || modeTest==2){
					numInputs = doExtractXfromData(dataTrainRaw,fileTrainProSell,maxMinsRaw,pipsTarget,pipsSL,limit,true,0);
			  		numInputs = doExtractXfromData(dataTrainTest,fileTestProSell,maxMinsTest,pipsTarget,pipsSL,limit,true,0);
			  		
			  		HyperParameterConf hConf = new HyperParameterConf ();
			  		hConf.setNumInputs(numInputs);
			  		hConf.setNumOutputs(numOutputs);
			  		hConf.setMomentumRate(momentumRate);
			  		hConf.setMaxBatchSize(batchSize);
			  		hConf.setMinBatchSize(batchSize);
			  		hConf.setMinNodes(15);
			  		hConf.setMaxNodes(15);
			  		hConf.setStepNodes(5);
			  		hConf.setMinTradesPer(10.0);//10%
			  		hConf.setMinEpochs(50);
			  		hConf.setMinEpochs(50);
			  		hConf.setStepEpochs(10);
			  		//hConf.setMinLayers(1);
			  		//hConf.setMaxLayers(3);
			  		//hConf.setStepLayers(1);
			  					  		
			  		//1.1) TRAIN
			        RecordReader rr = new CSVRecordReader();
			        rr.initialize(new FileSplit(new File(fileTrainProSell)));
			        trainIterSell = new RecordReaderDataSetIterator(rr,batchSize,0,1);
			        
			        //1.2) TEST
			        RecordReader rrTest = new CSVRecordReader();
			        rrTest.initialize(new FileSplit(new File(fileTestProSell)));
			        testIterSell = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);	
			        
			        //entrenamos
			  		modelSell = doTrain(n,trainIterSell,testIterSell,hConf,statsStorage);
			  		
			  		if (modelSell==null){
			  			System.out.println("MODELSELL A NULL");
			  		}
				}
				
				//Test conjunto o individual
				if (modelBuy!=null && modelSell!=null){
					doTestEnsemble(header,modelBuy,modelSell,trainIterBuy,testIterBuy,trainIterSell,testIterSell,maxThr,minThr,stepThr);
				}else if (modelBuy!=null){
					doTestModel(header,modelBuy,trainIterBuy,testIterBuy,minThr,maxThr,stepThr);
				}else if (modelSell!=null){
					doTestModel(header,modelSell,trainIterSell,testIterSell,minThr,maxThr,stepThr);
				}
			}//factorSL
		}//pipsTarget	
	 }


}
