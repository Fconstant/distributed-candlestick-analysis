package felipe.luciano.support;

import java.io.IOException;
import java.net.ServerSocket;

public class PortHandler {

	private int curPort;

	private PortHandler(int basePort){
		curPort = basePort;
	}

	public int getFreePort(){

		for(boolean flag = false; !flag;){
			try {
				ServerSocket sk = new ServerSocket(curPort);
				sk.close();
				flag = true;
			} catch (IOException e) {
				curPort++;
			}
		}
		return curPort;
	}
}
