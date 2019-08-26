package drosa.finance.utils;

import java.io.Serializable;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.BaseTrainingListener;

public class ModelListener extends BaseTrainingListener implements Serializable {
	 private int printIterations = 10;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 public  ModelListener(int printIterations) {
	        this.printIterations = printIterations;
	    }

	    /** Default constructor printing every 10 iterations */
	    public  ModelListener() {}

	    @Override
	    public void iterationDone(Model model, int iteration, int epoch) {
	        if (printIterations <= 0)
	            printIterations = 1;
	        if (iteration % printIterations == 0) {
	            double score = model.score();
	            System.out.println("Score at iteration {"+iteration+"} is {"+score+"}");
	        }
	    }

	    @Override
	    public String toString(){
	        return "ScoreIterationListener(" + printIterations + ")";
	    }

}
