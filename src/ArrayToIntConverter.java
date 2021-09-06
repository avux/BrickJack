import net.sourceforge.tess4j.Tesseract;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

public class ArrayToIntConverter {
    String datapath = "C:\\Users\\Noah Becker\\eclipse-workspace\\BrickOCR\\tessdata";
    
    
	public static int[][] intToCol(int[][] data) {
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
		
	public static int threshold(int[][] data) { // returns suitable threshold
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
	
	public static int threshold1(int[][] data) {
        int buckets = 10;
		int[] hist = new int[buckets];
		for (int[] ds : data) {
			for (int d : ds) {
                
				hist[(int) (buckets * (d==1000 ? 999 : d) / 1000)] += 1;
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
        Iterator<Integer> iter = maxBucks.iterator();
        System.out.print("Buckets ");
        while (iter.hasNext()) {
            int idx = iter.next();
            if (hist[idx] <= 0.05*hist[max]) {
                iter.remove();
            } else {
                System.out.print(" " + idx);
            }
        }
        System.out.println();
        return 1000 * (maxBucks.get(0) + maxBucks.get(maxBucks.size()-1)) / (buckets * 2);
    }
	
	public static int[][] thresholding(int[][] data) {
        //int thresh = 120; // threshold(data); HARDCODING THRESHOLD
        int thresh = threshold1(data);
        for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				data[x][y] = ((data[x][y] >= thresh) ? 1 : 0);
			}
		}
		return data;
    }
    
    public static int[][] removingEdges(int[][] data) {
        for (int y = 0; y < data[0].length; y++) { // Removing colored edges
			int x = 0;
			while (data[x][y] == 0) {
				data[x][y] = 1;
				x++;
			}
			x = 1;
			while (data[data.length - x][y] == 0) {
				data[data.length - x][y] = 1;
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
        double scale = 10;
        double crop = 0.1;
        int newH= (int)(img.getHeight()*scale);
        int newW= (int)(img.getWidth()*scale);
        String text;
        
        Tesseract tesseract = new Tesseract();
       // tesseract.setTessVariable("tessedit_char_whitelist", "0123456789liITboOsS");
      
        img = resize(img,scale);//scale
        //img = img.getSubimage(0,0,newW-(int)(newW*crop),newH-(int)(newH*crop));//crop
        //img = img.getSubimage(0+(int)(newW*0.1),(int)(newH*crop),newW-(int)(newW*crop),newH-(int)(newH*crop));//crop
        
        displayImage(img);
        tesseract.setDatapath(datapath);
        //tesseract.setLanguage("digits_comma");
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789liITboOsS");
        try{

        //tesseract.setTessVariable("tessedit_char_whitelist", "0123456789liITboOsS");
        text = tesseract.doOCR(img);

        }catch( Exception e){
            System.out.println("OCR failed");
            System.out.println(e);
            return 0;
        }
        System.out.println("||"+ text +"||");
        return extractnumber(text);
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
        this.displayImage(resize(img,scale));
        System.out.println(img.getHeight());
        System.out.println(after.getHeight());
        return after;
    }

    public static BufferedImage resize(BufferedImage img,double scale) {
        int newH= (int)(img.getHeight()*scale);
        int newW= (int)(img.getWidth()*scale);
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
    
    public static void blackjackCounterPart() {
    	System.out.println("hi");
    	ArrayToIntConverter atsc = new ArrayToIntConverter();
        ServerPC pc = new ServerPC();
        int[][] data = twoDArray(pc.readIntStream());
        System.out.println(Arrays.deepToString(data).replace("],", "]\n"));
        System.out.println(data.length);
        System.out.println(data[0].length);
        data = thresholding(data);
        //data = removingEdges(data);
        
        BufferedImage img = atsc.getImageFromArr(intToCol(data));
        atsc.displayImage(img);
        //int i = atsc.OCR(img);

        //System.out.println("||"+i+"||");
        //pc.writeInt(i);
    }

    public static void main(String args[]){
//
//        ArrayToIntConverter atsc = new ArrayToIntConverter();
//        
//        BufferedImage img = atsc.getImageFromArr(five());
//        
//
//        int i = atsc.OCR(img);
//
//        System.out.println("||"+i+"||");
    	
    	blackjackCounterPart();
    }

}

