import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Main2 {
  public static int[][] readArray() {
    try {
      File myObj = new File("brightnessText.txt");
      Scanner myReader = new Scanner(myObj);
      int lines = Integer.parseInt(myReader.nextLine());
      int[][] intData;
      int line = 0;
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        String[] numbers = ((data.replace("[","")).replace("]","")).split(", ");
        if (intData==null) {
          intData = new int[lines][numbers.length];
        }
        for (int i = 0; i< numbers.length; i++) {
          intData[line][i] = Integer.parseInt(numbers[i]);
        }
        line++;
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return intData;
  }
  
  public static void main(String[] args) {
    try {
      File myObj = new File("brightnessText.txt");
      Scanner myReader = new Scanner(myObj);
      int lines = Integer.parseInt(myReader.nextLine());
      int[][] intData;
      int line = 0;
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        String[] numbers = ((data.replace("[","")).replace("]","")).split(", ");
        if (intData==null) {
          intData = new int[lines][numbers.length];
        }
        for (int i = 0; i< numbers.length; i++) {
          intData[line][i] = Integer.parseInt(numbers[i]);
        }
        line++;
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    
    
    
  }
}