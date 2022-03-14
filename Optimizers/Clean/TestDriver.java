// This program trains a neural network to recognize handwritten digits.
// The training data is from the MNIST database of handwritten digits.
// Network architecture:
//   - input layer: 784 neurons (28x28 pixels)
//   - hidden layer: 128 neurons
//   - output layer: 10 neurons (0-9)

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class TestDriver {
  public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
    // Initialize network object
    Vector config = new Vector(new double[]{784, 32, 10});
    ActivationFunction[] activationFunctions = new ActivationFunction[]{
      ActivationFunction.LEAKY_RELU, ActivationFunction.SOFTMAX
    };
    Network network = new Network(config, activationFunctions, Error.CATEGORICAL_CROSS_ENTROPY).fromCustomGaussianDistribution(0.0, 0.1);
//    Network network = new Network("network.ser");

    // Obtain training data from serialized file
    // Serialized data is stored in 'input.ser', labels are stored in 'target.ser'
    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("input.ser"));

    // Read training data into multi-dimensional double array
    double[][] input = (double[][]) objectInputStream.readObject();
    objectInputStream.close();

    // Take first 60,000 samples for training
    input = Arrays.copyOfRange(input, 0, 60000);

    // Read labels into multi-dimensional double array
    objectInputStream = new ObjectInputStream(new FileInputStream("target.ser"));
    double[][] target = (double[][]) objectInputStream.readObject();
    objectInputStream.close();

    // Take first 60,000 labels for training
    target = Arrays.copyOfRange(target, 0, 60000);

    network.train(input, target, 5, 0.002, 10, Optimizer.ADAMAX);
    network.export("network.ser");
  }
}
