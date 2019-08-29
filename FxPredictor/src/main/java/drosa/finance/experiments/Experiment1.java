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
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.stats.StatsListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import drosa.finance.classes.QuoteShort;
import drosa.finance.utils.DateUtils;
import drosa.finance.utils.ModelHelper;
import drosa.finance.utils.PrintUtils;

public class Experiment1 {
	
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
			int end = data.size();
			//end = 2000;
			int total1s = 0;
			int total0s =0;
			for (int i=0;i<end;i++){
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
				String tradeHourStr = DateUtils.getHourBinary(h);
			
				
				//int diff50 = q.getOpen5()-sma50;
				double sma50val = 0;
				double sma60val = 0;
				sma50val = 0;
				double diff20atr = 0;
				double diff30atr = 0;
				int maxMinThr = 0;
				

			
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
					for (int j=i;j<end || labelFound;j++){
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
			
					if (label==1) total1s++;
					else if (label==0)total0s++;
					
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
			//System.out.println(totaltrades+" win% "+(wins*100.0/totaltrades)+" "+total1s+" "+total0s);
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
			 ArrayList<Integer> maxMinsTest,
			 StatsStorage statsStorage
			 ) throws IOException, InterruptedException{
		
		 DecimalFormat df = new DecimalFormat("0.0000"); 
		 
		int limit		= 500;
		int numOutputs 	= 1;
		int numInputs 	= 14;//4:10,4b:20
		double momentumRate = 0.90;
		String header="Experimento 1: Capas = 10 Nodos=10 Target=15 BatchSize = 32";
		System.out.println("**** "+header+" ****");
		for (int pipsTarget = 100;pipsTarget<=100;pipsTarget+=50){ 
	       	for (int factorSl=3;factorSl<=3;factorSl+=1){
		        	int pipsSL = factorSl*pipsTarget;
		        	for (int maxMinThr1=500;maxMinThr1<=500;maxMinThr1+=1){ 
			        	 //preprocesamiento calculando indicadores del dataset
				  		doExtractXfromData(dataTrainRaw,fileNameTrainPro,maxMinsRaw,pipsTarget,pipsSL,maxMinThr1,true);
				  		doExtractXfromData(dataTrainTest,fileNameTestPro,maxMinsTest,pipsTarget,pipsSL,maxMinThr1,true);
				  		
				  		for (int numHiddenNodes = 10;numHiddenNodes<=10;numHiddenNodes+=10){
				        	for (int numLayers =5;numLayers<=5;numLayers+=1){
				        		for (int batchSize=32;batchSize<=32;batchSize+=8){
				        			for (int nEpochs=100;nEpochs<=100;nEpochs+=1){
				        				for (double learningRate=0.01;learningRate<=0.01;learningRate+=0.010){		        					
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
											        MultiLayerNetwork model = ModelHelper.buildModel(numInputs,numHiddenNodes,
											        		numOutputs,numLayers,learningRate,momentumRate,
											        		Activation.RELU,Activation.SIGMOID,LossFunction.XENT);			       
											        model.init();
											        
											        if (statsStorage!=null){
											        	 //Then add the StatsListener to collect this information from the network, as it trains
											            model.setListeners(new StatsListener(statsStorage));
											        }
											        
											        
											        //3) APLICAMOS MODELO AL CONJUNTO DE ENTRENAMIENTO
											        /*for ( int n = 0; n < nEpochs; n++) {
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
											        
											        //DecimalFormat df = new DecimalFormat("0.0000");
											        System.out.println(
											        			pipsTarget+";"+pipsSL
											        			+";"+maxMinThr1
											        			+";"+nEpochs+";"+batchSize
											        			+";"+numHiddenNodes+";"+numLayers							        			
											        			//+";"+tp+";"+fp+";"+tn+";"+fn
											        			+";"+PrintUtils.format(df, accuracy*100.0)
											        			+";"+PrintUtils.format(df, pf)
											        			//+";"+format(df, pf)
											        			//+" || "+format(df, pipsTarget*tp*1.0/(pipsSL*fp))
											        			//+" || "+format(df, model.score())
											        			);
											        */
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
											        
											        if (result.getBestModelEpoch()<0){
											        	System.out.println("BestEpoch failed: "+result.getBestModelEpoch());
											        	continue;
											        }
											        
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
												        		pipsTarget+";"+pipsSL+";"+maxMinThr1
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
		        	}//maxMinThr
	       	}//factor SL
		}//pipsTarget		
	 }


}
