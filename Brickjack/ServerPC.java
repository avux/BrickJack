import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
public class ServerPC extends ConnectionParent {
		
	public ServerPC(){
		try{
		System.out.println("starting server");
		ServerSocket ssock = new ServerSocket(port);
		Socket sock = ssock.accept();
		System.out.println("connection accepted");
		InputStream sin = sock.getInputStream();
		this.in = new DataInputStream(sin);
		OutputStream sout = sock.getOutputStream();
		this.out = new DataOutputStream(sout);
		}catch(Exception e){
			System.out.println(e);
			System.out.println("no connection established");
		}
		
	}
	public static void main(String[] args) {
		ServerPC pc = new ServerPC();
		int[] data = pc.readIntStream();
		for (int d : data) {
			System.out.print(d + " ");
		}
		pc.writeInt(0);
	}
}
