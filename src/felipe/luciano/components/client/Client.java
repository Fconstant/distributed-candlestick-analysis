package felipe.luciano.components.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import felipe.luciano.finances.CandlestickPattern;
import felipe.luciano.finances.Expression;
import felipe.luciano.finances.GainStatistics;
import felipe.luciano.support.Consts;
import felipe.luciano.support.Log;

public class Client{

	private static final String MASTER_IP = "silverteam.ddns.net";

	public static void main(String[] args) {
		new Client().run();
	}

	private final Runnable resultReceiver = new Runnable() {

		@Override
		public void run() {
			try {
				ServerSocket serverReceiver = new ServerSocket(Consts.Ports.CLIENT_RECEIVE);
				while(true){
					Socket a = serverReceiver.accept();
					ObjectInputStream objReceiver = new ObjectInputStream(a.getInputStream());

					GainStatistics result = (GainStatistics) objReceiver.readObject();
					Log.p(result);
				}

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	};

	public void run(){

		new Thread(resultReceiver).start();

		try {

			Log.p("Cliente iniciando...");
			Socket sk = new Socket(InetAddress.getByName(MASTER_IP), Consts.Ports.CLIENT_SEND); // TODO DEFINIR IP COM DDNS DEPOIS
			sk.setKeepAlive(true);
			Log.p("Cliente conectado.");

			String exp = null;
			int expCount = 1;
			Scanner scan = new Scanner(System.in);
			ObjectOutputStream writer = new ObjectOutputStream(sk.getOutputStream());
			do {
				Log.p("Entre com as expressoes abaixo\nDigite NEXT para ir pra proximo conjunto de express�es,"
						+ "ou EXIT para parar a execucao e somente aguardar a resposta do Mestre");
				List<Expression> exps = new ArrayList<Expression>();

				boolean end;
				Log.p("Conjunto de expressoes Num " + expCount++);
				do{
					System.out.print("exp: ");
					exp = scan.nextLine();
					end = exp.equalsIgnoreCase("NEXT") || exp.equalsIgnoreCase("EXIT");
					if(!end){
						exps.add(new Expression(exp));
					}
				} while(!end);

				if(!exps.isEmpty()){
					CandlestickPattern pat = new CandlestickPattern(exps);

					Log.p("Enviando dados...");
					writer.writeObject(pat);
					Log.p("Dados enviados.");

					writer.flush();
				}

			} while(!exp.equalsIgnoreCase("EXIT"));
			writer.close();
			scan.close();
			sk.close();

		} catch (IOException e) {
			e.printStackTrace();
		}



	}

}
