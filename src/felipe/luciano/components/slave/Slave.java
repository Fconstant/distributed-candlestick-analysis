package felipe.luciano.components.slave;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
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

	private ServerSocket serverSocket;
	private Socket socket;
	private ExecutorService executor;

	private volatile GainStatistics statistics;

	public static void main(String[] args) {
		new Slave().start();
	}

	public void start() {
		Log.p("Escravo iniciado.");
		BroadcastReceiver.INSTANCE.receiveAndAnswer();

		try {
			// Conectando com o mestre
			int port = getPortToConnect();
			serverSocket = new ServerSocket(port);
			Log.p("Aguardando requisicao do Mestre...");
			socket = serverSocket.accept();
			Log.p("Conectado com o mestre.");

			if(!receiveFiles(port))
				System.exit(0);
			
		} catch (IOException e) {
			Log.e("Erro ao conectar-se com o Mestre");
		}

		while(work());
		try {
			Log.p("Fechando conexões e terminando execução...");
			socket.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private int getPortToConnect(){
		try {
			DatagramSocket dsocket = new DatagramSocket(Consts.Components.SLAVE_RAW_PORT);

			byte[] buffer = new byte[Consts.Broadcast.BUFFER_LENGTH];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			Log.p("Comecando a receber pacote de requisicao...");
			dsocket.receive(packet);

			int port = Integer.valueOf(new String(packet.getData()));
			Log.p("Porta a se conectar: " + port);

			dsocket.close();
			return port; 
		} catch (IOException e) {
			Log.e("Erro ao receber pacote com a porta a se conectar");
		}

		return -1;
	}

	private boolean receiveFiles(int port){
		FileReceiver receiver = new FileReceiver(port);
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

		File folder = new File("files/");
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
			Log.e("Houve um erro: Parece que ficou faltando algum(s) arquivo(s) para serem processado(s). Prosseguindo mesmo assim...");
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
