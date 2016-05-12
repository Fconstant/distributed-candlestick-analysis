package felipe.luciano.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Consts.Broadcast;

// Singleton class
public enum BroadcastSender {
	INSTANCE;

	private Thread sender;
	private Set<InetAddress> broadcastAddresses;

	public void startSearch(){
		findBroadcastAddresses();
		
		sender = new Thread(senderRunnable);
		sender.start();
	}

	public void stopSearch(){
		if(sender != null && sender.isAlive()){
			sender.interrupt();
		}
	}

	private void findBroadcastAddresses(){

		try {
			broadcastAddresses = new HashSet<>();
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback())
					continue;

				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast != null){
						broadcastAddresses.add(broadcast);
						sendMessage(broadcast);
					}
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(InetAddress broadcastAddress){
		try {
			DatagramSocket sk = new DatagramSocket();
			sk.setBroadcast(true);

			byte[] buffer = new byte[1];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
					broadcastAddress, Broadcast.BROADCAST_SEARCH);
			
			sk.send(packet);
			sk.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final Runnable senderRunnable = new Runnable() {
		public void run() {
			try {
				for(;;){
					for(InetAddress broadcast : broadcastAddresses)
						sendMessage(broadcast);
					Thread.sleep(Consts.Broadcast.SEND_DELAY); // Coloca a thread pra dormir para executar o mesmo serviço varias vezes
				}
			} catch (InterruptedException e) {
				// Fazer nada
			}
		}
	};
}
