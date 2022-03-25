import java.util.Arrays;
import java.util.Random;

public class Vector {
  private static final Random rand = new Random();
  private final double[] vector;

  Vector(int length) {
    vector = new double[length];
  }

  Vector(double[] values) {
    vector = values;
  }

  Vector(int[] values) {
    vector = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      vector[i] = values[i];
    }
  }

  static Vector add(Vector vector, Vector anotherVector) {
    if (anotherVector.length() != vector.length()) {
      throw new IllegalArgumentException("add(): Vectors not of same size: {" + vector.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(vector.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = vector.vector[i] + anotherVector.vector[i];
    }

    return output;
  }

  static Vector div(Vector vector, Vector anotherVector) {
    if (anotherVector.length() != vector.length()) {
      throw new IllegalArgumentException("div(): Vectors not of same size: {" + vector.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(vector.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = vector.vector[i] / anotherVector.vector[i];
    }

    return output;
  }

  static Vector mult(Vector vector, double value) {
    Vector output = new Vector(vector.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = vector.vector[i] * value;
    }

    return output;
  }

  static Vector add(Vector vector, double value) {
    Vector output = new Vector(vector.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = vector.vector[i] + value;
    }
    return output;
  }

  static Vector subtract(Vector vector, Vector anotherVector) {
    if (anotherVector.length() != vector.length()) {
      throw new IllegalArgumentException("subtract(): Vectors not of same size: {" + vector.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(vector.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = vector.vector[i] - anotherVector.vector[i];
    }

    return output;
  }

  static Vector log(Vector vector) {
    Vector out = new Vector(vector.length());
    for (int i = 0; i < vector.length(); i++) {
      out.set(i, Math.log(vector.vector[i]));
    }
    return out;
  }

  static Vector sqrt(Vector vector) {
    Vector out = new Vector(vector.length());
    for (int i = 0; i < vector.length(); i++) {
      out.set(i, Math.sqrt(vector.vector[i]));
    }
    return out;
  }

  static Vector mult(Vector vector, Vector anotherVector) {
    if (anotherVector.length() != vector.length()) {
      throw new IllegalArgumentException("mult(): Vectors not of same size: {" + vector.length() + ", " + anotherVector.length() + "}.");
    }

    Vector output = new Vector(vector.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = vector.vector[i] * anotherVector.vector[i];
    }

    return output;
  }

  void add(Vector anotherVector) {
    if (anotherVector.length() != length()) {
      throw new IllegalArgumentException("add(): Vectors not of same size: {" + length() + ", " + anotherVector.length() + "}.");
    }

    for (int i = 0; i < length(); i++) {
      vector[i] += anotherVector.vector[i];
    }
  }

  void div(Vector anotherVector) {
    if (anotherVector.length() != length()) {
      throw new IllegalArgumentException("add(): Vectors not of same size: {" + length() + ", " + anotherVector.length() + "}.");
    }

    for (int i = 0; i < length(); i++) {
      vector[i] /= anotherVector.vector[i];
    }
  }

  void mult(double value) {
    for (int i = 0; i < length(); i++) {
      vector[i] *= value;
    }
  }

  void add(double value) {
    for (int i = 0; i < length(); i++) {
      vector[i] += value;
    }
  }

  void subtract(Vector anotherVector) {
    if (anotherVector.length() != length()) {
      throw new IllegalArgumentException("add(): Vectors not of same size: {" + length() + ", " + anotherVector.length() + "}.");
    }

    for (int i = 0; i < length(); i++) {
      vector[i] -= anotherVector.vector[i];
    }
  }

  void log() {
    for (int i = 0; i < length(); i++) {
      vector[i] = Math.log(vector[i]);
    }
  }

  void sqrt() {
    for (int i = 0; i < length(); i++) {
      vector[i] = Math.sqrt(vector[i]);
    }
  }

  int length() {
    return vector.length;
  }

  double[] values() {
    return vector;
  }

  double get(int index) {
    return vector[index];
  }

  void set(int index, double value) {
    vector[index] = value;
  }

  double total() {
    double sum = 0;
    for (double val : vector) {
      sum += val;
    }
    return sum;
  }

  void mult(Vector anotherVector) {
    if (anotherVector.length() != length()) {
      throw new IllegalArgumentException("mult(): Vectors not of same size: {" + length() + ", " + anotherVector.length() + "}.");
    }

    for (int i = 0; i < length(); i++) {
      vector[i] *= anotherVector.vector[i];
    }
  }

  Vector fill(double value) {
    for (int i = 0; i < length(); i++) {
      vector[i] = value;
    }
    return this;
  }

  Vector fillGaussian() {
    for (int i = 0; i < length(); i++) {
      vector[i] = rand.nextGaussian();
    }
    return this;
  }

  Vector fillGaussian(double average, double deviation) {
    for (int i = 0; i < length(); i++) {
      vector[i] = rand.nextGaussian() * deviation + average;
    }
    return this;
  }

  Vector fillRandom() {
    for (int i = 0; i < length(); i++) {
      vector[i] = rand.nextDouble();
    }
    return this;
  }

  @Override
  public String toString() {
    return Arrays.toString(vector);
  }
}
