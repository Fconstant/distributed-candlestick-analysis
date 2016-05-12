package felipe.luciano.components.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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

	private Socket socket;
	private boolean isEnded = false;
	
	public static void main(String[] args) {
		new Client().run();
	}

	public void run(){
		Log.p("Cliente iniciado.");
		new Thread(resultReceiver).start();
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Digite o IP do mestre: ");
		String masterIP = scan.next();
		
		try {
			socket = new Socket(InetAddress.getByName(masterIP), Consts.Components.CLIENT_PORT);
			socket.setKeepAlive(true);
			Log.p("Mestre conectado com sucesso.");

			String exp = null;
			int expCount = 1;
			ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
			do {
				System.out.println("Entre um conjunto de expressoes abaixo\nDigite PROX para ir pra proximo conjunto de expressoes,"
						+ "ou PARAR para parar a execucao e somente aguardar a resposta do Mestre");
				List<Expression> exps = new ArrayList<Expression>();

				boolean end;
				System.out.println("Conjunto Nº" + expCount++);
				do{
					System.out.print("EXP: ");
					exp = scan.nextLine();
					end = exp.equalsIgnoreCase("PROX") || exp.equalsIgnoreCase("PARAR");
					if(!end){
						exps.add(new Expression(exp));
					}
				} while(!end);

				if(!exps.isEmpty()){
					CandlestickPattern pat = new CandlestickPattern(exps);

					Log.p("Enviando expressões...");
					writer.writeObject(pat);
					Log.p("Expressões enviadas.");

					writer.flush();
				}

			} while(!exp.equalsIgnoreCase("PARAR"));
			writer.close();
			scan.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		isEnded = true;
	}

	private final Runnable resultReceiver = new Runnable() {

		@Override
		public void run() {
			try {
				while(!isEnded){
					ObjectInputStream objReceiver = new ObjectInputStream(socket.getInputStream());

					GainStatistics result = (GainStatistics) objReceiver.readObject();
					Log.p(result);
				}
				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	};


}
