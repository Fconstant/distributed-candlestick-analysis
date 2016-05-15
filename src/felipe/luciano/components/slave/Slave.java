package felipe.luciano.components.slave;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import felipe.luciano.broadcast.BroadcastReceiver;
import felipe.luciano.files.FileReceiver;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainResult;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class Slave {

	private Socket socket;
	private ExecutorService executor;

	private volatile GainStatistics statistics;

	public static void main(String[] args) {
		new Slave().start();
	}

	public void start() {
		Log.p("Escravo iniciado.");
		InetAddress masterIP = BroadcastReceiver.INSTANCE.receive();

		try {
			// Conectando com o mestre
			Log.p("Conectando-se ao Mestre " + masterIP.getHostName() + "...");
			socket = new Socket(masterIP, Consts.Components.SLAVE_PORT);
			Log.p("Conectado com o Mestre.");

			receiveFiles();
			
		} catch (IOException e) {
			Log.e("Erro ao conectar-se com o Mestre");
		}

		boolean finished = false;
		do{
			finished = work();
		} while(!finished);
		
		try {
			Log.p("Fechando conexão e terminando execução...");
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean receiveFiles() throws IOException{
		FileReceiver receiver = new FileReceiver(socket.getInetAddress().getHostName(), socket.getInputStream());
		boolean res = receiver.receiveAndSave(); 
		if(res)
			Log.p("Arquivos recebidos com sucesso!");
		else
			Log.e("Houve um problema ao receber arquivos. Tente rodar novamente.");

		return res;
	}

	private boolean work(){

		// Aqui serão recebidos os objetos vindos do Master
		CandlestickPattern curPattern = null;
		try {
			ObjectInputStream masterReader = new ObjectInputStream(socket.getInputStream());

			Log.p("Aguardando requisição de novo objeto...");
			curPattern = (CandlestickPattern) masterReader.readObject();
			masterReader.close();
			Log.p("Objeto recebido:\n" + curPattern);
		} catch (ClassNotFoundException | IOException e) {
			return false;
		}

		// Começo do processamento dos arquivos de finanças
		Log.p("Comecando o processamento em massa...");

		File folder = new File(Consts.Files.FILES_LOCATION);
		statistics = new GainStatistics(folder.list().length, curPattern);
		executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()); // Criando uma piscina de Threads

		for(File file : folder.listFiles()){
			executor.execute(new Worker(file, curPattern, this));
		}
		
		// Verifica se todos os Threads já acabaram
		try {
			while(!executor.awaitTermination(3, TimeUnit.SECONDS)); // Espera 3 segs para verificar de novo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(statistics.resultSize() != folder.list().length){
			Log.e("Parece que ficou faltando algum(s) arquivo(s) para serem processado(s). Prosseguindo mesmo assim...");
		}
		
		try {
			ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
			writer.writeObject(statistics);

			writer.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	void notifyResult(String name, GainResult result){
		statistics.put(name, result);
	}

}
