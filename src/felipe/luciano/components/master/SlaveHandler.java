package felipe.luciano.components.master;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import felipe.luciano.files.FileTransmissor;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class SlaveHandler extends Thread{

	private final SlavesManager manager;
	private final Socket slaveSocket;

	private volatile boolean isPrepared = false;
	private CandlestickPattern objectToSend;

	SlaveHandler(Socket slaveSocket, SlavesManager manager) {
		this.manager = manager;
		this.slaveSocket = slaveSocket;
		prepareSlave();
	}

	private void prepareSlave(){
		if(!isPrepared){
			sendFiles();
			isPrepared = true;	
		}
	}

	public void terminate(){
		try {
			slaveSocket.close();
		} catch (IOException e) {
			Log.e("Não foi possível fechar socket de " + 
					slaveSocket.getInetAddress().getHostName() + ": Ainda sendo usado");
		}
	}

	@Override
	public void run() {
		while(!isPrepared){
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendObject();
	}

	public void setObjectToSend(CandlestickPattern pattern) {
		objectToSend = pattern;
	}

	// Envia os arquivos de finanças para o escravo
	private void sendFiles(){
		File folder = new File(Consts.Files.FILES_LOCATION);

		FileTransmissor transmissor = new FileTransmissor(slaveSocket);
		transmissor.send(folder);
	}

	// Envia o objeto em si, no caso o CandlestickPattern
	private void sendObject(){

		String host = slaveSocket.getInetAddress().getHostAddress();

		if(objectToSend == null){
			Log.e("Object a ser enviado é nulo, abortando...");
			return;
		}

		try {
			Log.p("Enviando objeto para " + host + "...");

			// Envio de Objeto
			OutputStream out = slaveSocket.getOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(out);

			writer.writeObject(objectToSend);
			Log.p("Objeto Enviado!");

			// Recepção de Resultados
			Log.p("Aguardando resposta de " + host);
			ObjectInputStream slaveReader = new ObjectInputStream(slaveSocket.getInputStream());

			manager.notifyResult((GainStatistics) slaveReader.readObject(), this);
			Log.p("Escravo " + host + " voltou a ficar ocioso.");

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
