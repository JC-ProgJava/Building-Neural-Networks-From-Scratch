// Import necessary libraries.
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class test3 {
  public static double actual = 0;

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // Prepare a list of file paths from 60001.txt to 70000.txt, these will
    // be used to test the network accuracy, not train the network.
    ArrayList<String> filenames = new ArrayList<>();
    for(int x = 60001; x <= 70000; x++){
      filenames.add("data/" + String.format("%05d", x) + ".txt");
    }

    // Read the layer object from the file we created in application.java.
    ObjectInputStream oiss = new ObjectInputStream(new BufferedInputStream(new FileInputStream("app.ser")));
    Layer lays = (Layer) oiss.readObject();
    int corrects = 0;

    // Read the file at the current index and step it through the network.
    // See if the result is the same as the answer given in the file.
    for (String z : filenames) {
      double[] a = scan(z);
      int x = getBestGuess(lays.test(a));
      corrects += x == actual ? 1 : 0;
    }
    // Print out the accuracy of the network by printing the amount of answers the
    // network was correct on out of 10000 (there were 10000 files).
    System.out.println("Testing: " + corrects + " / " + filenames.size() + " correct.");
  }

  // This is a method used to read all number values in the file path given,
  // returning the result as an array. Note that I assigned the correct value to a
  // global variable named *actual*.
  public static double[] scan(String filename) throws FileNotFoundException {
    Scanner in = new Scanner(new File(filename));
    double[] a = new double[784];
    for(int i = 0; i < 784; i++){
      a[i] = in.nextDouble() / 255;
    }
    actual = in.nextDouble();
    return a;
  }

  // Get the most probable answer from the network output. Since we organised our predictions
  // from 0-9, we can take the index at which the prediction is closest to 1 and return
  // that as the network’s prediction output.
  public static int getBestGuess(double[] result) {
    double k = Integer.MIN_VALUE;
    double index = 0;
    int current = 0;
    for (double a : result) {
      if (k < a) {
        k = a;
        index = current;
      }
      current++;
    }

    return (int) index;
  }
}
