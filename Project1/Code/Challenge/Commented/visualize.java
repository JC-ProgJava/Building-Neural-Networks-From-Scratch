// Import necessary libraries.
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class visualize {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // Define constants used later on in the program so that it is easily changeable.
    int row = 28;
    int column = 28;
    int layNum = row * column;
    int outNum = 10;

    // Read the Layer class so that weights can be obtained.
    ObjectInputStream oiss = new ObjectInputStream(new BufferedInputStream(new FileInputStream("app.ser")));
    Layer layer = (Layer) oiss.readObject();

    Neuron[] neurons = layer.getNeurons();
    double[][] weights = new double[outNum][layNum];
    double[][] transposeWeights = new double[layNum][outNum];

    // First collect all weights as a multidimensional array, then transpose the array
    // so that it is more usable and easier to loop through later on.
    for(int index = 0; index < neurons.length; index++) {
      transposeWeights[index] = neurons[index].getWeight();
    }

    for(int i = 0; i < transposeWeights.length; i++) {
      for(int j = 0; j < transposeWeights[i].length; j++) {
        weights[j][i] = transposeWeights[i][j];
      }
    }

    int index = 0;

    // Write each image by finding the range of the weights and judging the weight’s
    // importance relatively, and then filling a pixel with the same rgb values to get
    // a grayscale image.
    for(double[] z : weights) {
      File file = new File("images/" + index + ".png");
      BufferedImage bufferedImage = new BufferedImage(column, row, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();
      double max = getMax(z);
      double min = getMin(z);
      double diff = max - min;
      int ind = 0;
      for (int x = 0; x < column; x++) {
        for (int y = 0; y < row; y++) {
          int col = (int) (255 - ((z[ind] - min) / diff * 255));
          g2d.setColor(new Color(col, col, col));
          g2d.fillRect(x, y, 1, 1);
          ind++;
        }
      }
      g2d.dispose();
      ImageIO.write(bufferedImage, "png", file);
      index++;
    }
  }

  // Get the maximum weight value from an array.
  public static double getMax(double[] x) {
    double y = Double.MIN_VALUE;
    for(double z : x) {
      if(z > y) {
        y = z;
      }
    }
    return y;
  }

  // Get the minimum weight value from an array.
  public static double getMin(double[] x) {
    double y = Double.MAX_VALUE;
    for(double z : x) {
      if(z < y) {
        y = z;
      }
    }
    return y;
  }
}
