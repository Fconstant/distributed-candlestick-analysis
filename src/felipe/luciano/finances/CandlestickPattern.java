package felipe.luciano.finances;

import java.io.Serializable;
import java.util.List;

public class CandlestickPattern implements Serializable{
	
	private static final long serialVersionUID = -9003941841109469826L;
	
	private List<Expression> expressions;
	
	public CandlestickPattern(List<Expression> expressions){
		this.expressions = expressions;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return expressions.toString();
	}

}
