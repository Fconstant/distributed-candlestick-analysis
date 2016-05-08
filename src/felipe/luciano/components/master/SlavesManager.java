package felipe.luciano.components.master;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import felipe.luciano.broadcast.BroadcastListener;
import felipe.luciano.broadcast.BroadcastSender;
import felipe.luciano.finances.CandlestickPattern;
<<<<<<< HEAD
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;
import felipe.luciano.support.PortHandler;

public class SlavesManager implements BroadcastListener{

	private volatile Queue<SlaveHandler> slaveQueue;
	
	private final ExecutorService executor;
	private final PortHandler portHandler;
	private final Master master;

	SlavesManager(Master master){
		this.master = master;
		slaveQueue = new LinkedList<>();
		
=======
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
>>>>>>> origin/master
		executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
		
		portHandler = new PortHandler(Consts.Components.MASTER_SLAVE_BASE_PORT);
	}
<<<<<<< HEAD
	
	void findSlaves(){
		Log.p("Comecando a verificacao de escravos na rede...");
		BroadcastSender.INSTANCE.startSearch(this);
	}

	void notifyNewPattern(CandlestickPattern pattern){
		SlaveHandler freeSlave = slaveQueue.poll();
=======


	private void startListen(){

		Log.p("Comecando a verificacao de escravos na rede...");
		BroadcastSender.INSTANCE.startSearch(this);		
	}

	public void notifyNewPattern(CandlestickPattern pattern){
		InetAddress freeSlave = slaveQueue.poll();
		
>>>>>>> origin/master
		for(; freeSlave == null; freeSlave = slaveQueue.poll());
		
		executor.execute(new SlaveHandler<CandlestickPattern>(pattern, freeSlave, this));

<<<<<<< HEAD
		freeSlave.setObjectToSend(pattern);
		executor.execute(freeSlave);
	}

	@Override
	public void onReceiveAnswer(InetAddress ip) {
		SlaveHandler handler = new SlaveHandler(ip, portHandler.getFreePort(), this);
		handler.prepareSlave();
		slaveQueue.add(handler);
=======
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

>>>>>>> origin/master
	}

	public void notifyResult(GainStatistics result) {
		master.notifyResult(result);
	}

	@Override
	public void onReceiveAnswer(InetAddress ip) {


	}


}
