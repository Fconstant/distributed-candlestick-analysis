package felipe.luciano.broadcast;

import java.net.InetAddress;

public interface BroadcastListener {

	void onReceiveAnswer(InetAddress ip);
	
}
