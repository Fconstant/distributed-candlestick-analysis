package felipe.luciano.components.master;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import felipe.luciano.broadcast.BroadcastListener;
import felipe.luciano.broadcast.BroadcastSender;
import felipe.luciano.finances.CandlestickPattern;
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
		
		executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
		
		portHandler = new PortHandler(Consts.Components.MASTER_SLAVE_BASE_PORT);
	}
	
	void findSlaves(){
		Log.p("Comecando a verificacao de escravos na rede...");
		BroadcastSender.INSTANCE.startSearch(this);
	}

	void notifyNewPattern(CandlestickPattern pattern){
		SlaveHandler freeSlave = slaveQueue.poll();
		for(; freeSlave == null; freeSlave = slaveQueue.poll());

		freeSlave.setObjectToSend(pattern);
		executor.execute(freeSlave);
	}

	@Override
	public void onReceiveAnswer(InetAddress ip) {
		SlaveHandler handler = new SlaveHandler(ip, portHandler.getFreePort(), this);
		handler.prepareSlave();
		slaveQueue.add(handler);
	}

	public void notifyResult(GainStatistics result) {
		master.notifyResult(result);
	}

}
