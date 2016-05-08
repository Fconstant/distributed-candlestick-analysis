package felipe.luciano.support;

public final class Consts {
	
	private Consts(){}
	
	public final static class Files {

		private Files(){}
		
		public static final int BASE_PORT = 18500;
		
		public static final int FILE_BUFFER_LENGTH = 100;
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

		public static final long SEND_DELAY = 15 * 1000; // 15s
		public static final int BUFFER_LENGTH = 50;
	}
	
	public final static class Components {

		private Components(){}

		// Client Ports
		public static final int CLIENT_PORT = 20000;

		// Slave PortsPorts
		public static final int SLAVE_RAW_PORT = 20500;
		public static final int SLAVE_BASE_PORT = SLAVE_RAW_PORT + 1;
		// public static final int SLAVE_RECEIVE_BASE_PORT = 25500;
		
		// Master Ports
		public static final int MASTER_CLIENT_PORT = CLIENT_PORT;
		public static final int MASTER_SLAVE_BASE_PORT = SLAVE_BASE_PORT;
		// public static final int MASTER_RECEIVE_SLAVE_PORT = SLAVE_SEND_BASE_PORT;
		// public static final int MASTER_SEND_SLAVE_PORT = SLAVE_RECEIVE_BASE_PORT;
	}

}
