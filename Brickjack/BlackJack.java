
import java.util.Random;
import java.util.Scanner;

public class BlackJack {
    int[] players; //player array which keeps score of all player values
    boolean[] busts ;//true = player is in; false = player is out
    //dealer is always 0
    int winner =-1;
    int stillin;

    public void blackjack(){
        Scanner in = new Scanner(System.in);
        System.out.println("enter player number");
        int playernum = Integer.parseInt(in.nextLine().replaceAll("[^0-9]", ""))+1; //+1 is for dealer
        System.out.println("there are "+ playernum +" players including the dealer");
        players = new int[playernum];
        busts = new boolean[playernum];
        ;
        stillin = playernum;


        //beginning: draw two cards
        for (int p = 0; p < players.length; p++) {
            busts[p]=true;
            int one = drawpseudocard();
            int two = drawpseudocard();
            System.out.println("player " + p + " drew the cards "+ one +" and "+two+".");
            players[p] += one+two;
        }
        while(stillin >1){
            for (int player = 0; player < players.length; player++) {

                if(!busts[player]) continue;//player is not in the game anymore
                System.out.println("\n ------------------------------------------------------- \n");
                System.out.println("player "+ player+" turn. You have " + players[player]+" points.");
                System.out.println("do you want to draw a card? If yes type y");
                if(in.nextLine().contains("y")){
                    int card = drawpseudocard();
                    players[player] += card;
                    System.out.println("you drew "+card+". You have "+players[player]+" points.");
                    if(players[player]>21){//player lost
                        busts[player] = false;
                        stillin--;
                        players[player]=-1;
                    }
                }else{
                    busts[player]=false;
                }
            }
        }
        int max=-1;
        for(int i = 0; i< players.length;i++){
            if(players[i]>max){
                max = players[i];
                winner = i;
            }
        }

        System.out.println("the winner is player "+ winner);
    }

    public int drawpseudocard(){//returns value between 1 and 10
        Random random = new Random();
        return random.nextInt(9)+1;
    }

    public static void main(String args[]){
        BlackJack b = new BlackJack();
        b.blackjack();
    }

}



