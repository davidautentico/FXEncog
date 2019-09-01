package drosa.finance.classes;

public class HyperParameterConf {
	
	int numInputs = 30;
	int numOutputs = 1;
	double momentumRate = 0.5;
	double minTradesPer = 0.10;
	
	//nodes
	int maxNodes	= 20;
	int minNodes	= 20;
	int stepNodes	= 1;	
	//layers
	int maxLayers	= 1;
	int minLayers	= 1;
	int stepLayers	= 1;	
	//epochs
	int maxEpochs	= 100;
	int minEpochs	= 100;
	int stepEpochs	= 1;	
	//trainingTime
	int maxSeconds	= 60;
	int minSeconds	= 60;
	int stepSeconds	= 1;	
	//batchSize
	int maxBatchSize	= 64;
	int minBatchSize	= 64;
	int stepBatchSize	= 1;
	//batchSize
	double maxLR		= 0.05;
	double minLR		= 0.05;
	double stepLR		= 0.01;
	
	public void configNodes(int max,int min,int step){
		maxNodes	= max;
		minNodes	= min;
		stepNodes	= step;	
	}
	public void configLayers(int max,int min,int step){
		maxLayers	= max;
		minLayers	= min;
		stepLayers	= step;	
	}
	public void configEpochs(int max,int min,int step){
		maxEpochs	= max;
		minEpochs	= min;
		stepEpochs	= step;	
	}
	public void configTrainingTime(int max,int min,int step){
		maxSeconds	= max;
		minSeconds	= min;
		stepSeconds	= step;	
	}
	public void configBatchSize(int max,int min,int step){
		maxBatchSize	= max;
		minBatchSize	= min;
		stepBatchSize	= step;	
	}
	public void configLearningRate(double max,double min,double step){
		maxLR	= max;
		minLR	= min;
		stepLR	= step;	
	}
	
	
	public double getMinTradesPer() {
		return minTradesPer;
	}
	public void setMinTradesPer(double minTradesPer) {
		this.minTradesPer = minTradesPer;
	}
	public double getMomentumRate() {
		return momentumRate;
	}
	public void setMomentumRate(double momentumRate) {
		this.momentumRate = momentumRate;
	}
	public int getNumInputs() {
		return numInputs;
	}
	public void setNumInputs(int numInputs) {
		this.numInputs = numInputs;
	}
	public int getNumOutputs() {
		return numOutputs;
	}
	public void setNumOutputs(int numOutputs) {
		this.numOutputs = numOutputs;
	}
	public int getMaxNodes() {
		return maxNodes;
	}
	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}
	public int getMinNodes() {
		return minNodes;
	}
	public void setMinNodes(int minNodes) {
		this.minNodes = minNodes;
	}
	public int getStepNodes() {
		return stepNodes;
	}
	public void setStepNodes(int stepNodes) {
		this.stepNodes = stepNodes;
	}
	public int getMaxLayers() {
		return maxLayers;
	}
	public void setMaxLayers(int maxLayers) {
		this.maxLayers = maxLayers;
	}
	public int getMinLayers() {
		return minLayers;
	}
	public void setMinLayers(int minLayers) {
		this.minLayers = minLayers;
	}
	public int getStepLayers() {
		return stepLayers;
	}
	public void setStepLayers(int stepLayers) {
		this.stepLayers = stepLayers;
	}
	public int getMaxEpochs() {
		return maxEpochs;
	}
	public void setMaxEpochs(int maxEpochs) {
		this.maxEpochs = maxEpochs;
	}
	public int getMinEpochs() {
		return minEpochs;
	}
	public void setMinEpochs(int minEpochs) {
		this.minEpochs = minEpochs;
	}
	public int getStepEpochs() {
		return stepEpochs;
	}
	public void setStepEpochs(int stepEpochs) {
		this.stepEpochs = stepEpochs;
	}
	public int getMaxSeconds() {
		return maxSeconds;
	}
	public void setMaxSeconds(int maxSeconds) {
		this.maxSeconds = maxSeconds;
	}
	public int getMinSeconds() {
		return minSeconds;
	}
	public void setMinSeconds(int minSeconds) {
		this.minSeconds = minSeconds;
	}
	public int getStepSeconds() {
		return stepSeconds;
	}
	public void setStepSeconds(int stepSeconds) {
		this.stepSeconds = stepSeconds;
	}
	public int getMaxBatchSize() {
		return maxBatchSize;
	}
	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}
	public int getMinBatchSize() {
		return minBatchSize;
	}
	public void setMinBatchSize(int minBatchSize) {
		this.minBatchSize = minBatchSize;
	}
	public int getStepBatchSize() {
		return stepBatchSize;
	}
	public void setStepBatchSize(int stepBatchSize) {
		this.stepBatchSize = stepBatchSize;
	}
	public double getMaxLR() {
		return maxLR;
	}
	public void setMaxLR(double maxLR) {
		this.maxLR = maxLR;
	}
	public double getMinLR() {
		return minLR;
	}
	public void setMinLR(double minLR) {
		this.minLR = minLR;
	}
	public double getStepLR() {
		return stepLR;
	}
	public void setStepLR(double stepLR) {
		this.stepLR = stepLR;
	}
	
	
	
}
