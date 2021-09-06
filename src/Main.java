import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		
		ServerPC sp = new ServerPC();
		System.out.println("reading pixelData");
		//int[][] pixelData = sp.read;
		System.out.println("finished reading Pixel data");
		System.out.println(Arrays.deepToString(pixelData).replace("],","]\n"));
		ArrayToIntConverter atic = new ArrayToIntConverter();
		int[][] threshholdedData = atic.thresholding(pixelData);
		int[][] picArray = atic.intToCol(threshholdedData);
		BufferedImage img = atic.getImageFromArr(picArray);
		int number = atic.OCR(img);
		System.out.println("---------|"+number+"|------");
	}
	
	
	


}
