package felipe.luciano.support;

public final class Consts {
	
	private Consts(){}
	
	public final static class Files {

		private Files(){}
		
		public static final int FILE_BUFFER = 50;
		public static final String FILES_LOCATION = "finance/";
		
	}

	public final static class Finances {

		private Finances() {}
		
		public static final String EXP_REGEX = "[P][1-3]_[A-Z]+";
		
	}
	
	public final static class Broadcast {

		private Broadcast() {}
		
		public static final long SEND_DELAY = 10 * 1000; // 10s
		
	}
	
	public final static class Ports {

		private Ports(){}

		// Client Ports
		public static final int CLIENT_SEND = 25000;
		public static final int CLIENT_RECEIVE = 25001;

		// Slave Ports
		public static final int SLAVE_SEND = 26000;
		public static final int SLAVE_RECEIVE = 26001;
		
		// Master Ports
		public static final int MASTER_RECEIVE_CLIENT = CLIENT_SEND;
		public static final int MASTER_SEND_CLIENT = CLIENT_RECEIVE;
		public static final int MASTER_RECEIVE_SLAVE = SLAVE_SEND;
		public static final int MASTER_SEND_SLAVE = SLAVE_RECEIVE;
		
		// Broadcast Ports
		public static final int BROADCAST_SEARCH = 45000;
		public static final int BROADCAST_ANSWER = 45010;
	}

}
