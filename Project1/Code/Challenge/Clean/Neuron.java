import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Neuron implements Serializable {
  Random rand = new Random();
  Neuron[] connectTo;
  double[] weight;
  double input;
  double[] result;
  int index = 0;

  public Neuron() {
    weight = new double[]{1};
    result = new double[1];
  }

  public Neuron(int connectCount) {
    connectTo = new Neuron[connectCount];
    weight = new double[connectCount];
    result = new double[connectCount];
    initialise();
  }

  public void connect(Neuron neuron) {
    connectTo[index] = neuron;
    index++;
  }

  public void setInput(double input) {
    this.input += input;
  }

  public double getInput() {
    return input;
  }

  public void finalise() {
    input = sigmoid(this.input);
  }

  public double sigmoid(double input) {
    return 1 / (1 + Math.exp(-input));
  }

  public void initialise() {
    for (int i = 0; i < weight.length; i++) {
      weight[i] = rand.nextGaussian();
    }
  }

  public void step() {
    for (int i = 0; i < weight.length; i++) {
      result[i] = input * weight[i];
    }

    if (connectTo != null) {
      for (int index = 0; index < connectTo.length; index++) {
        connectTo[index].setInput(this.getResult(index));
      }
    }
  }

  public double[] getWeight() {
    return weight;
  }

  public void adjustWeight(double[] adjustment) {
    for (int i = 0; i < weight.length; i++) {
      weight[i] += adjustment[i];
    }
  }

  public double getResult(int index) {
    return this.result[index];
  }

  public double[] getResult() {
    return this.result;
  }

  public void clear() {
    input = 0;
    index = 0;
  }

  @Override
  public String toString() {
    return Arrays.toString(result);
  }
}