package drosa.finance.utils;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class ModelHelper {
	
	
	/**
	 * Aquí creamos el modelo a entrenar y evaluar posteriormente
	 * @param numLayers 
	 * @param numOutputs 
	 * @param numHiddenNodes 
	 * @param numInputs 
	 * @return 
	 */
	public static MultiLayerNetwork buildModel(
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

}
