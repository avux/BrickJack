import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Button;


public class EV3Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//10.0.1.5
		ServerPC sp = new ServerPC();
		sp.writetest();
//		MindClient mc = new MindClient();
//		mc.writetest();
		Button.waitForAnyEvent();
		
	}
	
	public static void serverstream() throws Exception
	{
		int port = 8071;
		ServerSocket ssock = new ServerSocket(port);
		Socket sock = ssock.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		writer.write("hallo connection geht server");
		writer.flush();
		String s = reader.readLine();
		System.out.println(s);
	}
	
	public static void clientstream() throws Exception{
		System.out.println("starting client");
		Socket sock = new Socket("10.0.1.15", 8070);
		System.out.println("established connection");
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		System.out.println("created buffers reading");
		String s = reader.readLine();
		System.out.println(s);
		System.out.println("writing");
		writer.write("hallo connection gehts client");
		writer.flush();
		
		}
	
	public static void clientdatastream() throws Exception{
		System.out.println("starting client");
		Socket sock = new Socket("10.0.1.15", 8070);
		System.out.println("established connection");
		InputStream in = sock.getInputStream();
		DataInputStream dataIn = new DataInputStream(in);
		System.out.println("created streams, reading");
		double d = dataIn.readDouble();
		OutputStream out = sock.getOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);
		System.out.println("writing");
		dataOut.writeDouble(3.212);
		
	}

}
