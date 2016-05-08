package felipe.luciano.components.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class ClientManager {

	private ServerSocket serverSk;
	private Socket clientSk;
	private InetAddress client;
	private Master master;
	
	ClientManager(Master master) {
		this.master = master;
	}
	
	public InetAddress getClientAddress(){
		return client;
	}
	
	public void acceptClient(){

		try {
			serverSk = new ServerSocket(Consts.Components.MASTER_CLIENT_PORT);
			
			clientSk = serverSk.accept();
			client = clientSk.getInetAddress();
			
			Log.p("Cliente conectado: " + client);
			new Thread(receiver).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Runnable receiver = new Runnable() {
		public void run() {

			try {
				ObjectInputStream reader = new ObjectInputStream(clientSk.getInputStream());
				
				while(true){
					CandlestickPattern pattern = (CandlestickPattern) reader.readObject();
					if(pattern == null) break;
					Log.p("Objeto Recebido pelo Server:\n\n" + pattern.toString());
					master.notifyNewPattern(pattern);
				}
				
				reader.close();
				clientSk.close();
				serverSk.close();
				
			} catch (IOException e) { 
				Log.e("Ocorreu um erro ao receber objetos do Client");
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				
			} finally {
				serverSk = null;
				clientSk = null;
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
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
