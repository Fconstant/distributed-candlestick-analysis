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
import felipe.luciano.support.Log;

// Singleton class
public enum BroadcastSender {
	INSTANCE;

	private BroadcastListener listener;
	private String message;
	private Thread sender, receiver;
	private Set<InetAddress> broadcastAddresses;

	public void startSearch(BroadcastListener listener, String messageToSend){
		this.listener = listener;
		this.message = messageToSend;
		
		sender = new Thread(senderRunnable);
		receiver = new Thread(receiverRunnable);

		findBroadcastAddresses();
		receiver.start();
	}
	
	public void startSearch(BroadcastListener listener){
		startSearch(listener, null);
	}

	public void stopSearch(){
		if(sender != null && sender.isAlive()){
			sender.interrupt();
		}

		if(receiver != null && receiver.isAlive()){
			receiver.interrupt();
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

			byte[] fullBuff = message.getBytes();
			byte[] buffer = message.getBytes();
			
			DatagramPacket packet = null;
			for(int byteCount = 0; byteCount < fullBuff.length ; byteCount++)
				
			packet = new DatagramPacket(buffer, Short.BYTES,
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


	private final Runnable receiverRunnable = new Runnable() {
		public void run() {
			
			DatagramSocket sk;
			try {
				sk = new DatagramSocket(Broadcast.BROADCAST_ANSWER);

				while(true){
					byte[] buffer = new byte[1];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

					sk.receive(packet);

					InetAddress inet = packet.getAddress();
					Log.p("Máquina reconhecida: " + inet.getHostName());
					listener.onReceiveAnswer(inet);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
