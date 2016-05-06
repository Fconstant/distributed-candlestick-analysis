package felipe.luciano.support;

public final class Consts {
	
	private Consts(){}
	
	public final static class Files {

		private Files(){}
		
		public static final int BASE_PORT = 18500;
		
		public static final int FILE_BUFFER = 50;
		public static final String FILES_LOCATION = "finance/";
		
	}

	public final static class Finances {

		private Finances() {}
		
		public static final String EXP_REGEX = "[P][1-3]_[A-Z]+";
		
	}
	
	public final static class Broadcast {

		private Broadcast() {}
		
		// Broadcast Ports
		public static final int BROADCAST_SEARCH = 25000;
		public static final int BROADCAST_ANSWER = 25010;

		public static final long SEND_DELAY = 10 * 1000; // 10s
		
	}
	
	public final static class Components {

		private Components(){}

		// Client Ports
		public static final int CLIENT_SEND_PORT = 20000;
		public static final int CLIENT_RECEIVE_PORT = 20010;

		// Slave Ports
		public static final int SLAVE_SEND_BASE_PORT = 20500;
		public static final int SLAVE_RECEIVE_BASE_PORT = 25500;
		
		// Master Ports
		public static final int MASTER_RECEIVE_CLIENT_PORT = CLIENT_SEND_PORT;
		public static final int MASTER_SEND_CLIENT_PORT = CLIENT_RECEIVE_PORT;
		// public static final int MASTER_RECEIVE_SLAVE_PORT = SLAVE_SEND_BASE_PORT;
		// public static final int MASTER_SEND_SLAVE_PORT = SLAVE_RECEIVE_BASE_PORT;
	}

}
