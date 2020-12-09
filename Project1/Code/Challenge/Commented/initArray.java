// Import necessary libraries
import java.io.*;
import java.util.*;

// Start of initArray class
public class initArray {
  public static void main(String[] args) throws IOException {
    /* 
       Keep track of the current time in milliseconds, then subtract it with the ending time to find the amount
       of milliseconds elapsed.
    */
    long start = System.currentTimeMillis();

    /*
       Keep a list of all file name numbers and shuffle using the built-in class Collections.shuffle().
       This is used to randomize the order of training each sample so that the network doesn’t have a bias
       to the ending samples because they appeared last.
    */
    ArrayList<Integer> arr = new ArrayList<>();
    /*
       Initialize the two arrays that we will use to store the input values and target values for the sample.
       These arrays are later serialized using ObjectOutputStream.
    */
    double[][] inputValues = new double[60000][784];
    double[][] targetValues = new double[60000][10];

    // Add all file name numbers(0-60000) to the ArrayList.
    for(int i = 0; i < 60000; i++){
      arr.add(i);
    }

    // Shuffle the ArrayList.
    Collections.shuffle(arr);

    /*
       Open the file at the current index in the ArrayList and read all 784 inputs and the target.
       Note the values are converted to grayscale in the range 0-255, so we need to divide by 255 to get a
       value between 0 and 1.
    */
    for(int index = 0; index < 60000; index++) {
      File file = new File("data/" + String.format("%05d", arr.get(index) + 1) + ".txt");

      FileInputStream fis = new FileInputStream(file);
      byte[] data = new byte[(int) file.length()];
      fis.read(data);
      fis.close();

      Scanner in = new Scanner(file);
      double[] x = new double[784];
      for(int i = 0; i < 784; i++){
        x[i] = in.nextDouble() / 255;
      }

      /*
	The target array doesn’t just contain a single value, but an array of values with a single ‘1’
	for the correct prediction’s index and ‘0’ for all other indexes.
      */
      double[] target = new double[10];
      target[(int) in.nextDouble()] = 1;

      inputValues[index] = x;
      targetValues[index] = target;
    }

    // Serialize the input array and store it as a file.
    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("input.ser")))) {
      oos.writeObject(inputValues);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    // Serialize the target array and store it as a file.
    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("target.ser")))) {
      oos.writeObject(targetValues);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    // Get the time elapsed and print to the console.
    long stop = System.currentTimeMillis();
    System.out.println(stop - start + " milliseconds.");
  }
}
