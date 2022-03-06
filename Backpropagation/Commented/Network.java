// Import necessary libraries.

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

// Our network class.
// Used to hide all complex operations performed
// in the Layer class and ensure an efficient
// user experience for training networks.
public class Network {
  // Improves the speed of our program by allocating more CPU cores to doing the computations.
  static {
    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", (Runtime.getRuntime().availableProcessors() * 4 / 5) + "");
  }
  
  // Stores the layers in the network in an array.
  private Layer[] layers;

  // Initialize the network given a configuration,
  // a list of activation functions (each index
  // represents a layer),and an error function (which
  // all layers in the network use).
  public Network(Vector config, ActivationFunction[] activationFunctions, Error errorType) {
    if ((config.length() - 1) != activationFunctions.length) {
      throw new IllegalArgumentException("Network(Vector, ActivationFunction[], Error errorType): Vector length must be one more than ActivationFunction[] length.");
    }

    // Initialize our layer array.
    layers = new Layer[activationFunctions.length];

    // Initialize each layer in the layer array
    for (int i = 0; i < config.length() - 1; i++) {
      if (i == layers.length - 1) { // Output layer
        layers[i] = new Layer(true, activationFunctions[i], errorType).fillGaussian((int) config.get(i), (int) config.get(i + 1));
      } else { // Other layers
        layers[i] = new Layer(false, activationFunctions[i], errorType).fillGaussian((int) config.get(i), (int) config.get(i + 1));
      }
    }
  }

