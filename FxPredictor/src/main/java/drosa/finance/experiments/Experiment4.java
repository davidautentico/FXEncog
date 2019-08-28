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
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import drosa.finance.classes.QuoteShort;
import drosa.finance.utils.DateUtils;
import drosa.finance.utils.MathUtils;
import drosa.finance.utils.ModelHelper;
import drosa.finance.utils.PrintUtils;

public class Experiment4 {
	
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
				String hourBinaryStr 	= DateUtils.getHourBinary(h);
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
	
	
	public static void doTestAlgo(
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
		  		doExtractXfromData(dataTrainRaw,fileNameTrainPro,maxMinsRaw,pipsTarget,pipsSL,limit,false);
		  		doExtractXfromData(dataTrainTest,fileNameTestPro,maxMinsTest,pipsTarget,pipsSL,limit,false);
		  		
		  		for (int numHiddenNodes = 20;numHiddenNodes<=20;numHiddenNodes+=1){
		        	for (int numLayers =3;numLayers<=3;numLayers+=1){
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
									        MultiLayerNetwork model = ModelHelper.buildModel(numInputs,numHiddenNodes,
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
