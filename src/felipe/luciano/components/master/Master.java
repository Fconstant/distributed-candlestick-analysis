package felipe.luciano.components.master;

import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Log;


/**
 * Classe que representa a máquina mestre, ela não faz nenhum tipo de processamento. Apenas a iniciação e interligação de dois componentes: {@link SlavesManager} e {@link ClientManager}.
 * @see {@link SlavesManager} {@link ClientManager}
 * @author Felipe
 * @author Luciano
 */
public class Master {

	// Vars
	private SlavesManager slavesManager;
	private ClientManager clientManager;

	public static void main(String[] args) {
		new Master().start();
	}

	public Master() {
		clientManager = new ClientManager(this);
		slavesManager = new SlavesManager(this);
	}

	public void start(){
		Log.p("Mestre iniciado.");

		// Configurando e iniciando o SlavesManager
		slavesManager.start();
		
		// Configurando e iniciando o ClientManager
		clientManager.acceptClient();
	}

	void notifyNewPattern(CandlestickPattern pattern){
		slavesManager.notifyNewPattern(pattern);
	}

	void notifyResult(GainStatistics result){
		clientManager.sendToClient(result);
	}

}
