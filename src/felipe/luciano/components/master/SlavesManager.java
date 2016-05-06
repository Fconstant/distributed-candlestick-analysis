package felipe.luciano.components.master;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.support.Log;
import felipe.luciano.support.Ports;

public class SlavesManager{

	private Queue<InetAddress> slaveQueue = new LinkedList<>();
	private ExecutorService executor;

	private final Runnable slaveConnectRun = new Runnable() {
		@Override
		public void run() {
			
		}
	};

	public SlavesManager(){
		executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
	}
	
	private void startListen(SlaveListener listener){
		this.listener = listener;
		Log.p("Comecando a verificacao de escravos na rede...");
		slaveListenerThread.start();
	}

	public void notifyNewPattern(CandlestickPattern pattern){
		InetAddress freeSlave = slaveQueue.poll();
		for(; freeSlave == null; freeSlave = slaveQueue.poll());

		try {
			
			executor.execute(slaveConnectRun);
			
			Log.p("Enviando objeto para " + freeSlave.getHostAddress() + "...");
			Socket sk = new Socket(freeSlave, Consts.Components.MASTER_SEND_SLAVE);

			OutputStream out = sk.getOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(out);

			writer.writeObject(pattern);

			Log.p("Objeto Enviado!");
			sk.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void findSlaves(SlaveListener listener){

		startListen(listener);

		try {

			Log.p("Encontrando maquinas-escravo...");
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback())
					continue;

				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;

					// Aqui vamos checar se o escravo responde a mensagem que enviamos
					DatagramSocket sk = new DatagramSocket();
					sk.setBroadcast(true);

					byte[] buffer = new byte[1]; // Buffer com nada
					DatagramPacket packet = new DatagramPacket(buffer, 1,
							broadcast, Consts.Components.MASTER_SEND_SLAVE);

					sk.send(packet);
					sk.close();
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
