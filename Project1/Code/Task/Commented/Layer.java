// Our layer class, which does all the work for machine learning.
class Layer {
    // Store the number of neurons in this
    // layer. Used during for-loops.
    int neuronNumber;
    // Store number of neurons in next layer.
    int nextNeuronNumber;
    // The neurons in this layer as an array of Neuron objects.
    Neuron[] neurons;
    // Keep the next layer's instance/object reference point.
    Layer nextLayer;
    // This stores the input, or current dataset that is being
    // used to train or test the neural network.
    double[] input;
    // Stores the result from the output neurons from
    // step() method/function.
    double[] result;
    // Stores the best result for each output neuron
    // for the current input set.
    double[] target;
    /*
       Stores the learning rate or constant that is multiplied
       with the rest of the calculations to increase/decrease
       the step taken/adjustment made to prevent missing a
       minima.
     */
    double learningRate;

    /*
        First constructor option for output layer. Output layers
        are not a separate class but are initialized differently.
        Requires the neuron number in the layer.
     */
    public Layer(int neuronNumber) {
        this.neuronNumber = neuronNumber;
        neurons = new Neuron[neuronNumber];
        this.result = new double[nextNeuronNumber];
        outputInit();
    }

    /*
        Second constructor option for hidden and input layers.
        Requires neuron number in current layer, next layer,
        the next layer's reference
     */
    public Layer(int neuronNumber, int next, Layer nextLayer, double learningRate) {
        this.neuronNumber = neuronNumber;
        this.nextNeuronNumber = next;
        neurons = new Neuron[neuronNumber];
        this.learningRate = learningRate;
        this.result = new double[nextNeuronNumber];
        this.nextLayer = nextLayer;
        initialise();
    }

    /*
        After stepping through the network, each neuron will
        have a sum of neuron outputs as input, but sigmoid
        function is not taken yet, so we finalise all the
        neurons' inputs before stepping through the layer.
     */
    public void inputFired() {
        for(Neuron x : neurons) {
            x.finalise();
        }
    }

    /*
        Set the input to the layer for current iteration
        of learning. The input is given as an array,
        which should have the same length as the
        number of neurons in the current layer.
     */
    public void setInput(double[] input) {
        this.input = input;
    }

    /*
        Set the target, which is an array with zeroes and ones.
        The ones indicate the neuron that should fire if the
        prediction is correct. See the 'test' class for the
        target array.
     */
    public void setTarget(double[] target) {
        this.target = target;
    }

    /*
        Set the learning rate to the network if you want to update
        it throughout training process.
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    /*
        You can print info about the layer's learning rate
        using this method.
     */
    public double getLearningRate() {
        return this.learningRate;
    }

    /*
        Initialise the neurons for a output neuron, which is
        constructed differently from the hidden and input neurons.
     */
    private void outputInit() {
        for(int i = 0; i < neuronNumber; i++) {
            neurons[i] = new Neuron();
            neurons[i].initialise();
        }
    }

    /*
        Initialise the neurons normally(for input and hidden layer
        neurons), we connect the neurons to the next layer's neurons
        here as well.
     */
    private void initialise() {
        for(int i = 0; i < neuronNumber; i++) {
            neurons[i] = new Neuron(nextNeuronNumber);
            neurons[i].initialise();
        }

        if(nextLayer != null) {
            for(int j = 0; j < neuronNumber; j++) {
                for(int k = 0; k < nextNeuronNumber; k++) {
                    neurons[j].connect(nextLayer.getNeurons()[k]);
                }
            }
        }
    }

    /*
        Learning method. First, step through the layer with inputs
        using the step() method. Then, we initialise a multi-dimensional
        array that contains the changes for each neuron. Then, we use the
        delta rule to calculate the change in weights for each neuron's weights.
        I use the general delta rule formula, which multiplies the product with
        an additional number which is the derived sigmoid given an input of
        the weighted sum of inputs to a neuron. Then, we give each neuron
        a list of changes that are added to the weight for the next epoch.
        Then, we perform the clear() function which uses the clear() in
        the Neuron class for each neuron in the layer. This makes it ready
        for another epoch of learning.
     */
    public void learn() {
        step();
        double[][] changeWeights = new double[neurons.length][nextNeuronNumber];
        for(int i = 0; i < nextNeuronNumber; i++) {
            for(int j = 0; j < neurons.length; j++) {
                changeWeights[j][i] = learningRate * input[j] * (target[i] - result[i]) * derivedSigmoid(neurons[j].getInput());
            }
        }

        for(int index = 0; index < neurons.length; index++) {
            neurons[index].adjustWeight(changeWeights[index]);
        }

        clear();
    }

    /*
        Step function named the same as one in the Neuron class. Sets
        the input for the neuron using the input array initialised with
        the constructor and calls the step() function on the neuron.
        Then, the result array gets the value of the output of the neuron,
        and is run through the Sigmoid Activation Function. Then call the
        inputFired() function for the next layer.
     */
    public void step() {
        for(int i = 0; i < input.length; i++) {
            neurons[i].setInput(input[i]);
            neurons[i].step();
            for(int j = 0; j < result.length; j++) {
                result[j] += neurons[i].getResult()[j];
            }
        }
        result = sigmoid(result);
        nextLayer.inputFired();
    }

    /*
        After learning, provide a method to test the network on a given
        input set/array. Basically, it performs a step() on the network,
        and prevents it from getting ready to learn the sample. Then,
        it calls clear() to get the network ready for another round of
        recognising if needed. Then, it returns the result as an array to
        the caller.
     */
    public double[] test(double[] input) {
        for(int i = 0; i < input.length; i++) {
            neurons[i].setInput(input[i]);
            neurons[i].step();
            for(int j = 0; j < result.length; j++) {
                result[j] += neurons[i].getResult()[j];
            }
        }
        result = sigmoid(result);
        this.clear();
        return result;
    }

    /*
        Clear the inputs and indexes kept in the current and next layer.
     */
    public void clear() {
        for (Neuron neuron : neurons) {
            neuron.clear();
        }

        for (Neuron neuron : nextLayer.getNeurons()) {
            neuron.clear();
        }
    }

    /*
        Take in an array of values and run the sigmoid activation function
        calculations on each of them, then return the array to the caller.
     */
    public double[] sigmoid(double[] result) {
        for(int i = 0; i < result.length; i++) {
            result[i] = sigmoid(result[i]);
        }
        return result;
    }

    /*
        Method overriding, used in the sigmoid() method above for calculating
        sigmoid of single value. Returns the value back to the caller.
     */
    public double sigmoid(double result) {
        return 1 / (1 + Math.exp(-result));
    }

    /*
        This method returns the value of the derived sigmoid function
        calculated on an input.
     */
    public double derivedSigmoid(double x) {
        return sigmoid(x) * (1 - sigmoid(x));
    }

    /*
        Return the neurons in the current layer to the caller. Used
        for the layer to access the next layer's neurons.
     */
    public Neuron[] getNeurons() {
        return this.neurons;
    }
}
