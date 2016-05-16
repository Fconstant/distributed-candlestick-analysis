package felipe.luciano.support;

public final class Log {
	
	private Log(){}
	
	public static void p(Object msg){
		System.out.println(msg);
	}
	
	public static void p(Object tag, Object msg){
		System.out.println(tag + ":\t" +  msg);
	}
	
	public static void e(Object msg){
		System.err.println("ERRO: " +  msg);
	}
}
