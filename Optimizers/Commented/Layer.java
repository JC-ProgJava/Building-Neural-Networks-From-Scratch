import java.util.stream.IntStream;

public class Layer {
  private final boolean isOutputLayer;
  private final ActivationFunction activationFunction;
  private final Error errorType;
  Vector[] vectors;
  Vector bias;
  private Vector error;
  private Vector displayError;

  private int BATCH_SIZE = 1;
  private int currentInputBatchId = 0;

  private Vector input;
  private Vector output;

  private Vector[] deltas;
  private Vector deltaBias;

  private Vector weightedSum;

  private Optimizer optimizer;
  private Vector[] prevUpdate;
  private Vector[] adaptDelta;
  private double beta1;
  private double beta2;


  Layer(boolean isOutputLayer, ActivationFunction activationFunction, Error error) {
    this.isOutputLayer = isOutputLayer;
    this.activationFunction = activationFunction;
    errorType = error;
  }

  Layer(Vector[] vectors, Vector bias, boolean isOutputLayer, ActivationFunction activationFunction, Error error) {
    this.vectors = vectors;
    this.bias = bias;
    this.isOutputLayer = isOutputLayer;
    this.activationFunction = activationFunction;
    errorType = error;
    initDeltaArray();
  }

  private static Vector softmax(Vector val) {
    Vector out = new Vector(val.length());
    double total = 0;

    for (double v : val.values()) {
      total += Math.exp(v);
    }

    if (total == 0.0) {
      System.out.println(val);
      throw new ArithmeticException("softmax(Vector val): total is 0, cannot divide by 0.");
    } else if (total == Double.POSITIVE_INFINITY) {
      System.out.println(val);
      throw new ArithmeticException("softmax(Vector val): total overflowed to infinity.");
    } else if (Double.isNaN(total)) {
      System.out.println(val);
      throw new ArithmeticException("softmax(Vector val): total is NaN.");
    }

    for (int i = 0; i < val.length(); i++) {
      out.set(i, Math.exp(val.get(i)) / total);
    }

    return out;
  }

  private static double elu(double val) {
    return val > 0 ? val : Math.exp(val) - 1.0;
  }

  private static double sigmoid(double val) {
    return 1.0 / (1.0 + Math.exp(-val));
  }

  private static double relu(double val) {
    return val < 0 ? 0 : val;
  }

  private static double leaky_relu(double val) {
    return val < 0 ? 0.01 * val : val;
  }

  private static double softplus(double val) {
    return Math.log(1.0 + Math.exp(val));
  }

  private static double tanh(double val) {
    return (Math.exp(val) - Math.exp(-val)) / (Math.exp(val) + Math.exp(-val));
  }

  private void setOptimizer(Optimizer optimizer) {
    this.optimizer = optimizer;

    if (prevUpdate == null) {
      if (optimizer == Optimizer.ADAM || optimizer == Optimizer.ADAMAX) {
        beta1 = 0.9;
        beta2 = 0.999;
      }
      prevUpdate = new Vector[vectors.length];
      adaptDelta = new Vector[vectors.length];
      for (int i = 0; i < vectors.length; i++) {
        prevUpdate[i] = new Vector(vectors[i].length());
        adaptDelta[i] = new Vector(vectors[i].length());
      }
    }
  }

  private void initDeltaArray() {
    deltas = new Vector[vectors.length];
    for (int i = 0; i < vectors.length; i++) {
      deltas[i] = new Vector(vectors[i].length());
    }

    deltaBias = new Vector(vectors.length);

    if (isOutputLayer) {
      displayError = new Vector(vectors.length);
    }
  }

  void setBatchSize(int BATCH_SIZE) {
    this.BATCH_SIZE = BATCH_SIZE;
  }

  int length() {
    return vectors.length;
  }

  private Vector getError() {
    return error;
  }

  Vector getDisplayError() {
    return displayError;
  }

  private void resetDisplayError() {
    if (displayError == null) {
      return;
    }
    displayError = displayError.mult(0);
  }

  Vector getOutput() {
    return output;
  }

  Error getErrorType() {
    return errorType;
  }

  Vector getBias() {
    return bias;
  }

  void clearCache() {
    prevUpdate = null;
    adaptDelta = null;
  }

  ActivationFunction getActivationFunction() {
    return activationFunction;
  }

  void feed(Vector input) {
    this.input = input;
    output = test(input);
  }

  Vector test(Vector input) {
    if (input.length() != vectors[0].length()) {
      throw new IllegalArgumentException("test(Vector): Input vector not the same size as weight vectors.");
    }

    Vector output = new Vector(vectors.length);
    for (int i = 0; i < vectors.length; i++) {
      output.set(i, vectors[i].mult(input).total());
    }

    output = output.add(bias);
    weightedSum = output;

    output = activation(weightedSum);

    return output;
  }

