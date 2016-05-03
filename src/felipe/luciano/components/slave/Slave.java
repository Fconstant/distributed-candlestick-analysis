package felipe.luciano.components.slave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;

import felipe.luciano.broadcast.BroadcastReceiver;
import felipe.luciano.finances.Candlestick;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainResult;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class Slave {

	private InetAddress masterAdress;
	
	private ServerSocket serverSocket;
	private Socket socket;

	public static void main(String[] args) {
		new Slave().run();
	}

	public void run() {

		Log.p("Escravo iniciado.");
		masterAdress = BroadcastReceiver.INSTANCE.receiveAndAnswer();
		
		try {

			// Aqui o Escravo fara papel de servidor para receber os arquivos de financas
			Log.p("Aguardando requisicao do Mestre...");
			serverSocket = new ServerSocket(Consts.Ports.SLAVE_RECEIVE);
			socket = serverSocket.accept();

			Log.p("Mestre conectado. Comecando a receber arquivos...");
			BufferedInputStream bufferInput = new BufferedInputStream(socket.getInputStream());
			DataInputStream input = new DataInputStream(bufferInput);
			int numArquivos = input.readInt();

			new File("files/").mkdir();
			for(int i = 0 ; i < numArquivos ; i++){

				Log.p("Comecando a receber arquivos do emissor...");
				String nomeArquivo = input.readUTF();
				long tamArquivo = input.readLong();

				Log.p("Recebendo arquivo: " + nomeArquivo + ", Tamanho: " + tamArquivo / 1000 + " KB...");

				BufferedOutputStream fileWriter = new BufferedOutputStream(
						new FileOutputStream("files/" + nomeArquivo));

				for (int readBytes = 0; readBytes < tamArquivo; readBytes++)
					fileWriter.write(bufferInput.read());

				fileWriter.close();
			}
			input.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		beginWork();

	}

	private void beginWork(){

		CandlestickPattern curPattern = null;
		try {

			// Aqui o Escravo fara papel de servidor para receber os arquivos de financas
			ServerSocket a = new ServerSocket(Consts.Ports.SLAVE_RECEIVE);
			Log.p("Aguardando requisicao do Mestre...");
			Socket sk = a.accept();

			Log.p("Requisicao de novo objeto chegou... Recebendo...");
			InputStream in = sk.getInputStream();
			ObjectInputStream reader = new ObjectInputStream(in);

			curPattern = (CandlestickPattern) reader.readObject();

			Log.p("Objeto recebido:\n" + curPattern);
			
			sk.close();
			a.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


		File folder = new File("files/");

		GainStatistics statistics = new GainStatistics(folder.list().length, curPattern);

		Log.p("Comecando o processamento em massa...");
		for(File file : folder.listFiles()){

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
				boolean check = curPattern.verify(allDays.subList(i - 4, i));

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
			statistics.put(file.getName(), result);
		}
		Log.p("Estatisticas finais:\n" + statistics);
		
		Socket sk;
		try {
			sk = new Socket(masterAdress, Consts.Ports.SLAVE_SEND);

			OutputStream out = sk.getOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(out);
			
			writer.writeObject(statistics);
			
			sk.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
