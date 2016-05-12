package felipe.luciano.finances;

import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import felipe.luciano.support.Log;

public class Verifier {

	private List<Candlestick> allDays;
	private CandlestickPattern pattern;
	private Expression strategy;
	
	private transient ScriptEngine engine;
	private transient ScriptContext context;
	
	public Verifier(CandlestickPattern pattern, List<Candlestick> allDays) {
		this.pattern = pattern;
		this.allDays = allDays;

		this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.context = new SimpleScriptContext();
		
		strategy = new Expression("P1_CLOSE > P2_CLOSE + P3_CLOSE + P4_CLOSE");
	}

	public GainResult verify() {
		
		GainResult gainResult = new GainResult();
		
		Bindings engineScope = context.getBindings(ScriptContext.ENGINE_SCOPE);
		for(int i = 4; i < allDays.size() - 3 ; i++){
			
			boolean result = false;
			for(Expression exp : pattern.getExpressions()){
				
				exp.loadVarsIntoScriptEngine(engineScope, allDays.subList(i - 4, i));
				
				try {
					result = Boolean.parseBoolean(engine.eval(exp.toString(), context).toString());
				} catch (ScriptException e) {
					e.printStackTrace();
				}
				engineScope.clear();
				if(!result){
					gainResult.countNotFound();
					break;
				}
			}
			
			if(result){

				strategy.loadVarsIntoScriptEngine(engineScope, allDays.subList(i, i + 3));
				
				try {
					result = Boolean.parseBoolean(engine.eval(strategy.toString(), context).toString());
				} catch (ScriptException e) {
					e.printStackTrace();
				}
				engineScope.clear();
				if(result)
					gainResult.countProfit();
				else
					gainResult.countLoss();
			}
			Log.p(gainResult);
		}
		
		return gainResult;
	}
	
}
