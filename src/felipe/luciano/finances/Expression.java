package felipe.luciano.finances;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;

import felipe.luciano.support.Consts;

public class Expression implements Serializable{

	private static final long serialVersionUID = 2320681330318812195L;

	private String exp;
	private Set<String> expVars;

	public Expression(String expression) {
		exp = expression;
		expVars = new HashSet<String>();

		Pattern pattern = Pattern.compile(Consts.Finances.EXP_REGEX);
		Matcher matcher = pattern.matcher(exp);

		while(matcher.find())
			expVars.add(exp.substring(matcher.start(), matcher.end()));
	}

	@Override
	public String toString() {
		return exp;
	}

	public static List<Expression> generateExpressions(String... expStrings){
		List<Expression> exps = new ArrayList<>(expStrings.length);
		for(String expS : expStrings)
			exps.add(new Expression(expS));
		return exps;
	}

	public void loadVarsIntoScriptEngine(Bindings engineScope, List<Candlestick> candles){

		for(String expVar : expVars){
			
			int dia = Character.getNumericValue(expVar.charAt(1));
			Candlestick candle = candles.get(dia - 1);

			char identifier = expVar.split("_")[1].charAt(0);

			double val = 0;
			switch(identifier){
			case 'O': val = candle.getOpen(); break; // Caso seja OPEN
			case 'C': val = candle.getClose(); break;
			case 'H': val = candle.getHigh(); break;
			case 'L': val = candle.getLow(); break;
			}

			engineScope.put(expVar, val);
		}
	}
}
