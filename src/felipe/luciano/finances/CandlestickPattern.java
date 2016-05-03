package felipe.luciano.finances;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

public class CandlestickPattern implements Serializable{
	
	private static final long serialVersionUID = -9003941841109469826L;
	
	private List<Expression> expressions;
	private transient List<Candlestick> curDays;

	private transient ScriptEngine engine;
	private transient ScriptContext context;
	
	public CandlestickPattern(List<Expression> expressions){
		this.expressions = expressions;
		this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.context = new SimpleScriptContext();
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	private void setDays(List<Candlestick> days){
		curDays = days;
	}
	
	public List<Candlestick> getDays(){
		return curDays;
	}
	
	public boolean verify(Candlestick... days) {
		return verify(Arrays.asList(days));
	}
	
	public boolean verify(List<Candlestick> days) {
		setDays(days);

		// VARIAVEIS
		Bindings engineScope = context.getBindings(ScriptContext.ENGINE_SCOPE);

		for(Expression exp : expressions){

			exp.loadVarsIntoScriptEngine(engineScope, this);
			boolean result;
			try {
				result = Boolean.parseBoolean(engine.eval(exp.toString(), context).toString());
				if(!result) return result;
			} catch (ScriptException e) {
				e.printStackTrace();
			}
			engineScope.clear();

		}
		return true;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return expressions.toString();
	}

}
