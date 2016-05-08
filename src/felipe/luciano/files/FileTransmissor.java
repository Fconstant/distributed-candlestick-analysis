package felipe.luciano.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class FileTransmissor {

	private InetAddress sendTo;
	private int port;

	public FileTransmissor(InetAddress sendTo, int port) {
		this.port = port;
		this.sendTo = sendTo;
	}

	public boolean send(File fileToSend){

		try {
			Socket sk = new Socket(sendTo, port);
			BufferedOutputStream saidaBuffer = new BufferedOutputStream(sk.getOutputStream(), Consts.Files.FILE_BUFFER_LENGTH);
			DataOutputStream saidaData = new DataOutputStream(saidaBuffer);

			Log.p("Enviando arquivos para a máquina " + sendTo.getHostName() + "...");

			byte[] buffer = new byte[Consts.Files.FILE_BUFFER_LENGTH];

			File[] files = prepareFileOrFolder(fileToSend);
			saidaData.writeInt(files.length); // Manda o numero de arquivos pro Slave

			for(File file : files){
				BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file), Consts.Files.FILE_BUFFER_LENGTH);

				saidaData.writeUTF(file.getName());
				saidaData.writeLong(file.length());

				Log.p(sendTo.getHostName() + ": Enviando '" + file.getName() + "', Tamanho: " + file.length() / 1000 + " KB");

				int byteCount = 0;
				while ((byteCount = fileReader.read(buffer, 0, Consts.Files.FILE_BUFFER_LENGTH)) != -1){
					saidaBuffer.write(buffer, 0, byteCount);
				}
				fileReader.close();

			}
			Log.p("Todos os arquivos enviados para: " + sendTo.getHostName());

			saidaBuffer.flush();
			saidaData.close();
			saidaBuffer.close();
			sk.close();
			
			return true;

		} catch (IOException e) {

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

