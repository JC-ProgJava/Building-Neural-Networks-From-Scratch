// Import necessary libraries.
import java.io.*;
import java.util.*;

public class application {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    System.out.println("Gathering resources...");
    // Start timing the amount of time it takes to get resources. 
    long start = System.currentTimeMillis();
    // Create two layer objects for the learning stage.
    Layer nextLayer = new Layer(10);
    Layer layer = new Layer(784, 10, nextLayer, 0.5);
    Random rand = new Random();

    // Create a list of random indexes to choose a set of inputs and targets from.
    ArrayList<String> randomIndex = new ArrayList<>();
    for(int index = 0; index < 60000; index++) {
      randomIndex.add(String.valueOf(rand.nextInt(60000) + 1));
    }

    // Read the input and target arrays from their respective files which
    // were generated after executing initArray.java.
    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("input.ser")));
    ObjectInputStream oiss = new ObjectInputStream(new BufferedInputStream(new FileInputStream("target.ser")));
    double[][] inputs = (double[][]) ois.readObject();
    double[][] targets = (double[][]) oiss.readObject();

    long stop = System.currentTimeMillis();
    System.out.println(stop - start + " milliseconds.");

    System.out.println("Training...");
    start = System.currentTimeMillis();

    // Start the learning stage by setting the input and target for the network and
    // use the learn() function in the Layer class to adjust weights in the network.
    for (int j = 0; j < 10; j++) {
      System.out.println("Epoch: " + j);
      for (int i = 0; i < inputs.length; i++) {
        layer.setInput(inputs[Integer.parseInt(randomIndex.get(i)) - 1]);
        layer.setTarget(targets[Integer.parseInt(randomIndex.get(i)) - 1]);
        layer.learn();
      }

      // Shuffle the list of random indexes for the next epoch.
      Collections.shuffle(randomIndex);
    }

    // Write our new neural network to a file named app.ser.
    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("app.ser")))) {
      oos.writeObject(layer);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    stop = System.currentTimeMillis();

    // Print the time (in milliseconds) that elapsed.
    System.out.println(stop - start + " milliseconds.");
    System.out.println("Done!");
  }
}
