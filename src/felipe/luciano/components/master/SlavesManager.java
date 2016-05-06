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

import felipe.luciano.broadcast.BroadcastListener;
import felipe.luciano.broadcast.BroadcastSender;
import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class SlavesManager implements BroadcastListener{

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


	private void startListen(){

		Log.p("Comecando a verificacao de escravos na rede...");
		BroadcastSender.INSTANCE.startSearch(this);		
	}

	public void notifyNewPattern(CandlestickPattern pattern){
		InetAddress freeSlave = slaveQueue.poll();
		
		for(; freeSlave == null; freeSlave = slaveQueue.poll());
		
		executor.execute(new SlaveHandler<CandlestickPattern>(pattern, freeSlave, this));

	}

	public void findSlaves(){

		startListen();

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


	@Override
	public void onReceiveAnswer(InetAddress ip) {


	}


}
