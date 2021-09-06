import lejos.robotics.Color;

import java.util.ArrayList;
import java.util.Iterator;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LegoScanner {
	int yRange;
	int xRange;
	int resolution;
	RegulatedMotor xMotor;
	RegulatedMotor yMotor;
	NXTTouchSensor xSensor;
	NXTTouchSensor ySensor;
	EV3ColorSensor lightsensor;
	float[] xSample;
	SampleProvider xProvider;
	float[] ySample;
	SampleProvider yProvider;
	float[] lightSample;
	SampleProvider lightProv;
	
	
	public LegoScanner() {
		yRange = -266;//range of motor in y direction
		xRange = 191;
		resolution = 3;
		xMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		yMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		xSensor = new NXTTouchSensor(SensorPort.S2);
		ySensor = new NXTTouchSensor(SensorPort.S4);

		lightsensor = new EV3ColorSensor(SensorPort.S3);

		xProvider = xSensor.getTouchMode();
		xSample = new float[xProvider.sampleSize()];

		yProvider = ySensor.getTouchMode();
		ySample = new float[yProvider.sampleSize()];

		lightsensor.setFloodlight(true);
		lightsensor.setFloodlight(Color.WHITE);
		lightProv = lightsensor.getRGBMode();
		lightSample = new float[lightProv.sampleSize()];
		
		
	}
	public void moveToStart() {
		this.moveToXStart();
		this.moveToYStart();
	}
	
	public void decreaseScanArea(int scale) {
		xMotor.rotate(xRange/scale);
		yMotor.rotate(yRange/scale);
		this.yRange = yRange-(yRange/scale);
		this.xRange = xRange-(xRange/scale);		
	}
	
	public void moveToXStart() {
		xMotor.setSpeed(100);
		xMotor.backward();
		do {
			xProvider.fetchSample(xSample, 0);
		} while (xSample[0] == 0);
		xMotor.stop();
	}
	
	public void moveToYStart() {
		yMotor.forward();
		yMotor.setSpeed(100);
		do {
			yProvider.fetchSample(ySample, 0);
		} while (ySample[0] == 0);
		yMotor.stop();
	}
	
	public int getCardLength(){	
		int result = 0;
		yMotor.rotate(yRange, true);	
		while(yMotor.isMoving()) {
			result = Math.abs(yMotor.getTachoCount());		
		}													
		yMotor.rotate(-yRange, true);
		while(yMotor.isMoving()) {}
		return result+10;
	}

	public float scan() {
		lightProv.fetchSample(lightSample, 0);
		float greyValue = 0;
		for (int i=0; i<lightSample.length; i++) {
			//LCD.drawString(Float.toString(lightSample[i]), 0, 2+i);
			greyValue += lightSample[i];
		}
		LCD.drawString(Float.toString(greyValue/3), 0, 5);
	
		return greyValue / 3;
	}
	
	public int[][] move(){
		moveToStart();
		//decreaseScanArea(2);
		int[][] measurements = new	int[(int) (Math.ceil(xRange/resolution))][this.getCardLength()];
		for(int deg = 0; deg < xRange; deg+=resolution) {
			if(Button.DOWN.isDown()) {break;}
			LCD.clear();
			LCD.drawString("-"+deg+"-|-"+ xRange +"-", 1, 3);
			xMotor.rotate(resolution);											//move in x direction			
			if(deg%2==0) {												//check if forwards(even) or backwards(odd)
				yMotor.rotate(yRange, true);							//start moving to end of card
			}else {														//or
				yMotor.rotate(-yRange, true);							//start moving to beginning of card
			}
			while(yMotor.isMoving()) {						//while motor is still moving			
				measurements[(int)(deg/resolution)][yMotor.getTachoCount()]= (int)scan()*1000;			
			}											
		}
		return measurements;
	}
	
	public void senddata(ArrayList<ArrayList<Integer>> scandata) {
		MindClient sender = new MindClient();							// create client
		ArrayList<ArrayList<Integer>> measurements = scandata;			//handle for data
		int[] sizes = new int[measurements.size()];			 			//array that keeps track of 2d array size. Size is 2Darrays width
		int i = 0;														//counter for size
		Iterator<ArrayList<Integer>> iter = measurements.listIterator();//get iterator over outer arrayList
		while(iter.hasNext()) {											//iterate over y axis?		
			ArrayList<Integer> rowList = iter.next();
			sizes[i++] = rowList.size();								//record array length
			Iterator<Integer> intIter = rowList.listIterator(); 			
			while(intIter.hasNext()) {									//iterate over x axis
				sender.writeInt(intIter.next());						//send int to pc
			}
		}
		sender.writeInt(-1);											//send endarray
		sender.writeIntarray(sizes);                                    //send sizes array
		sender.writeInt(-1);											// just to be sure send another -1
		
	}
	
	public void sendIntData(int[][] scandata) {
		MindClient sender = new MindClient();							// create client
		for(int[] row: scandata) {
			for(int pixel : row) {
				sender.writeInt(pixel);
			}
		}
		sender.writeInt(-1);											//send endarray
		sender.writeInt(scandata[0].length);                                    //send sizes array
		sender.writeInt(scandata.length);
		sender.writeInt(-1);											// just to be sure send another -1
		
	}
	
	public static void main(String args[]) {
		LCD.drawString("starting scan",1,1);
		LegoScanner scanner = new LegoScanner();
		int[][] data = scanner.move();
		LCD.drawString("finished scan,sending data",2,2);
		scanner.sendIntData(data);
	}
	
//	public static void main(String args[]) {
//		LCD.drawString("starting scan",1,1);
//		LegoScanner scanner = new LegoScanner();		
//		ArrayList<ArrayList<Integer>> data = scanner.move();
//		LCD.drawString("finished scan,sending data",2,2);
//		scanner.senddata(data);
//		LCD.drawString("finished sending",2,2);
//		Sound.beep();
//	}
	
//	public ArrayList<ArrayList<Integer>> move2(){
//		moveToStart();
//		//decreaseScanArea(2);
//		ArrayList<ArrayList<Integer>> measurements = new ArrayList<ArrayList<Integer>>();	
//		for(int deg = 0; deg < xRange; deg+=resolution) {
//			if(Button.DOWN.isDown()) {break;}
//			LCD.clear();
//			LCD.drawString("-"+deg+"-|-"+ xRange +"-", 1, 3);
//			ArrayList<Integer> pixelRow = new ArrayList<Integer>();
//			xMotor.rotate(resolution);											//move in x direction			
//			if(deg%2==0) {												//check if forwards(even) or backwards(odd)
//				yMotor.rotate(yRange, true);							//start moving to end of card
//			}else {														//or
//				yMotor.rotate(-yRange, true);							//start moving to beginning of card
//			}
//			int tacho = 0;
//			while(yMotor.isMoving()) {						//while motor is still moving			
//				int oldtacho= yMotor.getTachoCount();					//get old motor position
//				int newtacho = yMotor.getTachoCount();					//get new motor position
//				while(oldtacho==newtacho) {								//while the motor hasn't move, wait
//					newtacho = yMotor.getTachoCount();
//				}
//				int pixel = (int) (scan() * 1000);
//				if((newtacho-oldtacho) > 1) { 
//					LCD.clear();
//					LCD.drawString("|"+newtacho+"|",4,6); }	//debugging to tell if pixels have been skipped
//													//get pixel
//				if(deg%2==0) {													//Check if going forward or backwards. To mirror image top to bottom swap
//					pixelRow.add(pixel);										//append to end of row
//				}else {
//					pixelRow.add(0,pixel);										//insert in front of row
//				}				
//			}
//			measurements.add(pixelRow);												//to mirror left to right append to beginning
//		}
//		return measurements;
//	}
}

