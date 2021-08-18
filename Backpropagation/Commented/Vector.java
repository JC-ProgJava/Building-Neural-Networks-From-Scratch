// Import necessary libraries.

import java.util.Arrays;
import java.util.Random;

// Our vector class, which stores weights connecting to a particular neuron
// in the next layer.

// Additionally, vectors can store other arrays of values (i.e., the
// output of a layer)
public class Vector {
  // Values stored in a double array
  private final double[] vector;

  // A random object that helps initialize our vectors.
  private final Random rand = new Random();

  // Initialize our vector array to be able to store
  // 'length' number of weights/values.
  // Note that after creating our vectors from this
  // constructor, the double array of weights/values is filled
  // with 0s.
  Vector(int length) {
    vector = new double[length];
  }

  // Initialize our vector from an array of given values.
  Vector(double[] values) {
    vector = values;
  }

  // Initialize our vector from an array of given integral numbers.
  Vector(int[] values) {
    vector = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      vector[i] = values[i];
    }
  }

  // Return the length of the vector
  int length() {
    return this.vector.length;
  }

  // Return our values
  double[] values() {
    return vector;
  }

  // Return the value at a particular index of the weight/value array.
  double get(int index) {
    return vector[index];
  }

  // Set the weight/value at a particular index to a new value.
  // Used during training when updating our parameters.
  void set(int index, double value) {
    vector[index] = value;
  }

  // Total all values in the indices of the value array and
  // return the result to the callee.
  double total() {
    double sum = 0;
    for (double val : this.vector) {
      sum += val;
    }
    return sum;
  }

  // Perform element-wise addition
  Vector add(Vector anotherVector) {
    if (anotherVector.length() != this.length()) {
      throw new IllegalArgumentException("add(): Vectors not of same size: {" + this.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] + anotherVector.vector[i];
    }

    return output;
  }

  // Perform element-wise multiplication
  Vector mult(Vector anotherVector) {
    if (anotherVector.length() != this.length()) {
      throw new IllegalArgumentException("mult(): Vectors not of same size: {" + this.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] * anotherVector.vector[i];
    }

    return output;
  }

  // Perform element-wise division.
  // a / b
  // a -> The callee vector
  // b -> 'anotherVector'
  Vector div(Vector anotherVector) {
    if (anotherVector.length() != this.length()) {
      throw new IllegalArgumentException("div(): Vectors not of same size: {" + this.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] / anotherVector.vector[i];
    }

    return output;
  }

  // Multiply each value in the internal array by constant.
  Vector mult(double value) {
    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] * value;
    }

    return output;
  }

  // Add a constant to each value in the internal array.
  Vector add(double value) {
    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] + value;
    }
    return output;
  }

  // Perform element-wise subtraction
  // a - b
  // a -> the callee vector ('this')
  // b -> 'anotherVector'
  Vector subtract(Vector anotherVector) {
    if (anotherVector.length() != this.length()) {
      throw new IllegalArgumentException("subtract(): Vectors not of same size: {" + this.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] - anotherVector.vector[i];
    }

    return output;
  }

  // Fill our vector with some constant value
  Vector fill(double value) {
    for (int i = 0; i < this.length(); i++) {
      this.vector[i] = value;
    }
    return this;
  }

  // Initialize an array of values from gaussian distribution
  // Range: -1 to 1.
  Vector fillGaussian() {
    for (int i = 0; i < length(); i++) {
      this.vector[i] = rand.nextGaussian();
    }
    return this;
  }

  // Initialize an array of values from custom gaussian distribution.
  Vector fillGaussian(double average, double deviation) {
    for (int i = 0; i < length(); i++) {
      this.vector[i] = rand.nextGaussian() * deviation + average;
    }
    return this;
  }

  // Initialize an array of values from uniform distribution between 0 and 1.
  // Not recommended!
  Vector fillRandom() {
    for (int i = 0; i < length(); i++) {
      this.vector[i] = rand.nextDouble();
    }
    return this;
  }

  // Perform natural logarithm on each element in the current
  // vector and return the result in a new vector.
  Vector log() {
    Vector out = new Vector(length());
    for (int i = 0; i < length(); i++) {
      out.set(i, Math.log(vector[i]));
    }
    return out;
  }

  // Print the values in the vector.
  @Override
  public String toString() {
    return Arrays.toString(vector);
  }
}
