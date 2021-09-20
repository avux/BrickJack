import net.sourceforge.tess4j.Tesseract;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

public class ArrayToIntConverter {
    String datapath = "C:\\Users\\Noah Becker\\eclipse-workspace\\BrickOCR\\tessdata";
    int resolution =1;
    
	public int[][] intToCol(int[][] data) {
		int[][] colors = new int[data.length][data[0].length];
		int w = (new Color(255,255,255)).getRGB();
		int b = (new Color(0,0,0)).getRGB();
		for (int x = 0; x < colors.length; x++) {
		    for (int y = 0; y < colors[0].length; y++) {
		    	colors[x][y] = (data[x][y] == 1) ? w : b;
			}
		}
		return colors;
	}
	
	public static int[][] twoDArray(int[] data) {
		int[][] dataNew = new int[data[0]][data[1]];
		for (int x = 0; x < dataNew.length; x++) {
			for (int y = 0; y < dataNew[0].length; y++) {
				dataNew[x][y] = data[x*dataNew[0].length + y + 2];
			}
		}
		return dataNew;
	}
		
	public static int threshold(int[][] data) { // not used
		int buckets = 10;
		int[] hist = new int[buckets];
		for (int[] ds : data) {
			for (int d : ds) {
				hist[(int) (buckets * d / 1000)] += 1;
			}
		}
		int greatest = 0;
		int greaIdx = 0;
		int secondgreatest = 0;
		int secondgrIdx = 0;
		for (int i = 0; i < buckets; i++) {
			if (hist[i] > greatest) {
				secondgrIdx = greaIdx;
				secondgreatest = greatest;
				greaIdx = i;
				greatest = hist[i];
			} else if (hist[i] > secondgreatest) {
				secondgrIdx = i;
				secondgreatest = hist[i];
			}
		}
		return 1000 * (greaIdx + secondgrIdx) / (buckets * 2);
	}
	
	public int threshold1(int[][] data) { // returns suitable threshold
        int buckets = 50;
		int[] hist = new int[buckets];
		for (int[] ds : data) {
			for (int d : ds) {
                int idx = (int) (buckets * (d==1000 ? 999 : d) / 1000);
				hist[idx] += 1;
			}
		}
		ArrayList<Integer> maxBucks = new ArrayList<Integer>();
		int max = 0;
		for (int i = 0; i < buckets; i++) {
            int prev = (i==0) ? 0 : hist[i-1];
            int fol = (i==buckets-1) ? 0 : hist[i+1];
            if (hist[i] > prev && hist[i] > fol) {
            	maxBucks.add(i);
            }
            if (hist[i] > hist[max]) {
                max = i;
            }
        }
        if (maxBucks.size() > 0) {
        	return 1000 * (maxBucks.get(0) + maxBucks.get(maxBucks.size()-1)) / (buckets * 2);
        } else {
        	return 0;
        }
        
    }
	
