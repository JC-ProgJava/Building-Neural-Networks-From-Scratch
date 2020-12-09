import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class test3 {
  public static double actual = 0;

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    ArrayList<String> filenames = new ArrayList<>();
    for(int x = 60001; x <= 70000; x++){
      filenames.add("data/" + String.format("%05d", x) + ".txt");
    }

    ObjectInputStream oiss = new ObjectInputStream(new BufferedInputStream(new FileInputStream("app.ser")));
    Layer lays = (Layer) oiss.readObject();
    int corrects = 0;
    for (String z : filenames) {
      double[] a = scan(z);
      int x = getBestGuess(lays.test(a));
      corrects += x == actual ? 1 : 0;
    }
    System.out.println("Testing: " + corrects + " / " + filenames.size() + " correct.");
  }

  public static double[] scan(String filename) throws FileNotFoundException {
    Scanner in = new Scanner(new File(filename));
    double[] a = new double[784];
    for(int i = 0; i < 784; i++){
      a[i] = in.nextDouble() / 255;
    }
    actual = in.nextDouble();
    return a;
  }

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
