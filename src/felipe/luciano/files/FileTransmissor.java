package felipe.luciano.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class FileTransmissor {

    private OutputStream outStream;
    private String hostName;

	public FileTransmissor(String hostName, OutputStream outStream) {
		this.outStream = outStream;
		this.hostName = hostName;
	}

	public boolean send(File fileToSend){

		try {
			BufferedOutputStream saidaBuffer = new BufferedOutputStream(outStream, Consts.Files.FILE_BUFFER_LENGTH);
			DataOutputStream saidaData = new DataOutputStream(saidaBuffer);

			Log.p("Enviando arquivos para a máquina " + hostName + "...");

			byte[] buffer = new byte[Consts.Files.FILE_BUFFER_LENGTH];

			File[] files = prepareFileOrFolder(fileToSend);
			saidaData.writeInt(files.length); // Manda o numero de arquivos pro Slave

			for(File file : files){
				BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file), Consts.Files.FILE_BUFFER_LENGTH);

				saidaData.writeUTF(file.getName());
				saidaData.writeLong(file.length());

				Log.p(hostName + ": Enviando '" + file.getName() + "', Tamanho: " + file.length() / 1000 + " KB");

				int byteCount = 0;
				while ((byteCount = fileReader.read(buffer, 0, Consts.Files.FILE_BUFFER_LENGTH)) != -1){
					saidaBuffer.write(buffer, 0, byteCount);
				}
				fileReader.close();

			}
			Log.p("Todos os arquivos enviados para: " + hostName);

			saidaBuffer.flush();
			outStream.flush();
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

