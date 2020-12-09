import java.io.*;
import java.util.*;

public class application {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    System.out.println("Gathering resources...");
    long start = System.currentTimeMillis();
    Layer nextLayer = new Layer(10);
    Layer layer = new Layer(784, 10, nextLayer, 0.5);
    Random rand = new Random();

    ArrayList<String> randomIndex = new ArrayList<>();
    for(int index = 0; index < 60000; index++) {
      randomIndex.add(String.valueOf(rand.nextInt(60000) + 1));
    }

    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("input.ser")));
    ObjectInputStream oiss = new ObjectInputStream(new BufferedInputStream(new FileInputStream("target.ser")));
    double[][] inputs = (double[][]) ois.readObject();
    double[][] targets = (double[][]) oiss.readObject();

    long stop = System.currentTimeMillis();
    System.out.println(stop - start + " milliseconds.");

    System.out.println("Training...");
    start = System.currentTimeMillis();

    for (int j = 0; j < 10; j++) {
      System.out.println("Epoch: " + j);
      for (int i = 0; i < inputs.length; i++) {
        layer.setInput(inputs[Integer.parseInt(randomIndex.get(i)) - 1]);
        layer.setTarget(targets[Integer.parseInt(randomIndex.get(i)) - 1]);
        layer.learn();
      }
      
      Collections.shuffle(randomIndex);
    }

    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("app.ser")))) {
      oos.writeObject(layer);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    stop = System.currentTimeMillis();

    System.out.println(stop - start + " milliseconds.");
    System.out.println("Done!");
  }
}