  private Vector activation(Vector val) {
    int valSize = val.length();
    Vector out = new Vector(valSize);
    for (int index = 0; index < valSize; index++) {
      switch (activationFunction) {
        case IDENTITY:
          return val;
        case TANH:
          out.set(index, tanh(val.get(index)));
          break;
        case RELU:
          out.set(index, relu(val.get(index)));
          break;
        case LEAKY_RELU:
          out.set(index, leaky_relu(val.get(index)));
          break;
        case SIGMOID:
          out.set(index, sigmoid(val.get(index)));
          break;
        case SOFTPLUS:
          out.set(index, softplus(val.get(index)));
          break;
        case ELU:
          out.set(index, elu(val.get(index)));
          break;
        case SOFTMAX:
          return softmax(val);
        default:
          throw new IllegalArgumentException("activation(): No such ActivationFunction '" + activationFunction + "'.");
      }
    }
    return out;
  }

  private Vector derivative(int index) {
    int outLength = output.length();
    Vector returnVector = new Vector(outLength);
    switch (activationFunction) {
      case IDENTITY:
        return new Vector(output.length()).fill(1.0);
      case TANH:
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, 1.0 - output.get(i) * output.get(i));
        }
        return returnVector;
      case RELU: {
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, output.get(i) < 0.0 ? 0.0 : 1.0);
        }
        return returnVector;
      }
      case LEAKY_RELU: {
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, output.get(i) < 0.0 ? 0.01 : 1.0);
        }
        return returnVector;
      }
      case SIGMOID:
        return output.mult(output.mult(-1.0).add(1.0));
      case SOFTMAX: {
        for (int i = 0; i < outLength; i++) {
          if (i == index) {
            returnVector.set(i, output.get(i) * (1.0 - output.get(i)));
          } else {
            returnVector.set(i, -1.0 * output.get(i) * output.get(index));
          }
        }

        return returnVector;
      }
      case SOFTPLUS: {
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, sigmoid(weightedSum.get(i)));
        }
        return returnVector;
      }
      case ELU: {
        for (int i = 0; i < outLength; i++) {
          if (output.get(i) >= 0) {
            returnVector.set(i, 1.0);
          } else {
            returnVector.set(i, Math.exp(output.get(i)));
          }
        }

        return returnVector;
      }
      default:
        throw new IllegalArgumentException(activationFunction + " is not a valid ActivationFunction type.");
    }
  }


  void learn(Vector targetOutput, Layer nextLayer, double alpha, Optimizer optimizer) {
    if (alpha == 0) {
      throw new NullPointerException("learn(): learning rate can't be 0 (no learning).");
    }

    setOptimizer(optimizer);

    if (isOutputLayer) {
      resetDisplayError();
    }

    IntStream.range(0, vectors.length).parallel().forEach(index -> {
      Vector error;
      if (isOutputLayer) {
        Vector[] errorCalc = calcError(output, targetOutput, index);
        error = errorCalc[0];
        displayError = displayError.add(errorCalc[1]);
      } else {
        error = new Vector(vectors.length);
        for (int indice = 0; indice < nextLayer.vectors.length; indice++) {
          error.set(index, error.get(index) + nextLayer.vectors[indice].get(index) * nextLayer.getError().get(indice));
        }
        error = error.mult(derivative(index));
      }
      this.error = error;

      Vector delta = input.mult(this.error.get(index));
      delta = applyOptimizer(delta, index, alpha);

      deltas[index] = deltas[index].add(delta);
      deltaBias.set(index, deltaBias.get(index) + error.get(index) * alpha);
    });

    currentInputBatchId++;

    if (currentInputBatchId == BATCH_SIZE) {
      currentInputBatchId = 0;
      for (int index = 0; index < vectors.length; index++) {
        vectors[index] = vectors[index].subtract(deltas[index]);
        deltas[index] = deltas[index].mult(0.0);
      }
      bias = bias.subtract(deltaBias);
      deltaBias = deltaBias.mult(0.0);
    }
  }


  private Vector applyOptimizer(Vector delta, int index, double alpha) {
    switch (optimizer) {
      case MOMENTUM:
        delta = delta.mult(alpha).add(prevUpdate[index].mult(0.9));
        prevUpdate[index] = delta;
        break;
      case ADAGRAD: {
        final double epsilon = 1e-4;
        Vector finalAlpha = new Vector(vectors[index].length());
        for (int i = 0; i < vectors[index].length(); i++) {
          finalAlpha.set(i, alpha / Math.sqrt(prevUpdate[index].get(i) + epsilon));
          prevUpdate[index].set(i, prevUpdate[index].get(i) + delta.get(i) * delta.get(i));
        }
        delta = delta.mult(finalAlpha);
        break;
      }
      case RMSPROP: {
        final double epsilon = 1e-4;
        beta1 = 0.9;
        Vector finalAlpha = new Vector(vectors[index].length());
        for (int i = 0; i < vectors[index].length(); i++) {
          finalAlpha.set(i, alpha / Math.sqrt(prevUpdate[index].get(i) + epsilon));
          prevUpdate[index].set(i, prevUpdate[index].get(i) * beta1 + (1.0 - beta1) * delta.get(i) * delta.get(i));
        }
        delta = delta.mult(finalAlpha);
        break;
      }
      case ADADELTA: {
        final double epsilon = 1e-6;
        beta1 = 0.95;
        Vector finalAlpha = new Vector(vectors[index].length());
        for (int i = 0; i < vectors[index].length(); i++) {
          finalAlpha.set(i, Math.sqrt(adaptDelta[index].get(i) + epsilon) / Math.sqrt(prevUpdate[index].get(i) + epsilon));
          prevUpdate[index].set(i, prevUpdate[index].get(i) * beta1 + (1.0 - beta1) * delta.get(i) * delta.get(i));
          double adaptD = delta.get(i) * -finalAlpha.get(i);
          adaptDelta[index].set(i, adaptDelta[index].get(i) * beta1 + (1.0 - beta1) * adaptD * adaptD);
        }
        delta = delta.mult(finalAlpha);
        break;
      }
      case ADAM: {
        final double epsilon = 1e-7;
        Vector finalAlpha = new Vector(vectors[index].length());
        for (int i = 0; i < vectors[index].length(); i++) {
          prevUpdate[index].set(i, prevUpdate[index].get(i) * beta1 + (1.0 - beta1) * delta.get(i));
          adaptDelta[index].set(i, adaptDelta[index].get(i) * beta2 + (1.0 - beta2) * delta.get(i) * delta.get(i));
          finalAlpha.set(i, alpha / (Math.sqrt((adaptDelta[index].get(i)) / (1.0 - beta2)) + epsilon));
        }
        delta = prevUpdate[index].mult(1.0 / (1.0 - beta1)).mult(finalAlpha);
        break;
      }
      case ADAMAX: {
        final double epsilon = 1e-7;
        for (int i = 0; i < vectors[index].length(); i++) {
          prevUpdate[index].set(i, prevUpdate[index].get(i) * beta1 + (1.0 - beta1) * delta.get(i));
          adaptDelta[index].set(i, Math.max(beta2 * adaptDelta[index].get(i), Math.abs(delta.get(i))));
        }
        delta = prevUpdate[index].mult(alpha / (1.0 - beta1)).div(adaptDelta[index].add(epsilon));
        break;
      }
      default: {
        delta = delta.mult(alpha);
        break;
      }
    }
    return delta;
  }

  private Vector[] calcError(Vector output, Vector targetOutput, int index) {
    switch (errorType) {
      case MEAN_SQUARED: {
        Vector err = (output.subtract(targetOutput));
        return new Vector[]{err.mult(2).mult(derivative(index)), err.mult(err)};
      }

      case BINARY_CROSS_ENTROPY: {
        Vector display = (targetOutput.mult(output.log()).add((targetOutput.mult(-1.0).add(1.0))).mult(output.mult(-1.0).add(1.0).log())).mult(-1.0);
        return new Vector[]{(output.subtract(targetOutput)).div(output.mult(output.mult(-1.0).add(1.0))), display};
      }

      case CATEGORICAL_CROSS_ENTROPY: {
        Vector display = targetOutput.mult(output.log()).mult(-1.0);
        return new Vector[]{output.subtract(targetOutput), display};
      }

      default:
        throw new IllegalArgumentException("calcError(): Error type " + errorType + " unknown.");
    }
  }

  Layer fillGaussian(int size, int numVectors) {
    vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      vectors[i] = new Vector(size).fillGaussian();
    }

    bias = new Vector(numVectors).fillGaussian();
    initDeltaArray();

    return this;
  }

  void fillGaussian(double average, double deviation) {
    Vector[] replaceVect = new Vector[vectors.length];

    for (int i = 0; i < replaceVect.length; i++) {
      replaceVect[i] = new Vector(vectors[i].length()).fillGaussian(average, deviation);
    }

    bias = new Vector(vectors.length).fillGaussian(average, deviation);

    vectors = replaceVect;
    initDeltaArray();
  }

  void fillRandom(int size, int numVectors) {
    vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      vectors[i] = new Vector(size).fillRandom();
    }

    bias = new Vector(numVectors).fillRandom();
    initDeltaArray();
  }
}
