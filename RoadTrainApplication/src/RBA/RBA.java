package RBA;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class RBA {
	ArrayList<TableEntry> cache;
	String currentMessage = "New message", newMessage;
	int currentSeqNum, currentLastHop, currentTimesForwarded, currentUser, currentSender, numMessageCreated = 1;
	DatagramSocket socket;
	boolean listen;
	
	
	public RBA(int currentUser, int port) throws SocketException{
		this.currentUser = currentUser;
		cache = new ArrayList<TableEntry>();
		populateCache();
		socket = new DatagramSocket(port);
		listen = true;
	}
	
	
	//This populates the cache the first time with empty messages. This is necessary but is
	//done so that each nodes message goes in there car-1 value.
	private void populateCache(){
		for(int i = 0; i<10; i++){
			cache.add(new TableEntry(i+1));
		}
	}
	
	
	//loops to listen for a message. 
	public void listenForMessage(){
		byte[] recieved = new byte[4096];
		while(listen){
			DatagramPacket receivePacket = new DatagramPacket(recieved, recieved.length);
            try {
				socket.receive(receivePacket);
				String packetInfo = new String(receivePacket.getData());
				System.out.println(packetInfo);
				parsePacket(packetInfo);
				checkShouldForward();
				//This break is done for testing
				break;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	//Parse the packet that is recieved
	private void parsePacket(String packetInfo){
		Scanner packetScanner = new Scanner(packetInfo);
		packetScanner.useDelimiter(",");
		
		//The packet contains sender, lastHop, times forwarded, message
		currentSender = packetScanner.nextInt();
		currentSeqNum = packetScanner.nextInt();
		currentLastHop = packetScanner.nextInt();
		currentTimesForwarded = packetScanner.nextInt();
		currentMessage = packetScanner.next();
		
		packetScanner.close();
	}
	
	
	//Forwards the message to connecting cars.
	public void forwardMessage(){
		 String packetInfo = currentSender + ","+ currentSeqNum+","+currentUser+","+(currentTimesForwarded+1)+","+currentMessage;
		 byte[] sendData = new byte[4096];
		 sendData = packetInfo.getBytes();
		 
		 //For testing purposes. These IP addresses will need to come from the config file.
		 InetAddress IPAddress;
		try {
			IPAddress = InetAddress.getByName("localhost");
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			socket.send(sendPacket);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 
	}
	
	
	//Checks to see if a message should be forwarded
	private boolean checkShouldForward(){
		
		boolean returnVal = true;
		
		//Checks sequence number. If it's the same, it uses the RBA to determine if it should be forwarded
		if(currentSeqNum == cache.get(currentSender - 1).getSeqNum()){
				int forwards = cache.get(currentSender-1).getNumOfForwards();
				double probability = 1;
				
				for(int i = 1; i <= forwards; i++){
					probability = probability/2;
				}
				
				if(new Random().nextDouble() <= probability){
					forwardMessage();
				}
				cache.get(currentSender-1).setNumOfForwards(forwards+1);
			//if the current sequence number is greater, then it automatically caches the message and forwards it.	
		} else if (currentSeqNum > cache.get(currentSender - 1).getSeqNum()){
				cacheMessage();
				forwardMessage();
		}else{
			return false;
		}
		return returnVal;
	}
	
	private boolean cacheMessage(){	
		cache.get(currentSender -1).setMessage(currentMessage);
		cache.get(currentSender-1).setLastHop(currentUser);
		cache.get(currentSender-1).setNumOfForwards(currentTimesForwarded+1);
		cache.get(currentSender-1).setSeqNum(currentSeqNum);
		return true;
	}
	
	
	public void sendNewMessage(String message, int reciever){
		
		//Used a ',' delimited file... The packet contains sender, seqNum, lastHop, times forwarded, message
		String newPacket = currentUser + ","+ numMessageCreated+ ",-1,0," + message;
		byte[] send = new byte[4096];
		send = newPacket.getBytes();
		
		try {
			InetAddress IPAddress = InetAddress.getByName("localhost");
			DatagramPacket sendPacket = new DatagramPacket(send, send.length, IPAddress, 9876);
			socket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		numMessageCreated++;
	}
	
	public void setListen(boolean listen){
		this.listen = listen;
	}
	
}
