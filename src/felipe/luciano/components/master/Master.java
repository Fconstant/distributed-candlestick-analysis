package felipe.luciano.components.master;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import felipe.luciano.components.master.SlavesManager.SlaveListener;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;
import felipe.luciano.support.PortHandler;

public class Master {
	
	// Vars
	private Queue<CandlestickPattern> patterns;
	private List<File> csvFiles;
	private SlavesManager slavesManager;
	private ClientManager clientManager;
	
	public static void main(String[] args) {
		new Master().run();
	}
	
	public Master() {
		clientManager = new ClientManager();
		slavesManager = new SlavesManager();
	}

	public void run(){
		Log.p("Mestre iniciado.");
		
		
		// Conectando com o cliente
		
		
		// Procura de escravos
		slavesManager.findSlaves(this);
		
		File folder = new File(Consts.Files.FILES_LOCATION);
		csvFiles = Arrays.asList(folder.listFiles());
		
		new Thread(clientReceiver).start();

	}
	
	@Override
	public void onFindSlave(InetAddress slave) {

		// Comeco de envio dos arquivos
		try {	
			Socket sk = new Socket(slave, Consts.Components.MASTER_SEND_SLAVE_PORT);
			BufferedOutputStream saidaBuffer = new BufferedOutputStream(sk.getOutputStream(), Consts.Files.FILE_BUFFER);
			DataOutputStream saidaData = new DataOutputStream(saidaBuffer);

			Log.p("Enviando arquivos para o escravo " + slave.getHostName() + "...");
			
			byte[] buffer = new byte[Consts.Files.FILE_BUFFER];
			saidaData.writeInt(csvFiles.size()); // Manda o numero de arquivos pro Slave
			
			for(File file : csvFiles){
				BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file), Consts.Files.FILE_BUFFER);
		
				saidaData.writeUTF(file.getName());
				saidaData.writeLong(file.length());

				Log.p(slave.getHostName() + ": " + file.getName() + ", Tamanho: " + file.length() / 1000 + " KB...");
				
				int byteCount = 0;
				while ((byteCount = fileReader.read(buffer, 0, Consts.Files.FILE_BUFFER)) != -1)
                    saidaBuffer.write(buffer, 0, byteCount);
                fileReader.close();

			}
			Log.p("Todos os arquivos enviados para o escravo "
					+ slave.getHostAddress() + ".");

            saidaBuffer.flush();
            saidaData.close();
            saidaBuffer.close();
			sk.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private final Runnable clientReceiver = new Runnable() {
		
		@Override
		public void run() {

			try {
				ServerSocket a = new ServerSocket(Consts.Components.MASTER_RECEIVE_CLIENT_PORT);
				patterns = new LinkedList<>();

				Socket sk = a.accept();
				client = sk.getInetAddress();
				Log.p("Cliente conectado: " + client);
				new Thread(slaveResponseListener).start();
				
				InputStream in = sk.getInputStream();
				ObjectInputStream reader = new ObjectInputStream(in);
				
				while(true){
					CandlestickPattern pattern = (CandlestickPattern) reader.readObject();
					if(pattern == null) break;
					Log.p("Objeto Recebido pelo Server:\n\n" + pattern.toString());
					patterns.add(pattern);
					slavesManager.notifyNewPattern(pattern);
				}
				reader.close();
				in.close();
				sk.close();
				a.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	};
	

	private final Runnable slaveResponseListener = new Runnable() {

		@Override
		public void run() {
			try {
				Socket clientSk = new Socket(client, Consts.Components.MASTER_SEND_CLIENT_PORT);
				ServerSocket a = new ServerSocket(Consts.Components.MASTER_RECEIVE_SLAVE_PORT);
				while(true){
					Socket slaveSk = a.accept();
					slavesManager.slaveQueue.add(slaveSk.getInetAddress());
					Log.p("Escravo " + slaveSk.getInetAddress() + " voltou a ficar ocioso.");
					
					ObjectInputStream slaveReader = new ObjectInputStream(slaveSk.getInputStream());
					ObjectOutputStream clientWriter = new ObjectOutputStream(clientSk.getOutputStream());
					
					clientWriter.writeObject(slaveReader.readObject());
					slaveSk.close();
				}
				
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	};

	

}
