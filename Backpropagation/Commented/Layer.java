// Import necessary libraries.

import java.util.stream.IntStream;

// Our layer class stores weights in the layer specified.
class Layer {
  // Stores whether the layer is the output layer of a network.
  private final boolean isOutputLayer;

  // Stores the activation function used in the layer.
  private final ActivationFunction activationFunction;

  // Stores the error function used during training.
  private final Error errorType;

  // Stores our weights and biases
  Vector[] vectors; // > weights
  Vector bias;      // > biases

  // Stores the actual and display error of our layer
  // during training. Actual error is just the derivative
  // of the error function with respect to the layer output.
  // Display error is the actual error function itself applied on
  // our layer output.
  Vector error;        // > actual error
  Vector displayError; // > display error

  // Stores the batch size for training.
  // By default, SGD (stochastic gradient descent)
  // is used during training.
  private int BATCH_SIZE = 1;

  // Stores the current example's order in the batch.
  // If the number is equal to the BATCH_SIZE,
  // then we perform a weight and bias update and reset
  // this variable.
  private int currentInputBatchId = 0;

  // Stores the input to the layer and the output of the
  // layer when given the input.
  private Vector input;
  private Vector output;

  // Stores the amount to change our parameters by.
  // These are only reset after each batch.
  private Vector[] deltas;  // > weight update
  private Vector deltaBias; // > biases update

  // Stores the sum of the product of weights in this
  // layer and their respective inputs.
  private Vector weightedSum;

  // Initialize the layer.
  Layer(boolean isOutputLayer, ActivationFunction activationFunction, Error error) {
    this.isOutputLayer = isOutputLayer;
    this.activationFunction = activationFunction;
    this.errorType = error;
  }

  // Initialize the layer with given weight and bias values.
  Layer(Vector[] vectors, Vector bias, boolean isOutputLayer, ActivationFunction activationFunction, Error error) {
    this.vectors = vectors;
    this.bias = bias;
    this.isOutputLayer = isOutputLayer;
    this.activationFunction = activationFunction;
    this.errorType = error;
    initDeltaArray();
  }

  // Initialize our parameter update storage arrays.
  // These need to wait until the parameter arrays are
  // initialized as they store values in arrays of the
  // same size as the parameter arrays.
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

  // Set the batch size (if a change is desired).
  void setBatchSize(int BATCH_SIZE) {
    this.BATCH_SIZE = BATCH_SIZE;
  }

  // Returns the number of neurons in the next layer
  int length() {
    return this.vectors.length;
  }

  // Returns the actual error amount to the callee.
  Vector getError() {
    return error;
  }

  // Returns the display error amount to the callee.
  Vector getDisplayError() {
    return displayError;
  }

  // Resets the display error.
  // Note that this only happens after each epoch.
  void resetDisplayError() {
    if (displayError == null || !isOutputLayer) {
      return;
    }
    displayError = displayError.mult(0);
  }

  // Returns the output of the layer after being
  // fed an input.
  Vector getOutput() {
    return output;
  }

  // Returns the error function that is used.
  Error getErrorType() {
    return errorType;
  }

  // Returns the bias values.
  Vector getBias() {
    return bias;
  }

  // Returns the activation function used.
  ActivationFunction getActivationFunction() {
    return activationFunction;
  }

  // Feeds the layer an input.
  // Note that this is just a wrapper around test() that stores
  // the output values. Use feed() for training purposes and
  // test() for testing purposes.
  void feed(Vector input) {
    this.input = input;
    this.output = test(input);
  }

  // Feeds the layer a set of inputs.
  Vector test(Vector input) {
    if (input.length() != vectors[0].length()) {
      throw new IllegalArgumentException("test(Vector): Input vector not the same size as weight vectors.");
    }

    // Multiply all weights by their inputs and get the sum going
    // to each neuron in the next layer.
    Vector output = new Vector(vectors.length);
    for (int i = 0; i < vectors.length; i++) {
      output.set(i, vectors[i].mult(input).total());
    }

    // Add the biases to the weighted sums.
    // Note that add() performs element-wise addition.
    output = output.add(bias);

    // Store this as our weighted sum.
    this.weightedSum = output;

    // Apply our activation function onto the weight sum
    output = activation(weightedSum);

    // Return the output
    return output;
  }

