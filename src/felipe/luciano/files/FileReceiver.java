package felipe.luciano.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class FileReceiver {

	private int port;
	
	public FileReceiver(int port){
		this.port = port;
	}
	
	public boolean receiveAndSave(){
		try {

			// Aqui o Escravo fara papel de servidor para receber os arquivos de financas
			Log.p("Aguardando requisição do Transmissor...");
			ServerSocket serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();

			Log.p("Transmissor conectado" + socket.getInetAddress().getHostName() + ". Começando a receber arquivos...");
			BufferedInputStream bufferInput = new BufferedInputStream(socket.getInputStream());
			DataInputStream input = new DataInputStream(bufferInput);
			int numArquivos = input.readInt();

			new File(Consts.Files.FILES_LOCATION).mkdir();
			for(int i = 0 ; i < numArquivos ; i++){

				Log.p("Comecando a receber arquivos do transmissor...");
				String nomeArquivo = input.readUTF();
				long tamArquivo = input.readLong();

				Log.p("Recebendo: " + nomeArquivo + ", Tamanho: " + tamArquivo / 1000 + " KB...");

				BufferedOutputStream fileWriter = new BufferedOutputStream(
						new FileOutputStream(Consts.Files.FILES_LOCATION + nomeArquivo));

				for (int readBytes = 0; readBytes < tamArquivo; readBytes++)
					fileWriter.write(bufferInput.read());

				fileWriter.close();
			}
			input.close();
			socket.close();
			serverSocket.close();
			
			return true;
			
		} catch (SocketException e) {
			
		} catch (IOException e) {
			Log.e("Problema ao receber arquivo - " + e.getMessage());
		}
		return false;
	}
	
}
