package felipe.luciano.components.master;

<<<<<<< HEAD
=======
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

>>>>>>> origin/master
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Log;


/**
 * Classe que representa a máquina mestre, ela não faz nenhum tipo de processamento. Apenas a iniciação e interligação de dois componentes: {@link SlavesManager} e {@link ClientManager}.
 * @see {@link SlavesManager} {@link ClientManager}
 * @author Felipe
 * @author Luciano
 */
public class Master {

	// Vars
	private SlavesManager slavesManager;
	private ClientManager clientManager;

	public static void main(String[] args) {
		new Master().start();
	}

	public Master() {
		clientManager = new ClientManager(this);
		slavesManager = new SlavesManager(this);
	}

	public void start(){
		Log.p("Mestre iniciado.");
<<<<<<< HEAD
=======
		
		
		// Conectando com o cliente
		
		
		// Procura de escravos
		slavesManager.findSlaves();
		
		File folder = new File(Consts.Files.FILES_LOCATION);
		csvFiles = Arrays.asList(folder.listFiles());
		
		new Thread(clientReceiver).start();

	}
	
	public void onFindSlave(InetAddress slave) {
		
		// Comeco de envio dos arquivos
		try {	
			Socket sk = new Socket(slave, Consts.Components.MASTER_SEND_SLAVE_PORT);
			BufferedOutputStream saidaBuffer = new BufferedOutputStream(sk.getOutputStream(), Consts.Files.FILE_BUFFER);
			DataOutputStream saidaData = new DataOutputStream(saidaBuffer);
>>>>>>> origin/master

		// Configurando e iniciando o SlavesManager
		slavesManager.findSlaves();
		
		// Configurando e iniciando o ClientManager
		clientManager.acceptClient();
	}

	void notifyNewPattern(CandlestickPattern pattern){
		slavesManager.notifyNewPattern(pattern);
	}

	void notifyResult(GainStatistics result){
		clientManager.sendToClient(result);
	}

}
