import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
public class ConnectionParent {
	Socket sock;
	DataInputStream in;
	DataOutputStream out;
	int port = 8064;
	
	public int readInt(){
		try{
		if(in == null) {System.out.println("in is null");}
		return in.readInt();
		}catch(Exception e){
			System.out.println("readInt failed");
			System.out.println(e);
		}
		return -2;
	}
	
	public int[] readIntStream(){
		ArrayList<Integer> data = new ArrayList<Integer>();
		try{
			while(true){
				int i = in.readInt();
				if(i == -1) break;
				data.add(i);
				}
		}catch(Exception e){
			System.out.println("readIntStream() failed");
			System.out.println(e);
		}
		int[] ret = new int[data.size()];
		    for (int i=0; i < ret.length; i++){
		        ret[i] = data.get(i).intValue();
		    }
		return ret;
	}
	
	public void writeInt(int i){
		try{
		out.writeInt(i);
		out.flush();
		}catch(Exception e){
			System.out.println("writeInt failed");
			System.out.println(e);
		}
	}
	
	public void writeIntarray(int[] arr){
		try{
		for(int i: arr){
			out.writeInt(i);			
		}
		out.writeInt(-1);
		out.flush();
		}catch(Exception e){
			System.out.println("writeIntStream() failed");
			System.out.println(e);
		}
	}
	
	public void write2dIntarray(int[][] arr){
		try{
			out.writeInt(arr.length);
			out.writeInt(arr[0].length);
			for(int[] line: arr ) {
				for(int i: line){
					out.writeInt(i);
				}
			}
			out.flush();
			out.writeInt(-1);
			out.flush();
		}catch(Exception e){}
	}
	
	public int[][] readPixelData() {	
		int[] pixelData = this.readIntStream();
		int[] dimensions = this.readIntStream();
		return processPixelData(pixelData, dimensions);
	}
	
	public int[][] processPixelData(int[] data, int[]dimensions) {
		System.out.println(Arrays.toString(dimensions));				//print array for debug
		int cols = dimensions.length;
		int rows = -1;													
		for(int i: dimensions) {										//get maximum row length
			if (i>rows) rows = i;			
		}		

		int[][] pixels = new int[cols][rows];
		int counter =0;
		for(int c=0;c<cols; c++){
			for(int r=0; r<dimensions[c];r++){
				pixels[c][r]=data[counter++];
			}
		}
		
		return pixels;
	}
	
	public void writetest() {
		int test1 = 1;
		int[] test2 = {2,3,4,5,6};
		System.out.println("writing 1");
		this.writeInt(test1);
		System.out.println("writing 2");
		this.writeIntarray(test2);
		System.out.println("finished");
		System.out.println("\n finished");		
	}
	
	public void readtest() {	
	
		System.out.println("reading 1");	
		int i1 = this.readInt();
		System.out.println(i1);
		System.out.println("reading 2");
		int[] i2 = this.readIntStream();		
		for(int j: i2) System.out.print(j);
		}
	
	
	

}
