package felipe.luciano.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class FileTransmissor {

    private Socket socket;

	public FileTransmissor(Socket socketToSend) {
		socket = socketToSend;
	}

	public boolean send(File fileToSend){

        String host = socket.getInetAddress().getHostName();
		try {
			BufferedOutputStream saidaBuffer = new BufferedOutputStream(socket.getOutputStream(), Consts.Files.FILE_BUFFER_LENGTH);
			DataOutputStream saidaData = new DataOutputStream(saidaBuffer);

			Log.p("Enviando arquivos para a máquina " + host + "...");

			byte[] buffer = new byte[Consts.Files.FILE_BUFFER_LENGTH];

			File[] files = prepareFileOrFolder(fileToSend);
			saidaData.writeInt(files.length); // Manda o numero de arquivos pro Slave

			for(File file : files){
				BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file), Consts.Files.FILE_BUFFER_LENGTH);

				saidaData.writeUTF(file.getName());
				saidaData.writeLong(file.length());

				Log.p(host + ": Enviando '" + file.getName() + "', Tamanho: " + file.length() / 1000 + " KB");

				int byteCount = 0;
				while ((byteCount = fileReader.read(buffer, 0, Consts.Files.FILE_BUFFER_LENGTH)) != -1){
					saidaBuffer.write(buffer, 0, byteCount);
				}
				fileReader.close();

			}
			Log.p("Todos os arquivos enviados para: " + host);

			saidaBuffer.flush();
			saidaData.close();
			saidaBuffer.close();
			return true;

		} catch (IOException e) {
			Log.e("Problema ao enviar arquivo - " + e.getMessage());
		}
		return false;
	}


	private File[] prepareFileOrFolder(File fileOrFolder){

		if(fileOrFolder.isDirectory()){
			return fileOrFolder.listFiles();
		}
		return new File[]{ fileOrFolder };

	}

}

