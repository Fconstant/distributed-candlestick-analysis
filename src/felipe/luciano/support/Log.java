package felipe.luciano.support;

public final class Log {
	
	private Log(){}
	
	public static void p(Object msg){
		System.out.println(msg);
	}
	
	public static void p(Object tag, Object msg){
		System.out.println(tag + ": " +  msg);
	}
	
	public static void e(Object msg){
		System.err.println("ERROR: " +  msg);
	}
}
