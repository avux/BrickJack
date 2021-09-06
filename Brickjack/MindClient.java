
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MindClient extends ConnectionParent {
	
	public MindClient(){
		try{
		//System.out.println("Starting client");
		sock = new Socket("10.0.1.5", port);
		//System.out.println("established connection");
		InputStream sin = sock.getInputStream();
		this.in = new DataInputStream(sin);
		OutputStream sout = sock.getOutputStream();
		this.out = new DataOutputStream(sout);
		}catch(Exception e){
			System.out.println(e);
			System.out.println("failed to establish connection");
		}
	}
	
	
	
}
