package thebeast.pml.training;

import java.util.List;

import thebeast.pml.SparseVector;
import thebeast.pml.Weights;
import thebeast.pml.FeatureVector;
import thebeast.pml.PropertyName;

public class CDAUpdateRule implements UpdateRule {

	private boolean enforceSigns = false;
	private double sigma = 1.0;
	private double C = -1.0;
	private int T = -1;

	
	public void setProperty(PropertyName name, Object value) {
		if (name.getHead().equals("signs"))
			enforceSigns = (Boolean) value;
	    if (name.getHead().equals("sigma")) {
	    	sigma = (Double) value;
	    	System.out.println("Sigma = " + sigma);
	    }
	    if (name.getHead().equals("c"))
		      C = (Double) value;
	    if (name.getHead().equals("T"))
		      T = (Integer) value;
	    if ((C > 0) && (T > 0)) {
	    	sigma = 1/ (C*T);
	    	System.out.println("Sigma = " + sigma);
	    }
	}

	
	public Object getProperty(PropertyName name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void endEpoch() {
		// TODO Auto-generated method stub

	}

	public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights, int t) {
		double goldScore = weights.score(gold);
		int index = 0;
		double scale = (double)(t-1)/t;
		//System.out.println("Scale = " + scale);
		double learningRate = 0;
	    for (FeatureVector guess : candidates) {	        
	    	SparseVector diffLocal = gold.getLocal().add(-1.0, guess.getLocal());
	        SparseVector diffNN = gold.getFalseVector().add(-1.0, guess.getFalseVector());
	        SparseVector diffNP = gold.getTrueVector().add(-1.0, guess.getTrueVector());
	        double sqNorm = diffLocal.squaredNorm() + diffNN.squaredNorm() + diffNP.squaredNorm();
	        double loss = losses.get(index++);
	        double guessScore = weights.score(guess);
	        double sufferLoss = scale*(guessScore - goldScore) + loss;
	        sufferLoss = Math.max(sufferLoss, 0);
	        //System.out.println("Suffer loss = " + sufferLoss);
	        learningRate = (sqNorm != 0.0) ? sufferLoss / sqNorm : 0.0;
	        learningRate = Math.min(learningRate, 1/(sigma*t));
	        //System.out.println("Learning rate = " + learningRate);
	        if (enforceSigns) {
	        	weights.scale(scale);
	            weights.add(learningRate, diffLocal);
	            weights.add(learningRate, diffNN, true);
	            weights.add(learningRate, diffNP, false);
	            weights.enforceBound(gold.getLocalNonnegativeIndices(), true, 0.0);
	            weights.enforceBound(gold.getLocalNonpositiveIndices(), false, 0.0);
	        } else {	        	
	        	weights.scale(scale);
	            weights.add(learningRate, diffLocal);
	            weights.add(learningRate, diffNN);
	            weights.add(learningRate, diffNP);
	       }
	    }
	}


}
