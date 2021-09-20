import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		//blackJackCounterPart();
		readOneCard();
	}
	
	public static void readOneCard() {
		ServerPC sp = new ServerPC();
		System.out.println("reading pixelData");
		int[][] pixelData = sp.readPixelData();
		System.out.println("finished reading Pixel data");
		System.out.println(Arrays.deepToString(pixelData).replace("],","]\n"));
		ArrayToIntConverter atic = new ArrayToIntConverter();
		int[][] thresholdedData = atic.thresholding(pixelData);
		//int[][] unditheredData = atic.removeDithering(thresholdedData);
		int[][] picArray = atic.intToCol(thresholdedData); //(unditheredData);
		BufferedImage img = atic.getImageFromArr(picArray);
		int number = atic.OCR(img);
		System.out.println("---------|"+number+"|------");
		sp.writeInt(number);
	}
	public static void blackJackCounterPart() {
		ServerPC sp = new ServerPC();
		ArrayToIntConverter atic = new ArrayToIntConverter();
		
		while(true) {
			System.out.println("reading pixelData");
			int[][] pixelData = sp.readPixelData();
			if (pixelData == null) {
				return;
			}
			System.out.println("finished reading Pixel data");
			System.out.println(Arrays.deepToString(pixelData).replace("],","]\n"));		
			int[][] thresholdedData = atic.thresholding(pixelData);
			int[][] unditheredData = atic.removeDithering(thresholdedData);
			int[][] picArray = atic.intToCol(unditheredData);
			BufferedImage img = atic.getImageFromArr(picArray);
			int number = atic.OCR(img);
			System.out.println("---------|"+number+"|------");
			sp.writeInt(number);
		}
		
	}
	
	
//	public static void blackJackCounterPart() {
//		ServerPC sp = new ServerPC();
//		ArrayToIntConverter atic = new ArrayToIntConverter();
//		System.out.println("Done with initializing");
//		while (true) {
//			System.out.println("Entering while loop");
//			System.out.println("reading pixelData");
//			int[][] pixelData = sp.readPixelData();
//			System.out.println("finished reading Pixel data");
//			if (pixelData == null) {
//				return;
//			}
//			System.out.println(Arrays.deepToString(pixelData).replace("],","]\n"));
//			int[][] thresholdedData = atic.thresholding(pixelData);
//			
//			int[][] unditheredData = atic.removeDithering(thresholdedData);
//			
//			int[][] picArray = atic.intToCol(unditheredData);
//			
//			BufferedImage img = atic.getImageFromArr(picArray);
//			
//			int number = atic.OCR(img);
//			
//			sp.writeInt(number);
//			System.out.println("---------|"+number+"|------");
//		}
//	}
	
	public static void oldMethod() {
		ServerPC sp = new ServerPC();
		System.out.println("reading pixelData");
		int[][] pixelData = sp.readPixelData();
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
