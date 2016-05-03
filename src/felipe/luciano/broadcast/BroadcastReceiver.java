package felipe.luciano.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public enum BroadcastReceiver {
	INSTANCE;
	
	public InetAddress receiveAndAnswer(){
		InetAddress masterAddress = null;
		
		try {
			DatagramSocket dsocket = new DatagramSocket(Consts.Ports.BROADCAST_SEARCH);

			byte[] buffer = new byte[1];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			Log.p("Comecando a receber pacote de requisicao...");
			dsocket.receive(packet);

			masterAddress = packet.getAddress();
			Log.p("Recebido! Address do emissor: " + masterAddress);

			packet = new DatagramPacket(buffer, 1, masterAddress, Consts.Ports.BROADCAST_ANSWER);
			dsocket.send(packet);
			Log.p("Pacote reenviado ao emissor.");
			dsocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return masterAddress;
	}

}
