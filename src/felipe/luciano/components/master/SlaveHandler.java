package felipe.luciano.components.master;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Ports;

public class SlaveHandler<T> extends Thread{

	private final InetAddress slave;
	private final SlavesManager manager;
	private final T objectToSend;
	private Socket slaveSocket;

	SlaveHandler(T objectToSend, InetAddress slave, SlavesManager manager) {
		this.slave = slave;
		this.manager = manager;
		this.objectToSend = objectToSend;
	}

	@Override
	public void run() {

		try {

			if(slaveSocket == null || slaveSocket.isClosed())
				slaveSocket = new Socket(slave, Consts.Ports.MASTER_SEND_SLAVE);
			Log.p("Enviando objeto para " + slave.getHostAddress() + "...");

			// Envio de Objeto
			OutputStream out = slaveSocket.getOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(out);

			writer.writeObject(objectToSend);
			Log.p("Objeto Enviado!");

			// Recepção de Resultados
			InputStream in = slaveSocket.getInputStream();
			ObjectInputStream reader = new ObjectInputStream(in);
			
			Log.p("Escravo " + slave + " voltou a ficar ocioso.");
			
			ObjectInputStream slaveReader = new ObjectInputStream(slaveSocket.getInputStream());
			ObjectOutputStream clientWriter = new ObjectOutputStream(slaveSocket.getOutputStream());
			
			clientWriter.writeObject(slaveReader.readObject());
			slaveSk.close();
		


		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
