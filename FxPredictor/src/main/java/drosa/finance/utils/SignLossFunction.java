package drosa.finance.utils;

import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.primitives.Pair;

public class SignLossFunction implements ILossFunction  {

	@Override
	public double computeScore(INDArray labels, INDArray preOutput,
			IActivation activationFn, INDArray mask, boolean average) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public INDArray computeScoreArray(INDArray labels, INDArray preOutput,
			IActivation activationFn, INDArray mask) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INDArray computeGradient(INDArray labels, INDArray preOutput,
			IActivation activationFn, INDArray mask) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Double, INDArray> computeGradientAndScore(INDArray labels,
			INDArray preOutput, IActivation activationFn, INDArray mask,
			boolean average) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

}
