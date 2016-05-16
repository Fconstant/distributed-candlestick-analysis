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
	private String name;

	private ScriptEngine engine;
	private ScriptContext context;

	public Verifier(String name, CandlestickPattern pattern, List<Candlestick> allDays) {
		this.name = name;
		this.pattern = pattern;
		this.allDays = allDays;

		this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.context = new SimpleScriptContext();

		strategy = new Expression("P1_CLOSE > (P2_CLOSE + P3_CLOSE + P4_CLOSE) / 3");
	}

	public GainResult verify() {

		GainResult gainResult = new GainResult();
		ResultPrinter printer = new ResultPrinter(gainResult);
		printer.start();

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
				strategy.loadVarsIntoScriptEngine(engineScope, allDays.subList(i, i + 4));

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
		}
		printer.interrupt();
		return gainResult;
	}

	private class ResultPrinter extends Thread{

		private GainResult result;

		public ResultPrinter(GainResult result) {
			this.result = result;
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				try {
					Thread.sleep(2000);
					Log.p(name, result);
				} catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
				}
			}
		}

	}

}
