import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class NetworkTest {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Network network = new Network("network.ser");

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.ser"));
    ObjectInputStream oiss = new ObjectInputStream(new FileInputStream("target.ser"));
    double[][] inputs = (double[][]) ois.readObject();
    double[][] targets = (double[][]) oiss.readObject();

    int corrects = 0;
    for (int i = 0; i < 60000; i++) {
      int x = getMax(network.test(inputs[i]));
      int actual = -1;
      for (int j = 0; j < targets[i].length; j++) {
        if (targets[i][j] == 1) {
          actual = j;
          break;
        }
      }
      corrects += x == actual ? 1 : 0;
    }
    System.out.println("Training: " + corrects + " / " + 60000 + " correct.");
    double accuracy = 100.0 * (double) corrects / 60000.0;
    System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");

    corrects = 0;
    for (int i = 60000; i < 70000; i++) {
      int x = getMax(network.test(inputs[i]));
      int actual = -1;
      for (int j = 0; j < targets[i].length; j++) {
        if (targets[i][j] == 1) {
          actual = j;
          break;
        }
      }
      corrects += x == actual ? 1 : 0;
    }
    System.out.println("Testing: " + corrects + " / " + 10000 + " correct.");
    accuracy = 100.0 * (double) corrects / 10000.0;
    System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");
  }

  private static int getMax(Vector out) {
    int index = 0;
    double val = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < out.length(); i++) {
      if (out.get(i) > val) {
        val = out.get(i);
        index = i;
      }
    }
    return index;
  }
}
