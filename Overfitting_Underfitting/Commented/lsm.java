public class lsm {
  public static void main(String[] args) {
    // STEP 0: initialize everything
    // note I put '.0' behind every whole number to make sure the
    // Java interpreter understands that I want floating-point
    // precision.

    // Dataset Input
    double[] x = {
            2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0
    };

    // Dataset Output
    double[] y = {
            2.0, 4.0, 3.0, 4.5, 6.0, 5.0, 7.6, 6.0
    };

    // Store average of all input values
    double xAverage = 0;

    // Store average of all output values
    double yAverage = 0;

    // Store STEP 3 summation #1
    double sum1 = 0;

    // Store STEP 3 summation #2
    double sum2 = 0;

    // STEP 1
    for (double val : x) {
      // Summation of all values in x
      xAverage += val;
    }

    for (double val : y) {
      // Summation of all values in y
      yAverage += val;
    }

    // Step 2

    // Divide by length of x array, finding average
    xAverage /= x.length;

    // Divide by length of y array, finding average
    yAverage /= y.length;

    // Step 3
    for (int i = 0; i < x.length; i++) {
      sum1 += (x[i] - xAverage) * (y[i] - yAverage);
      sum2 += (x[i] - xAverage) * (x[i] - xAverage);
    }

    // ...and divide it by sum2
    double slope = sum1 / sum2; // slope of line

    // Step 4
    // Find y-intercept
    double inter = yAverage - slope * xAverage;

    // Print out values
    System.out.println(sum1);
    System.out.println(sum2);
    System.out.println(slope);
    System.out.println(inter);
    System.out.println(xAverage);
    System.out.println(yAverage);
  }
}
