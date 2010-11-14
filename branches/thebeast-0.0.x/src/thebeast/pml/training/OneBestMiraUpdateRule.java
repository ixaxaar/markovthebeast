package thebeast.pml.training;

import java.util.List;

import thebeast.pml.FeatureVector;
import thebeast.pml.PropertyName;
import thebeast.pml.SparseVector;
import thebeast.pml.Weights;

public class OneBestMiraUpdateRule implements UpdateRule {

	private boolean enforceSigns = false;
	
	public void setProperty(PropertyName name, Object value) {
		// TODO Auto-generated method stub

	}

	public Object getProperty(PropertyName name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void endEpoch() {
		// TODO Auto-generated method stub

	}

	public void update(FeatureVector gold, List<FeatureVector> candidates,
			List<Double> losses, Weights weights, int t) 
	{
		double goldScore = weights.score(gold);
		int index = 0;
		double learningRate = 0;
	    for (FeatureVector guess : candidates) {	        
	    	SparseVector diffLocal = gold.getLocal().add(-1.0, guess.getLocal());
	        SparseVector diffNN = gold.getFalseVector().add(-1.0, guess.getFalseVector());
	        SparseVector diffNP = gold.getTrueVector().add(-1.0, guess.getTrueVector());
	        double sqNorm = diffLocal.squaredNorm() + diffNN.squaredNorm() + diffNP.squaredNorm();
	        double loss = losses.get(index++);
	        double guessScore = weights.score(guess);
	        double sufferLoss = (guessScore - goldScore) + loss;
	        sufferLoss = Math.max(sufferLoss, 0);
	        learningRate = (sqNorm != 0.0) ? sufferLoss / sqNorm : 0.0;	        	         
	        if (enforceSigns) {
	            weights.add(learningRate, diffLocal);
	            weights.add(learningRate, diffNN, true);
	            weights.add(learningRate, diffNP, false);
	            weights.enforceBound(gold.getLocalNonnegativeIndices(), true, 0.0);
	            weights.enforceBound(gold.getLocalNonpositiveIndices(), false, 0.0);
	        } else {	        	
	            weights.add(learningRate, diffLocal);
	            weights.add(learningRate, diffNN);
	            weights.add(learningRate, diffNP);
	       }
	    }
	}

}
