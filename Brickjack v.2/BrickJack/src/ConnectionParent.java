import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
public class ConnectionParent {
	Socket sock;
	DataInputStream in;
	DataOutputStream out;
	int port = 8069;
	
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
	
	public void sendIntData(int[][] scandata) {
		for(int[] row: scandata) {
			for(int pixel : row) {
				this.writeInt(pixel);
			}
		}
		this.writeInt(-1);											//send endarray
		this.writeInt(scandata.length);                                    //send sizes array
		this.writeInt(scandata[0].length);
		this.writeInt(-1);											// just to be sure send another -1
	}
	
	public int[][] readPixelData() {//reads pixel data assuming ListArray
		int[] pixelData = this.readIntStream();
		int[] dimensions = this.readIntStream();
		if (pixelData.length == 0 && dimensions.length == 0) {
			return null;
		}
		return processPixelArrayData(pixelData, dimensions);
	}
	
//	public int[][] processPixelArralListData(int[] data, int[]dimensions) {
//		System.out.println(Arrays.toString(dimensions));				//print array for debug
//		int cols = dimensions.length;
//		int rows = -1;													
//		for(int i: dimensions) {										//get maximum row length
//			if (i>rows) rows = i;			
//		}		
//
//		int[][] pixels = new int[cols][rows];
//		int counter =0;
//		for(int c=0;c<cols; c++){
//			for(int r=0; r<dimensions[c];r++){
//				pixels[c][r]=data[counter++];
//			}
//		}
//		
//		return pixels;
//	}
	
	public int[][] processPixelArrayData(int[] data, int[]dimensions) {
		int numberOfCols=dimensions[0];
		int numberOfRows=dimensions[1];
		System.out.println(Arrays.toString(data));
		boolean[] discard = new boolean[numberOfCols];
		int pixels[][] = new int[numberOfCols][numberOfRows];
		for(int col =0;col<numberOfCols;col++) {
			int prev = 260;
			int count = 0;
			for(int row=0;row<numberOfRows;row++) {
				int pixel = data[row+(col*numberOfRows)];
				if(pixel==0) {
					count += 1;
					pixels[col][row]=prev;
				} else {
					pixels[col][row]=pixel;
					prev=pixel;
				}
			}
			if (count >= 0.5*numberOfRows) {
				discard[col] = true;
			}
		}
		int disSum = 0;//(Arrays.stream(discard)).sum();
		for (boolean b : discard) {
			if (b) disSum += 1;
		}

		if (disSum > 0) {
			System.out.println(disSum + " > 0");
			int[][] pixOld = pixels;
			pixels = new int[numberOfCols-disSum][numberOfRows];
			int yReal = 0;
			for (int y = 0; y < pixOld.length; y++) {
				if (discard[y]) {
					continue;
				}
				for (int x = 0; x < numberOfRows; x++) {
					pixels[yReal][x] = pixOld[y][x];
				}
				yReal += 1;
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
