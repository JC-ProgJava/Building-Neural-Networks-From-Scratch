import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Network {
  private Layer[] layers;

  public Network(Vector config, ActivationFunction[] activationFunctions, Error errorType) {
    if ((config.length() - 1) != activationFunctions.length) {
      throw new IllegalArgumentException("Network(Vector, ActivationFunction[], Error errorType): Vector length must be one more than ActivationFunction[] length.");
    }

    layers = new Layer[activationFunctions.length];

    for (int i = 0; i < config.length() - 1; i++) {
      if (i == layers.length - 1) {
        layers[i] = new Layer(true, activationFunctions[i], errorType).fillGaussian((int) config.get(i), (int) config.get(i + 1));
      } else {
        layers[i] = new Layer(false, activationFunctions[i], errorType).fillGaussian((int) config.get(i), (int) config.get(i + 1));
      }
    }
  }

  public Network(String filepath) {
    if (!Files.exists(Path.of(filepath))) {
      throw new IllegalArgumentException("Network(file): Please input a valid filepath to the exported network.");
    }

    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
      Object[] objects = (Object[]) ois.readObject();

      if (objects.length < 4) {
        throw new IllegalArgumentException("Network(file): Cannot import saved network.");
      }

      double[][][] weights = (double[][][]) objects[0];
      double[][] bias = (double[][]) objects[1];
      ActivationFunction[] activationFunctions = (ActivationFunction[]) objects[2];
      Error type = (Error) objects[3];

      layers = new Layer[weights.length];

      boolean isOutputLayer = false;
      for (int i = 0; i < weights.length; i++) {
        Vector[] vectors = new Vector[weights[i].length];
        for (int j = 0; j < weights[i].length; j++) {
          vectors[j] = new Vector(weights[i][j]);
        }
        if (i == weights.length - 1) {
          isOutputLayer = true;
        }
        Layer matrix = new Layer(vectors, new Vector(bias[i]), isOutputLayer, activationFunctions[i], type);
        layers[i] = matrix;
      }
    } catch (IOException | ClassNotFoundException | ClassCastException e) {
      System.err.println("An error occurred when importing an exported network.");
      System.err.println(e);
    }
    System.out.println("Note: You are using an exported network.");
  }

  public Network fromCustomGaussianDistribution(double average, double deviation) {
    for (Layer layer : layers) {
      layer.fillGaussian(average, deviation);
    }

    return this;
  }

  public Network fromUniformDistribution() {
    for (Layer layer : layers) {
      layer.fillRandom(layer.vectors[0].length(), layer.length());
    }

    return this;
  }

  public void train(double[][] input, double[][] target, int epoch, double alpha, int BATCH_SIZE) {
    if (input.length != target.length) {
      throw new IllegalArgumentException("train(): Input and target (expected) indices must have the same number of examples.");
    }

    if (BATCH_SIZE <= 0) {
      throw new IllegalArgumentException("train(): Batch size must be positive.");
    } else if (input.length % BATCH_SIZE != 0) {
      throw new IllegalArgumentException("train(): Batch size must evenly divide data.");
    }

    for (Layer layer : layers) {
      layer.setBatchSize(BATCH_SIZE);
    }

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

    for (int iter = 1; iter <= epoch; iter++) {
      for (Layer layer : layers) {
        layer.resetDisplayError();
      }
      long start = System.currentTimeMillis();
      for (int index = 0; index < input.length; index++) {
        layers[0].feed(new Vector(input[index]));
        for (int indice = 1; indice < layers.length; indice++) {
          layers[indice].feed(layers[indice - 1].getOutput());
        }

        layers[layers.length - 1].learn(new Vector(target[index]), null, alpha);
        for (int indice = 1; indice < layers.length; indice++) {
          layers[layers.length - indice - 1].learn(null, layers[layers.length - indice], alpha);
        }
      }
      if (Double.isNaN(layers[layers.length - 1].getDisplayError().total())) {
        System.err.println("Error exploded, producing NaN (not a number). Program terminated automatically.");
        System.exit(-1);
      }
      if (epoch < 100 || iter % 50 == 0) {
        System.out.println("Epoch: " + iter + " Error: " + layers[layers.length - 1].getDisplayError().total() / input.length + " Time: " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds.");
      }
    }
  }

  public void export(String name) {
    System.out.printf("Exporting to %s.\n", name);

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

    Object[] objects = {weights, bias, activationFunctions, type};

    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(name)))) {
      oos.writeObject(objects);
    } catch (IOException ex) {
      System.err.printf("Cannot export to %s.\n", name);
    }
  }

  public Vector test(double[] input) {
    Vector out = layers[0].test(new Vector(input));
    for (int index = 1; index < layers.length; index++) {
      out = layers[index].test(out);
    }
    return out;
  }

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
