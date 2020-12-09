// In this class, we will test our neural network.
// Goal: Classify digits 0-9
// Requires: learning rate, number of neurons, target
// values, input values.
public class test2 {
    public static void main(String[] args) {
        /*
           Here, we store the desired result, where all neurons
           should ideally output 0, while the correct neuron should
           output a 1.
         */
        final double[][] target = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 0
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, // 1
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, // 2
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, // 3
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // 4
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0}, // 5
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0}, // 6
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, // 7
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, // 8
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1}  // 9
        };

        /*
            Store our training dataset as a multi-dimensional
            array, where the numbers should form a 5x3(row * column)
            grid that looks like a digit respectively. Stored from 0-9
            to make it easier to retrieve the output of the network
            as a result not array in the getBestGuess() method.
         */
        final double[][] inputs = {
                {1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1}, // 0
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0}, // 1
                {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1}, // 2
                {1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1}, // 3
                {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1}, // 4
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1}, // 5
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1}, // 6
                {1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1}, // 7
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1}, // 8
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1}  // 9
        };

        /*
            Initialise the layers from output layer to input layer,
            output layer doesn't require a layer to be connected with it.
            The hidden and input layers should be connected to one another.
         */
        Layer nextLayer = new Layer(10);
        Layer layer = new Layer(15, 10, nextLayer, 0.5);

        /*
            Epoch: 1000
            Objective: make the network learn the dataset with 10 sets of inputs.
            Roughly 1000 epochs are needed to make the desired output neuron output
            0.9 or higher. The input is set using setInput() and target using setTarget().
            The layer learns when the learn() function is called.
         */
        for(int j = 0; j < 1000; j++) {
            for (int i = 0; i < inputs.length; i++) {
                layer.setInput(inputs[i]);
                layer.setTarget(target[i]);
                layer.learn();
            }
        }

        /*
            View our result! Run through each of the input sets and see our network's
            classification result. Should look like:
            0
            1
            2
            3
            4
            5
            6
            7
            8
            9
         */
        for (double[] input : inputs) {
            System.out.println(getBestGuess(layer.test(input)));
        }
    }

    /*
        Get the answer. E.g., 5, 3, 4... instead of an array of values between
        0 and 1. This method is not stored in the Layer because it is more
        convenient for the programmer to define the output of the network given
        a set of values that each output neuron outputs.
     */
    public static int getBestGuess(double[] result){
        double k = Integer.MIN_VALUE;
        double index = 0;
        int current = 0;
        for(double a : result){
            if(k < a){
                k = a;
                index = current;
            }
            current++;
        }

        return (int)index;
    }
}
