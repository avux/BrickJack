import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import lejos.hardware.Sound;
import lejos.remote.ev3.RemoteEV3;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ConnectionManager {

	public static void main(String[] args) {
		System.out.println("start");
		MindClient mc = new MindClient();
		mc.readtest();
//		ServerPC pc = new ServerPC();
//		pc.readtest();
		System.out.println("finished");
		
	}
	

//	public static void serverstream() throws Exception
//	{
//		int port = 8071;
//		ServerSocket ssock = new ServerSocket(port);
//		Socket sock = ssock.accept();
//		System.out.println("connection accepted");
//		BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
//		System.out.println("created buffers, writing");
//		writer.write("hallo connection geht");
//		writer.flush();
//		System.out.println("reading");
//		String s = reader.readLine();
//		System.out.println(s);
//	}
//	
//	public static void serverdata()throws Exception{
//		System.out.println("starting server");
//		int port = 8070;
//		ServerSocket ssock = new ServerSocket(port);
//		Socket sock = ssock.accept();
//		System.out.println("connection accepted");
//		OutputStream out = sock.getOutputStream();
//		DataOutputStream dataOut = new DataOutputStream(out);
//		System.out.println("writing");
//		dataOut.writeDouble(3.212);
//		InputStream in = sock.getInputStream();
//		DataInputStream dataIn = new DataInputStream(in);
//		System.out.println("created streams, reading");
//		double d = dataIn.readDouble();
//		
//	}
//	
//	
//	public static void connectionTest()throws Exception{
//		String host = "10.0.1.2";
//		RemoteEV3 ev3 = new RemoteEV3(host);
//		ev3.setDefault();
//		System.out.println("connection successful");
//		Sound.beep();
//	}

}
