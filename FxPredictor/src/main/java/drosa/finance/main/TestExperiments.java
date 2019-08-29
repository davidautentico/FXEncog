package drosa.finance.main;

import java.io.BufferedWriter;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;

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
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
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
import org.slf4j.Logger;

import drosa.finance.classes.DAO;
import drosa.finance.classes.QuoteShort;
import drosa.finance.classes.TestLines;
import drosa.finance.experiments.Experiment1;
import drosa.finance.experiments.Experiment3;
import drosa.finance.experiments.Experiment4;
import drosa.finance.types.DataProvider;
import drosa.finance.utils.DataUtils;
import drosa.finance.utils.MathUtils;
import drosa.finance.utils.ModelListener;
import drosa.finance.utils.TradingUtils;


public class TestExperiments {
	
	
	
	

	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Initialize the user interface backend
	 UIServer uiServer = UIServer.getInstance();
	    //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
	 StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
	  //Configure where the network information (gradients, activations, score vs. time etc) is to be stored
      //Then add the StatsListener to collect this information from the network, as it trains
      //StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
      int listenerFrequency = 1;
	  
	    //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
	   uiServer.attach(statsStorage);
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
		
		dataTrainRaw	= DataUtils.readData(fileNameTrainRaw);
		dataTestRaw 	=  DataUtils.readData(fileNameTestRaw);
		
		ArrayList<Integer> maxMinsRaw = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataTrainRaw);
		ArrayList<Integer> maxMinsTest = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(dataTestRaw);
		
		System.out.println("Leido raw data,tamaños datos leidos: "+dataTrainRaw.size()+" "+dataTestRaw.size());
		
		//EXPERIMENTOS
		
		//experimento de clasificación
		//Experiment4.doTestAlgo(fileNameTrainPro, fileNameTestPro, dataTrainRaw, dataTestRaw, maxMinsRaw, maxMinsTest);
		//Experiment1.doTestAlgo(fileNameTrainPro, fileNameTestPro, dataTrainRaw, dataTestRaw, maxMinsRaw, maxMinsTest,statsStorage);
		
		//regresion
		Experiment3.doTestAlgo(fileNameTrainPro, fileNameTestPro, dataTrainRaw, dataTestRaw, maxMinsRaw, maxMinsTest,statsStorage);

		
		
		System.out.println("Programa finalizado");
	}



}
