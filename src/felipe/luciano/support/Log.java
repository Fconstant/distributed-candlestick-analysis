package felipe.luciano.support;

public final class Log {
	
	private Log(){}
	
	private static StackTraceElement getCalledClassStack(){
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		return stack[stack.length - 2];
	}
	
	public static void p(Object msg){
		StackTraceElement el = getCalledClassStack();
		System.out.println("[ " + el.getClassName() + "." + el.getMethodName() + " ] " + msg);
	}
	
	public static void p(Object tag, Object msg){
		StackTraceElement el = getCalledClassStack();
		System.out.println("[ " + el.getClassName() + "." + el.getMethodName() + " ] " + tag + ": " +  msg);
	}
	
	public static void e(Object msg){
		StackTraceElement el = getCalledClassStack();
		System.err.println("[ " + el.getClassName() + "." + el.getMethodName() + " ] ERROR: " +  msg);
	}
}
