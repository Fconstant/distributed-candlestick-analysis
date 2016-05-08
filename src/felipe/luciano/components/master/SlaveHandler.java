package felipe.luciano.components.master;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import felipe.luciano.files.FileTransmissor;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class SlaveHandler extends Thread{

	private final InetAddress slave;
	private final SlavesManager manager;
	private final int port;

	private boolean isPrepared = false;
	private CandlestickPattern objectToSend;
	private Socket slaveSocket;

	SlaveHandler(InetAddress slave, int port, SlavesManager manager) {
		this.slave = slave;
		this.manager = manager;
		this.port = port;
	}

	public void prepareSlave(){
		sendPort();
		sendFiles();
		isPrepared = true;
	}

	@Override
	public void run() {
		while(!isPrepared){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendObject();
	}

	public void setObjectToSend(CandlestickPattern pattern) {
		objectToSend = pattern;
	}

	// Envia a porta em que o escravo deve se conectar e ficar conectado até o fim do programa
	private void sendPort(){
		try {
			DatagramSocket sk = new DatagramSocket();
			byte[] buffer = String.valueOf(port).getBytes();

			DatagramPacket packet = new DatagramPacket(buffer, Consts.Broadcast.BUFFER_LENGTH,
					slave, Consts.Components.SLAVE_RAW_PORT);

			Log.p("Enviando pacote contendo porta que o escravo deve se conectar: " + port);
			sk.send(packet);
			sk.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Envia os arquivos de finanças para o escravo
	private void sendFiles(){
		File folder = new File(Consts.Files.FILES_LOCATION);

		FileTransmissor transmissor = new FileTransmissor(slave, port);
		transmissor.send(folder);
	}

	// Envia o objeto em si, no caso o CandlestickPattern
	private void sendObject(){

		if(objectToSend == null){
			Log.e("Object a ser enviado é nulo, abortando...");
			return;
		}

		while(slaveSocket == null || slaveSocket.isClosed()) {
			try {
				slaveSocket = new Socket(slave, port);
			} catch(IOException e){
				Log.e("Porta " + port + " em uso, aguardando para nova tentativa de conexão...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
		
		try {
			Log.p("Enviando objeto para " + slave.getHostAddress() + "...");

			// Envio de Objeto
			OutputStream out = slaveSocket.getOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(out);

			writer.writeObject(objectToSend);
			Log.p("Objeto Enviado!");

			// Recepção de Resultados
			Log.p("Aguardando resposta de " + slave);
			ObjectInputStream slaveReader = new ObjectInputStream(slaveSocket.getInputStream());

			manager.notifyResult((GainStatistics) slaveReader.readObject());
			Log.p("Escravo " + slave + " voltou a ficar ocioso.");
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
