package felipe.luciano.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class FileReceiver {

	private Socket socket;
	
	public FileReceiver(Socket socketToReceive){
		this.socket = socketToReceive;
	}
	
	public boolean receiveAndSave(){
		
		String host = socket.getInetAddress().getHostAddress();
		try {
			// Aqui o Escravo fara papel de servidor para receber os arquivos de financas
			BufferedInputStream bufferInput = new BufferedInputStream(socket.getInputStream());
			DataInputStream input = new DataInputStream(bufferInput);
			int numArquivos = input.readInt();

			Log.p("Começando a receber arquivos de " + host + "...");
			new File(Consts.Files.FILES_LOCATION).mkdir();
			for(int i = 0 ; i < numArquivos ; i++){

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

			return true;
			
		} catch (IOException e) {
			Log.e("Problema ao receber arquivo - " + e.getMessage());
		}
		return false;
	}
	
}
