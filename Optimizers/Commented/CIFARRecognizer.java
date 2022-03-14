// This program trains a neural network to recognize objects.
// The training data is from the CIFAR-10 database of images.
// Network architecture:
//   - input layer: 3072 neurons (32x32x3 pixels)
//   - hidden layer: 32 neurons
//   - output layer: 10 neurons (0-9)

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CIFARRecognizer {
  public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
    // Initialize network object
    Vector config = new Vector(new double[]{1024, 128, 10});
    ActivationFunction[] activationFunctions = new ActivationFunction[]{
      ActivationFunction.SIGMOID, ActivationFunction.SIGMOID
    };
//    Network network = new Network(config, activationFunctions, Error.CATEGORICAL_CROSS_ENTROPY).fromCustomGaussianDistribution(0.0, 0.1);
    Network network = new Network("network-cifar.ser");

    // Obtain training data from serialized file
    // Serialized data is stored in 'input.ser', labels are stored in 'target.ser'
    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("input-cifar10.ser"));

    // Read training data into multi-dimensional double array
    double[][] input = (double[][]) objectInputStream.readObject();
    objectInputStream.close();

    // Read labels into multi-dimensional double array
    objectInputStream = new ObjectInputStream(new FileInputStream("target-cifar10.ser"));
    double[][] target = (double[][]) objectInputStream.readObject();
    objectInputStream.close();

    network.train(input, target, 5, 0.001, 100, Optimizer.ADAM);
    network.export("network-cifar.ser");
  }
}
