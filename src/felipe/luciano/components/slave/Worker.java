package felipe.luciano.components.slave;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;

import felipe.luciano.finances.Candlestick;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainResult;
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
		GainResult result = new GainResult();
		for(int i = 4; i < allDays.size() - 3 ; i++){
			boolean check = pattern.verify(allDays.subList(i - 4, i));

			if(check){
				double compareClose = allDays.get(i).getClose() * 3;
				double sum = 0;

				for(int j = 1 ; j <= 3 ; j++)
					sum += allDays.get(i + j).getClose();

				if(compareClose > sum)
					result.countProfit();
				else
					result.countLoss();

			} else result.countNotFound();
			Log.p(result);
		}
		
		slave.notifyResult(file.getName(), result);
	}
	
}
