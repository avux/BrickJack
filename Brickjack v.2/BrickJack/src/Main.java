import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Main {

	public static void main(String[] args) {
		RegulatedMotor Motor = new EV3LargeRegulatedMotor(MotorPort.B);

	}

}
