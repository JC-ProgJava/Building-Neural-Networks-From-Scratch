import java.util.stream.IntStream;

class Layer {
  private final boolean isOutputLayer;
  private final ActivationFunction activationFunction;
  private final Error errorType;
  Vector[] vectors;
  Vector bias;

  Vector error;
  Vector displayError;

  private int BATCH_SIZE = 1;
  private int currentInputBatchId = 0;

  private Vector input;
  private Vector output;

  private Vector[] deltas;
  private Vector deltaBias;
  
  private Vector weightedSum;

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
    initDeltaArray();
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
    return this.vectors.length;
  }

  Vector getError() {
    return error;
  }

  Vector getDisplayError() {
    return displayError;
  }

  void resetDisplayError() {
    if (displayError == null || !isOutputLayer) {
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

  ActivationFunction getActivationFunction() {
    return activationFunction;
  }

  void feed(Vector input) {
    this.input = input;
    this.output = test(input);
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
    this.weightedSum = output;

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

  private Vector softmax(Vector val) {
    Vector out = new Vector(val.length());
    double total = 0.0;

    for (int i = 0; i < val.length(); i++) {
      total += Math.exp(val.get(i));
    }

    if (total == 0.0) {
      System.out.println(val);
      throw new ArithmeticException("softmax(Vector val): total is 0, cannot divide by 0.");
    }
    for (int i = 0; i < val.length(); i++) {
      out.set(i, (Math.exp(val.get(i)) / total));
    }

    return out;
  }

  private double elu(double val) {
    return val > 0 ? val : Math.exp(val) - 1.0;
  }

  private double sigmoid(double val) {
    return 1.0 / (1.0 + Math.exp(-val));
  }

  private double relu(double val) {
    return val <= 0 ? 0 : val;
  }

  private double leaky_relu(double val) {
    return val <= 0 ? 0.01 * val : val;
  }

  private double softplus(double val) {
    return Math.log(1.0 + Math.exp(val));
  }

  private double tanh(double val) {
    return (Math.exp(val) - Math.exp(-val)) / (Math.exp(val) + Math.exp(-val));
  }

  Vector derivative(int index) {
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
            returnVector.set(i, Math.exp(weightedSum.get(i)));
          }
        }

        return returnVector;
      }
      default:
        throw new IllegalArgumentException(activationFunction + " is not a valid ActivationFunction type.");
    }
  }


  void learn(Vector targetOutput, Layer nextLayer, double alpha) {
    if (alpha == 0) {
      throw new NullPointerException("learn(): learning rate can't be 0 (no learning).");
    }

    IntStream.range(0, vectors.length).parallel().forEach(index -> {
      Vector error;
      if (isOutputLayer) {
        Vector[] errorCalc = calcError(output, targetOutput);
        error = errorCalc[0];
        displayError = displayError.add(errorCalc[1]);
      } else {
        error = new Vector(vectors.length);
        for (int indice = 0; indice < nextLayer.vectors.length; indice++) {
          error.set(index, error.get(index) + nextLayer.vectors[indice].get(index) * nextLayer.getError().get(indice));
        }
      }
      error = error.mult(derivative(index));
      this.error = error;

      Vector delta = input.mult(error.get(index));
      delta = delta.mult(alpha);
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

  Vector[] calcError(Vector output, Vector targetOutput) {
    switch (errorType) {
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
        throw new IllegalArgumentException("calcError(): Error type " + errorType + " unknown.");
      }
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
      replaceVect[i] = new Vector(this.vectors[i].length()).fillGaussian(average, deviation);
    }

    bias = new Vector(vectors.length).fillGaussian(average, deviation);

    this.vectors = replaceVect;
    initDeltaArray();
  }

  void fillRandom(int size, int numVectors) {
    this.vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      vectors[i] = new Vector(size).fillRandom();
    }

    bias = new Vector(numVectors).fillRandom();
    initDeltaArray();
  }
}
