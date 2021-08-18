// Import necessary libraries.

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

// Test our network on the training and testing
// data respectively.
public class NetworkTest {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // Import a network from a file.
    Network network = new Network("network.ser");

    // Read our MNIST dataset (contains all 70K examples)
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.ser"));
    ObjectInputStream oiss = new ObjectInputStream(new FileInputStream("target.ser"));

    // Store the objects and type-cast.
    double[][] inputs = (double[][]) ois.readObject();
    double[][] targets = (double[][]) oiss.readObject();

    // See how many examples the network gets correct
    // in the training dataset.
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

    // Print the results.
    System.out.println("Training: " + corrects + " / " + 60000 + " correct.");
    double accuracy = 100.0 * (double) corrects / 60000.0;
    System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");

    // See how many examples the network gets correct in the testing datset.
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

    // Print the results
    System.out.println("Testing: " + corrects + " / " + 10000 + " correct.");
    accuracy = 100.0 * (double) corrects / 10000.0;
    System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");
  }

  // A helper function that calculates the result of the network.
  // The output of the network is an array. This function just gets
  // the output of the biggest value (of which, it means that our
  // network is most certain that it is that particular output neuron).

  // For MNIST since we store our network's predictions as digits
  // 0-9 for each respective array indice 0-9, we can take
  // a few shortcuts.
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
