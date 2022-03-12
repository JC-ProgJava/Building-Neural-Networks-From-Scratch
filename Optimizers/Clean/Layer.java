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

  Layer(boolean isOutputLayer, ActivationFunction activationFunction, Error error) {
    this.isOutputLayer = isOutputLayer;
    this.activationFunction = activationFunction;
    this.errorType = error;
  }

  Layer(Vector[] vectors, Vector bias, boolean isOutputLayer, ActivationFunction activationFunction, Error error) {
    this.vectors = vectors;
    this.bias = bias;
    this.isOutputLayer = isOutputLayer;
    this.activationFunction = activationFunction;
    this.errorType = error;
    this.initDeltaArray();
  }

  private static Vector softmax(Vector val) {
    Vector out = new Vector(val.length());
    double total = 0.0;

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
    if (this.prevUpdate == null) {
      this.prevUpdate = new Vector[this.vectors.length];
      this.adaptDelta = new Vector[this.vectors.length];
      for (int i = 0; i < this.vectors.length; i++) {
        this.prevUpdate[i] = new Vector(this.vectors[i].length());
        this.adaptDelta[i] = new Vector(this.vectors[i].length());
      }
    }
  }

  private void initDeltaArray() {
    this.deltas = new Vector[this.vectors.length];
    for (int i = 0; i < this.vectors.length; i++) {
      this.deltas[i] = new Vector(this.vectors[i].length());
    }

    this.deltaBias = new Vector(this.vectors.length);

    if (this.isOutputLayer) {
      this.displayError = new Vector(this.vectors.length);
    }
  }

  void setBatchSize(int BATCH_SIZE) {
    this.BATCH_SIZE = BATCH_SIZE;
  }

  int length() {
    return this.vectors.length;
  }

  private Vector getError() {
    return this.error;
  }

  Vector getDisplayError() {
    return this.displayError;
  }

  private void resetDisplayError() {
    if (this.displayError == null) {
      return;
    }
    this.displayError = this.displayError.mult(0);
  }

  Vector getOutput() {
    return this.output;
  }

  Error getErrorType() {
    return this.errorType;
  }

  Vector getBias() {
    return this.bias;
  }

  ActivationFunction getActivationFunction() {
    return this.activationFunction;
  }

  void feed(Vector input) {
    this.input = input;
    this.output = this.test(input);
  }

  Vector test(Vector input) {
    if (input.length() != this.vectors[0].length()) {
      throw new IllegalArgumentException("test(Vector): Input vector not the same size as weight vectors.");
    }

    Vector output = new Vector(this.vectors.length);
    for (int i = 0; i < this.vectors.length; i++) {
      output.set(i, this.vectors[i].mult(input).total());
    }

    output = output.add(this.bias);
    this.weightedSum = output;

    output = this.activation(this.weightedSum);

    return output;
  }

  private Vector activation(Vector val) {
    int valSize = val.length();
    Vector out = new Vector(valSize);
    for (int index = 0; index < valSize; index++) {
      switch (this.activationFunction) {
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
          throw new IllegalArgumentException("activation(): No such ActivationFunction '" + this.activationFunction + "'.");
      }
    }
    return out;
  }

  private Vector derivative(int index) {
    int outLength = this.output.length();
    Vector returnVector = new Vector(outLength);
    switch (this.activationFunction) {
      case IDENTITY:
        return new Vector(this.output.length()).fill(1.0);
      case TANH:
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, 1.0 - this.output.get(i) * this.output.get(i));
        }
        return returnVector;
      case RELU: {
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, this.output.get(i) < 0.0 ? 0.0 : 1.0);
        }
        return returnVector;
      }
      case LEAKY_RELU: {
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, this.output.get(i) < 0.0 ? 0.01 : 1.0);
        }
        return returnVector;
      }
      case SIGMOID:
        return this.output.mult(this.output.mult(-1.0).add(1.0));
      case SOFTMAX: {
        for (int i = 0; i < outLength; i++) {
          if (i == index) {
            returnVector.set(i, this.output.get(i) * (1.0 - this.output.get(i)));
          } else {
            returnVector.set(i, -1.0 * this.output.get(i) * this.output.get(index));
          }
        }

        return returnVector;
      }
      case SOFTPLUS: {
        for (int i = 0; i < outLength; i++) {
          returnVector.set(i, sigmoid(this.weightedSum.get(i)));
        }
        return returnVector;
      }
      case ELU: {
        for (int i = 0; i < outLength; i++) {
          if (this.output.get(i) >= 0) {
            returnVector.set(i, 1.0);
          } else {
            returnVector.set(i, Math.exp(this.output.get(i)));
          }
        }

        return returnVector;
      }
      default:
        throw new IllegalArgumentException(this.activationFunction + " is not a valid ActivationFunction type.");
    }
  }


  void learn(Vector targetOutput, Layer nextLayer, double alpha, Optimizer optimizer) {
    if (alpha == 0) {
      throw new NullPointerException("learn(): learning rate can't be 0 (no learning).");
    }

    this.setOptimizer(optimizer);

    if (this.isOutputLayer) {
      this.resetDisplayError();
    }

    IntStream.range(0, this.vectors.length).parallel().forEach(index -> {
      Vector error;
      if (this.isOutputLayer) {
        Vector[] errorCalc = this.calcError(this.output, targetOutput);
        error = errorCalc[0];
        this.displayError = this.displayError.add(errorCalc[1]);
      } else {
        error = new Vector(this.vectors.length);
        for (int indice = 0; indice < nextLayer.vectors.length; indice++) {
          error.set(index, error.get(index) + nextLayer.vectors[indice].get(index) * nextLayer.getError().get(indice));
        }
      }
      error = error.mult(this.derivative(index));
      this.error = error;

      Vector delta = this.input.mult(this.error.get(index));
      delta = this.applyOptimizer(delta, index, alpha);

      this.deltas[index] = this.deltas[index].add(delta);
      this.deltaBias.set(index, this.deltaBias.get(index) + error.get(index) * alpha);
    });

    this.currentInputBatchId++;

    if (this.currentInputBatchId == this.BATCH_SIZE) {
      this.currentInputBatchId = 0;
      for (int index = 0; index < this.vectors.length; index++) {
        this.vectors[index] = this.vectors[index].subtract(this.deltas[index]);
        this.deltas[index] = this.deltas[index].mult(0.0);
      }
      this.bias = this.bias.subtract(this.deltaBias);
      this.deltaBias = this.deltaBias.mult(0.0);
    }
  }


  private Vector applyOptimizer(Vector delta, int index, double alpha) {
    switch (this.optimizer) {
      case MOMENTUM:
        delta = delta.mult(alpha).add(this.prevUpdate[index].mult(0.9));
        this.prevUpdate[index] = delta;
        break;
      case ADAGRAD: {
        final double epsilon = 1e-4;
        Vector finalAlpha = new Vector(this.vectors[index].length());
        for (int i = 0; i < this.vectors[index].length(); i++) {
          finalAlpha.set(i, alpha / Math.sqrt(this.prevUpdate[index].get(i) + epsilon));
          this.prevUpdate[index].set(i, this.prevUpdate[index].get(i) + delta.get(i) * delta.get(i));
        }
        delta = delta.mult(finalAlpha);
        break;
      }
      case RMSPROP: {
        final double epsilon = 1e-4;
        final double beta = 0.9;
        Vector finalAlpha = new Vector(this.vectors[index].length());
        for (int i = 0; i < this.vectors[index].length(); i++) {
          finalAlpha.set(i, alpha / Math.sqrt(this.prevUpdate[index].get(i) + epsilon));
          this.prevUpdate[index].set(i, this.prevUpdate[index].get(i) * beta + (1.0 - beta) * delta.get(i) * delta.get(i));
        }
        delta = delta.mult(finalAlpha);
        break;
      }
      case ADADELTA: {
        final double epsilon = 1e-6;
        final double beta = 0.95;
        Vector finalAlpha = new Vector(this.vectors[index].length());
        for (int i = 0; i < this.vectors[index].length(); i++) {
          finalAlpha.set(i, Math.sqrt(this.adaptDelta[index].get(i) + epsilon) / Math.sqrt(this.prevUpdate[index].get(i) + epsilon));
          this.prevUpdate[index].set(i, this.prevUpdate[index].get(i) * beta + (1.0 - beta) * delta.get(i) * delta.get(i));
          double adaptD = delta.get(i) * -finalAlpha.get(i);
          this.adaptDelta[index].set(i, this.adaptDelta[index].get(i) * beta + (1.0 - beta) * adaptD * adaptD);
        }
        delta = delta.mult(finalAlpha);
        break;
      }
      case ADAM: {
        final double epsilon = 1e-7;
        final double beta1 = 0.9;
        final double beta2 = 0.999;
        Vector finalAlpha = new Vector(this.vectors[index].length());
        for (int i = 0; i < this.vectors[index].length(); i++) {
          this.prevUpdate[index].set(i, this.prevUpdate[index].get(i) * beta1 + (1.0 - beta1) * delta.get(i));
          this.adaptDelta[index].set(i, this.adaptDelta[index].get(i) * beta2 + (1.0 - beta2) * delta.get(i) * delta.get(i));
          finalAlpha.set(i, alpha / (Math.sqrt((this.adaptDelta[index].get(i)) / (1.0 - beta2)) + epsilon));
        }
        delta = this.prevUpdate[index].mult(1.0 / (1.0 - beta1)).mult(finalAlpha);
        break;
      }
      default: {
        delta = delta.mult(alpha);
        break;
      }
    }
    return delta;
  }

  private Vector[] calcError(Vector output, Vector targetOutput) {
    switch (this.errorType) {
      case MEAN_SQUARED: {
        Vector err = (output.subtract(targetOutput));
        return new Vector[]{err.mult(2), err.mult(err)};
      }

      case BINARY_CROSS_ENTROPY: {
        Vector display = (targetOutput.mult(output.log()).add((targetOutput.mult(-1.0).add(1.0))).mult(output.mult(-1.0).add(1.0).log())).mult(-1.0);
        return new Vector[]{(output.subtract(targetOutput)).div(output.mult(output.mult(-1.0).add(1.0))), display};
      }

      case CATEGORICAL_CROSS_ENTROPY: {
        Vector display = targetOutput.mult(output.log()).mult(-1.0);
        return new Vector[]{output.subtract(targetOutput), display};
      }

      default: {
        throw new IllegalArgumentException("calcError(): Error type " + this.errorType + " unknown.");
      }
    }
  }

  Layer fillGaussian(int size, int numVectors) {
    this.vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      this.vectors[i] = new Vector(size).fillGaussian();
    }

    this.bias = new Vector(numVectors).fillGaussian();
    this.initDeltaArray();

    return this;
  }

  void fillGaussian(double average, double deviation) {
    Vector[] replaceVect = new Vector[this.vectors.length];

    for (int i = 0; i < replaceVect.length; i++) {
      replaceVect[i] = new Vector(this.vectors[i].length()).fillGaussian(average, deviation);
    }

    this.bias = new Vector(this.vectors.length).fillGaussian(average, deviation);

    this.vectors = replaceVect;
    this.initDeltaArray();
  }

  void fillRandom(int size, int numVectors) {
    this.vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      this.vectors[i] = new Vector(size).fillRandom();
    }

    this.bias = new Vector(numVectors).fillRandom();
    this.initDeltaArray();
  }
}
