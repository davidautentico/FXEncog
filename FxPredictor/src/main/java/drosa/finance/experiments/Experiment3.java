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
import org.datavec.api.split.InputSplit;
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

import drosa.finance.classes.QuoteShort;
import drosa.finance.utils.DateUtils;
import drosa.finance.utils.ModelHelper;
import drosa.finance.utils.PrintUtils;

public class Experiment3 {
	
	
	/**
	 * Se extraen las características de los datos planos y la clase a la que pertenece cada vector input,
	 * basandose en si se optienen pripero pipsTarget pips comprando o vendiendo
	 * @param maxMins 
	 * @param dataTrainRaw
	 * @param fileNameTrainPro
	 * @throws IOException 
	 */
	private static void doExtractXfromData(
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
				String hourBinaryStr 	= DateUtils.getHourBinary(h);
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
	 public static void doTestAlgo(
			 String fileNameTrainPro,
			 String fileNameTestPro,
			 ArrayList<QuoteShort> dataTrainRaw,
			 ArrayList<QuoteShort> dataTrainTest,
			 ArrayList<Integer> maxMinsRaw,
			 ArrayList<Integer> maxMinsTest,
			 StatsStorage statsStorage
			 ) throws IOException, InterruptedException{
		
		 DecimalFormat df = new DecimalFormat("0.0000"); 
		 
		int limit		= 500;
		int numOutputs 	= 1;
		int numInputs 	= 25;//4:10,4b:20
		double momentumRate = 0.50;
		
		for (int pipsTarget = 100;pipsTarget<=100;pipsTarget+=50){ 
       	for (int factorSl=1;factorSl<=1;factorSl+=1){
	        	int pipsSL = factorSl*pipsTarget;
	        	//preprocesamiento calculando indicadores del dataset
		  		doExtractXfromData(dataTrainRaw,fileNameTrainPro);
		  		doExtractXfromData(dataTrainTest,fileNameTestPro);
		  		
		  		for (int numHiddenNodes = 10;numHiddenNodes<=10;numHiddenNodes+=5){
		        	for (int numLayers =5;numLayers<=5;numLayers+=1){
		        		for (int batchSize=64;batchSize<=64;batchSize+=8){
		        			for (int nEpochs=100;nEpochs<=100;nEpochs+=1){
		        				for (double learningRate=0.001;learningRate<=0.001;learningRate+=0.01){		        					
		        					for (int maxSeconds = 120;maxSeconds<=120;maxSeconds+=60){	
		        						for (double stratThr=0.005;stratThr<=0.005;stratThr+=0.01){
			        						//1.1) TRAIN
									        RecordReader rr = new CSVRecordReader();
									        rr.initialize((InputSplit) new FileSplit(new File(fileNameTrainPro)));
									        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,1);
									        
									        //1.2) TEST
									        RecordReader rrTest = new CSVRecordReader();
									        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
									        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,1);
									        
									       //2) CONSTRUIMOS EL MODELO
									        MultiLayerNetwork model = ModelHelper.buildModel(numInputs,numHiddenNodes,
									        		numOutputs,numLayers,learningRate,momentumRate,
									        		Activation.SIGMOID,Activation.RELU,LossFunction.MSE);			       
									        model.init();
									        
									        if (statsStorage!=null){
									        	 //Then add the StatsListener to collect this information from the network, as it trains
									            model.setListeners(new StatsListener(statsStorage));
									        }
									        
									        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
									                .epochTerminationConditions(new MaxEpochsTerminationCondition(10000), 
									                		new ScoreImprovementEpochTerminationCondition(5)) //Max of 50 epochs
									                .evaluateEveryNEpochs(1)
									                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxSeconds, TimeUnit.SECONDS)) //Max of 20 minutes
									                //.scoreCalculator(new DataSetLossCalculator(testIter, true))     //Calculate test set score
									                .scoreCalculator(new RegressionScoreCalculator(Metric.MAE, testIter))//para regresion
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
										        		+";"+PrintUtils.format(df,result.getBestModelScore())
										        		+";"+trades+";"+PrintUtils.format(df,wins*100.0/trades)
										        		+";"+PrintUtils.format(df,wins*pipsTarget*1.0/(losses*pipsSL))
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
}
