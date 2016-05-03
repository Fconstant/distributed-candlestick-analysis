package felipe.luciano.components.slave;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.LocalDate;

import felipe.luciano.finances.Candlestick;

public class CSVReader {

	private String filePath;
	private CSVFormat csvFormat;

	public CSVReader(String filePath, CSVFormat csvFormat){
		this.filePath = filePath;
		this.csvFormat = csvFormat;
	}

	public List<Candlestick> csvToCandlesticks() throws FileNotFoundException, IOException{
		List<Candlestick> sheetsInfo = new ArrayList<>();
		FileReader in = new FileReader(filePath);

		CSVParser parser = new CSVParser(in, csvFormat.withHeader());

		for(CSVRecord records : parser){
			Candlestick info = new Candlestick(
					LocalDate.parse(records.get("Date")),
					Double.parseDouble(records.get("Open")),
					Double.parseDouble(records.get("High")), 
					Double.parseDouble(records.get("Low")),
					Double.parseDouble(records.get("Close"))
					);

			sheetsInfo.add(info);
		}
		parser.close();

		return sheetsInfo;
	}
	
}
