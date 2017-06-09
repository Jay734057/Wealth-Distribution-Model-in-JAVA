/**
 * Land Class: represent each location on the landscape and its growth. The 
 * 			grow method is used for grain growth each location. maxGrain 
 * 			variable means the maximum amount of grain an object can reach,
 * 			and actGrain indicates the actual amount. The variable 
 * 			numOfOccupation represents the number of people standing on it.
 * 
 */

public class Land {
	private double maxGrain;
	private double actGrain;
	private int numOfOccupation;
	
	public Land(int max, int act){
		this.setMax(max);
		this.setAct(act);
		numOfOccupation = 0;
	}
	
	public Land(Land l){
		this.setAct(l.getAct());
		this.setMax(l.getMax());
		this.numOfOccupation = l.getOccupation();
	}
	
	void grow(int interval, int amount, int turn){
		//the amount of grain grows in certain amount at certain rate
		if ((turn % interval) == 0)
			actGrain = actGrain + amount;
		if (actGrain > maxGrain)
			actGrain = maxGrain;
	}
	
	int getOccupation(){
		return numOfOccupation;
	}
	
	public void arrive(){
		//when one people arrive the land, increase the number of 
		//occupation
		numOfOccupation++;
	}
	
	public void leave(){
		//when one people leave the land, decrease the number of 
		//occupation
		numOfOccupation--;
		if(numOfOccupation == 0){
			setAct(0);
		}
	}
	
	public void setMax(double d){
		maxGrain = d;
	}
	
	public void setAct(double d){
		actGrain = d;
	}
	
	public double getMax(){
		return maxGrain;
	}
	
	public double getAct(){
		return actGrain;
	}
	
	public String toString(){
		return "(" + actGrain + "," + maxGrain + ")";
	}
	
}
