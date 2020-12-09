public class test2 {
  public static void main(String[] args) {
    final double[][] target = {
      {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    final double[][] inputs = {
      {1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1},
      {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
      {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
      {1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
      {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1},
      {1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1},
      {1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1},
      {1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
      {1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
      {1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
    };

    Layer nextLayer = new Layer(10);
    Layer layer = new Layer(15, 10, nextLayer, 0.5);

    for (int j = 0; j < 1000; j++) {
      for (int i = 0; i < inputs.length; i++) {
        layer.setInput(inputs[i]);
        layer.setTarget(target[i]);
        layer.learn();
      }
    }

    for (double[] input : inputs) {
      System.out.println(getBestGuess(layer.test(input)));
    }
  }

  public static int getBestGuess(double[] result) {
    double k = Integer.MIN_VALUE;
    double index = 0;
    int current = 0;
    for (double a : result) {
      if (k < a) {
        k = a;
        index = current;
      }
      current++;
    }

    return (int) index;
  }
}