public class lsm {
  public static void main(String[] args) {
    double[] x = {
            2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0
    };

    double[] y = {
            2.0, 4.0, 3.0, 4.5, 6.0, 5.0, 7.6, 6.0
    };

    double xAverage = 0;
    double yAverage = 0;
    double sum1 = 0;
    double sum2 = 0;

    for (double val : x) {
      xAverage += val;
    }

    for (double val : y) {
      yAverage += val;
    }

    xAverage /= x.length;
    yAverage /= y.length;

    for (int i = 0; i < x.length; i++) {
      sum1 += (x[i] - xAverage) * (y[i] - yAverage);
      sum2 += (x[i] - xAverage) * (x[i] - xAverage);
    }

    double slope = sum1 / sum2;
    double inter = yAverage - slope * xAverage;

    System.out.println(sum1);
    System.out.println(sum2);
    System.out.println(slope);
    System.out.println(inter);
    System.out.println(xAverage);
    System.out.println(yAverage);
  }
}
