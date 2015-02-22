package RBA;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
	
	public void listenForMessage(){
		byte[] recieved = new byte[4096];
		while(listen){
			DatagramPacket receivePacket = new DatagramPacket(recieved, recieved.length);
            try {
				socket.receive(receivePacket);
				String packetInfo = new String(receivePacket.getData());
				System.out.println(packetInfo);
				parsePacket(packetInfo);
				break;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parsePacket(String packetInfo){
		Scanner packetScanner = new Scanner(packetInfo);
		packetScanner.useDelimiter(",");
		
		//The packet contains sender, lastHop, times forwarded, message
		currentSender = packetScanner.nextInt();
		currentLastHop = packetScanner.nextInt();
		currentTimesForwarded = packetScanner.nextInt();
		currentMessage = packetScanner.next();
		
		packetScanner.close();
	}
	
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
	
	public void sendNewMessage(String message, int reciever){
		
		//Used a ',' delimited file... The packet contains sender, seqNum, lastHop, times forwarded, message
		String newPacket = currentUser + ","+ numMessageCreated+ ",-1,0," + message;
		byte[] send = new byte[4096];
		send = newPacket.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(send, send.length);
		try {
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
