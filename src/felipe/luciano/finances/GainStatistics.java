package felipe.luciano.finances;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GainStatistics implements Serializable{
	
	private static final long serialVersionUID = -5587321212048680101L;
	
	private final Map<String, GainResult> stat;
	private final CandlestickPattern pattern;
	
	public GainStatistics(int size, CandlestickPattern currentPattern) {
		stat = new HashMap<>(size);
		pattern = currentPattern;
	}
	
	public void put(String subject, GainResult result){
		stat.put(subject, result);
	}
	
	public int resultSize(){
		return stat.size();
	}
	
	public GainResult get(String subject){
		return stat.get(subject);
	}
	
	public CandlestickPattern pattern(){
		return pattern;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Patterns: " + pattern.toString() + " Results:" + stat.toString();
	}
	
}
