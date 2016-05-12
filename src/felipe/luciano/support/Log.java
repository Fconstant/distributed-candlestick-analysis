package felipe.luciano.support;

public final class Log {
	
	private Log(){}
	
	private static StackTraceElement getCalledClassStack(){
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		return stack[stack.length - 2];
	}
	
	public static void p(Object msg){
		StackTraceElement el = getCalledClassStack();
		String klass = el.getClassName();
		
		System.out.println("[ " + klass.substring(klass.lastIndexOf('.') + 1) + "." + el.getMethodName() + "() ] " + msg);
	}
	
	public static void p(Object tag, Object msg){
		StackTraceElement el = getCalledClassStack();
		String klass = el.getClassName();
		
		System.out.println("[ " + klass.substring(klass.lastIndexOf('.') + 1) + "." + el.getMethodName() + "() ] " + tag + ": " +  msg);
	}
	
	public static void e(Object msg){
		StackTraceElement el = getCalledClassStack();
		String klass = el.getClassName();
		
		System.err.println("[ " + klass.substring(klass.lastIndexOf('.') + 1) + "." + el.getMethodName() + "() ] ERROR: " +  msg);
	}
}
