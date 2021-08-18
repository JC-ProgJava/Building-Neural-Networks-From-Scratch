// Import necessary libraries.

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

// A runner for using our Network architecture in Chapter 8
public class Driver {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // Create a network of size 784-30-10.
    // Stored as a Vector (see Vector.java).
    Vector config = new Vector(new int[]{
            784, 30, 10
    });

    // Choose the activation functions for each layer
    // Note: there are two layers: {784 - 30} and {30 - 10}.
    // We use the ActivationFunction enums here. (see ActivationFunction.java)
    ActivationFunction[] activationFunctions = new ActivationFunction[]{
            ActivationFunction.SIGMOID,
            ActivationFunction.SIGMOID,
    };

    // Create our network object with the above parameters.
    // We will use MSE (mean squared error) with a custom
    // gaussian distribution with average 0 and deviation 0.1.
    Network network = new Network(config, activationFunctions, Error.MEAN_SQUARED).fromCustomGaussianDistribution(0.0, 0.1);

    // Read the MNIST Dataset from serialized files.
    // To see how to create these files, look at Task 1 - initArray.java
    // The serialized file from initArray.java is used here.
    // The only modification is initArray.java reads all 70000 files.
    // 'input.ser' represents input digit data.
    // 'target.ser' gives the target/desired prediction result.
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.ser"));
    ObjectInputStream oiss = new ObjectInputStream(new FileInputStream("target.ser"));

    // Store the objects read into arrays
    // Take only the training portion from the file.
    double[][] inputs = (double[][]) ois.readObject();
    inputs = Arrays.copyOfRange(inputs, 0, 60000);
    double[][] targets = (double[][]) oiss.readObject();
    targets = Arrays.copyOfRange(targets, 0, 60000);

    // Train our network on the data.
    // Config: 5 epochs, 0.01 learning rate and 1 example per batch.
    // 1 example per batch = Stochastic Gradient Descent
    network.train(inputs, targets, 5, 0.01, 1);

    // Write our network to a file after training.
    network.export("network.ser");

    // Run NetworkTest (see NetworkTest.java)
    NetworkTest.main(null);
  }
}
