import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class Driver {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Vector config = new Vector(new int[]{
            784, 30, 10
    });

    ActivationFunction[] activationFunctions = new ActivationFunction[]{
            ActivationFunction.SIGMOID,
            ActivationFunction.SIGMOID,
    };

    Network network = new Network(config, activationFunctions, Error.MEAN_SQUARED).fromCustomGaussianDistribution(0.0, 0.1);

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.ser"));
    ObjectInputStream oiss = new ObjectInputStream(new FileInputStream("target.ser"));
    double[][] inputs = (double[][]) ois.readObject();
    inputs = Arrays.copyOfRange(inputs, 0, 60000);
    double[][] targets = (double[][]) oiss.readObject();
    targets = Arrays.copyOfRange(targets, 0, 60000);

    network.train(inputs, targets, 5, 0.01, 1);
    network.export("network.ser");
    NetworkTest.main(null);
  }
}
