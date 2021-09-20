import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lejos.hardware.Sound;

public class CardDealer {
	RegulatedMotor pusher;
	RegulatedMotor ejector;
	LegoScanner scan;
//operates on motor A
//	public static void main(String[] args) {
//		Sound.playNote(Sound.PIANO, 420, 200);
//		
//		CardDealer d = new CardDealer();
//		d.dealnCards(10);
//		Sound.playNote(Sound.PIANO, 220, 200);
//		// TODO Auto-generated method stub
//		
//	}
	
	public CardDealer(LegoScanner scanner) {
		pusher = new EV3LargeRegulatedMotor(MotorPort.D);
		//ejector = new EV3LargeRegulatedMotor(MotorPort.B);
		//scan = scanner;
		//hold();
	}
	
	public void hold() {
		ejector.forward();
		while (ejector.isMoving()) {}
		ejector.rotate(-20);
	}
	
	public void dealCard() {
		pusher.rotate(-400);
		pusher.rotate(300);
	}
	
	public void dealnCards(int n) {
		for(int i = 0; i<n; i++) {
			this.dealCard();
		}
	}
	
	public void ejectToDealer() {
//		ejector.forward();
//		while (ejector.isMoving()) {}
//		ejector.rotate(-20);
//		scan.moveToXStart();
//		scan.moveToYStart();
//		
//		ejector.rotate(-90);
//		
//		ejector.backward();
//		while (ejector.isMoving()) {}
//		ejector.rotate(20);
//		scan.moveToEnd();
	}
	
	public void ejectToPlayer() {
//		ejector.backward();
//		while (ejector.isMoving()) {}
//		ejector.rotate(20);
//		scan.moveToXStart();
//		scan.moveToYStart();
//		
//		ejector.rotate(90);
//		
//		ejector.forward();
//		while (ejector.isMoving()) {}
//		ejector.rotate(-20);
//		scan.moveToEnd();
//		ejector.stop();
	}
	public static void main(String[] args) {
		CardDealer dealer = new CardDealer(new LegoScanner());
		dealer.ejectToPlayer();
		Delay.msDelay(5000);
		dealer.ejectToDealer();
		
	}
}