	public int[][] thresholding(int[][] data) { // returns an array that consists only of ones and zeros
        int thresh = threshold1(data);
        for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				data[x][y] = ((data[x][y] >= thresh) ? 1 : 0);
			}
		}
		return data;
    }
    
    public static int[][] removingEdges(int[][] data) {
        for (int y = 0; y < data.length; y++) { // Removing colored edges
			int x = 0;
			if (data[y][x] == 1) {
				x += 1;
			}
			while (y<data.length/3 && x<data[0].length && data[y][x] == 0) {
				data[y][x] = 1;
				x++;
			}
			x = 1;
			if (data[y][x] == 1) {
				x += 1;
			} 
			while (x<=data[0].length && data[y][data[0].length - x] == 0) {
				data[y][data[0].length - x] = 1;
				x++;
			}
		}
		return data;
    }

    public BufferedImage getImageFromArr(int[][] pixels) {
        int width = pixels[0].length;
        int height = pixels.length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int rgb = pixels[h][w];
                image.setRGB(w,h,rgb);
            }
        }
        return image;
    }

    public void displayImage(Image img){
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
    }

    public int OCR(BufferedImage img){
        double scale = 0.2;
        double crop = 0.1;
        int newH= (int)(img.getHeight()*scale);
        int newW= (int)(img.getWidth()*scale);
        String text;
        
        Tesseract tesseract = new Tesseract();
       // tesseract.setTessVariable("tessedit_char_whitelist", "0123456789liITboOsS");
        
        displayImage(resize(img,1.5,resolution));
        img = resize(img,scale,resolution);//scale
        displayImage(img);
        //img = img.getSubimage(0,0,newW-(int)(newW*crop),newH-(int)(newH*crop));//crop
        //img = img.getSubimage(0+(int)(newW*0.1),(int)(newH*crop),newW-(int)(newW*crop),newH-(int)(newH*crop));//crop
        
       
        tesseract.setDatapath(datapath);
        tesseract.setLanguage("eng");
        //tesseract.setTessVariable("tessedit_char_whitelist", "0123456789liITboOsS");
        try{

       // tesseract.setTessVariable("tessedit_char_whitelist", "0123456789liITboOsS");
       tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");
        text = tesseract.doOCR(img);

        }catch( Exception e){
            System.out.println("OCR failed");
            System.out.println(e);
            return 0;
        }
        System.out.println("||"+ text +"||");
        return extractnumber(text);
    }
    
    public int getNumberFromImage(String path) {
    	File url = new File(path);
    	BufferedImage img;
		try {
			img = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
    	return OCR(img);
    }
    
    public int[][] removeDithering(int[][] data) {
    	for (int y = 0; y < data.length; y++) { // einzelne Pixel in x-Richtung ausgleichen
    		for (int x = 1; x < data[0].length-1; x++) {
    			//if (data[y][x-1] == 0 && data[y][x+1] == 0 && data[y][x] == 1) {
    			if (data[y][x-1] == data[y][x+1] && data[y][x+1] != data[y][x]) {
    				data[y][x] = data[y][x+1];
    			}
    		}
    	}
    	for (int y = 1; y < data.length-1; y++) { // einzelne Pixel in y-Richtung ausgleichen
    		for (int x = 0; x < data[0].length; x++) {
    			if (data[y-1][x] == data[y+1][x] && data[y+1][x] != data[y][x]) {
    				data[y][x] = data[y+1][x];
    			}
    		}
    	}
    	data = this.removingEdges(data);
    	return data;
    }

    public int extractnumber(String s){

        s =s.replace('l','1');
        s =s.replace('i','1');
        s =s.replace('I','1');
        s =s.replace('T','7');
        s =s.replace('b','6');
        s =s.replace('o','0');
        s =s.replace('O','0');
        s =s.replace('s','5');
        s =s.replace('S','5');
        s =s.replace("1","11");//the 1 card is potentially worth 11 points (ace)
        s =s.replace("0","10");//the 0 card is worth 10 points
        s= s.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(s);
        }catch( Exception e){
            System.out.println("error parsing result");
            System.out.println(e);
        }
        return 0;
    }

    public BufferedImage scaleimage(BufferedImage img,double scale){//stolen from SO

        int w = (int)(img.getWidth());
        int h = (int)(img.getHeight());
        //img = resize(img,scale,scale);

        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);//scale
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(img, after);
        //this.displayImage(img);
        this.displayImage(resize(img,scale,3));
        System.out.println(img.getHeight());
        System.out.println(after.getHeight());
        return after;
    }

    public static BufferedImage resize(BufferedImage img,double scale,int resolution) {

        int newH= (int)(img.getHeight()*scale);
        int newW= (int)(img.getWidth()*scale*resolution);
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static int[][] five(){
        Color bcol = new Color(0,0,0);
        Color wcol = new Color(255,255,255);
        int w = wcol.getRGB();
        int b =bcol.getRGB();
        int[][] five ={
                {w,w,w,w,w,w,w,w,w,w,w,w},
                {w,w,w,w,w,w,w,w,w,w,w,w},
                {w,w,b,b,b,b,b,b,b,w,w,w},
                {w,w,b,w,w,w,w,w,w,w,w,w},
                {w,w,b,w,w,w,w,w,w,w,w,w},
                {w,w,b,b,b,b,b,b,b,w,w,w},
                {w,w,w,w,w,w,w,w,b,b,w,w},
                {w,w,w,w,w,w,w,w,b,w,w,w},
                {w,w,b,b,b,b,b,b,w,w,w,w},
                {w,w,w,w,w,w,w,w,w,w,w,w},
                {w,w,w,w,w,w,w,w,w,w,w,w},
                {w,w,w,w,w,w,w,w,w,w,w,w}

        };
        return five;
    }

    public int A2I(int[][] array){
        BufferedImage img = this.getImageFromArr(array);
        int result = OCR(img);
        return result;
    }
    
//    public static void blackjackCounterPart() { //This is old code that's not used anymore
//    	System.out.println("hi");
//    	ArrayToIntConverter atsc = new ArrayToIntConverter();
//        ServerPC pc = new ServerPC();
//        int[][] data = twoDArray(pc.readIntStream());
//        System.out.println(Arrays.deepToString(data).replace("],", "]\n"));
//        System.out.println(data.length);
//        System.out.println(data[0].length);
//        data = thresholding(data);
//        //data = removingEdges(data);
//        
//        BufferedImage img = atsc.getImageFromArr(intToCol(data));
//        atsc.displayImage(img);
//        //int i = atsc.OCR(img);
//
//        //System.out.println("||"+i+"||");
//        //pc.writeInt(i);
//    }

    public static void main(String args[]){
//
         ArrayToIntConverter atsc = new ArrayToIntConverter();
//        
//        BufferedImage img = atsc.getImageFromArr(five());
//        
//
//        int i = atsc.OCR(img);
//
//        System.out.println("||"+i+"||");
    	
    	//blackjackCounterPart();
         atsc.getNumberFromImage("C:\\Users\\Noah Becker\\Desktop\\Brick3.png");
    	
    }

}

