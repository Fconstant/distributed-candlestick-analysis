package felipe.luciano.components.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import felipe.luciano.broadcast.BroadcastSender;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class SlavesManager{

	private volatile Queue<SlaveHandler> slaveQueue;

	private final ExecutorService executor;
	private final Master master;

	private boolean isEnded = false;

	SlavesManager(Master master){
		this.master = master;
		slaveQueue = new LinkedList<>();

		executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
	}

	void start(){
		// Prepara a verificação de escravos, usando Broadcast
		Log.p("Comecando a verificacao de escravos na rede...");
		BroadcastSender.INSTANCE.startSearch();

		// Prepara o recebimento dos escravos via TCP
		try {
			ServerSocket server = new ServerSocket(Consts.Components.MASTER_SLAVE_PORT);
			while(!isEnded){
				Socket serverSocket = server.accept();
				Log.p("Escravo conectado: " + serverSocket.getInetAddress().getHostName());
				SlaveHandler handler = new SlaveHandler(serverSocket, this);
				slaveQueue.add(handler);
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BroadcastSender.INSTANCE.stopSearch();
		try {
			while(!executor.awaitTermination(5, TimeUnit.SECONDS)); // Espera 5 segs para verificar de novo
			
			Log.p("Todos os escravos pararam de executar.");
			
			Log.p("Fechando conexões com escravos...");
			for(SlaveHandler handler : slaveQueue)
				handler.terminate();
			Log.p("Conexões fechadas com todos os escravos.");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void notifyNewPattern(CandlestickPattern pattern){
		if(pattern == null){
			Log.p("Encerrando operações...");
			isEnded = true;
			
		} else {
			SlaveHandler freeSlave = slaveQueue.poll();
			for(; freeSlave == null; freeSlave = slaveQueue.poll());

			freeSlave.setObjectToSend(pattern);
			executor.execute(freeSlave);
		}
	}

	public void notifyResult(GainStatistics result, SlaveHandler whichSlave) {
		slaveQueue.add(whichSlave);
		master.notifyResult(result);
	}

}
