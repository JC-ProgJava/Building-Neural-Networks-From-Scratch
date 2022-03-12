import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Network {
  private Layer[] layers;

  public Network(Vector config, ActivationFunction[] activationFunctions, Error errorType) {
    if ((config.length() - 1) != activationFunctions.length) {
      throw new IllegalArgumentException("Network(Vector, ActivationFunction[], Error errorType): Vector length must be one more than " +
        "ActivationFunction[] length.");
    }

    this.layers = new Layer[activationFunctions.length];

    for (int i = 0; i < config.length() - 1; i++) {
      if (i == this.layers.length - 1) {
        this.layers[i] = new Layer(true, activationFunctions[i], errorType).fillGaussian((int) config.get(i), (int) config.get(i + 1));
      } else {
        this.layers[i] = new Layer(false, activationFunctions[i], errorType).fillGaussian((int) config.get(i), (int) config.get(i + 1));
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

      this.layers = new Layer[weights.length];

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
        this.layers[i] = matrix;
      }
    } catch (IOException | ClassNotFoundException | ClassCastException e) {
      System.err.println("An error occurred when importing an exported network.");
      System.err.println(e);
    }
    System.out.println("Note: You are using an exported network.");
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

  Network fromCustomGaussianDistribution(double average, double deviation) {
    for (Layer layer : this.layers) {
      layer.fillGaussian(average, deviation);
    }

    return this;
  }

  public Network fromUniformDistribution() {
    for (Layer layer : this.layers) {
      layer.fillRandom(layer.vectors[0].length(), layer.length());
    }

    return this;
  }

  void train(double[][] input, double[][] target, int epoch, double alpha, int BATCH_SIZE, Optimizer optimizer)
    throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("input.ser"));
    ObjectInputStream oiss = new ObjectInputStream(new FileInputStream("target.ser"));
    double[][] testInputs = (double[][]) ois.readObject();
    double[][] testTargets = (double[][]) oiss.readObject();

    if (input.length != target.length) {
      throw new IllegalArgumentException("train(): Input and target (expected) indices must have the same number of examples.");
    }

    if (BATCH_SIZE <= 0) {
      throw new IllegalArgumentException("train(): Batch size must be positive.");
    } else if (input.length % BATCH_SIZE != 0) {
      throw new IllegalArgumentException("train(): Batch size must evenly divide data.");
    }

    for (Layer layer : this.layers) {
      layer.setBatchSize(BATCH_SIZE);
    }

    {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++) {
        this.layers[0].feed(new Vector(input[i]));
        int size = this.layers.length;
        for (int indice = 1; indice < size; indice++) {
          this.layers[indice].feed(this.layers[indice - 1].getOutput());
        }

        this.layers[this.layers.length - 1].learn(new Vector(target[i]), null, alpha, optimizer);
        for (int indice = 1; indice < size; indice++) {
          this.layers[this.layers.length - indice - 1].learn(null, this.layers[this.layers.length - indice], alpha, optimizer);
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
      long start = System.currentTimeMillis();
      for (int index = 0; index < input.length; index++) {
        this.layers[0].feed(new Vector(input[index]));
        for (int indice = 1; indice < this.layers.length; indice++) {
          this.layers[indice].feed(this.layers[indice - 1].getOutput());
        }

        this.layers[this.layers.length - 1].learn(new Vector(target[index]), null, alpha, optimizer);
        for (int indice = 1; indice < this.layers.length; indice++) {
          this.layers[this.layers.length - indice - 1].learn(null, this.layers[this.layers.length - indice], alpha, optimizer);
        }
        if (index % 1000 == 0) {
          String str = String.format("%-60.60s",
            "\rEpoch: " + iter + " Error: " + this.layers[this.layers.length - 1].getDisplayError().total() / (index + 1) + " Time: " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds.") + " |" + String.format("%-10.10s", ("=").repeat((int) ((double) index / (double) input.length * 10.0))) + "|";
          System.out.print(str);
        }
      }
      if (Double.isNaN(this.layers[this.layers.length - 1].getDisplayError().total())) {
        System.err.println("Error exploded, producing NaN (not a number). Program terminated automatically.");
        System.exit(-1);
      }

      int corrects = 0;
      for (int i = 0; i < 60000; i++) {
        int x = getMax(this.test(testInputs[i]));
        int actual = -1;
        for (int j = 0; j < testTargets[i].length; j++) {
          if (testTargets[i][j] == 1) {
            actual = j;
            break;
          }
        }
        corrects += x == actual ? 1 : 0;
      }
      double accuracyTrain = 100.0 * (double) corrects / 60000.0;

      corrects = 0;
      for (int i = 60000; i < 70000; i++) {
        int x = getMax(this.test(testInputs[i]));
        int actual = -1;
        for (int j = 0; j < testTargets[i].length; j++) {
          if (testTargets[i][j] == 1) {
            actual = j;
            break;
          }
        }
        corrects += x == actual ? 1 : 0;
      }
      double accuracyTest = 100.0 * (double) corrects / 10000.0;

      String str =
        "\rEpoch: " + iter + " Error: " + this.layers[this.layers.length - 1].getDisplayError().total() / (input.length) + " Time: " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds.";
      System.out.print(str);
      System.out.println();
      System.out.printf("\tTrain Acc. (%%): %.2f\n", accuracyTrain);
      System.out.printf("\tTest Acc. (%%): %.2f\n", accuracyTest);
    }
  }

  void export(String name) {
    System.out.printf("Exporting to %s.\n", name);

    double[][][] weights = new double[this.layers.length][][];
    double[][] bias = new double[this.layers.length][];
    ActivationFunction[] activationFunctions = new ActivationFunction[this.layers.length];
    Error type = this.layers[this.layers.length - 1].getErrorType();
    for (int i = 0; i < this.layers.length; i++) {
      double[][] layerWeights = new double[this.layers[i].length()][];
      for (int j = 0; j < layerWeights.length; j++) {
        layerWeights[j] = this.layers[i].vectors[j].values();
      }
      activationFunctions[i] = this.layers[i].getActivationFunction();
      weights[i] = layerWeights;
      bias[i] = this.layers[i].getBias().values();
    }

    Object[] objects = {weights, bias, activationFunctions, type};

    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(name)))) {
      oos.writeObject(objects);
    } catch (IOException ex) {
      System.err.printf("Cannot export to %s.\n", name);
    }
  }

  public Vector test(double[] input) {
    Vector out = this.layers[0].test(new Vector(input));
    for (int index = 1; index < this.layers.length; index++) {
      out = this.layers[index].test(out);
    }
    return out;
  }

  @Override
  public String toString() {
    int trainableParams = 0;
    for (Layer layer : this.layers) {
      trainableParams += (layer.vectors[0].length() + 1) * (layer.length());
    }
    StringBuilder out = new StringBuilder();
    out.append("Network with ").append(this.layers.length).append(" layers and ").append(trainableParams).append(" trainable parameters.\n");
    out.append("Size: {");
    out.append(this.layers[0].vectors[0].length()).append(", ");
    for (int i = 0; i < this.layers.length; i++) {
      out.append(this.layers[i].length());
      if (i != this.layers.length - 1) {
        out.append(", ");
      } else {
        out.append("}");
      }
    }
    for (int i = 0; i < this.layers.length; i++) {
      out.append("\n\tLayer ").append(i + 1).append(":\n");
      out.append("\t\tSize: ").append(this.layers[i].vectors[0].length()).append("-").append(this.layers[i].length());
      out.append("\n\t\tActivation Function: ").append(this.layers[i].getActivationFunction());
      out.append("\n\t\tError: ").append(this.layers[i].getErrorType());
    }
    return out.toString();
  }

  void exportToExternal(String name) {
    name += ".txt";
    System.out.printf("Exporting to %s. You can read this file using other programming languages.\n", name);

    double[][][] weights = new double[this.layers.length][][];
    double[][] bias = new double[this.layers.length][];
    for (int i = 0; i < this.layers.length; i++) {
      double[][] layerWeights = new double[this.layers[i].length()][];
      for (int j = 0; j < layerWeights.length; j++) {
        layerWeights[j] = this.layers[i].vectors[j].values();
      }
      weights[i] = layerWeights;
      bias[i] = this.layers[i].bias.values();
    }

    try (FileWriter fileWriter = new FileWriter(name)) {
      for (int i = 0; i < this.layers.length; i++) {
        if (i != 0) {
          fileWriter.write("!");
        }
        for (int j = 0; j < weights[i].length; j++) {
          if (j != 0) {
            fileWriter.write("\n");
          }
          for (int k = 0; k < weights[i][j].length; k++) {
            if (k != 0) {
              fileWriter.write(",");
            }
            fileWriter.write(String.valueOf(weights[i][j][k]));
          }
        }
      }

      fileWriter.write("\nbias:");

      for (int i = 0; i < this.layers.length; i++) {
        if (i != 0) {
          fileWriter.write("\n");
        }
        for (int j = 0; j < bias[i].length; j++) {
          if (j != 0) {
            fileWriter.write(",");
          }

          fileWriter.write(String.valueOf(bias[i][j]));
        }
      }
    } catch (IOException e) {
      System.err.println("An error occurred during the export.");
    }
  }
}