  // Initializes our network from a file exported using the
  // 'export()' method.
  public Network(String filepath) {
    // Throw an exception if the filepath specified doesn't refer
    // to a legitimate file.
    if (!Files.exists(Path.of(filepath))) {
      throw new IllegalArgumentException("Network(file): Please input a valid filepath to the exported network.");
    }

    // Try extract a written network from the file.
    // Catch all errors that may occur and print them
    // if they do.
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
      Object[] objects = (Object[]) ois.readObject();

      if (objects.length < 4) { // Ensure that the file contains an object array of the right size.
        throw new IllegalArgumentException("Network(file): Cannot import saved network.");
      }

      // Read and type-cast all values in the object array.
      // See export() to see how the network was exported.
      double[][][] weights = (double[][][]) objects[0];
      double[][] bias = (double[][]) objects[1];
      ActivationFunction[] activationFunctions = (ActivationFunction[]) objects[2];
      Error type = (Error) objects[3];

      // Initialize our layer array.
      layers = new Layer[weights.length];

      boolean isOutputLayer = false;

      // Create our layers based on the weights array
      for (int i = 0; i < weights.length; i++) {
        Vector[] vectors = new Vector[weights[i].length];
        for (int j = 0; j < weights[i].length; j++) {
          vectors[j] = new Vector(weights[i][j]);
        }
        if (i == weights.length - 1) { // if it is the output layer
          isOutputLayer = true;
        }
        Layer matrix = new Layer(vectors, new Vector(bias[i]), isOutputLayer, activationFunctions[i], type);
        layers[i] = matrix;
      }
    } catch (IOException | ClassNotFoundException | ClassCastException e) {
      System.err.println("An error occurred when importing an exported network.");
      System.err.println(e);
    }
    // Alert users that they are using an exported network.
    System.out.println("Note: You are using an exported network.");
  }

  // Create a network with parameters initialized from a custom gaussian distribution
  // with given average and deviation values.
  // Note that the network must by initialized with (preferably) the first constructor.
  public Network fromCustomGaussianDistribution(double average, double deviation) {
    for (Layer layer : layers) {
      layer.fillGaussian(average, deviation);
    }

    return this;
  }

  // Create a network with parameters initialized randomly (with uniform distribution).
  // (Not recommended approach)
  public Network fromUniformDistribution() {
    for (Layer layer : layers) {
      layer.fillRandom(layer.vectors[0].length(), layer.length());
    }

    return this;
  }

  // Train our network on a give input dataset and desired output values.
  // Epoch -> the number of times to train our network over the entire dataset.
  // Alpha -> learning rate.
  // BATCH_SIZE -> the batch size (how many examples the network sees before
  //               update its parameters). Default is 0.
  public void train(double[][] input, double[][] target, int epoch, double alpha, int BATCH_SIZE) {
    // Throw possible errors.
    if (input.length != target.length) {
      throw new IllegalArgumentException("train(): Input and target (expected) indices must have the same number of examples.");
    }

    if (BATCH_SIZE <= 0) {
      throw new IllegalArgumentException("train(): Batch size must be positive.");
    } else if (input.length % BATCH_SIZE != 0) {
      throw new IllegalArgumentException("train(): Batch size must evenly divide data.");
    }

    // Set the batch size for each layer.
    for (Layer layer : layers) {
      layer.setBatchSize(BATCH_SIZE);
    }

    // Try to predict how long it will take to train the network.
    {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++) {
        layers[0].feed(new Vector(input[i]));
        int size = layers.length;
        for (int indice = 1; indice < size; indice++) {
          layers[indice].feed(layers[indice - 1].getOutput());
        }

        layers[layers.length - 1].learn(new Vector(target[i]), null, alpha);
        for (int indice = 1; indice < size; indice++) {
          layers[layers.length - indice - 1].learn(null, layers[layers.length - indice], alpha);
        }
      }
      double time = (System.currentTimeMillis() - start);
      time *= input.length * epoch;
      time /= 1000.0;
      time /= 1000.0;
      if ((time / 60.0) > 60.0) {
        System.out.println("Training will take approximately " + Math.round(time / 3600.0) + " hours.");
      } else {
        System.out.println("Training will take approximately " + Math.round(time / 60.0) + " minutes.");
      }
    }

    // Perform the actual training.
    for (int iter = 1; iter <= epoch; iter++) {
      for (Layer layer : layers) { // Resets the display error after each epoch.
        layer.resetDisplayError();
      }
      // Start timing...
      long start = System.currentTimeMillis();

      // Iterate through all examples
      for (int index = 0; index < input.length; index++) {
        // Feedforward through all layers in the network.
        layers[0].feed(new Vector(input[index]));
        for (int indice = 1; indice < layers.length; indice++) {
          layers[indice].feed(layers[indice - 1].getOutput());
        }

        // Learning via backpropagation starting from the last
        // layer and ending on the first.
        layers[layers.length - 1].learn(new Vector(target[index]), null, alpha);
        for (int indice = 1; indice < layers.length; indice++) {
          layers[layers.length - indice - 1].learn(null, layers[layers.length - indice], alpha);
        }
      }
      // After each epoch, check if our error is NaN.
      // If so, exit as there isn't a point in training anymore (error exploded
      // and will continue to do so if not exited).
      if (Double.isNaN(layers[layers.length - 1].getDisplayError().total())) {
        System.err.println("Error exploded, producing NaN (not a number). Program terminated automatically.");
        System.exit(-1);
      }

      // Print out learning information after each epoch.
      if (epoch < 100 || iter % 50 == 0) {
        System.out.println("Epoch: " + iter + " Error: " + layers[layers.length - 1].getDisplayError().total() / input.length + " Time: " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds.");
      }
    }
  }

  // Export our network.
  public void export(String name) {
    // Print the filename of the file the network is exported to.
    System.out.printf("Exporting to %s.\n", name);

    // Obtain information from our network that is vital
    // when importing.
    double[][][] weights = new double[layers.length][][];
    double[][] bias = new double[layers.length][];
    ActivationFunction[] activationFunctions = new ActivationFunction[layers.length];
    Error type = layers[layers.length - 1].getErrorType();
    for (int i = 0; i < layers.length; i++) {
      double[][] layerWeights = new double[layers[i].length()][];
      for (int j = 0; j < layerWeights.length; j++) {
        layerWeights[j] = layers[i].vectors[j].values();
      }
      activationFunctions[i] = layers[i].getActivationFunction();
      weights[i] = layerWeights;
      bias[i] = layers[i].getBias().values();
    }

    // Store it into an Object array.
    Object[] objects = {weights, bias, activationFunctions, type};

    // Write a serialized file containing the object array.
    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(name)))) {
      oos.writeObject(objects);
    } catch (IOException ex) { // Catch and print errors.
      System.err.printf("Cannot export to %s.\n", name);
    }
  }

  // Test our network on an input.
  // Note that this function doesn't change our parameters.
  // It only feeds the input and obtains the output.
  public Vector test(double[] input) {
    Vector out = layers[0].test(new Vector(input));
    for (int index = 1; index < layers.length; index++) {
      out = layers[index].test(out);
    }
    return out;
  }

  // Print handy and length information about out network and its
  // configurations. This includes things like activation function,
  // number of parameters, etc.
  @Override
  public String toString() {
    int trainableParams = 0;
    for (Layer layer : layers) {
      trainableParams += (layer.vectors[0].length() + 1) * (layer.length());
    }
    StringBuilder out = new StringBuilder();
    out.append("Network with ").append(layers.length).append(" layers and ").append(trainableParams).append(" trainable parameters.\n");
    out.append("Size: {");
    out.append(layers[0].vectors[0].length()).append(", ");
    for (int i = 0; i < layers.length; i++) {
      out.append(layers[i].length());
      if (i != layers.length - 1) {
        out.append(", ");
      } else {
        out.append("}");
      }
    }
    for (int i = 0; i < layers.length; i++) {
      out.append("\n\tLayer ").append(i + 1).append(":\n");
      out.append("\t\tSize: ").append(layers[i].vectors[0].length()).append("-").append(layers[i].length());
      out.append("\n\t\tActivation Function: ").append(layers[i].getActivationFunction());
      out.append("\n\t\tError: ").append(layers[i].getErrorType());
    }
    return out.toString();
  }
}