  // Perform our activation function on a set of values.
  private Vector activation(Vector val) {
    int valSize = val.length();

    // Create a new vector to store our outputs.
    Vector out = new Vector(valSize);

    // Apply the activation function on each value in the inputted array.
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

  // Actual computations of activation functions happen below.

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

  // Performs the derivative of the activation function with respect to its
  // weighted sum input. Note that 'index' refers to the index of the neuron
  // the weight sum is going to.
  // This index is only used by the softmax activation function.
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

  // Main learning method.
  // Accepts the target/desired output of the layer (null for non-output layers)
  // Accepts the object holding a reference to the next layer (null for the output layer)
  // Accepts a learning rate.
  void learn(Vector targetOutput, Layer nextLayer, double alpha) {
    if (alpha == 0) {
      throw new NullPointerException("learn(): learning rate can't be 0 (no learning).");
    }

    // Parallelize the learning process
    // Speedup approximately 25-33%
    IntStream.range(0, vectors.length).parallel().forEach(index -> {
      Vector error;
      if (isOutputLayer) { // Delta rule
        Vector[] errorCalc = calcError(output, targetOutput);
        error = errorCalc[0];
        displayError = displayError.add(errorCalc[1]);
      } else { // Backpropagation
        error = new Vector(vectors.length);
        for (int indice = 0; indice < nextLayer.vectors.length; indice++) {
          error.set(index, error.get(index) + nextLayer.vectors[indice].get(index) * nextLayer.getError().get(indice));
        }
      }
      // Multiply by activation function derivative
      error = error.mult(derivative(index));

      // Store this error (used by backpropagation algorithm)
      this.error = error;

      // Multiply the gradient by the input and learning rate.
      Vector delta = input.mult(error.get(index));
      delta = delta.mult(alpha);

      // Add the adjustments to our parameter change arrays.
      deltas[index] = deltas[index].add(delta);
      deltaBias.set(index, deltaBias.get(index) + error.get(index) * alpha);
    });

    // Increment the current example's batch id.
    currentInputBatchId++;

    // If we reach the batch size, perform a parameter update and
    // clear our parameter update arrays afterwards.
    // Notice that we don't average the parameter update values
    // by the batch size because theoretically, over-adjustments
    // to parameters will not occur when the errors of the
    // parameters on all examples is summed.
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

  // Calculate the error with respect to the expected/desired output, the
  // layer output, and the error function type itself.
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

  // Initializes our layer with gaussian values from -1 to 1.
  Layer fillGaussian(int size, int numVectors) {
    vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      vectors[i] = new Vector(size).fillGaussian();
    }

    bias = new Vector(numVectors).fillGaussian();
    initDeltaArray();

    return this;
  }

  // Initializes our layer with gaussian values of given average
  // and deviation values.
  void fillGaussian(double average, double deviation) {
    Vector[] replaceVect = new Vector[vectors.length];

    for (int i = 0; i < replaceVect.length; i++) {
      replaceVect[i] = new Vector(this.vectors[i].length()).fillGaussian(average, deviation);
    }

    bias = new Vector(vectors.length).fillGaussian(average, deviation);

    this.vectors = replaceVect;
    initDeltaArray();
  }

  // Fills our layer with random values (NOT RECOMMENDED).
  void fillRandom(int size, int numVectors) {
    this.vectors = new Vector[numVectors];
    for (int i = 0; i < numVectors; i++) {
      vectors[i] = new Vector(size).fillRandom();
    }

    bias = new Vector(numVectors).fillRandom();
    initDeltaArray();
  }
}
