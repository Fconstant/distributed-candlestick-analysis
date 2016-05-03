package felipe.luciano.support;

public final class Log {
	
	private Log(){}
	
	private static StackTraceElement getCalledClassStack(){
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		return stack[stack.length - 2];
	}
	
	public static void p(Object msg){
		StackTraceElement el = getCalledClassStack();
		Log.p("[ " + el.getClassName() + "." + el.getMethodName() + " ] " + msg);
	}
	
	public static void p(Object tag, Object msg){
		StackTraceElement el = getCalledClassStack();
		Log.p("[ " + el.getClassName() + "." + el.getMethodName() + " ] " + tag + ": " +  msg);
	}
}
