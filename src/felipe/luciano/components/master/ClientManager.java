package felipe.luciano.components.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class ClientManager extends Thread {

	private ServerSocket serverSk;
	private Socket clientSk;
	private Master master;

	ClientManager(Master master) {
		this.master = master;
	}

	public void run(){

		try {
			serverSk = new ServerSocket(Consts.Components.MASTER_CLIENT_PORT);

			clientSk = serverSk.accept();

			Log.p("Cliente conectado: " + clientSk.getInetAddress());
			new Thread(receiver).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Runnable receiver = new Runnable() {
		public void run() {

			try {
				ObjectInputStream reader = new ObjectInputStream(clientSk.getInputStream());

				CandlestickPattern pattern;
				do {
					pattern = (CandlestickPattern) reader.readObject();
					if(pattern == null) {
						Log.p("Cliente parou de enviar objetos");
					} else {
						Log.p("Objeto Recebido pelo Server:\n\n" + pattern.toString());
					}
					master.notifyNewPattern(pattern);
				} while(pattern != null);

			} catch (IOException e) { 
				Log.e("Ocorreu um erro ao receber objetos do Client");

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	};

	public void sendToClient(GainStatistics stat){
		ObjectOutputStream writer;
		try {
			Log.p("Reenviando objeto ao cliente...");
			writer = new ObjectOutputStream(clientSk.getOutputStream());
			writer.writeObject(stat);
			Log.p("Objeto reenviado ao cliente.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
