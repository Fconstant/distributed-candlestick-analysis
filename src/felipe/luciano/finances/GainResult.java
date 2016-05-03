package felipe.luciano.finances;

import java.io.Serializable;

public class GainResult implements Serializable{
	
	private static final long serialVersionUID = -1384423464347952217L;
	
	private int pos = 0, neg = 0, none = 0;

	public void countProfit(){
		pos++;
	}
	
	public void countLoss(){
		neg++;
	}
	
	public void countNotFound(){
		none++;
	}
	
	public int getProfit(){
		return pos;
	}
	
	public int getLoss(){
		return neg;
	}
	
	public int getNotFound(){
		return none;
	}

	public int total(){
		return none + neg + pos;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Profit: " + getProfit() + ", Loss: " + getLoss() + ", NotFound: " + getNotFound() + "\tTOTAL: " + total();
	}
	
}
