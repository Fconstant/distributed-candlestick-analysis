package felipe.luciano.components.master;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
			try {
				sendFiles();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	private void sendFiles() throws IOException{
		File folder = new File(Consts.Files.FILES_LOCATION);

		FileTransmissor transmissor = new FileTransmissor(
				slaveSocket.getInetAddress().getHostName(), slaveSocket.getOutputStream());
		transmissor.send(folder);
	}

	// Envia o objeto em si, no caso o CandlestickPattern
	private void sendObject(){

		String host = slaveSocket.getInetAddress().getHostAddress();
		
		try {
			if(objectToSend == null){
				Log.e("Object a ser enviado é nulo, abortando...");
				slaveSocket.close();
				return;
			}

			Log.p("Enviando objeto para " + host + "...");

			// Envio de Objeto
			OutputStream out = slaveSocket.getOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(out);

			writer.writeObject(objectToSend);
			writer.flush();
			Log.p("Objeto Enviado!");

			// Recepção de Resultados
			InputStream in = slaveSocket.getInputStream();
			ObjectInputStream reader = new ObjectInputStream(in);
			Log.p("Aguardando resposta de " + host);

			while(reader.read() != 105){
				System.out.print("");
			}
			Log.p("Lendo objeto...");
			manager.notifyResult((GainStatistics) reader.readObject(), this);

			Log.p("Escravo " + host + " voltou a ficar ocioso.");

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
