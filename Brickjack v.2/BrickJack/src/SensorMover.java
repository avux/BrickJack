import lejos.robotics.Color;
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

public class SensorMover {

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

	int xRange = 200;
	int yRange = -266;

	public SensorMover() {
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
		
		this.moveToStart();
		yMotor.rotate(yRange);
		xMotor.rotate(xRange);
		
		// CHANGING RANGES
		xRange = xRange/2;
		yRange= yRange/2;
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

	public void moveToStart() {
		this.moveToXStart();
		this.moveToYStart();
	} // -190, 266


	public int[] moveAround(float[][] brightness) { // should  be float[150][500]
		int xspeed = 20;
		int yspeed = 45;
		int ydelay = 15;
		int xrotation = 2;
		int x = 0;
		int y = 0;
		int ymax = 0;
		int ysum = 0;
		
		this.moveToStart();
		
		xMotor.setSpeed(xspeed);
		yMotor.setSpeed(yspeed);
		xMotor.resetTachoCount();
		
		while (xMotor.getTachoCount() < xRange) {
			y = 0;
			yMotor.resetTachoCount();
			yMotor.backward();
			LCD.drawString("backward", 0, 7);
			while(yMotor.getTachoCount() > yRange) {
				while (-yMotor.getTachoCount() < y) {}
				brightness[x][y++] = this.scan(); // INCREMENTING y IN THIS LINE
				//Delay.msDelay(ydelay);
			}
			yMotor.stop();
			xMotor.rotate(xrotation);
			x++;
			
			ysum += y;
			if (y > ymax)
				ymax = y;
			
			yMotor.resetTachoCount();
			yMotor.forward();
			LCD.drawString("forward ", 0, 7);
			while(y > 0 && yMotor.getTachoCount() < 0) {
				while (-yMotor.getTachoCount() > y) {}
				brightness[x][--y] = this.scan(); // DECREMENTING y IN THIS LINE
				//Delay.msDelay(ydelay);
			}
			yMotor.stop();
			this.moveToYStart();
			xMotor.rotate(xrotation);
			x++;
		}
		yMotor.rotate(yRange);
		
		LCD.clear();
		LCD.drawInt((2*ysum/x), 0, 0);
		LCD.drawInt(ymax, 0, 1);
		LCD.drawInt(x, 0, 2);
		
		Sound.playNote(Sound.PIANO, 220, 200);
		Delay.msDelay(4000);
		int[] dims = {x, (int) Math.ceil(2*ysum/x)};
		return dims;
	}
	
	public int[] moveAround1(float[][] brightness) { // should  be float[150][500]
		int xspeed = 20;
		int yspeed = 40;
		int ybackspeed = 200;
		int xrotation = 3;
		int x = 0;
		int y = 0;
		int ymax = 0;
		int ysum = 0;
		
		this.moveToStart();
		
		xMotor.setSpeed(xspeed);
		yMotor.setSpeed(yspeed);
		xMotor.resetTachoCount();
		
		while (xMotor.getTachoCount() < xRange) {
			y = 0;
			yMotor.setSpeed(yspeed);
			yMotor.resetTachoCount();
			yMotor.backward();
			LCD.drawString("backward", 0, 7);
			while(yMotor.getTachoCount() > yRange) {
				while (-yMotor.getTachoCount() < y) {}
				brightness[x][y++] = this.scan(); // INCREMENTING y IN THIS LINE
			}
			yMotor.stop();
			LCD.drawString("forward ", 0, 7);
			yMotor.setSpeed(ybackspeed);
			this.moveToYStart();
			xMotor.rotate(xrotation);
			x++;
			ysum += y;
			if (y > ymax)
				ymax = y;
		}
		yMotor.rotate(yRange);
		
		LCD.clear();
		LCD.drawInt(ysum/x, 0, 0);
		LCD.drawInt(ymax, 0, 1);
		LCD.drawInt(x, 0, 2);
		
		Sound.playNote(Sound.PIANO, 220, 200);
		Delay.msDelay(4000);
		int[] dims = {x, (int) Math.ceil(ysum/x)};
		return dims;
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

	public int[][] createImage() {
		float[][] brightness = new float[150][500];

		Sound.playNote(Sound.PIANO, 220, 200); // STARTING TO MOVE AROUND
		int[] dims = this.moveAround1(brightness); // Scan in card -- CURRENTLY USING moveAround1()

		int[][] img = new int[dims[0]][dims[1]]; // casting to integer
		for (int x = 0; x < dims[0]; x++) {
			for (int y = 0; y < dims[1]; y++) {
				img[x][y] = (int) (1000*brightness[x][y]);
			}
		}

		return img; // Hopefully a correct image of the card.
	}

	public static void main(String[] args) {
		SensorMover mover = new SensorMover();
		MindClient bigBrain = new MindClient();
		
		int[][] img = mover.createImage();
		
		bigBrain.write2dIntarray(img);
	    System.out.println("written");
		int card = bigBrain.readInt();
	    System.out.println("read " + card);
	    Delay.msDelay(4000);
	}
	
	/*public static void main(String[] args) {
		SensorMover mover = new SensorMover();
		while (Button.DOWN.isUp()) {
			mover.lightProv.fetchSample(mover.lightSample, 0);
			LCD.drawString(Float.toString(mover.lightSample[0]) + "  ", 0, 1);
		}
	}*/
}
