import java.io.*;
import java.util.*;

public class initArray {
  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();
    ArrayList<Integer> arr = new ArrayList<>();
    double[][] inputValues = new double[60000][784];
    double[][] targetValues = new double[60000][10];

    for(int i = 0; i < 60000; i++){
      arr.add(i);
    }
    Collections.shuffle(arr);

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

      double[] target = new double[10];
      target[(int) in.nextDouble()] = 1;

      inputValues[index] = x;
      targetValues[index] = target;
    }

    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("input.ser")))) {
      oos.writeObject(inputValues);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("target.ser")))) {
      oos.writeObject(targetValues);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    long stop = System.currentTimeMillis();
    System.out.println(stop - start + " milliseconds.");
  }
}
