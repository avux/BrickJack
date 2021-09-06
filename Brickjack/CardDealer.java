import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.hardware.Sound;

public class CardDealer {
	RegulatedMotor pusher;
//operates on motor A
	public static void main(String[] args) {
		Sound.playNote(Sound.PIANO, 420, 200);
		
		CardDealer d = new CardDealer();
		d.dealnCards(10);
		Sound.playNote(Sound.PIANO, 220, 200);
		// TODO Auto-generated method stub
		
	}
	
	public CardDealer() {
		pusher = new EV3LargeRegulatedMotor(MotorPort.D);
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
	
	public void ejectToPlayer() {
		// TODO
	}
	
	public void ejectToDealer() {
		// TODO
	}
}
