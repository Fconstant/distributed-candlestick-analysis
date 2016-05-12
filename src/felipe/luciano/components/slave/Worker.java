package felipe.luciano.components.slave;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;

import felipe.luciano.finances.Candlestick;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainResult;
import felipe.luciano.finances.Verifier;
import felipe.luciano.support.Log;

public class Worker implements Runnable {

	private final File file;
	private final CandlestickPattern pattern;
	private final Slave slave;
	
	Worker(File fileToWork, CandlestickPattern pattern, Slave slave) {
		file = fileToWork;
		this.pattern = pattern;
		this.slave = slave;
	}
	
	@Override
	public void run() {
		
		Log.p("Usando arquivo " + file.getName() + "...");
		CSVReader reader = new CSVReader(file.getAbsolutePath(), CSVFormat.EXCEL);
		List<Candlestick> allDays = null;

		try {
			allDays = reader.csvToCandlesticks();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.p("Processando arquivo atual...");
		
		Verifier ver = new Verifier(pattern, allDays);
		GainResult result = ver.verify();
		slave.notifyResult(file.getName(), result);
	}
	
}
