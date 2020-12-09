//import all necessary libraries

// Java Utilities Array import: used 1 time in
// @Override toString() method below clear().
import java.util.Arrays;
// Gaussian distribution for weights is done
// in initialise(), requires Random package.
import java.util.Random;

/*
    This will be our Neuron class, based on
    the perceptron. Note that I have not
    included the bias component or threshold
    component. They are not necessary for the
    network to classify digits extremely accurately.
 */
class Neuron {
    /*
        Random instance for entire class. This
        makes object more efficient because a
        new Random instance is not initialised/created
        for every new weight when finding a gaussian-distributed
        value for it.
    */
    Random rand = new Random();
    /*
        This array stores the Neuron objects that the current
        neuron connects to in the next layer. This is used to
        directly transfer the result to the neuron instead of
        implementing it in the Layer class. The connections are
        made in the initialise() function in the Layer class.
     */
    Neuron[] connectTo;
    /*
        This stores the weight values that connect the current
        neuron to a neuron in the next layer according to index.
        The weights are initialised using Gaussian Distribution
        in this program.
     */
    double[] weight;
    /*
        I store all inputs as a single variable. If this neuron is
        the input neuron, a single value is passed to it. If it is
        from a hidden or output layer, the input is added to it.
        Then, the Layer class calls the finalise() method, when all
        neurons have fired their inputs to the next layer's neurons
        which runs the Sigmoid Activation Function on the overall
        input.
     */
    double input;
    /*
        The result from firing the input into the Neuron is stored as
        an array to each of the neurons this neuron is connected to.
        Then, each of them is given to the next neuron using the
        setInput() method.
     */
    double[] result;
    /*
        I put a global 'index' variable to store the connections
        the current neuron has with the next layer's neurons. It
        is incremented, and just in case there is some
        ArrayIndexOutOfBoundsException, I use a clear()
        method, which is called after stepping through the neuron.
        The clear() method also clears the input to the neuron so
        the input doesn't accumulate throughout the epochs for
        training the network.
     */
    int index = 0;

    /*
        Initialise Neuron, providing nothing. This is how the Layer
        class initialises the output layer's neurons. Other neurons
        are initialised using the other constructor.
     */
    public Neuron() {
        weight = new double[]{1};
        result = new double[1];
    }

    /*
        This is how hidden and input layers' neurons are initialised.
        The arrays are given lengths and initialised using the
        initialise() function.
     */
    public Neuron(int connectCount) {
        connectTo = new Neuron[connectCount];
        weight = new double[connectCount];
        result = new double[connectCount];
        initialise();
    }

    /*
        The layer class uses this method to connect neurons to other
        neurons. Another alternative is to get a Neuron array
        (Neuron[]), and set the array here instead.
        In that case, the 'index' global variable can be removed.
     */
    public void connect(Neuron neuron) {
        connectTo[index] = neuron;
        index++;
    }

    /*
        The input layer neurons are given a single input,
        but the output and hidden layer neurons are given multiple.
        These have to be added, and later finalised(take the sigmoid
        of the number, using the activation function in finalise()).
     */
    public void setInput(double input) {
        this.input += input;
    }

    /*
        This method isn't used in the class, but the Layer class
        needs it to perform the learning(delta rule formula) on the
        neuron's weights.
     */
    public double getInput() {
        return input;
    }

    /*
        The function simply makes the input fit between 0 and 1, by
        passing it through the Sigmoid Activation Function.
     */
    public void finalise() {
        input = sigmoid(this.input);
    }

    /*
        Sigmoid activation function, you can find the formula in Chapter
        3, but it is below as well. Math.exp(x) is used to express
        e^(x), which is basically Euler's number to the power of x.
     */
    public double sigmoid(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    /*
        Here, weights are initialised using Gaussian Distribution.
        The Random import class provides the nextGaussian() method
        which generates Gaussian-distributed numbers.
     */
    public void initialise() {
        for (int i = 0; i < weight.length; i++) {
            weight[i] = rand.nextGaussian();
        }
    }

    /*
        Probably the most complicated function in the entire class,
        it is quite self-explanatory. The input and weight variables
        have been initialised, so we calculate the result array's values,
        then, we pass these values to each of the neuron's connected neurons
        in the next layer if there is a set of connections for the neuron.
        The if condition checks this because output neurons are not a separate
        class, but initialized differently(I provided two constructor methods)
        without the connectTo array with connections(output is network's output,
        not connected to another layer of neurons).
     */
    public void step() {
        for(int i = 0; i < weight.length; i++) {
            result[i] = input * weight[i];
        }

        if (connectTo != null) {
            for (int index = 0; index < connectTo.length; index++) {
                connectTo[index].setInput(this.getResult(index));
            }
        }
    }

    /*
        The adjustWeight() function is put here to reduce the amount of work
        the Layer class needs to do. A array of adjustments are given after
        the Layer class runs through the delta rule in the learn() function.
        The values are added to the weight. (NOT SUBTRACTED)
     */
    public void adjustWeight(double[] adjustment) {
        for(int i = 0; i < weight.length; i++){
            weight[i] += adjustment[i];
        }
    }

    /*
        The current class uses it to assign input values together to each
        of the neurons the neuron is connected to in the step() method.
        Note that it gets the result at a single index in the result array.
     */
    public double getResult(int index) {
        return this.result[index];
    }

    /*
        The Layer class needs the result of a neuron to compute changes for the
        neuron's weights using the delta learning rule in the learn() function.
        Note that the entire array is returned, so that the for-loop in the
        Layer class can use the values.
     */
    public double[] getResult() {
        return this.result;
    }

    /*
        Simply clears the 'input' variable for a new epoch of learning.
        The 'index' variable is cleared in case of future adjustments that
        may require it.
     */
    public void clear() {
        input = 0;
        index = 0;
    }

    /*
        Overrides the Object.toString() method to return something more meaningful.
        Used for debugging and testing/implementing like in the 'test' class.
     */
    @Override
    public String toString() {
        return Arrays.toString(result);
    }
}