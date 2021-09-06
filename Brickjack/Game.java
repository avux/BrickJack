import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import java.util.ArrayList;
import java.util.Iterator;
//import java.time.Duration;
//import java.time.Instant;
	
	public class Game {
		CardDealer dealer = new CardDealer();
		//MindClient bigBrain = new MindClient();
		SensorMover reader = new SensorMover();
		NXTTouchSensor hitButton = new NXTTouchSensor(SensorPort.S1);
	    SampleProvider samProv = hitButton.getTouchMode();
	    float[] buttonSample = new float[samProv.sampleSize()];
	    ArrayList<Integer> cheatcards;

		int playerNum;
	    int stillPlaying;
	    boolean[] finished;
	    int[] sums;
	    int[] ace; // aces initially added up as 11. if (sum[i] > 21 && ace[i] > 0) {sum[i] -= 10; ace[i] -= 1;}
		
	    public Game() {
	    	Sound.beep();
	    }
	    
		public void moderate() {		    
		    LCD.clear();
		    System.out.println("Let's play a round of Black Jack!");
		    
		    playerNum = takePlayerNumber();
		    int[] cardlist ={8,10,5,4,6,5,2,2,10,5,8,2,11,11,9,2,3,4,5,6,7,8,9,10,10,10,10,10,10};
		    cheatcards = new ArrayList<Integer>();
		    for(int i : cardlist) {cheatcards.add(i);}
		    stillPlaying = playerNum+1;
		    finished = new boolean[playerNum+1];
		    sums = new int[playerNum+1];
		    ace = new int[playerNum+1];
		    
		    for (int i=0; i<playerNum+1; i++) { // First two cards.
		        sums[i] += this.giveCard(i);
		        sums[i] += this.giveCard(i);
		        if (sums[i] != 21) {
		        	System.out.println((i==playerNum) ? "Dealer" : ("Player " + Integer.toString(i)) + " has now a sum of " + sums[i] + ".");
		        } else {
					String name = (i==playerNum) ? "Dealer" : ("Player " + Integer.toString(i));
		        	System.out.println(name + " has a blackjack!");
		        	System.out.println("They won!");
		        	finished[i] = true;
		        	stillPlaying -= 1;
		        }
		        Delay.msDelay(3000);
		    }
		    
		    while(stillPlaying > 0) {
		        for (int i = 0; i < playerNum; i++) {
		            if (!finished[i]) {
	                    if (wantsCard(i)) {
	                        sums[i] += this.giveCard(i);
	                        if (sums[i] > 21 && ace[i] > 0) {
	                        	sums[i] -= 10;
	                        	ace[i] -= 1;
	                        } else if (sums[i] > 21) {
	                            System.out.println("Player " + Integer.toString(i) + " busts!");
	                            System.out.println("Their card sum is " + Integer.toString(sums[i]) + ".");
	                            finished[i] = true;
	                            stillPlaying -=1;
	                        } else {
	                        	System.out.println("Player " + i + "'s card sum is " + sums[i] + ".");
	                        }
	                    } else {
	                        finished[i] = true;
	                        stillPlaying -= 1;
	                        System.out.println("Player " + Integer.toString(i) + " passes with a card sum of " + Integer.toString(sums[i]) + ".");
	                    }
	                    Delay.msDelay(4000);
	                    LCD.clear();
		            }
		        }
		        if (!finished[playerNum]) { // Dealer takes card or not
		        	sums[playerNum] += this.giveCard(playerNum);
		        	if (this.sums[playerNum] >= 17 && this.sums[playerNum] <= 21) {
		        		finished[playerNum] = true;
		        		stillPlaying -= 1;
		        		System.out.println("Dealer passes with a card sum of " + Integer.toString(sums[playerNum]) + ".");
		        	} else if (sums[playerNum] > 21) { // Dealer looses, game over.
                        System.out.println("Dealer busts!");
                        System.out.println("Their card sum is " + Integer.toString(sums[playerNum]) + ".");
                        Delay.msDelay(5000);
                        break;
		        	} else {
						System.out.println("Dealer's card sum is " + sums[playerNum] + ".");
					}
		        }
		        LCD.clear();
		    }
		    winners();
		    Delay.msDelay(10000);
		}
		
		public void winners() {
			ArrayList<Integer> win = new ArrayList<Integer>();
		    int j = 0;
		    while (sums[j] > 21 && j < playerNum) { // first under 21?
		    	j++;
		    }
		    if (j == playerNum) {
		    	LCD.drawString("All players busted.",0,0);
		    	LCD.drawString("Dealer wins!", 0, 1);
		    	return;
		    }
		    win.add(j);
		    for (int i=1; i<this.playerNum; i++) { // filling winners with all players' numbers who have the highest score
		        if (sums[i] > sums[win.get(0)] && sums[i] <= 21) {
		            win.clear();
		            win.add(i);
		        } else if (sums[i] == sums[win.get(0)] && sums[i] <= 21) {
					win.add(i);
		        }
		    }
		    // messages according to win cases.
		    Iterator<Integer> winIt = win.iterator();
		    String msg = "";
		    if (sums[win.get(0)] < sums[playerNum] && sums[playerNum] <= 21) {
		    	msg = "Dealer wins with a sum of " + sums[playerNum] + ", all the players lost.";
		    } else if (sums[win.get(0)] == sums[playerNum] && sums[playerNum] <= 21) {
		    	msg = "Draw!\n";
		    	msg += "Dealer and ";
		    	if (win.size() > 1) {
		    		msg += "players " + winIt.next();
					while (winIt.hasNext()) {
						msg += ", " + winIt.next();
					}
		    		msg += " win!";
		    	} else {
		    		msg += "player " + winIt.next() + " win!";
		    	}
		    } else {
				if (win.size() > 1) {
		    		msg += "Players " + winIt.next();
					while (winIt.hasNext()) {
						msg += ", " + winIt.next();
					}
		    		msg += " win!";
		    	} else {
		    		msg += "Player " + winIt.next() + " win!";
		    	}
			}
		    System.out.println(msg);
		    return;
		}
		
		public int cheatnumber(){
			int result=0;
			int butId = Button.waitForAnyPress();
			while (butId != Button.ID_ENTER) {
				
		        if (butId == Button.ID_UP) {
		            result += 1;
		        } else if (butId == Button.ID_DOWN) {
		            playerNum -= 1;
		        }
		        LCD.drawInt(result,6,6);
		    }
		    butId = Button.waitForAnyPress();
		   return result;
		}
		
		public int cheatdraw(){
			return cheatcards.remove(0);		
		}
		
		public boolean wantsCard(int num) {
		    System.out.println("Player " + Integer.toString(num) + ", another card?");
		    System.out.println("Current sum: " + Integer.toString(this.sums[num]));
		    System.out.println("Hit: push button once.");
		    System.out.println("Stand: hold button for 1s.");
		    /* do {
		        samProv.fetchSample(buttonSample, 0);
	        } while (buttonSample[0] == 0);
	        Instant start = Instant.now();
	        Instant finish = Instant.now();
	        while (buttonSample[0] == 1 && (Duration.between(start,finish).toMillis() < 1000)) {
	            samProv.fetchSample(buttonSample, 0);
	            finish = Instant.now();
	        }
	        LCD.clear();
	        return (buttonSample[0] != 1 && (Duration.between(start,finish).toMillis() < 1000)); */
		    
		    do {
		        samProv.fetchSample(buttonSample, 0);
	        } while (buttonSample[0] == 0);
		    Delay.msDelay(1000);
		    samProv.fetchSample(buttonSample, 0);
		    return buttonSample[0]==0;
		}
		
		public int giveCard(int num) {
		    if (num == playerNum) {
		        System.out.println("A card for the dealer.");
		    } else {
		        System.out.println("A card for player " + Integer.toString(num) + ".");
		    }
		    //dealer.dealCard();
		    //bigBrain.write2dIntarray(reader.createImage());
		    //int card = bigBrain.readInt(); 
		    
		    //------------------------cheating-------------------------
		    int card = cheatdraw();
		    dealer.ejectToPlayer(); // TODO: implement ejection
		    LCD.clear();
		    
		    // ace logic
		    if (card == 11) { // TODO make 1 to 11 in ArrayToIntConverter
		    	ace[num] += 1;
		    }
		    
		    return card;
		}
		
		public static int takePlayerNumber() {
		    int playerNum = 2;
		    int maxPlayer = 9;
		    
		    System.out.println("");
		    System.out.println("Number of players");
		    LCD.drawInt(playerNum,0,5);
		    int butId = Button.waitForAnyPress();
		    while (butId != Button.ID_ENTER) {
		        if (butId == Button.ID_UP && playerNum < maxPlayer) {
		            playerNum += 1;
		        } else if (butId == Button.ID_DOWN && playerNum > 1) {
		            playerNum -= 1;
		        }
		        LCD.drawInt(playerNum,0,5);
			    butId = Button.waitForAnyPress();
		    }
		    LCD.clear();
		    System.out.println("Black Jack with " + Integer.toString(playerNum) + " players.");
		    Delay.msDelay(500);
		    System.out.println("Hoping you shuffled well...");
		    Delay.msDelay(3000);
		    LCD.clear();
		    
		    return playerNum;
		}

		public static void main(String[] args) {
			Game game = new Game();
			Sound.beep();
			game.moderate();
		}
}
