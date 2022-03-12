import java.util.Arrays;
import java.util.Random;

public class Vector {
  private final double[] vector;
  private final Random rand = new Random();

  Vector(int length) {
    this.vector = new double[length];
  }

  Vector(double[] values) {
    this.vector = values;
  }

  Vector(int[] values) {
    this.vector = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      this.vector[i] = values[i];
    }
  }

  int length() {
    return this.vector.length;
  }

  double[] values() {
    return this.vector;
  }

  double get(int index) {
    return this.vector[index];
  }

  void set(int index, double value) {
    this.vector[index] = value;
  }

  double total() {
    double sum = 0;
    for (double val : this.vector) {
      sum += val;
    }
    return sum;
  }

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

  Vector mult(double value) {
    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] * value;
    }

    return output;
  }

  Vector add(double value) {
    Vector output = new Vector(this.length());
    for (int i = 0; i < output.length(); i++) {
      output.vector[i] = this.vector[i] + value;
    }
    return output;
  }

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

  Vector fill(double value) {
    for (int i = 0; i < this.length(); i++) {
      this.vector[i] = value;
    }
    return this;
  }

  Vector fillGaussian() {
    for (int i = 0; i < this.length(); i++) {
      this.vector[i] = this.rand.nextGaussian();
    }
    return this;
  }

  Vector fillGaussian(double average, double deviation) {
    for (int i = 0; i < this.length(); i++) {
      this.vector[i] = this.rand.nextGaussian() * deviation + average;
    }
    return this;
  }

  Vector fillRandom() {
    for (int i = 0; i < this.length(); i++) {
      this.vector[i] = this.rand.nextDouble();
    }
    return this;
  }

  Vector log() {
    Vector out = new Vector(this.length());
    for (int i = 0; i < this.length(); i++) {
      out.set(i, Math.log(this.vector[i]));
    }
    return out;
  }

  Vector sqrt() {
    Vector out = new Vector(this.length());
    for (int i = 0; i < this.length(); i++) {
      out.set(i, Math.sqrt(this.vector[i]));
    }
    return out;
  }

  @Override
  public String toString() {
    return Arrays.toString(this.vector);
  }
}
