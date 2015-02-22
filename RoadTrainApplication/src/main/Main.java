package main;


import RBA.RBA;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//this port will need to be a command line argument.
			int port = 9876;
			RBA r = new RBA(1, port);
			r.forwardMessage();
			r.listenForMessage();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
