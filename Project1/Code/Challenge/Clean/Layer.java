import java.io.Serializable;

public class Layer implements Serializable {
  private int neuronNumber;
  private int nextNeuronNumber;
  private Neuron[] neurons;
  private Layer nextLayer;
  private double[] input;
  private double[] result;
  private double[] target;
  private double learningRate;

  public Layer(int neuronNumber) {
    this.neuronNumber = neuronNumber;
    neurons = new Neuron[neuronNumber];
    result = new double[nextNeuronNumber];
    outputInit();
  }

  public Layer(int neuronNumber, int next, Layer nextLayer, double learningRate) {
    this.neuronNumber = neuronNumber;
    nextNeuronNumber = next;
    neurons = new Neuron[neuronNumber];
    this.learningRate = learningRate;
    result = new double[nextNeuronNumber];
    this.nextLayer = nextLayer;
    initialise();
  }

  private void inputFired() {
    for (Neuron x : neurons) {
      x.finalise();
    }
  }

  public void setInput(double[] input) {
    this.input = input;
  }

  public void setTarget(double[] target) {
    this.target = target;
  }

  public void setLearningRate(double learningRate) {
    this.learningRate = learningRate;
  }

  public double getLearningRate() {
    return this.learningRate;
  }

  private void outputInit() {
    for (int i = 0; i < neuronNumber; i++) {
      neurons[i] = new Neuron();
      neurons[i].initialise();
    }
  }

  private void initialise() {
    for (int i = 0; i < neuronNumber; i++) {
      neurons[i] = new Neuron(nextNeuronNumber);
      neurons[i].initialise();
    }

    if (nextLayer != null) {
      for (int j = 0; j < neuronNumber; j++) {
        for (int k = 0; k < nextNeuronNumber; k++) {
          neurons[j].connect(nextLayer.getNeurons()[k]);
        }
      }
    }
  }

  public void learn() {
    step();
    double[][] changeWeights = new double[neurons.length][nextNeuronNumber];
    for (int i = 0; i < nextNeuronNumber; i++) {
      for (int j = 0; j < neurons.length; j++) {
        changeWeights[j][i] = learningRate * input[j] * (target[i] - result[i]) * derivedSigmoid(neurons[j].getInput());
      }
    }

    for (int index = 0; index < neurons.length; index++) {
      neurons[index].adjustWeight(changeWeights[index]);
    }

    clear();
  }

  private void step() {
    for (int i = 0; i < input.length; i++) {
      neurons[i].setInput(input[i]);
      neurons[i].step();
      for (int j = 0; j < result.length; j++) {
        result[j] += neurons[i].getResult()[j];
      }
    }
    result = sigmoid(result);
    nextLayer.inputFired();
  }

  public double[] test(double[] input) {
    for (int i = 0; i < input.length; i++) {
      neurons[i].setInput(input[i]);
      neurons[i].step();
      for (int j = 0; j < result.length; j++) {
        result[j] += neurons[i].getResult()[j];
      }
    }
    result = sigmoid(result);
    clear();
    return result;
  }

  private void clear() {
    for (Neuron neuron : neurons) {
      neuron.clear();
    }

    for (Neuron neuron : nextLayer.getNeurons()) {
      neuron.clear();
    }
  }

  private double[] sigmoid(double[] result) {
    for (int i = 0; i < result.length; i++) {
      result[i] = sigmoid(result[i]);
    }
    return result;
  }

  private double sigmoid(double result) {
    return 1 / (1 + Math.exp(-result));
  }

  private double derivedSigmoid(double x) {
    return sigmoid(x) * (1 - sigmoid(x));
  }

  public Neuron[] getNeurons() {
    return neurons;
  }
}
